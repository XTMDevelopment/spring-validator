package id.xtramile.validator.validator.data;

import id.xtramile.validator.annotation.data.ValidAccountNumber;
import id.xtramile.validator.enums.Group;
import id.xtramile.validator.util.MessageUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import static id.xtramile.validator.util.ValidatorUtils.isBlank;

/**
 * Validator implementation for {@link ValidAccountNumber} annotation.
 * <p>
 * Validates a bank/account number consisting only of digits, with length between min and max.
 * This validator ensures the account number contains only numeric digits and meets
 * the specified length requirements.
 * 
 * <p>The validator performs the following checks:
 * <ul>
 * <li>Accepts null/blank values as valid</li>
 * <li>Validates that the string contains only digits</li>
 * <li>Checks that the length is within the specified range</li>
 * </ul>
 * 
 * @see ValidAccountNumber
 */
public class AccountNumberValidator implements ConstraintValidator<ValidAccountNumber, String> {
    private int min;
    private int max;

    /**
     * Initializes the validator with the annotation parameters.
     * @param annotation the ValidAccountNumber annotation instance
     */
    @Override
    public void initialize(ValidAccountNumber annotation) {
        this.min = annotation.min();
        this.max = annotation.max();
    }

    /**
     * Validates the account number against the configured constraints.
     * @param value the account number string to validate
     * @param context the constraint validator context
     * @return true if the account number is valid or is null/blank
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (isBlank(value)) return true;

        if (!value.matches("^\\d+$")) {
            MessageUtils.buildViolation(context, Group.DATA, "account-number.numbers");
            return false;
        }

        int len = value.length();
        if (len < min) {
            MessageUtils.buildViolation(context, Group.DATA, "account-number.min", min);
            return false;
        }
        
        if (len > max) {
            MessageUtils.buildViolation(context, Group.DATA, "account-number.max", max);
            return false;
        }

        return true;
    }
}
