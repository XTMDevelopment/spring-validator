package id.xtramile.validator.annotation.datetime;

import id.xtramile.validator.validator.datetime.DateTimeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates that a String can be parsed into a LocalDateTime using the provided pattern.
 * <p>
 * Null/blank values are considered valid. This validator ensures the string
 * represents a valid date-time according to the specified format.
 *
 * <p>Example usage:
 * <pre>{@code
 * @ValidDateTime // default pattern yyyy-MM-dd HH:mm:ss
 * private String createdAt; // e.g., 2025-08-11 23:24:00
 *
 * @ValidDateTime(pattern = "yyyy/MM/dd HH:mm")
 * private String publishedAt; // e.g., 2025/08/11 09:30
 * }</pre>
 *
 * @see DateTimeValidator
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateTimeValidator.class)
public @interface ValidDateTime {
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
     * The date-time pattern to use for parsing.
     *
     * @return the date-time pattern to use for parsing
     */
    String pattern() default "yyyy-MM-dd HH:mm:ss";
}
