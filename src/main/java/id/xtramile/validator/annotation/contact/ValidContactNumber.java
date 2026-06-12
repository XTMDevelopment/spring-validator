package id.xtramile.validator.annotation.contact;

import id.xtramile.validator.validator.contact.ContactNumberValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates a general E.164-style phone number: optional '+' followed by 7-15 digits, first digit 1-9 (no leading zero).
 * <p>
 * This validator supports international phone number formats following E.164 standard.
 * Null/blank values are considered valid.
 *
 * <p>Example usage:
 * <pre>{@code
 * @ValidContactNumber
 * private String contact; // e.g., +6281234567890 or 12025550123
 *
 * @ValidContactNumber
 * private String phoneNumber; // e.g., +1234567890
 * }</pre>
 *
 * @see ContactNumberValidator
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ContactNumberValidator.class)
public @interface ValidContactNumber {
    String message() default "{friendly.default}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
