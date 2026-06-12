package id.xtramile.validator.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class AnnotationUtils {
    private AnnotationUtils() {}

    public static Map<String, Object> getAnnotationAttributes(Class<?> dtoClass, String fieldName, Class<?> annotationType) {
        if (dtoClass == null || fieldName == null || annotationType == null) {
            return new HashMap<>();
        }

        Annotation annotation = findFieldAnnotation(dtoClass, fieldName, annotationType);
        if (annotation != null) {
            return extractAnnotationValues(annotation);
        } else {
            return extractAnnotationDefaults(annotationType);
        }
    }

    @SuppressWarnings("unchecked")
    public static Annotation findFieldAnnotation(Class<?> dtoClass, String fieldName, Class<?> annotationType) {
        try {
            Field field = resolveLeafField(dtoClass, fieldName);
            if (field == null) {
                return null;
            }

            return field.getAnnotation((Class<? extends Annotation>) annotationType);

        } catch (Exception e) {
            return null;
        }
    }

    public static Map<String, Object> extractAnnotationValues(Annotation annotation) {
        Map<String, Object> map = new HashMap<>();
        if (annotation == null) {
            return map;
        }

        try {
            Method[] methods = annotation.annotationType().getDeclaredMethods();

            for (Method method : methods) {
                Object value = method.invoke(annotation);
                map.put(method.getName(), value);
            }

        } catch (Exception ignored) {}

        return map;
    }

    public static Field findFieldRecursive(Class<?> type, String fieldName) {
        Class<?> current = type;

        while (current != null) {
            try {
                Field field = current.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field;

            } catch (NoSuchFieldException ignored) {
                current = current.getSuperclass();
            }
        }

        return null;
    }

    public static Map<String, Object> extractAnnotationDefaults(Class<?> annotationType) {
        Map<String, Object> map = new HashMap<>();
        if (annotationType == null || !annotationType.isAnnotation()) {
            return map;
        }

        try {
            for (Method method : annotationType.getDeclaredMethods()) {
                map.put(method.getName(), method.getDefaultValue());
            }
        } catch (Exception ignored) {}

        return map;
    }

    private static Field resolveLeafField(Class<?> root, String path) {
        if (root == null || path == null || path.isEmpty()) {
            return null;
        }

        int dot = path.indexOf('.');
        if (dot < 0) {
            return findFieldRecursive(root, path);
        }

        String segment = path.substring(0, dot);
        String rest = path.substring(dot + 1);

        Field parentField = findFieldRecursive(root, segment);
        if (parentField == null) {
            return null;
        }

        return resolveLeafField(parentField.getType(), rest);
    }
}
