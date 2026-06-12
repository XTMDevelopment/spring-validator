package id.xtramile.validator.web;

public class ValidationAnnotationTypeRegistry {

    private ValidationAnnotationTypeRegistry() {}

    public static Class<?> resolve(String annotationName) {
        return AnnotationRegistry.resolve(annotationName);
    }
}
