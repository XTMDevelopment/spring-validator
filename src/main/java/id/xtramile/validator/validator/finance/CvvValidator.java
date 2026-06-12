package id.xtramile.validator.validator.finance;

import id.xtramile.validator.annotation.finance.ValidCVV;
import id.xtramile.validator.enums.Group;
import id.xtramile.validator.util.MessageUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import static id.xtramile.validator.util.ValidatorUtils.isBlank;

/**
 * Validator implementation for {@link ValidCVV} annotation.
 * <p>
 * Validates CVV (Card Verification Value) codes for credit/debit cards.
 * This validator ensures the CVV follows the standard format with optional
 * support for 4-digit CVVs (American Express).
 * 
 * <p>The validator performs the following operations:
 * <ul>
 * <li>Accepts null/blank values as valid</li>
 * <li>Validates 3-digit CVV format by default</li>
 * <li>Optionally supports 4-digit CVV format</li>
 * <li>Uses regex pattern matching for validation</li>
 * </ul>
 * 
 * @see ValidCVV
 */
public class CvvValidator implements ConstraintValidator<ValidCVV, String> {
    private boolean allow4;

    /**
     * Initializes the validator with the annotation parameters.
     * @param annotation the ValidCVV annotation instance
     */
    @Override
    public void initialize(ValidCVV annotation) {
        this.allow4 = annotation.allowFourDigits();
    }

    /**
     * Validates the CVV code format.
     * @param value the CVV string to validate
     * @param context the constraint validator context
     * @return true if the CVV is valid or is null/blank
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (isBlank(value)) return true;

        if (!allow4 && value.length() == 4) {
            MessageUtils.buildViolation(context, Group.FINANCE, "cvv.four");
            return false;
        }

        if (allow4 && !value.matches("\\d{3,4}")) {
            MessageUtils.buildViolation(context, Group.FINANCE, "cvv");
            return false;
        }

        if (!allow4 && !value.matches("\\d{3}")) {
            MessageUtils.buildViolation(context, Group.FINANCE, "cvv");
            return false;
        }

        return true;
    }
}
