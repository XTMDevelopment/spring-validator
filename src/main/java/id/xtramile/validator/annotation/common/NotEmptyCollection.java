package id.xtramile.validator.annotation.common;

import id.xtramile.validator.validator.common.NotEmptyCollectionValidator;
import id.xtramile.validator.validator.common.NotEmptyMapValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates that a Collection or Map is not null and not empty.
 * <p>
 * This validator works with Collections, Maps, and Arrays. It ensures that the
 * collection contains at least one element. Null collections are considered invalid.
 *
 * <p>Example usage:
 * <pre>{@code
 * @NotEmptyCollection
 * private List<String> tags;
 *
 * @NotEmptyCollection
 * private Map<String, Object> attributes;
 *
 * @NotEmptyCollection
 * private String[] categories;
 * }</pre>
 *
 * @see NotEmptyCollectionValidator
 * @see NotEmptyMapValidator
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {
        NotEmptyCollectionValidator.class,
        NotEmptyMapValidator.class
})
public @interface NotEmptyCollection {
    /**
     * Default violation message template.
     *
     * @return the message template
     */
    String message() default "{friendly.default}";

    /**
     * Validation groups for conditional validation.
     *
     * @return the validation groups
     */
    Class<?>[] groups() default {};

    /**
     * Payload types for extensibility metadata.
     *
     * @return the payload types
     */
    Class<? extends Payload>[] payload() default {};
}
