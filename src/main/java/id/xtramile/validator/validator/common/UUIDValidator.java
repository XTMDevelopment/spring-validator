package id.xtramile.validator.validator.common;

import id.xtramile.validator.annotation.common.ValidUUID;
import id.xtramile.validator.enums.Group;
import id.xtramile.validator.util.MessageUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Validator implementation for {@link ValidUUID} annotation.
 * <p>
 * Validates that a string value is a valid UUID (java.util.UUID format).
 * This validator ensures the string follows the standard UUID format (8-4-4-4-12 hexadecimal digits)
 * and can be parsed as a valid UUID.
 * 
 * <p>The validator performs the following checks:
 * <ul>
 * <li>Validates the UUID format using regex pattern</li>
 * <li>Attempts to parse the string as a UUID using UUID.fromString()</li>
 * <li>Handles parsing exceptions gracefully</li>
 * </ul>
 * 
 * <p>Null/blank values are considered valid.
 * 
 * @see ValidUUID
 */
public class UUIDValidator implements ConstraintValidator<ValidUUID, String> {
    private static final Pattern REGEX = Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");

    /**
     * Validates that the string is a valid UUID.
     * @param value the string value to validate
     * @param context the constraint validator context
     * @return true if the string is a valid UUID or is null/blank
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) return true;

        if (!REGEX.matcher(value).matches()) {
            MessageUtils.buildViolation(context, Group.COMMON, "uuid");
            return false;
        }

        try {
            UUID.fromString(value);
            return true;

        } catch (Exception e) {
            MessageUtils.buildViolation(context, Group.COMMON, "uuid");
            return false;
        }
    }
}
