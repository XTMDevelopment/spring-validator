package id.xtramile.validator.validator.cross;

import id.xtramile.validator.annotation.cross.RequiredWith;
import id.xtramile.validator.enums.Group;
import id.xtramile.validator.util.MessageUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;

import static id.xtramile.validator.util.ValidatorUtils.isPresent;

/**
 * Validator implementation for {@link RequiredWith} annotation.
 * <p>
 * Validates conditional field requirements based on the presence of a trigger field.
 * This validator ensures that when the "when" field is present, all "require" fields
 * must also be present, implementing conditional validation logic.
 * 
 * <p>The validator performs the following operations:
 * <ul>
 * <li>Accepts null beans as valid</li>
 * <li>Checks if the trigger field is present and truthy</li>
 * <li>Validates all required fields are present when triggered</li>
 * <li>Adds violations to missing required fields</li>
 * </ul>
 * 
 * @see RequiredWith
 */
public class RequiredWithValidator implements ConstraintValidator<RequiredWith, Object> {
    private String when;
    private String[] require;

    /**
     * Initializes the validator with the annotation parameters.
     * @param annotation the RequiredWith annotation instance
     */
    @Override
    public void initialize(RequiredWith annotation) {
        this.when = annotation.when();
        this.require = annotation.require();
    }

    /**
     * Validates conditional field requirements.
     * @param bean the object to validate
     * @param context the constraint validator context
     * @return true if requirements are met or bean is null
     */
    @Override
    public boolean isValid(Object bean, ConstraintValidatorContext context) {
        if (bean == null) return true;

        BeanWrapperImpl wrapper = new BeanWrapperImpl(bean);

        Object trigger = wrapper.getPropertyValue(when);
        if (trigger == null || (trigger instanceof Boolean && !(Boolean) trigger)) {
            return true;
        }

        if (trigger instanceof Number && ((Number) trigger).doubleValue() == 0.0) {
            return true;
        }

        if (!isPresent(trigger)) return true;

        boolean ok = true;
        for (String r : require) {
            if (!isPresent(wrapper.getPropertyValue(r))) {
                ok = false;

                MessageUtils.buildViolation(context, Group.CROSS, "required-with", r);
            }
        }

        return ok;
    }
}
