package id.xtramile.validator.validator.cross;

import id.xtramile.validator.annotation.cross.AtLeastOneOf;
import id.xtramile.validator.enums.Group;
import id.xtramile.validator.util.MessageUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;

import static id.xtramile.validator.util.ValidatorUtils.isPresent;

/**
 * Validator implementation for {@link AtLeastOneOf} annotation.
 * <p>
 * Validates that at least one of the specified fields is present (not null/empty).
 * This validator ensures that at least one field from the list has a value,
 * providing flexible validation for optional field groups.
 * 
 * <p>The validator performs the following operations:
 * <ul>
 * <li>Accepts null beans as valid</li>
 * <li>Checks each specified field for presence</li>
 * <li>Returns true if any field is present</li>
 * <li>Adds violation to the first field if none are present</li>
 * </ul>
 * 
 * @see AtLeastOneOf
 */
public class AtLeastOneOfValidator implements ConstraintValidator<AtLeastOneOf, Object> {
    private String[] fields;

    /**
     * Initializes the validator with the annotation parameters.
     * @param annotation the AtLeastOneOf annotation instance
     */
    @Override
    public void initialize(AtLeastOneOf annotation) {
        this.fields = annotation.fields();
    }

    /**
     * Validates that at least one of the specified fields is present.
     * @param bean the object to validate
     * @param context the constraint validator context
     * @return true if at least one field is present or bean is null
     */
    @Override
    public boolean isValid(Object bean, ConstraintValidatorContext context) {
        if (bean == null) return true;

        BeanWrapperImpl wrapper = new BeanWrapperImpl(bean);

        for (String field : fields) {
            if (isPresent(wrapper.getPropertyValue(field))) {
                return true;
            }
        }

        MessageUtils.buildViolation(context, Group.CROSS, "at-least-one");
        return false;
    }
}
