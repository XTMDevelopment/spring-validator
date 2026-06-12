package id.xtramile.validator.web;

import id.xtramile.validator.annotation.common.FieldName;
import id.xtramile.validator.util.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class ValidationFieldDisplayNames {

    public String resolve(Class<?> dtoClass, String fieldName) {
        if (dtoClass == null || fieldName == null) {
            return fieldName;
        }

        Class<?> targetClass = dtoClass;
        String targetFieldName = fieldName;

        if (fieldName.contains(".")) {
            String[] pathParts = fieldName.split("\\.");
            targetFieldName = pathParts[pathParts.length - 1];

            for (int i = 0; i < pathParts.length - 1; i++) {
                try {
                    Field field = AnnotationUtils.findFieldRecursive(targetClass, pathParts[i]);
                    if (field == null) {
                        return fieldName;
                    }

                    targetClass = field.getType();

                } catch (Exception e) {
                    return fieldName;
                }
            }
        }

        Annotation annotation = AnnotationUtils.findFieldAnnotation(targetClass, targetFieldName, FieldName.class);
        if (annotation instanceof FieldName) {
            return ((FieldName) annotation).value();
        }

        return targetFieldName;
    }
}
