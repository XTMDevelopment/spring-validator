package id.xtramile.validator.annotation.datetime;

import id.xtramile.validator.validator.datetime.InvalidFutureDateValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates that a date string is not in the future, except when allowed by the specified tolerance.
 * <p>
 * A value is considered valid if it represents the current time or falls within N hours after the current time
 * (as defined by toleranceHours). Null or blank values are considered valid.
 * <p>
 * For ISO 8601 format (yyyy-MM-dd'T'HH:mm:ssX), input data is expected to be in UTC format and UTC time.
 * The validation compares the UTC input with the current time in UTC+7, applying tolerance in hours.
 *
 * <p>Example usage:
 * <pre>{@code
 * @InvalidFutureDate(pattern = "yyyy-MM-dd'T'HH:mm:ssX", toleranceHours = 2)
 * private String date; // valid if UTC+7 until UTC+9 (tolerance 2 hours)
 *
 * @InvalidFutureDate(pattern = "yyyy-MM-dd HH:mm:ss")
 * private String dateTime; // validates using current time and tolerance (no conversion)
 *
 * @InvalidFutureDate(pattern = "yyyy-MM-dd")
 * private String birthDate; // validates only the date
 * }</pre>
 *
 * @see InvalidFutureDateValidator
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = InvalidFutureDateValidator.class)
public @interface InvalidFutureDate {
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
     * The number of hours allowed after the current time before the date is considered invalid.
     *
     * @return the allowed future-hour tolerance
     */
    int toleranceHours() default 0;

    /**
     * The date pattern to use for parsing.
     * Default is ISO 8601 format with timezone: yyyy-MM-dd'T'HH:mm:ssX
     *
     * @return the date pattern
     */
    String pattern() default "yyyy-MM-dd'T'HH:mm:ssX";
}
