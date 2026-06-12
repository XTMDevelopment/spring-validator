package id.xtramile.validator.validator.data;

import id.xtramile.validator.annotation.data.ValidHexColor;
import id.xtramile.validator.enums.Group;
import id.xtramile.validator.util.MessageUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

import static id.xtramile.validator.util.ValidatorUtils.isBlank;

/**
 * Validator implementation for {@link ValidHexColor} annotation.
 * <p>
 * Validates a hex color code in either long form (#RRGGBB) or short form (#RGB).
 * This validator ensures the string follows the correct hex color format
 * with optional support for short form colors.
 *
 * <p>The validator performs the following checks:
 * <ul>
 * <li>Accepts null/blank values as valid</li>
 * <li>Validates long form hex colors (#RRGGBB)</li>
 * <li>Optionally validates short form hex colors (#RGB)</li>
 * <li>Case-insensitive validation</li>
 * </ul>
 *
 * @see ValidHexColor
 */
public class HexColorValidator implements ConstraintValidator<ValidHexColor, String> {
    private static final Pattern LONG = Pattern.compile("^#([0-9a-fA-F]{6})$");
    private static final Pattern SHORT = Pattern.compile("^#([0-9a-fA-F]{3})$");
    private boolean allowShort;

    /**
     * Initializes the validator with the annotation parameters.
     *
     * @param annotation the ValidHexColor annotation instance
     */
    @Override
    public void initialize(ValidHexColor annotation) {
        this.allowShort = annotation.shortFormAllowed();
    }

    /**
     * Validates the hex color string against the configured format.
     *
     * @param value   the hex color string to validate
     * @param context the constraint validator context
     * @return true if the string is a valid hex color or is null/blank
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (isBlank(value)) return true;

        boolean matchesLong = LONG.matcher(value).matches();
        boolean matchesShort = allowShort && SHORT.matcher(value).matches();

        if (matchesLong || matchesShort) {
            return true;
        }

        if (allowShort && value.startsWith("#") && value.length() == 4) {
            MessageUtils.buildViolation(context, Group.DATA, "hex-color.short");
            return false;
        }

        if (value.startsWith("#") && value.length() == 7) {
            MessageUtils.buildViolation(context, Group.DATA, "hex-color.long");
            return false;
        }

        MessageUtils.buildViolation(context, Group.DATA, "hex-color");
        return false;
    }
}
