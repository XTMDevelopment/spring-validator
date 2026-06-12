package id.xtramile.validator.annotation.finance;

import id.xtramile.validator.validator.finance.CvvValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates a CVV (Card Verification Value) string.
 * <p>
 * Null/blank values are considered valid. This validator ensures the CVV
 * is a 3 or 4 digit number as required by card issuers.
 *
 * <p>Example usage:
 * <pre>{@code
 * @ValidCVV
 * private String cvv; // e.g., 123 (3 digits)
 *
 * @ValidCVV(allowFourDigits = false)
 * private String amexCvv; // e.g., 1234 (4 digits for Amex)
 * }</pre>
 *
 * @see CvvValidator
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CvvValidator.class)
public @interface ValidCVV {
    String message() default "{friendly.default}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Whether to allow 4-digit CVV (for American Express cards).
     */
    boolean allowFourDigits() default true;
}
