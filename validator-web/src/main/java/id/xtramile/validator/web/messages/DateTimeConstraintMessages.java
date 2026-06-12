package id.xtramile.validator.web.messages;

import id.xtramile.validator.annotation.datetime.*;
import id.xtramile.validator.util.DateUtils;
import id.xtramile.validator.web.MessageResourceResolver;
import id.xtramile.validator.web.ValidationFieldDisplayNames;

import java.util.Map;

import static id.xtramile.validator.enums.Group.DATETIME;

final class DateTimeConstraintMessages extends AbstractGroupConstraintMessages {

    private final ValidationFieldDisplayNames fieldNames;

    DateTimeConstraintMessages(MessageResourceResolver messageResolver, ValidationFieldDisplayNames fieldNames) {
        super(messageResolver, DATETIME);
        this.fieldNames = fieldNames;
    }

    @Override
    @SuppressWarnings("DuplicatedCode")
    public String resolve(String field, Class<?> type, Map<String, Object> attrs, Class<?> dtoClass) {
        if (type == ValidDateTime.class) {
            String format = (String) attrs.get("pattern");
            return messageResolver.getMessage(DATETIME, "datetime", field, format);
        }

        if (type == ValidDate.class) {
            String format = (String) attrs.get("pattern");
            return messageResolver.getMessage(DATETIME, "date", field, format);
        }

        if (type == ValidTime.class) {
            String format = (String) attrs.get("pattern");
            return messageResolver.getMessage(DATETIME, "time", field, format);
        }

        if (type == ValidISO8601.class) {
            return messageResolver.getMessage(DATETIME, "iso8601", field);
        }

        if (type == ValidPastDate.class) {
            return messageResolver.getMessage(DATETIME, "past-date", field);
        }

        if (type == ValidFutureDate.class) {
            return messageResolver.getMessage(DATETIME, "future-date", field);
        }

        if (type == InvalidPastDate.class) {
            Integer tolerance = (Integer) attrs.getOrDefault("tolerance", 0);
            if (tolerance == 0) {
                return messageResolver.getMessage(DATETIME, "invalid-past-date", field);
            }

            return messageResolver.getMessage(DATETIME, "invalid-past-date.tolerance", field, tolerance);
        }

        if (type == InvalidFutureDate.class) {
            return messageResolver.getMessage(DATETIME, "invalid-future-date", field);
        }

        if (type == InvalidPastFutureDate.class) {
            Integer toleranceHours = (Integer) attrs.getOrDefault("toleranceHours", 0);

            if (toleranceHours == 0) {
                return messageResolver.getMessage(DATETIME, "invalid-past-future-date", field);
            }

            return messageResolver.getMessage(DATETIME, "invalid-past-future-date.tolerance", field, toleranceHours);
        }

        if (type == DateBefore.class) {
            String first = (String) attrs.get("first");
            String second = (String) attrs.get("second");

            String firstDisplay = fieldNames.resolve(dtoClass, first);
            String secondDisplay = fieldNames.resolve(dtoClass, second);

            Long maxDistance = (Long) attrs.getOrDefault("maxDistance", -1L);

            if (maxDistance >= 0) {
                String precisionStr = DateUtils.pluralLabel((Enum<?>) attrs.get("precision"));
                return messageResolver.getMessage(DATETIME, "date-before.distance", firstDisplay, secondDisplay, maxDistance, precisionStr);
            }

            return messageResolver.getMessage(DATETIME, "date-before", firstDisplay, secondDisplay);
        }

        if (type == DateAfter.class) {
            String first = (String) attrs.get("first");
            String second = (String) attrs.get("second");

            String firstDisplay = fieldNames.resolve(dtoClass, first);
            String secondDisplay = fieldNames.resolve(dtoClass, second);

            Long maxDistance = (Long) attrs.getOrDefault("maxDistance", -1L);

            if (maxDistance >= 0) {
                String precisionStr = DateUtils.pluralLabel((Enum<?>) attrs.get("precision"));
                return messageResolver.getMessage(DATETIME, "date-after.distance", firstDisplay, secondDisplay, maxDistance, precisionStr);
            }

            return messageResolver.getMessage(DATETIME, "date-after", firstDisplay, secondDisplay);
        }

        return null;
    }
}
