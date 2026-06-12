package id.xtramile.validator.web.messages;

import id.xtramile.validator.annotation.cross.*;
import id.xtramile.validator.web.MessageResourceResolver;
import id.xtramile.validator.web.ValidationFieldDisplayNames;

import java.util.Map;

import static id.xtramile.validator.enums.Group.CROSS;

final class CrossConstraintMessages extends AbstractGroupConstraintMessages {

    private final ValidationFieldDisplayNames fieldNames;

    CrossConstraintMessages(MessageResourceResolver messageResolver, ValidationFieldDisplayNames fieldNames) {
        super(messageResolver, CROSS);
        this.fieldNames = fieldNames;
    }

    @Override
    @SuppressWarnings("DuplicatedCode")
    public String resolve(String field, Class<?> type, Map<String, Object> attrs, Class<?> dtoClass) {
        if (type == FieldMatch.class) {
            String first = (String) attrs.get("first");
            String second = (String) attrs.get("second");

            String firstDisplay = fieldNames.resolve(dtoClass, first);
            String secondDisplay = fieldNames.resolve(dtoClass, second);

            return messageResolver.getMessage(CROSS, "field-match", firstDisplay, secondDisplay);
        }

        if (type == AtLeastOneOf.class) {
            String[] fields = (String[]) attrs.get("fields");
            String[] displayNames = new String[fields.length];

            for (int i = 0; i < fields.length; i++) {
                displayNames[i] = fieldNames.resolve(dtoClass, fields[i]);
            }

            String fieldsList = String.join(", ", displayNames);
            return messageResolver.getMessage(CROSS, "at-least-one", fieldsList);
        }

        if (type == DifferentFrom.class) {
            String first = (String) attrs.get("field");
            String second = (String) attrs.get("other");

            String firstDisplay = fieldNames.resolve(dtoClass, first);
            String secondDisplay = fieldNames.resolve(dtoClass, second);

            return messageResolver.getMessage(CROSS, "different-from", firstDisplay, secondDisplay);
        }

        if (type == OnlyOneOf.class) {
            String[] fields = (String[]) attrs.get("fields");
            String[] displayNames = new String[fields.length];

            for (int i = 0; i < fields.length; i++) {
                displayNames[i] = fieldNames.resolve(dtoClass, fields[i]);
            }

            String fieldsList = String.join(", ", displayNames);
            return messageResolver.getMessage(CROSS, "only-one", fieldsList);
        }

        if (type == RequiredWith.class) {
            String[] requiredFields = (String[]) attrs.get("require");
            String[] displayNames = new String[requiredFields.length];

            for (int i = 0; i < requiredFields.length; i++) {
                displayNames[i] = fieldNames.resolve(dtoClass, requiredFields[i]);
            }

            String fieldsList = String.join(", ", displayNames);
            return messageResolver.getMessage(CROSS, "required-with", field, fieldsList);
        }

        return null;
    }
}
