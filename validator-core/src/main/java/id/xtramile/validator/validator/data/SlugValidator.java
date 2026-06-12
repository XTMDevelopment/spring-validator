package id.xtramile.validator.validator.data;

import id.xtramile.validator.annotation.data.ValidSlug;
import id.xtramile.validator.enums.Group;
import id.xtramile.validator.util.MessageUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

import static id.xtramile.validator.util.ValidatorUtils.isBlank;

/**
 * Validator implementation for {@link ValidSlug} annotation.
 * <p>
 * Validates a URL-friendly slug: lowercase letters and digits separated by single hyphens.
 * This validator ensures the slug follows the pattern ^[a-z0-9]+(?:-[a-z0-9]+)*$
 * with total length between min and max.
 *
 * <p>The validator performs the following checks:
 * <ul>
 * <li>Accepts null/blank values as valid</li>
 * <li>Validates the slug pattern (lowercase letters, digits, hyphens)</li>
 * <li>Checks that the length is within the specified range</li>
 * <li>Ensures proper hyphen usage (no leading/trailing hyphens)</li>
 * </ul>
 *
 * @see ValidSlug
 */
public class SlugValidator implements ConstraintValidator<ValidSlug, String> {
    private static final Pattern REGEX = Pattern.compile("^[a-z0-9]+(?:-[a-z0-9]+)*$");
    private int min;
    private int max;

    /**
     * Initializes the validator with the annotation parameters.
     *
     * @param annotation the ValidSlug annotation instance
     */
    @Override
    public void initialize(ValidSlug annotation) {
        this.min = annotation.min();
        this.max = annotation.max();
    }

    /**
     * Validates the slug string against the configured constraints.
     *
     * @param value   the slug string to validate
     * @param context the constraint validator context
     * @return true if the slug is valid or is null/blank
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (isBlank(value)) return true;

        String trimmed = value.trim();
        int len = trimmed.length();

        if (len < min) {
            MessageUtils.buildViolation(context, Group.DATA, "slug.min", min);
            return false;
        }

        if (len > max) {
            MessageUtils.buildViolation(context, Group.DATA, "slug.max", max);
            return false;
        }

        if (!REGEX.matcher(trimmed).matches()) {
            MessageUtils.buildViolation(context, Group.DATA, "slug");
            return false;
        }

        return true;
    }
}
