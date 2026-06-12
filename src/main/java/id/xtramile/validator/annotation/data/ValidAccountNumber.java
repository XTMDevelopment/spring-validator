package id.xtramile.validator.annotation.data;

import id.xtramile.validator.validator.data.AccountNumberValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates a bank/account number consisting only of digits, with length between min and max.
 * <p>
 * Null/blank values are considered valid. This validator ensures the account number
 * contains only numeric digits and meets the specified length requirements.
 *
 * <p>Example usage:
 * <pre>{@code
 * @ValidAccountNumber(min = 8, max = 20)
 * private String accountNumber;
 *
 * @ValidAccountNumber(min = 10, max = 16)
 * private String bankAccount; // e.g., 1234567890
 * }</pre>
 *
 * @see AccountNumberValidator
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AccountNumberValidator.class)
public @interface ValidAccountNumber {
    String message() default "{friendly.default}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Minimum account number length.
     */
    int min() default 8;

    /**
     * Maximum account number length.
     */
    int max() default 20;
}
