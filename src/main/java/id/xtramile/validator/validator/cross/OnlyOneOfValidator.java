package id.xtramile.validator.validator.cross;

import id.xtramile.validator.annotation.cross.OnlyOneOf;
import id.xtramile.validator.enums.Group;
import id.xtramile.validator.util.MessageUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;

import static id.xtramile.validator.util.ValidatorUtils.isPresent;

/**
 * Validator implementation for {@link OnlyOneOf} annotation.
 * <p>
 * Validates that exactly one of the specified fields is present (not null/empty).
 * This validator ensures mutual exclusivity between fields, allowing only one
 * field from the list to have a value.
 * 
 * <p>The validator performs the following operations:
 * <ul>
 * <li>Accepts null beans as valid</li>
 * <li>Counts how many fields are present</li>
 * <li>Returns true if exactly one field is present</li>
 * <li>Adds violation to appropriate field based on count</li>
 * </ul>
 * 
 * @see OnlyOneOf
 */
public class OnlyOneOfValidator implements ConstraintValidator<OnlyOneOf, Object> {
    private String[] fields;

    /**
     * Initializes the validator with the annotation parameters.
     * @param annotation the OnlyOneOf annotation instance
     */
    @Override
    public void initialize(OnlyOneOf annotation) {
        this.fields = annotation.fields();
    }

    /**
     * Validates that exactly one of the specified fields is present.
     * @param bean the object to validate
     * @param context the constraint validator context
     * @return true if exactly one field is present or bean is null
     */
    @Override
    public boolean isValid(Object bean, ConstraintValidatorContext context) {
        if (bean == null) return true;

        BeanWrapperImpl wrapper = new BeanWrapperImpl(bean);
        int count = 0;
        String last = null;

        for (String field : fields) {
            if (isPresent(wrapper.getPropertyValue(field))) {
                count++;
                last = field;
            }

            if (count > 1) {
                break;
            }
        }

        boolean ok = count == 1;
        if (!ok && fields.length > 0 && context != null) {
            String target = (count == 0) ? fields[0] : last;

            MessageUtils.buildViolation(context, Group.CROSS, "only-one", target);
        }

        return ok;
    }
}
