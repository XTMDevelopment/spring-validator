package id.xtramile.validator.validator.finance;

import id.xtramile.validator.annotation.finance.ValidCardNumber;
import id.xtramile.validator.enums.Group;
import id.xtramile.validator.util.MessageUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import static id.xtramile.validator.util.ValidatorUtils.isBlank;

/**
 * Validator implementation for {@link ValidCardNumber} annotation.
 * <p>
 * Validates credit/debit card numbers using the Luhn algorithm.
 * This validator ensures the card number passes the Luhn checksum algorithm
 * for basic validity with optional separator stripping.
 * 
 * <p>The validator performs the following operations:
 * <ul>
 * <li>Accepts null/blank values as valid</li>
 * <li>Optionally strips spaces and dashes</li>
 * <li>Validates length (12-19 digits)</li>
 * <li>Applies Luhn algorithm validation</li>
 * <li>Handles digit doubling and sum calculation</li>
 * </ul>
 * 
 * @see ValidCardNumber
 */
public class CardNumberValidator implements ConstraintValidator<ValidCardNumber, String> {
    private boolean strip;

    /**
     * Initializes the validator with the annotation parameters.
     * @param annotation the ValidCardNumber annotation instance
     */
    @Override
    public void initialize(ValidCardNumber annotation) {
        this.strip = annotation.stripSeparators();
    }

    /**
     * Validates the card number using Luhn algorithm.
     * @param value the card number string to validate
     * @param context the constraint validator context
     * @return true if the card number is valid or is null/blank
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (isBlank(value)) return true;

        if (!strip && (value.contains(" ") || value.contains("-"))) {
            MessageUtils.buildViolation(context, Group.FINANCE, "card-number.strip");
            return false;
        }

        String v = strip ? value.replaceAll("[\\s-]", "").replace(" ", "") : value;
        if (!v.matches("^\\d{12,19}$")) {
            MessageUtils.buildViolation(context, Group.FINANCE, "card-number");
            return false;
        }

        int sum = 0;
        boolean dbl = false;

        for (int i = v.length() - 1; i >= 0; i--) {
            int d = v.charAt(i) - '0';
            if (dbl) {
                d *= 2;
                if (d > 9) d -= 9;
            }

            sum += d;
            dbl = !dbl;
        }

        if (sum % 10 != 0) {
            MessageUtils.buildViolation(context, Group.FINANCE, "card-number");
            return false;
        }

        return true;
    }
}
