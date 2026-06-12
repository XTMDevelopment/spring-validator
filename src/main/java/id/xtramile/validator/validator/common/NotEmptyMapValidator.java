package id.xtramile.validator.validator.common;

import id.xtramile.validator.annotation.common.NotEmptyCollection;
import id.xtramile.validator.enums.Group;
import id.xtramile.validator.util.MessageUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Map;

/**
 * Validator implementation for {@link NotEmptyCollection} annotation on Maps.
 * <p>
 * Validates that a Map is not null and not empty.
 * This validator ensures that the map contains at least one key-value pair.
 *
 * <p>The validator performs the following checks:
 * <ul>
 * <li>Rejects null maps</li>
 * <li>Rejects empty maps</li>
 * </ul>
 *
 * @see NotEmptyCollection
 */
public class NotEmptyMapValidator implements ConstraintValidator<NotEmptyCollection, Map<?, ?>> {
    /**
     * Validates that the map is not null and not empty.
     *
     * @param value   the map to validate
     * @param context the constraint validator context
     * @return true if the map is not null and not empty
     */
    @Override
    public boolean isValid(Map<?, ?> value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            MessageUtils.buildViolation(context, Group.COMMON, "not-empty-collection");
            return false;
        }

        return true;
    }
}
