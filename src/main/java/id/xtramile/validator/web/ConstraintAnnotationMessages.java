package id.xtramile.validator.web;

import jakarta.validation.constraints.*;

import java.util.Map;

import static id.xtramile.validator.enums.Group.*;

final class ConstraintAnnotationMessages {

    private static final String VALIDATION_DEFAULT = "validation.default";

    private final MessageResourceResolver messageResolver;
    private final ValidationFieldDisplayNames fieldNames;

    public ConstraintAnnotationMessages(MessageResourceResolver messageResolver, ValidationFieldDisplayNames fieldNames) {
        this.messageResolver = messageResolver;
        this.fieldNames = fieldNames;
    }

    public String resolveFromAnnotation(String field, Class<?> annotationType, Map<String, Object> attrs, Class<?> dtoClass) {
        if (annotationType == null) {
            return messageResolver.getMessage(VALIDATION_DEFAULT, field);
        }

        String springMessage = springValidatorMessage(field, annotationType, attrs);
        if (springMessage != null) {
            return springMessage;
        }

        String customMessage = customValidatorMessage(field, annotationType, attrs, dtoClass);
        if (customMessage != null) {
            return customMessage;
        }

        return messageResolver.getMessage(VALIDATION_DEFAULT, field);
    }

    private String springValidatorMessage(String field, Class<?> type, Map<String, Object> attrs) {
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
            Map<String, Object> a = attrs != null ? attrs : Map.of();
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
            Long value = (Long) attrs.getOrDefault("value", 0L);
            return messageResolver.getMessage(SPRING, "min", field, value);
        }

        if (type == Max.class) {
            Long value = (Long) attrs.getOrDefault("value", Long.MAX_VALUE);
            return messageResolver.getMessage(SPRING, "max", field, value);
        }

        if (type == DecimalMin.class) {
            String value = (String) attrs.getOrDefault("value", "0");

            Boolean inclusive = (Boolean) attrs.getOrDefault("inclusive", Boolean.TRUE);
            String key = inclusive ? "decimal-min" : "decimal-min.exclusive";

            return messageResolver.getMessage(SPRING, key, field, value);
        }

        if (type == DecimalMax.class) {
            String value = (String) attrs.getOrDefault("value", "0");

            Boolean inclusive = (Boolean) attrs.getOrDefault("inclusive", Boolean.TRUE);
            String key = inclusive ? "decimal-max" : "decimal-max.exclusive";

            return messageResolver.getMessage(SPRING, key, field, value);
        }

        if (type == Digits.class) {
            Integer integer = (Integer) attrs.getOrDefault("integer", 0);
            Integer fraction = (Integer) attrs.getOrDefault("fraction", 0);

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

    private String customValidatorMessage(String field, Class<?> type, Map<String, Object> attrs, Class<?> dtoClass) {
        String common = commonValidatorMessage(field, type, attrs);
        if (common != null) {
            return common;
        }

        String contact = contactValidatorMessage(field, type, attrs);
        if (contact != null) {
            return contact;
        }

        String cross = crossValidatorMessage(field, type, attrs, dtoClass);
        if (cross != null) {
            return cross;
        }

        String data = dataValidatorMessage(field, type);
        if (data != null) {
            return data;
        }

        String dateTime = dateTimeValidatorMessage(field, type, attrs, dtoClass);
        if (dateTime != null) {
            return dateTime;
        }

        String file = fileValidatorMessage(field, type, attrs);
        if (file != null) {
            return file;
        }

        String finance = financeValidatorMessage(field, type);
        if (finance != null) {
            return finance;
        }

        String kyc = kycValidatorMessage(field, type);
        if (kyc != null) {
            return kyc;
        }

        String location = locationValidatorMessage(field, type, attrs);
        if (location != null) {
            return location;
        }

        return networkValidatorMessage(field, type);
    }

    private String commonValidatorMessage(String field, Class<?> type, Map<String, Object> attrs) {
        return null;
    }

    private String contactValidatorMessage(String field, Class<?> type, Map<String, Object> attrs) {
        return null;
    }

    private String crossValidatorMessage(String field, Class<?> type, Map<String, Object> attrs, Class<?> dtoClass) {
        return null;
    }

    private String dataValidatorMessage(String field, Class<?> type) {
        return null;
    }

    private String dateTimeValidatorMessage(String field, Class<?> type, Map<String, Object> attrs, Class<?> dtoClass) {
        return null;
    }

    private String fileValidatorMessage(String field, Class<?> type, Map<String, Object> attrs) {
        return null;
    }

    private String financeValidatorMessage(String field, Class<?> type) {
        return null;
    }

    private String kycValidatorMessage(String field, Class<?> type) {
        return null;
    }

    private String locationValidatorMessage(String field, Class<?> type, Map<String, Object> attrs) {
        return null;
    }

    private String networkValidatorMessage(String field, Class<?> type) {
        return null;
    }

    private static int intConstraintAttribute(Map<String, Object> attrs, String key, int defaultValue) {
        Object v = attrs.get(key);
        if (v instanceof Number) {
            return ((Number) v).intValue();
        }

        return defaultValue;
    }
}
