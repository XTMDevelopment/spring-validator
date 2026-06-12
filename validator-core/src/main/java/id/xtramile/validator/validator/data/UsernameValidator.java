package id.xtramile.validator.validator.data;

import id.xtramile.validator.annotation.data.ValidUsername;
import id.xtramile.validator.enums.Group;
import id.xtramile.validator.util.MessageUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

import static id.xtramile.validator.util.ValidatorUtils.isBlank;

/**
 * Validator implementation for {@link ValidUsername} annotation.
 * <p>
 * Validates a username composed only of letters, digits, dot (.), and underscore (_).
 * This validator ensures the username follows common naming conventions with
 * proper length constraints.
 *
 * <p>The validator performs the following checks:
 * <ul>
 * <li>Accepts null/blank values as valid</li>
 * <li>Validates length is between min and max</li>
 * <li>Allows only letters, digits, dots, and underscores</li>
 * <li>Trims whitespace before validation</li>
 * </ul>
 *
 * @see ValidUsername
 */
public class UsernameValidator implements ConstraintValidator<ValidUsername, String> {
    private int min;
    private int max;

    /**
     * Initializes the validator with the annotation parameters.
     *
     * @param annotation the ValidUsername annotation instance
     */
    @Override
    public void initialize(ValidUsername annotation) {
        this.min = annotation.min();
        this.max = annotation.max();
    }

    /**
     * Validates the username string against the configured constraints.
     *
     * @param value   the username string to validate
     * @param context the constraint validator context
     * @return true if the username is valid or is null/blank
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (isBlank(value)) return true;

        String trimmed = value.trim();
        int len = trimmed.length();

        if (len < min) {
            MessageUtils.buildViolation(context, Group.DATA, "username.min", min);
            return false;
        }

        if (len > max) {
            MessageUtils.buildViolation(context, Group.DATA, "username.max", max);
            return false;
        }

        Pattern pattern = Pattern.compile("^[a-zA-Z0-9._]+$");
        if (!pattern.matcher(trimmed).matches()) {
            MessageUtils.buildViolation(context, Group.DATA, "username");
            return false;
        }

        return true;
    }
}
