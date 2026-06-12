package id.xtramile.validator.validator.location;

import id.xtramile.validator.annotation.location.ValidCoordinates;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import static id.xtramile.validator.util.ValidatorUtils.isBlank;

/**
 * Validator implementation for {@link ValidCoordinates} annotation.
 * <p>
 * Validates geographic coordinate strings in "lat,lon" or "lon,lat" format.
 * This validator ensures the coordinates are within valid ranges with optional
 * coordinate order flipping for different input formats.
 *
 * <p>The validator performs the following operations:
 * <ul>
 * <li>Accepts null/blank values as valid</li>
 * <li>Validates comma-separated coordinate format</li>
 * <li>Supports coordinate order flipping (lat,lon vs lon,lat)</li>
 * <li>Validates latitude range (-90 to 90)</li>
 * <li>Validates longitude range (-180 to 180)</li>
 * </ul>
 *
 * @see ValidCoordinates
 */
public class CoordinateValidator implements ConstraintValidator<ValidCoordinates, String> {
    private boolean flipCoordinates;

    /**
     * Initializes the validator with the annotation parameters.
     *
     * @param annotation the ValidCoordinates annotation instance
     */
    @Override
    public void initialize(ValidCoordinates annotation) {
        this.flipCoordinates = annotation.flipCoordinates();
    }

    /**
     * Validates the coordinate string format and ranges.
     *
     * @param value   the coordinate string to validate
     * @param context the constraint validator context
     * @return true if the coordinates are valid or is null/blank
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (isBlank(value)) return true;

        String[] parts = value.split(",");
        if (parts.length != 2) return false;

        return flipCoordinates
                ? checkCoordinate(parts[1].trim(), parts[0].trim())
                : checkCoordinate(parts[0].trim(), parts[1].trim());
    }

    /**
     * Checks if the coordinate values are within valid ranges.
     *
     * @param first  the first coordinate value
     * @param second the second coordinate value
     * @return true if both coordinates are within valid ranges
     */
    private boolean checkCoordinate(String first, String second) {
        try {
            double lat = Double.parseDouble(first);
            double lon = Double.parseDouble(second);

            return (lat >= -90 && lat <= 90) && (lon >= -180 && lon <= 180);
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
