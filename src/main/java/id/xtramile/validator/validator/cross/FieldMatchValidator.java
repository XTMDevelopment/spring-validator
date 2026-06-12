package id.xtramile.validator.validator.cross;

import id.xtramile.validator.annotation.cross.FieldMatch;
import id.xtramile.validator.enums.Group;
import id.xtramile.validator.util.MessageUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;

/**
 * Validator implementation for {@link FieldMatch} annotation.
 * <p>
 * Validates that two specified fields have equal values.
 * This validator ensures field equality with proper null handling,
 * commonly used for password confirmation scenarios.
 *
 * <p>The validator performs the following operations:
 * <ul>
 * <li>Accepts null beans as valid</li>
 * <li>Compares the two specified fields for equality</li>
 * <li>Handles null values (both null is considered equal)</li>
 * <li>Adds violation to the second field if they don't match</li>
 * </ul>
 *
 * @see FieldMatch
 */
public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object> {
    private String first;
    private String second;

    /**
     * Initializes the validator with the annotation parameters.
     *
     * @param annotation the FieldMatch annotation instance
     */
    @Override
    public void initialize(FieldMatch annotation) {
        this.first = annotation.first();
        this.second = annotation.second();
    }

    /**
     * Validates that the two fields have equal values.
     *
     * @param bean    the object to validate
     * @param context the constraint validator context
     * @return true if the fields match or bean is null
     */
    @Override
    public boolean isValid(Object bean, ConstraintValidatorContext context) {
        if (bean == null) return true;

        BeanWrapperImpl wrapper = new BeanWrapperImpl(bean);
        Object firstValue = wrapper.getPropertyValue(first);
        Object secondValue = wrapper.getPropertyValue(second);

        boolean matches = (firstValue == null && secondValue == null)
                || (firstValue != null && firstValue.equals(secondValue));

        if (!matches && context != null) {
            MessageUtils.buildViolation(context, Group.CROSS, "field-match", first, second);
        }

        return matches;
    }
}
