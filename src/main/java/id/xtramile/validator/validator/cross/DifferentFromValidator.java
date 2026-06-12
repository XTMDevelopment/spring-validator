package id.xtramile.validator.validator.cross;

import id.xtramile.validator.annotation.cross.DifferentFrom;
import id.xtramile.validator.enums.Group;
import id.xtramile.validator.util.MessageUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;

/**
 * Validator implementation for {@link DifferentFrom} annotation.
 * <p>
 * Validates that the value of one field is different from the value of another field.
 * This validator ensures two fields have different values, supporting null handling
 * and proper equality comparison.
 * 
 * <p>The validator performs the following operations:
 * <ul>
 * <li>Accepts null beans as valid</li>
 * <li>Compares the two specified fields for difference</li>
 * <li>Handles null values appropriately</li>
 * <li>Adds violation to the first field if they are equal</li>
 * </ul>
 * 
 * @see DifferentFrom
 */
public class DifferentFromValidator implements ConstraintValidator<DifferentFrom, Object> {
    private String field;
    private String other;

    /**
     * Initializes the validator with the annotation parameters.
     * @param annotation the DifferentFrom annotation instance
     */
    @Override
    public void initialize(DifferentFrom annotation) {
        this.field = annotation.field();
        this.other = annotation.other();
    }

    /**
     * Validates that the two fields have different values.
     * @param bean the object to validate
     * @param context the constraint validator context
     * @return true if the fields are different or bean is null
     */
    @Override
    public boolean isValid(Object bean, ConstraintValidatorContext context) {
        if (bean == null) return true;

        boolean areDifferent = isDifferent(bean);

        if (!areDifferent && context != null) {
            MessageUtils.buildViolation(context, Group.CROSS, "different-from", field, other);
        }

        return areDifferent;
    }

    /**
     * Checks if the two fields have different values.
     * @param bean the object to check
     * @return true if the fields are different
     */
    private boolean isDifferent(Object bean) {
        BeanWrapperImpl wrapper = new BeanWrapperImpl(bean);
        Object fieldBean = wrapper.getPropertyValue(field);
        Object otherBean = wrapper.getPropertyValue(other);

        boolean areDifferent = fieldBean == null && otherBean != null ||
                fieldBean != null && otherBean == null ||
                fieldBean != null && !fieldBean.equals(otherBean);

        if (fieldBean == null && otherBean == null) {
            areDifferent = true;
        }

        return areDifferent;
    }
}
