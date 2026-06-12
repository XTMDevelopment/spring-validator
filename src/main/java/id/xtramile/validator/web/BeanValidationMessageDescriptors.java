package id.xtramile.validator.web;

import org.springframework.util.StringUtils;

import java.util.Map;

public final class BeanValidationMessageDescriptors {

    private static final String FRIENDLY_DEFAULT = "{friendly.default}";

    private BeanValidationMessageDescriptors() {}

    public static boolean isFriendlyDefault(String template) {
        return FRIENDLY_DEFAULT.equals(template);
    }

    public static boolean isDefaultTemplate(String template) {
        if (template == null) {
            return false;
        }

        String t = template.trim();

        if (t.isEmpty()) {
            return false;
        }
        if (FRIENDLY_DEFAULT.equals(t)) {
            return true;
        }

        return isDefaultBeanValidationMessageDescriptor(template);
    }

    public static boolean isDefaultBeanValidationMessageDescriptor(String template) {
        if (template == null) {
            return false;
        }
        String t = template.trim();
        if (t.isEmpty()) {
            return false;
        }

        if (t.startsWith("{jakarta.validation.") && t.endsWith("}")) {
            return true;
        }

        if (t.startsWith("{javax.validation.") && t.endsWith("}")) {
            return true;
        }

        if (t.startsWith("{org.hibernate.validator.") && t.endsWith("}")) {
            return true;
        }

        if (t.startsWith("jakarta.validation.constraints.") && t.endsWith(".message")) {
            return true;
        }

        if (t.startsWith("javax.validation.constraints.") && t.endsWith(".message")) {
            return true;
        }

        return t.startsWith("org.hibernate.validator.") && t.endsWith(".message");
    }

    public static boolean shouldResolveFieldErrorUsingConstraintDefaults(Map<String, Object> attrs) {
        if (attrs == null || attrs.isEmpty()) {
            return false;
        }

        Object messageAttr = attrs.get("message");
        if (!(messageAttr instanceof String)) {
            return true;
        }

        return usesConstraintDefaultOrFriendlyMessageKey((String) messageAttr);
    }

    public static boolean usesConstraintDefaultOrFriendlyMessageKey(String messageTemplate) {
        if (!StringUtils.hasText(messageTemplate)) {
            return true;
        }

        if (FRIENDLY_DEFAULT.equals(messageTemplate)) {
            return true;
        }

        if (isDefaultBeanValidationMessageDescriptor(messageTemplate)) {
            return true;
        }

        String t = messageTemplate.trim();
        if (t.startsWith("{") && t.endsWith("}")) {
            String inner = t.substring(1, t.length() - 1);
            return inner.startsWith("jakarta.validation.")
                    || inner.startsWith("javax.validation.")
                    || inner.startsWith("org.hibernate.validator.");
        }

        return false;
    }
}
