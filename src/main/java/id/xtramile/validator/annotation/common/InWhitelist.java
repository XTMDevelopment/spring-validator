package id.xtramile.validator.annotation.common;

import id.xtramile.validator.validator.common.InWhitelistValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates that the String value is one of the provided whitelist entries.
 * <p>
 * By default, comparison is case-insensitive. Null/blank values are considered valid.
 * This is useful for validating against predefined lists of allowed values.
 *
 * <p>Example usage:
 * <pre>{@code
 * @InWhitelist(values = {"DRAFT", "PUBLISHED", "ARCHIVED"}, ignoreCase = true)
 * private String status;
 *
 * @InWhitelist(values = {"ACTIVE", "INACTIVE"})
 * private String userStatus;
 * }</pre>
 *
 * @see InWhitelistValidator
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = InWhitelistValidator.class)
public @interface InWhitelist {
    String message() default "{friendly.default}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * The list of allowed values to validate against.
     */
    String[] values();

    /**
     * Whether the comparison should be case-insensitive.
     */
    boolean ignoreCase() default true;
}
