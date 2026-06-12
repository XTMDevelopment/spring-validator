package id.xtramile.validator.web;

/**
 * Facade for resolving validation annotation types by name.
 */
public class ValidationAnnotationTypeRegistry {

    private ValidationAnnotationTypeRegistry() {
    }

    /**
     * Resolves an annotation class by its simple name.
     *
     * @param annotationName the annotation simple name
     * @return the annotation type, or {@code null} if unknown
     */
    public static Class<?> resolve(String annotationName) {
        return AnnotationRegistry.resolve(annotationName);
    }
}
