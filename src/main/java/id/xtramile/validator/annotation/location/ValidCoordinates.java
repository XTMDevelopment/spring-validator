package id.xtramile.validator.annotation.location;

import id.xtramile.validator.validator.location.CoordinateValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates a coordinate string in "lat,long" or "long,lat" format.
 * <p>
 * Null/blank values are considered valid. This validator ensures coordinates
 * are in the correct format and within valid latitude/longitude ranges.
 *
 * <p>Example usage:
 * <pre>{@code
 * @ValidCoordinates
 * private String coordinates; // e.g., -6.200000,106.816666
 *
 * @ValidCoordinates(flipCoordinates = true)
 * private String lngLatFormat; // e.g., 106.816666,-6.200000
 * }</pre>
 *
 * @see CoordinateValidator
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CoordinateValidator.class)
public @interface ValidCoordinates {
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
     * Default: lat,long. Flip to be long,lat.
     *
     * @return the default: lat,long. Flip to be long,lat
     */
    boolean flipCoordinates() default false;
}
