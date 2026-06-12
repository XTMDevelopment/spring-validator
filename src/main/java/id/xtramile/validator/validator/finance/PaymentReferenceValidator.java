package id.xtramile.validator.validator.finance;

import id.xtramile.validator.annotation.finance.ValidPaymentReference;
import id.xtramile.validator.enums.Group;
import id.xtramile.validator.util.MessageUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

import static id.xtramile.validator.util.ValidatorUtils.isBlank;

/**
 * Validator implementation for {@link ValidPaymentReference} annotation.
 * <p>
 * Validates payment reference strings using a configurable regex pattern.
 * This validator ensures the payment reference follows the specified format
 * for payment processing and tracking.
 * 
 * <p>The validator performs the following operations:
 * <ul>
 * <li>Accepts null/blank values as valid</li>
 * <li>Uses configurable regex pattern for validation</li>
 * <li>Compiles pattern during initialization</li>
 * <li>Applies pattern matching for validation</li>
 * </ul>
 * 
 * @see ValidPaymentReference
 */
public class PaymentReferenceValidator implements ConstraintValidator<ValidPaymentReference, String> {
    private Pattern pattern;

    /**
     * Initializes the validator with the annotation parameters.
     * @param annotation the ValidPaymentReference annotation instance
     */
    @Override
    public void initialize(ValidPaymentReference annotation) {
        this.pattern = Pattern.compile(annotation.pattern());
    }

    /**
     * Validates the payment reference against the configured pattern.
     * @param value the payment reference string to validate
     * @param context the constraint validator context
     * @return true if the payment reference is valid or is null/blank
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (isBlank(value)) return true;

        if (!pattern.matcher(value).matches()) {
            MessageUtils.buildViolation(context, Group.FINANCE, "payment-reference.pattern", pattern.pattern());
            return false;
        }

        return true;
    }
}
