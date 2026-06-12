package id.xtramile.validator.validator.datetime;

import id.xtramile.validator.annotation.datetime.DateBefore;
import id.xtramile.validator.enums.DatePrecision;
import id.xtramile.validator.enums.Group;
import id.xtramile.validator.util.DateUtils;
import id.xtramile.validator.util.MessageUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;

import java.time.LocalDateTime;

import static id.xtramile.validator.util.ValidatorUtils.isBlank;

/**
 * Validator implementation for {@link DateBefore} annotation.
 * <p>
 * Validates that the first date field is before the second date field.
 * Optionally enforces a maximum distance between the two dates based on precision.
 * Both fields are parsed as date-time strings using the specified pattern.
 *
 * <p>The validator performs the following operations:
 * <ul>
 * <li>Accepts null beans as valid</li>
 * <li>Accepts null/blank field values as valid</li>
 * <li>Parses both date fields using the specified pattern</li>
 * <li>Validates that the first date is before the second date</li>
 * <li>Optionally validates maximum distance between dates if configured</li>
 * <li>Adds violation to the first field if validation fails</li>
 * </ul>
 *
 * @see DateBefore
 */
public class DateBeforeValidator implements ConstraintValidator<DateBefore, Object> {
    private String first;
    private String second;
    private String pattern;
    private long maxDistance;
    private DatePrecision precision;

    /**
     * Initializes the validator with the annotation parameters.
     *
     * @param annotation the DateBefore annotation instance
     */
    @Override
    public void initialize(DateBefore annotation) {
        this.first = annotation.first();
        this.second = annotation.second();
        this.pattern = annotation.pattern();
        this.maxDistance = annotation.maxDistance();
        this.precision = annotation.precision();
    }

    /**
     * Validates that the first date field is before the second date field.
     *
     * @param bean    the object to validate
     * @param context the constraint validator context
     * @return true if the first date is before the second date, or bean/fields are null
     */
    @Override
    @SuppressWarnings("DuplicatedCode")
    public boolean isValid(Object bean, ConstraintValidatorContext context) {
        if (bean == null) return true;

        BeanWrapperImpl wrapper = new BeanWrapperImpl(bean);
        Object firstValue = wrapper.getPropertyValue(first);
        Object secondValue = wrapper.getPropertyValue(second);

        if (firstValue == null || secondValue == null) return true;
        if (!(firstValue instanceof String firstStr) || !(secondValue instanceof String secondStr)) return true;

        if (isBlank(firstStr) || isBlank(secondStr)) return true;

        try {
            LocalDateTime firstDate = DateUtils.parseDateTime(firstStr, pattern);
            LocalDateTime secondDate = DateUtils.parseDateTime(secondStr, pattern);

            if (firstDate == null || secondDate == null) {
                MessageUtils.buildViolation(context, Group.DATETIME, "date-before.pattern", pattern);
                return false;
            }

            if (!firstDate.isBefore(secondDate)) {
                MessageUtils.buildViolation(context, Group.DATETIME, "date-before");
                return false;
            }

            if (maxDistance >= 0) {
                long actualDistance = DateUtils.calculateDistance(firstDate, secondDate, precision);
                if (actualDistance > maxDistance) {
                    MessageUtils.buildViolation(context, Group.DATETIME, "date-before.distance", secondDate, maxDistance, precision);
                    return false;
                }
            }

            return true;

        } catch (Exception e) {
            MessageUtils.buildViolation(context, Group.DATETIME, "date-before.pattern", pattern);
            return false;
        }
    }
}