package id.xtramile.validator.validator.finance;

import id.xtramile.validator.annotation.finance.ValidTransactionAmount;
import id.xtramile.validator.enums.Group;
import id.xtramile.validator.util.MessageUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator implementation for {@link ValidTransactionAmount} annotation.
 * <p>
 * Validates monetary transaction amounts as Long values with configurable min/max bounds.
 * This validator ensures the amount meets financial transaction requirements
 * for large monetary values.
 * 
 * <p>The validator performs the following operations:
 * <ul>
 * <li>Accepts null values as valid</li>
 * <li>Checks minimum and maximum bounds (inclusive)</li>
 * <li>Validates zero amount handling based on allowZero flag</li>
 * <li>Rejects negative amounts</li>
 * </ul>
 * 
 * @see ValidTransactionAmount
 */
public class TransactionAmountValidator implements ConstraintValidator<ValidTransactionAmount, Long> {
    private Long min;
    private Long max;
    private boolean allowZero;

    /**
     * Initializes the validator with the annotation parameters.
     * @param annotation the ValidTransactionAmount annotation instance
     */
    @Override
    public void initialize(ValidTransactionAmount annotation) {
        this.min = annotation.min();
        this.max = annotation.max();
        this.allowZero = annotation.allowZero();
    }

    /**
     * Validates the transaction amount against the configured constraints.
     * @param value the Long amount to validate
     * @param context the constraint validator context
     * @return true if the amount is valid or is null
     */
    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        if (value == null) return true;

        if (value.compareTo(min) < 0) {
            MessageUtils.buildViolation(context, Group.FINANCE, "transaction-amount.min", String.valueOf(min));
            return false;
        }

        if (max != Long.MAX_VALUE && value.compareTo(max) > 0) {
            MessageUtils.buildViolation(context, Group.FINANCE, "transaction-amount.max", String.valueOf(max));
            return false;
        }

        if (!allowZero && value.compareTo(0L) == 0) {
            MessageUtils.buildViolation(context, Group.FINANCE, "transaction-amount.zero");
            return false;
        }

        return true;
    }
}
