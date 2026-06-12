package id.xtramile.validator.annotation.common;

import id.xtramile.validator.validator.common.NotInBlacklistValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates that the String value is not one of the provided blacklist entries.
 * <p>
 * By default, comparison is case-insensitive. Null/blank values are considered valid.
 * This is useful for validating against predefined lists of forbidden values.
 *
 * <p>Example usage:
 * <pre>{@code
 * @NotInBlacklist(values = {"BAD", "INVALID", "UNKNOWN"}, ignoreCase = true)
 * private String code;
 *
 * @NotInBlacklist(values = {"admin", "root", "system"})
 * private String username;
 * }</pre>
 *
 * @see NotInBlacklistValidator
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NotInBlacklistValidator.class)
public @interface NotInBlacklist {
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
     * The list of forbidden values to validate against.
     *
     * @return the array of forbidden values to validate against
     */
    String[] values();

    /**
     * Whether the comparison should be case-insensitive.
     *
     * @return Whether the comparison should be case-insensitive
     */
    boolean ignoreCase() default true;
}
