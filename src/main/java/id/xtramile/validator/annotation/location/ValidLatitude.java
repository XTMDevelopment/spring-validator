package id.xtramile.validator.annotation.location;

import id.xtramile.validator.validator.location.LatitudeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates a latitude value (double or string) between -90 and 90 degrees.
 * <p>
 * Null values are considered valid. This validator ensures latitude values
 * are within the valid range for geographic coordinates.
 *
 * <p>Example usage:
 * <pre>{@code
 * @ValidLatitude
 * private Double latitude; // e.g., -6.200000
 *
 * @ValidLatitude
 * private String latString; // e.g., "-6.200000"
 * }</pre>
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = LatitudeValidator.class)
public @interface ValidLatitude {
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
