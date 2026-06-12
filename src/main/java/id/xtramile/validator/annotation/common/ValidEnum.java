package id.xtramile.validator.annotation.common;

import id.xtramile.validator.validator.common.EnumValueValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates that the String value matches one of the names of the provided enum class.
 * <p>
 * By default, comparison is case-insensitive. Null/blank values are considered valid.
 * This is useful for validating string inputs against enum constants.
 *
 * <p>Example usage:
 * <pre>{@code
 * public enum Role { ADMIN, USER, GUEST }
 *
 * @ValidEnum(enumClass = Role.class, ignoreCase = true)
 * private String role;
 *
 * @ValidEnum(enumClass = Status.class)
 * private String status;
 * }</pre>
 *
 * @see EnumValueValidator
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EnumValueValidator.class)
public @interface ValidEnum {
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
     * The enum class to validate against.
     *
     * @return the enum class to validate against
     */
    Class<? extends Enum<?>> enumClass();

    /**
     * Whether the comparison should be case-insensitive.
     *
     * @return the whether the comparison should be case-insensitive
     */
    boolean ignoreCase() default true;
}
