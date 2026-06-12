package id.xtramile.validator.annotation.datetime;

import id.xtramile.validator.validator.datetime.FutureDateValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates that a date string (parsed with the given pattern) is strictly after today.
 * <p>
 * Null/blank values are considered valid. This validator ensures the date
 * is in the future relative to the current date.
 *
 * <p>Example usage:
 * <pre>{@code
 * @ValidFutureDate
 * private String expiresOn; // must be a future date
 *
 * @ValidFutureDate(pattern = "dd/MM/yyyy")
 * private String eventDate; // must be in the future
 * }</pre>
 *
 * @see FutureDateValidator
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FutureDateValidator.class)
public @interface ValidFutureDate {
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
