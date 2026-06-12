package id.xtramile.validator.validator.location;

import id.xtramile.validator.annotation.location.ValidLongitude;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import static id.xtramile.validator.util.ValidatorUtils.isBlank;

/**
 * Validator implementation for {@link ValidLongitude} annotation.
 * <p>
 * Validates longitude values within the valid range of -180 to 180 degrees.
 * This validator ensures the longitude is within the standard geographic
 * coordinate system bounds.
 *
 * <p>The validator performs the following operations:
 * <ul>
 * <li>Accepts null/blank values as valid</li>
 * <li>Parses the string as a double value</li>
 * <li>Validates longitude range (-180.0 to 180.0)</li>
 * <li>Handles parsing exceptions gracefully</li>
 * </ul>
 *
 * @see ValidLongitude
 */
public class LongitudeValidator implements ConstraintValidator<ValidLongitude, String> {

    /**
     * Validates the longitude value against the valid range.
     *
     * @param value   the longitude string to validate
     * @param context the constraint validator context
     * @return true if the longitude is valid or is null/blank
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (isBlank(value)) return true;

        try {
            double longitude = Double.parseDouble(value);
            return longitude >= -180.0 && longitude <= 180.0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
