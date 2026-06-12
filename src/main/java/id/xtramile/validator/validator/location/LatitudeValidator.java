package id.xtramile.validator.validator.location;

import id.xtramile.validator.annotation.location.ValidLatitude;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import static id.xtramile.validator.util.ValidatorUtils.isBlank;

/**
 * Validator implementation for {@link ValidLatitude} annotation.
 * <p>
 * Validates latitude values within the valid range of -90 to 90 degrees.
 * This validator ensures the latitude is within the standard geographic
 * coordinate system bounds.
 *
 * <p>The validator performs the following operations:
 * <ul>
 * <li>Accepts null/blank values as valid</li>
 * <li>Parses the string as a double value</li>
 * <li>Validates latitude range (-90.0 to 90.0)</li>
 * <li>Handles parsing exceptions gracefully</li>
 * </ul>
 *
 * @see ValidLatitude
 */
public class LatitudeValidator implements ConstraintValidator<ValidLatitude, String> {

    /**
     * Validates the latitude value against the valid range.
     *
     * @param value   the latitude string to validate
     * @param context the constraint validator context
     * @return true if the latitude is valid or is null/blank
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (isBlank(value)) return true;

        try {
            double latitude = Double.parseDouble(value);
            return latitude >= -90.0 && latitude <= 90.0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
