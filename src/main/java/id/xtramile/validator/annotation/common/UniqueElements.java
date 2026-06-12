package id.xtramile.validator.annotation.common;

import id.xtramile.validator.validator.common.UniqueElementsValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates that a Collection contains only unique elements (no duplicates).
 * <p>
 * This validator ensures that all elements in the collection are unique.
 * Null collection is considered valid.
 *
 * <p>Example usage:
 * <pre>{@code
 * @UniqueElements
 * private List<String> usernames;
 *
 * @UniqueElements
 * private Set<Integer> userIds;
 * }</pre>
 *
 * @see UniqueElementsValidator
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueElementsValidator.class)
public @interface UniqueElements {
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
