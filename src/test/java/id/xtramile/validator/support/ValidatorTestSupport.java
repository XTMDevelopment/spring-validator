package id.xtramile.validator.support;

import jakarta.validation.ConstraintValidator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public final class ValidatorTestSupport {

    private ValidatorTestSupport() {
    }

    public static <A extends Annotation> A getAnnotation(Class<?> dtoClass, String fieldName, Class<A> annotationType) {
        try {
            Field field = dtoClass.getDeclaredField(fieldName);
            A annotation = field.getAnnotation(annotationType);
            if (annotation == null) {
                throw new IllegalArgumentException(
                        "No @" + annotationType.getSimpleName() + " on " + dtoClass.getSimpleName() + "." + fieldName);
            }
            return annotation;
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException(
                    "Field " + fieldName + " not found on " + dtoClass.getSimpleName(), e);
        }
    }

    public static <V extends ConstraintValidator<A, ?>, A extends Annotation> void initializeValidator(
            V validator, A annotation) {
        validator.initialize(annotation);
    }

    public static <V extends ConstraintValidator<A, ?>, A extends Annotation> void initializeValidator(
            V validator, Class<?> dtoClass, String fieldName, Class<A> annotationType) {
        initializeValidator(validator, getAnnotation(dtoClass, fieldName, annotationType));
    }
}
