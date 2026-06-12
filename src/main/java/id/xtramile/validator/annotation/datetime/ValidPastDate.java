package id.xtramile.validator.annotation.datetime;

import id.xtramile.validator.validator.datetime.PastDateValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates that a date string (parsed with the given pattern) is strictly before today.
 * <p>
 * Null/blank values are considered valid. This validator ensures the date
 * is in the past relative to the current date.
 *
 * <p>Example usage:
 * <pre>{@code
 * @ValidPastDate
 * private String startedAt; // must be a past date
 *
 * @ValidPastDate(pattern = "dd/MM/yyyy")
 * private String birthDate; // must be in the past
 * }</pre>
 *
 * @see PastDateValidator
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PastDateValidator.class)
public @interface ValidPastDate {
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
     * The date pattern to use for parsing.
     *
     * @return the date pattern to use for parsing
     */
    String pattern() default "yyyy-MM-dd";
}
