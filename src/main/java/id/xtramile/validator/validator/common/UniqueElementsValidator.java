package id.xtramile.validator.validator.common;

import id.xtramile.validator.annotation.common.UniqueElements;
import id.xtramile.validator.enums.Group;
import id.xtramile.validator.util.MessageUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Collection;
import java.util.HashSet;

/**
 * Validator implementation for {@link UniqueElements} annotation.
 * <p>
 * Validates that a Collection contains only unique elements (no duplicates).
 * This validator ensures that all elements in the collection are unique by
 * comparing the original collection size with the size of a HashSet created from it.
 * 
 * <p>The validator performs the following operations:
 * <ul>
 * <li>Accepts null collections as valid</li>
 * <li>Creates a HashSet from the collection to remove duplicates</li>
 * <li>Compares the original size with the HashSet size</li>
 * </ul>
 * 
 * @see UniqueElements
 */
public class UniqueElementsValidator implements ConstraintValidator<UniqueElements, Collection<?>> {
    /**
     * Validates that the collection contains only unique elements.
     * @param value the collection to validate
     * @param context the constraint validator context
     * @return true if all elements are unique or the collection is null
     */
    @Override
    public boolean isValid(Collection<?> value, ConstraintValidatorContext context) {
        if (value == null) return true;
        if (value.size() == new HashSet<>(value).size()) {
            return true;
        }

        MessageUtils.buildViolation(context, Group.COMMON, "unique-elements");
        return false;
    }
}
