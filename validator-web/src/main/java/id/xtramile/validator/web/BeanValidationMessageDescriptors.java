package id.xtramile.validator.web;

import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * Utilities for detecting default Bean Validation and library-specific message templates.
 */
public final class BeanValidationMessageDescriptors {

    private static final String FRIENDLY_DEFAULT = "{friendly.default}";

    private BeanValidationMessageDescriptors() {
    }

    /**
     * Returns whether the template is the library's friendly-default placeholder.
     *
     * @param template the message template
     * @return {@code true} if the template is {@code {friendly.default}}
     */
    public static boolean isFriendlyDefault(String template) {
        return FRIENDLY_DEFAULT.equals(template);
    }

    /**
     * Returns whether the template is a default (uncustomized) validation message.
     *
     * @param template the message template
     * @return {@code true} if blank, friendly-default, or a standard BV descriptor
     */
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

    /**
     * Returns whether the template is a Jakarta/JSV/Hibernate default message descriptor.
     *
     * @param template the message template
     * @return {@code true} if it matches a known default descriptor pattern
     */
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

    /**
     * Returns whether a field error should be resolved from constraint defaults.
     *
     * @param attrs constraint annotation attributes from the field error
     * @return {@code true} if the message attribute is absent or uses a default key
     */
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

    /**
     * Returns whether the message template defers to constraint or friendly defaults.
     *
     * @param messageTemplate the message template from a constraint or field error
     * @return {@code true} if blank, friendly-default, or a standard BV key
     */
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
