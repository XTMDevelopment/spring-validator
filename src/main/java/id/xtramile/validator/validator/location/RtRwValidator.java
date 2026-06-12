package id.xtramile.validator.validator.location;

import id.xtramile.validator.annotation.location.ValidRTRW;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

import static id.xtramile.validator.util.ValidatorUtils.isBlank;

/**
 * Validator implementation for {@link ValidRTRW} annotation.
 * <p>
 * Validates Indonesian RT/RW (Rukun Tetangga/Rukun Warga) address format.
 * This validator ensures the RT/RW code follows the Indonesian address
 * standard format with 3-digit numeric codes.
 *
 * <p>The validator performs the following operations:
 * <ul>
 * <li>Accepts null/blank values as valid</li>
 * <li>Trims whitespace before validation</li>
 * <li>Validates 3-digit numeric format</li>
 * <li>Uses regex pattern matching for validation</li>
 * </ul>
 *
 * @see ValidRTRW
 */
public class RtRwValidator implements ConstraintValidator<ValidRTRW, String> {
    private static final Pattern REGEX = Pattern.compile("^[0-9]{3}$");

    /**
     * Validates the RT/RW code format.
     *
     * @param value   the RT/RW string to validate
     * @param context the constraint validator context
     * @return true if the RT/RW code is valid or is null/blank
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (isBlank(value)) return true;

        String trimmed = value.trim();
        return REGEX.matcher(trimmed).matches();
    }
}
