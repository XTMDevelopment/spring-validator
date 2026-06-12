package id.xtramile.validator.validator.contact;

import id.xtramile.validator.annotation.contact.ValidContactNumber;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

import static id.xtramile.validator.util.ValidatorUtils.isBlank;

/**
 * Validator implementation for {@link ValidContactNumber} annotation.
 * <p>
 * Validates a general E.164-style contact number with optional '+' prefix.
 * This validator ensures the contact number follows the E.164 standard format
 * with proper length and digit constraints.
 *
 * <p>The validator performs the following checks:
 * <ul>
 * <li>Accepts null/blank values as valid</li>
 * <li>Validates E.164 format: optional '+', then 7-15 digits</li>
 * <li>Ensures first digit is 1-9 (no leading zero)</li>
 * <li>Supports international format examples</li>
 * </ul>
 *
 * <p>Examples: +6281234567890, 6281234567890, 12025550123
 *
 * @see ValidContactNumber
 */
public class ContactNumberValidator implements ConstraintValidator<ValidContactNumber, String> {
    private static final Pattern REGEX = Pattern.compile("^(?:\\+?[1-9]\\d{6,14})$");

    /**
     * Validates the contact number against E.164 format.
     *
     * @param value   the contact number string to validate
     * @param context the constraint validator context
     * @return true if the contact number is valid or is null/blank
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (isBlank(value)) return true;
        return REGEX.matcher(value).matches();
    }
}
