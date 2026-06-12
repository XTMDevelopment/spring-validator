package id.xtramile.validator.util;

import id.xtramile.validator.enums.Group;
import jakarta.validation.ConstraintValidatorContext;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MessageUtils {
    private MessageUtils() {}

    private static final ThreadLocal<Map<String, Object[]>> ARG_STORAGE = ThreadLocal.withInitial(ConcurrentHashMap::new);

    public static void buildViolation(ConstraintValidatorContext context, Group group, String key) {
        buildViolation(context, group, key, (Object[]) null);
    }

    public static void buildViolation(ConstraintValidatorContext context, Group group, String key, Object... args) {
        if (context == null) return;

        context.disableDefaultConstraintViolation();

        String fullKey = buildKey(group, key);

        if (args != null && args.length > 0) {
            ARG_STORAGE.get().put(fullKey, args);
        }

        context.buildConstraintViolationWithTemplate(fullKey).addConstraintViolation();
    }

    public static Object[] getStoredArgs(String template) {
        Map<String, Object[]> storage = ARG_STORAGE.get();
        return storage.remove(template);
    }

    public static void clearStoredArgs() {
        ARG_STORAGE.get().clear();
        ARG_STORAGE.remove();
    }

    public static String join(Set<String> set) {
        List<String> sorted = new ArrayList<>(set);
        Collections.sort(sorted);

        return String.join(", ", sorted);
    }

    public static String buildKey(Group group, String key) {
        return "validation." + group.name().toLowerCase() + "." + key;
    }
}
