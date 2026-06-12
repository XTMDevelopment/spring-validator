package id.xtramile.validator.validator.common;

import id.xtramile.validator.annotation.common.NotEmptyCollection;
import id.xtramile.validator.enums.Group;
import id.xtramile.validator.util.MessageUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Collection;

/**
 * Validator implementation for {@link NotEmptyCollection} annotation on Collections.
 * <p>
 * Validates that a Collection is not null and not empty.
 * This validator ensures that the collection contains at least one element.
 *
 * <p>The validator performs the following checks:
 * <ul>
 * <li>Rejects null collections</li>
 * <li>Rejects empty collections</li>
 * </ul>
 *
 * @see NotEmptyCollection
 */
public class NotEmptyCollectionValidator implements ConstraintValidator<NotEmptyCollection, Collection<?>> {
    /**
     * Validates that the collection is not null and not empty.
     *
     * @param value   the collection to validate
     * @param context the constraint validator context
     * @return true if the collection is not null and not empty
     */
    @Override
    public boolean isValid(Collection<?> value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            MessageUtils.buildViolation(context, Group.COMMON, "not-empty-collection");
            return false;
        }

        return true;
    }
}
