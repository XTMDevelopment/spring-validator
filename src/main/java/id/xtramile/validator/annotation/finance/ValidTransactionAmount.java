package id.xtramile.validator.annotation.finance;

import id.xtramile.validator.validator.finance.TransactionAmountValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates a monetary transaction amount as a Long with configurable min/max bounds.
 * <p>
 * Null values are considered valid. This validator ensures monetary amounts
 * meet specified constraints for financial transactions, typically used for large
 * amounts (e.g., 500,000 to 5,000,000,000).
 * 
 * <p>Example usage:
 * <pre>{@code
 * @ValidTransactionAmount(min = 500_000L, max = 5_000_000_000L)
 * private Long amount; // e.g., 1_000_000L
 * 
 * @ValidTransactionAmount(min = 1_000_000L, allowZero = false)
 * private Long transferAmount; // must be positive and non-zero
 * }</pre>
 * 
 * @see TransactionAmountValidator
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TransactionAmountValidator.class)
public @interface ValidTransactionAmount {
    String message() default "{friendly.default}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    /**
     * Inclusive minimum amount (default 0L).
     */
    long min() default 0L;

    /**
     * Inclusive maximum amount (default Long.MAX_VALUE for no maximum limit).
     */
    long max() default Long.MAX_VALUE;

    /**
     * If true, value may be zero. If false, zero values are rejected even if
     * they meet the minimum requirement.
     */
    boolean allowZero() default true;
}
