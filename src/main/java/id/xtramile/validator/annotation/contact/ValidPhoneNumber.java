package id.xtramile.validator.annotation.contact;

import id.xtramile.validator.validator.contact.PhoneNumberValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates Indonesian MSISDN format: must start with 62 followed by 8-13 digits (total length 10-15).
 * <p>
 * This validator is specifically designed for Indonesian phone numbers in MSISDN format.
 * Null/blank values are considered valid.
 *
 * <p>Example usage:
 * <pre>{@code
 * @ValidPhoneNumber
 * private String msisdn; // e.g., 6281234567890
 *
 * @ValidPhoneNumber
 * private String phoneNumber; // e.g., 62812345678901
 * }</pre>
 *
 * @see PhoneNumberValidator
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PhoneNumberValidator.class)
public @interface ValidPhoneNumber {
    String message() default "{friendly.default}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
