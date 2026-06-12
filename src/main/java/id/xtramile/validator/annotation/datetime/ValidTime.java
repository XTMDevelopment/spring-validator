package id.xtramile.validator.annotation.datetime;

import id.xtramile.validator.validator.datetime.TimeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates that a String can be parsed into a LocalTime using the provided pattern.
 * <p>
 * Null/blank values are considered valid. This validator ensures the string
 * represents a valid time according to the specified format.
 *
 * <p>Example usage:
 * <pre>{@code
 * @ValidTime // default pattern HH:mm:ss
 * private String meetingTime; // e.g., 14:05:30
 *
 * @ValidTime(pattern = "HH:mm")
 * private String shortTime; // e.g., 09:45
 * }</pre>
 *
 * @see TimeValidator
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TimeValidator.class)
public @interface ValidTime {
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
     * The time pattern to use for parsing.
     *
     * @return the time pattern to use for parsing
     */
    String pattern() default "HH:mm:ss";
}
