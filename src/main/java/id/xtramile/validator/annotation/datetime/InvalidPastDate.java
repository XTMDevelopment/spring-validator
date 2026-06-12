package id.xtramile.validator.annotation.datetime;

import id.xtramile.validator.validator.datetime.InvalidPastDateValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates that a date string is not in the past, except when allowed by the specified tolerance.
 * <p>
 * A value is considered valid if it represents today or falls within the last N days
 * (as defined by tolerance). Null or blank values are considered valid.
 *
 * <p>Example usage:
 * <pre>{@code
 * @InvalidPastDate(pattern = "yyyy-MM-dd", tolerance = 1)
 * private String date; // valid if today or within 1 day before today
 *
 * @InvalidPastDate(tolerance = 0)
 * private String strictDate; // valid only if today or future
 * }</pre>
 *
 * @see InvalidPastDateValidator
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = InvalidPastDateValidator.class)
public @interface InvalidPastDate {
    /**
     * Default violation message template.
     *
     * @return the message template
     */
    String message() default "{friendly.default}";

    /**
     * The number of past days allowed before the date is considered invalid.
     *
     * @return the allowed past-day tolerance
     */
    int tolerance() default 0;

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
     * The date pattern to use for parsing.
     *
     * @return the date pattern
     */
    String pattern() default "yyyy-MM-dd";
}
