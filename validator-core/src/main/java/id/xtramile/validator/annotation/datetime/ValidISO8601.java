package id.xtramile.validator.annotation.datetime;

import id.xtramile.validator.validator.datetime.ISO8601Validator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates that a String is a valid ISO 8601 date-time.
 * <p>
 * Parseable by java.time.OffsetDateTime. Null/blank values are considered valid.
 * This validator ensures the string follows ISO 8601 standard format.
 *
 * <p>Example usage:
 * <pre>{@code
 * @ValidISO8601
 * private String isoTimestamp; // e.g., 2025-08-11T16:24:00Z
 *
 * @ValidISO8601
 * private String eventTime; // e.g., 2025-08-11T16:24:00+07:00
 * }</pre>
 *
 * @see ISO8601Validator
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ISO8601Validator.class)
public @interface ValidISO8601 {
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
}
