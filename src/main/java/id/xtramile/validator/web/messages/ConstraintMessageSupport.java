package id.xtramile.validator.web.messages;

import java.util.Map;

final class ConstraintMessageSupport {

    private ConstraintMessageSupport() {
    }

    static Map<String, Object> nullSafeAttrs(Map<String, Object> attrs) {
        return attrs != null ? attrs : Map.of();
    }

    static int intConstraintAttribute(Map<String, Object> attrs, String key, int defaultValue) {
        Object v = attrs.get(key);
        if (v instanceof Number) {
            return ((Number) v).intValue();
        }

        return defaultValue;
    }
}
