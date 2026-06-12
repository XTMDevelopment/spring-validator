package id.xtramile.validator.util;

import id.xtramile.validator.enums.Group;
import jakarta.validation.ConstraintValidatorContext;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Builds constraint violations with localized message keys and thread-local argument storage.
 */
public class MessageUtils {
    private static final ThreadLocal<Map<String, Object[]>> ARG_STORAGE = ThreadLocal.withInitial(ConcurrentHashMap::new);

    private MessageUtils() {
    }

    /**
     * Adds a constraint violation with a group-scoped message key and no arguments.
     *
     * @param context the validator context
     * @param group   the validation message group
     * @param key     the message sub-key
     */
    public static void buildViolation(ConstraintValidatorContext context, Group group, String key) {
        buildViolation(context, group, key, (Object[]) null);
    }

    /**
     * Adds a constraint violation with a group-scoped message key and format arguments.
     *
     * @param context the validator context
     * @param group   the validation message group
     * @param key     the message sub-key
     * @param args    optional arguments stored for later message resolution
     */
    public static void buildViolation(ConstraintValidatorContext context, Group group, String key, Object... args) {
        if (context == null) return;

        context.disableDefaultConstraintViolation();

        String fullKey = buildKey(group, key);

        if (args != null && args.length > 0) {
            ARG_STORAGE.get().put(fullKey, args);
        }

        context.buildConstraintViolationWithTemplate(fullKey).addConstraintViolation();
    }

    /**
     * Retrieves and removes stored format arguments for a message template.
     *
     * @param template the full message template key
     * @return the stored arguments, or {@code null} if none
     */
    public static Object[] getStoredArgs(String template) {
        Map<String, Object[]> storage = ARG_STORAGE.get();
        return storage.remove(template);
    }

    /** Clears all thread-local stored message arguments. */
    public static void clearStoredArgs() {
        ARG_STORAGE.get().clear();
        ARG_STORAGE.remove();
    }

    /**
     * Joins strings in sorted order with comma separators.
     *
     * @param set the values to join
     * @return the joined string
     */
    public static String join(Set<String> set) {
        List<String> sorted = new ArrayList<>(set);
        Collections.sort(sorted);

        return String.join(", ", sorted);
    }

    /**
     * Builds a full validation message key from a group and sub-key.
     *
     * @param group the validation message group
     * @param key   the sub-key
     * @return the full key (e.g. {@code validation.common.enum})
     */
    public static String buildKey(Group group, String key) {
        return "validation." + group.name().toLowerCase() + "." + key;
    }
}
