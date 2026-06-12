package id.xtramile.validator.annotation.datetime;

import id.xtramile.validator.validator.datetime.InvalidPastFutureDateValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates that a date string is within an acceptable past range and not in the future.
 * <p>
 * A value is considered valid if it represents the current time or falls within the last N hours
 * (as defined by toleranceHours). Null or blank values are considered valid.
 * <p>
 * For ISO 8601 format (yyyy-MM-dd'T'HH:mm:ssX), input data is expected to be in UTC format and UTC time.
 * The validation compares the UTC input with the current time in UTC+7, applying tolerance in hours.
 *
 * <p>Example usage:
 * <pre>{@code
 * @InvalidPastFutureDate(pattern = "yyyy-MM-dd'T'HH:mm:ssX", toleranceHours = 2)
 * private String date; // valid if within 2 hours before UTC+7 current time, not in the future
 *
 * @InvalidPastFutureDate(pattern = "yyyy-MM-dd HH:mm:ss", toleranceHours = 1)
 * private String dateTime; // validates using current time and tolerance (no conversion)
 *
 * @InvalidPastFutureDate(pattern = "yyyy-MM-dd", toleranceHours = 0)
 * private String strictDate; // valid only if today, not past or future
 * }</pre>
 *
 * @see InvalidPastFutureDateValidator
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = InvalidPastFutureDateValidator.class)
public @interface InvalidPastFutureDate {
    /**
     * Default violation message template.
     *
     * @return the message template
     */
    String message() default "{friendly.default}";

    /**
     * The number of past hours allowed before the date is considered invalid.
     *
     * @return the allowed past-hour tolerance
     */
    int toleranceHours() default 0;

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
     * Default is ISO 8601 format with timezone: yyyy-MM-dd'T'HH:mm:ssX
     *
     * @return the date pattern
     */
    String pattern() default "yyyy-MM-dd'T'HH:mm:ssX";
}
