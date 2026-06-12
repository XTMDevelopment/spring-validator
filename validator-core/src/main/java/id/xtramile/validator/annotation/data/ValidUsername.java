package id.xtramile.validator.annotation.data;

import id.xtramile.validator.validator.data.UsernameValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates a username composed only of letters, digits, dot (.), and underscore (_).
 * <p>
 * Length must be between min and max. Null/blank values are considered valid.
 * This validator ensures usernames follow common naming conventions.
 *
 * <p>Example usage:
 * <pre>{@code
 * @ValidUsername(min = 4, max = 20)
 * private String username; // e.g., john.doe_123
 *
 * @ValidUsername(min = 3, max = 15)
 * private String displayName; // e.g., user_123
 * }</pre>
 *
 * @see UsernameValidator
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UsernameValidator.class)
public @interface ValidUsername {
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
     * Minimum username length.
     *
     * @return the minimum username length
     */
    int min() default 4;

    /**
     * Maximum username length.
     *
     * @return the maximum username length
     */
    int max() default 20;
}
