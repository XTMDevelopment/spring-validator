package id.xtramile.validator.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Reflection helpers for reading constraint annotation values from DTO fields.
 */
public class AnnotationUtils {
    private static final Logger log = LoggerFactory.getLogger(AnnotationUtils.class);

    private AnnotationUtils() {
    }

    /**
     * Returns annotation attribute values for a field, or defaults if the annotation is absent.
     *
     * @param dtoClass       the DTO class
     * @param fieldName      the field or nested property path
     * @param annotationType the constraint annotation class
     * @return a map of attribute names to values
     */
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

    /**
     * Finds a constraint annotation on a DTO field, including nested property paths.
     *
     * @param dtoClass       the DTO class
     * @param fieldName      the field or nested property path
     * @param annotationType the annotation class to find
     * @return the annotation instance, or {@code null} if not present
     */
    @SuppressWarnings("unchecked")
    public static Annotation findFieldAnnotation(Class<?> dtoClass, String fieldName, Class<?> annotationType) {
        if (dtoClass == null || fieldName == null || annotationType == null) {
            return null;
        }

        Field field = resolveLeafField(dtoClass, fieldName);
        if (field == null) {
            return null;
        }

        return field.getAnnotation((Class<? extends Annotation>) annotationType);
    }

    /**
     * Extracts all attribute values from an annotation instance.
     *
     * @param annotation the annotation instance
     * @return a map of attribute names to values
     */
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

        } catch (ReflectiveOperationException e) {
            log.debug("Failed to extract values from annotation {}", annotation.annotationType().getName(), e);
        }

        return map;
    }

    /**
     * Finds a declared field by name, searching superclasses.
     *
     * @param type      the class to search
     * @param fieldName the field name
     * @return the accessible field, or {@code null} if not found
     */
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

    /**
     * Returns default attribute values declared on an annotation type.
     *
     * @param annotationType the annotation class
     * @return a map of attribute names to default values
     */
    public static Map<String, Object> extractAnnotationDefaults(Class<?> annotationType) {
        Map<String, Object> map = new HashMap<>();
        if (annotationType == null || !annotationType.isAnnotation()) {
            return map;
        }

        for (Method method : annotationType.getDeclaredMethods()) {
            map.put(method.getName(), method.getDefaultValue());
        }

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
