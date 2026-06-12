package id.xtramile.validator.annotation.contact;

import id.xtramile.validator.validator.contact.OtpValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates an OTP string with configurable length and character set.
 * <p>
 * By default, digits only and length 6. Set alphabets=true to allow alphanumeric (a-z, A-Z, 0-9).
 * Null/blank values are considered valid.
 *
 * <p>Example usage:
 * <pre>{@code
 * // 6-digit numeric OTP
 * @ValidOtp
 * private String otpNumeric;
 *
 * // 8-character alphanumeric OTP
 * @ValidOtp(length = 8, alphabets = true)
 * private String otpAlphaNum;
 * }</pre>
 *
 * @see OtpValidator
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = OtpValidator.class)
public @interface ValidOtp {
    /**
     * Default violation message template.
     *
     * @return the message template
     */
    String message() default "{friendly.default}";

    /**
     * Validation groups for conditional validation.
     *
     * @return the validation groups
     */
    Class<?>[] groups() default {};

    /**
     * Payload types for extensibility metadata.
     *
     * @return the payload types
     */
    Class<? extends Payload>[] payload() default {};

    /**
     * The exact length the OTP must have.
     *
     * @return the exact length the OTP must have
     */
    int length() default 6;

    /**
     * Whether to allow alphabetic characters in addition to digits.
     *
     * @return the whether to allow alphabetic characters in addition to digits
     */
    boolean alphabets() default false;
}
