package id.xtramile.validator.web.messages;

import id.xtramile.validator.annotation.common.*;
import id.xtramile.validator.web.MessageResourceResolver;
import id.xtramile.validator.web.ValidationMessageArgsBuilder;

import java.util.Map;

import static id.xtramile.validator.enums.Group.COMMON;

final class CommonConstraintMessages extends AbstractGroupConstraintMessages {

    CommonConstraintMessages(MessageResourceResolver messageResolver) {
        super(messageResolver, COMMON);
    }

    @Override
    public String resolve(String field, Class<?> type, Map<String, Object> attrs, Class<?> dtoClass) {
        if (type == InWhitelist.class) {
            String values = ValidationMessageArgsBuilder.joinSortedComma((String[]) attrs.get("values"));

            boolean ignoreCase = (Boolean) attrs.getOrDefault("ignoreCase", true);
            String key = ignoreCase ? "in-whitelist" : "in-whitelist.sensitive";

            return messageResolver.getMessage(COMMON, key, field, values);
        }

        if (type == NotInBlacklist.class) {
            String values = ValidationMessageArgsBuilder.joinSortedComma((String[]) attrs.get("values"));

            boolean ignoreCase = (Boolean) attrs.getOrDefault("ignoreCase", true);
            String key = ignoreCase ? "not-in-blacklist" : "not-in-blacklist.sensitive";

            return messageResolver.getMessage(COMMON, key, field, values);
        }

        if (type == ValidEnum.class) {
            @SuppressWarnings("unchecked")
            Class<? extends Enum<?>> enumClass = (Class<? extends Enum<?>>) attrs.get("enumClass");
            String label = enumClass != null ? enumClass.getSimpleName() : "";

            boolean ignoreCase = (Boolean) attrs.getOrDefault("ignoreCase", true);
            String key = ignoreCase ? "enum" : "enum.sensitive";

            return messageResolver.getMessage(COMMON, key, field, label);
        }

        if (type == UniqueElements.class) {
            return messageResolver.getMessage(COMMON, "unique-elements", field);
        }

        if (type == NotEmptyCollection.class) {
            return messageResolver.getMessage(COMMON, "not-empty-collection", field);
        }

        if (type == ValidUUID.class) {
            return messageResolver.getMessage(COMMON, "uuid", field);
        }

        return null;
    }
}
