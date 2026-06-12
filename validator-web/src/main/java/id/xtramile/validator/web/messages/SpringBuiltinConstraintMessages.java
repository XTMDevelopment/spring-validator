package id.xtramile.validator.web.messages;

import id.xtramile.validator.web.MessageResourceResolver;
import jakarta.validation.constraints.*;

import java.util.Map;

import static id.xtramile.validator.enums.Group.SPRING;
import static id.xtramile.validator.web.messages.ConstraintMessageSupport.intConstraintAttribute;
import static id.xtramile.validator.web.messages.ConstraintMessageSupport.nullSafeAttrs;

final class SpringBuiltinConstraintMessages extends AbstractGroupConstraintMessages {

    SpringBuiltinConstraintMessages(MessageResourceResolver messageResolver) {
        super(messageResolver, SPRING);
    }

    @Override
    public String resolve(String field, Class<?> type, Map<String, Object> attrs, Class<?> dtoClass) {
        Map<String, Object> a = nullSafeAttrs(attrs);

        if (type == NotBlank.class) {
            return messageResolver.getMessage(SPRING, "not-blank", field);
        }

        if (type == NotNull.class) {
            return messageResolver.getMessage(SPRING, "not-null", field);
        }

        if (type == Null.class) {
            return messageResolver.getMessage(SPRING, "null", field);
        }

        if (type == NotEmpty.class) {
            return messageResolver.getMessage(SPRING, "not-empty", field);
        }

        if (type == Email.class) {
            return messageResolver.getMessage(SPRING, "email", field);
        }

        if (type == Pattern.class) {
            return messageResolver.getMessage(SPRING, "pattern", field);
        }

        if (type == Size.class) {
            int min = intConstraintAttribute(a, "min", 0);
            int max = intConstraintAttribute(a, "max", Integer.MAX_VALUE);

            if (min > 0 && max < Integer.MAX_VALUE) {
                return messageResolver.getMessage(SPRING, "size", field, min, max);

            } else if (min > 0) {
                return messageResolver.getMessage(SPRING, "size.min", field, min);

            } else {
                return messageResolver.getMessage(SPRING, "size.max", field, max);
            }
        }

        if (type == Min.class) {
            Long value = (Long) a.getOrDefault("value", 0L);
            return messageResolver.getMessage(SPRING, "min", field, value);
        }

        if (type == Max.class) {
            Long value = (Long) a.getOrDefault("value", Long.MAX_VALUE);
            return messageResolver.getMessage(SPRING, "max", field, value);
        }

        if (type == DecimalMin.class) {
            String value = (String) a.getOrDefault("value", "0");

            Boolean inclusive = (Boolean) a.getOrDefault("inclusive", Boolean.TRUE);
            String key = inclusive ? "decimal-min" : "decimal-min.exclusive";

            return messageResolver.getMessage(SPRING, key, field, value);
        }

        if (type == DecimalMax.class) {
            String value = (String) a.getOrDefault("value", "0");

            Boolean inclusive = (Boolean) a.getOrDefault("inclusive", Boolean.TRUE);
            String key = inclusive ? "decimal-max" : "decimal-max.exclusive";

            return messageResolver.getMessage(SPRING, key, field, value);
        }

        if (type == Digits.class) {
            Integer integer = (Integer) a.getOrDefault("integer", 0);
            Integer fraction = (Integer) a.getOrDefault("fraction", 0);

            return messageResolver.getMessage(SPRING, "digits", field, integer, fraction);
        }

        if (type == Positive.class) {
            return messageResolver.getMessage(SPRING, "positive", field);
        }

        if (type == PositiveOrZero.class) {
            return messageResolver.getMessage(SPRING, "positive-zero", field);
        }

        if (type == Negative.class) {
            return messageResolver.getMessage(SPRING, "negative", field);
        }

        if (type == NegativeOrZero.class) {
            return messageResolver.getMessage(SPRING, "negative-zero", field);
        }

        if (type == AssertTrue.class) {
            return messageResolver.getMessage(SPRING, "true", field);
        }

        if (type == AssertFalse.class) {
            return messageResolver.getMessage(SPRING, "false", field);
        }

        if (type == Past.class) {
            return messageResolver.getMessage(SPRING, "past", field);
        }

        if (type == PastOrPresent.class) {
            return messageResolver.getMessage(SPRING, "past-present", field);
        }

        if (type == Future.class) {
            return messageResolver.getMessage(SPRING, "future", field);
        }

        if (type == FutureOrPresent.class) {
            return messageResolver.getMessage(SPRING, "future-present", field);
        }

        return null;
    }
}
