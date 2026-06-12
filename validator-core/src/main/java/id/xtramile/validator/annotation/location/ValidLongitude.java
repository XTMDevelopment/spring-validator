package id.xtramile.validator.annotation.location;

import id.xtramile.validator.validator.location.LongitudeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates a longitude value (double or string) between -180 and 180 degrees.
 * <p>
 * Null values are considered valid. This validator ensures longitude values
 * are within the valid range for geographic coordinates.
 *
 * <p>Example usage:
 * <pre>{@code
 * @ValidLongitude
 * private Double longitude; // e.g., 106.816666
 *
 * @ValidLongitude
 * private String lngString; // e.g., "106.816666"
 * }</pre>
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = LongitudeValidator.class)
public @interface ValidLongitude {
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
