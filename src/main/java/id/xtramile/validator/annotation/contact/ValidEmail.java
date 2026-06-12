package id.xtramile.validator.annotation.contact;

import id.xtramile.validator.validator.contact.EmailValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates that the String value is a well-formed email address.
 * <p>
 * This validator delegates to jakarta.validation.constraints.Email for validation.
 * Null/blank values are considered valid.
 *
 * <p>Example usage:
 * <pre>{@code
 * @ValidEmail
 * private String email; // e.g., user@example.com
 *
 * @ValidEmail
 * private String contactEmail; // e.g., support@mycompany.co
 * }</pre>
 *
 * @see EmailValidator
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EmailValidator.class)
public @interface ValidEmail {
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
}
