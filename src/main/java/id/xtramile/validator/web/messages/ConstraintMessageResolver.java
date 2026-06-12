package id.xtramile.validator.web.messages;

import java.util.Map;

/**
 * Resolves localized messages for a specific validation annotation group.
 */
public interface ConstraintMessageResolver {

    /**
     * Returns whether this resolver handles the given annotation type.
     *
     * @param annotationType the constraint annotation class
     * @return {@code true} if this resolver supports the type
     */
    boolean supports(Class<?> annotationType);

    /**
     * Resolves a localized message for the constraint violation.
     *
     * @param field          display name of the validated field
     * @param annotationType the constraint annotation class
     * @param attrs          constraint annotation attributes
     * @param dtoClass       the validated DTO class
     * @return the resolved message, or {@code null} to defer to the next resolver
     */
    String resolve(String field, Class<?> annotationType, Map<String, Object> attrs, Class<?> dtoClass);
}
