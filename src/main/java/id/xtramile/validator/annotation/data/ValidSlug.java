package id.xtramile.validator.annotation.data;

import id.xtramile.validator.validator.data.SlugValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates a URL-friendly slug: lowercase letters and digits separated by single hyphens.
 * <p>
 * Pattern: ^[a-z0-9]+(?:-[a-z0-9]+)*$ with total length between min and max.
 * Null/blank values are considered valid.
 *
 * <p>Example usage:
 * <pre>{@code
 * @ValidSlug(min = 3, max = 50)
 * private String articleSlug; // e.g., my-first-post-2025
 *
 * @ValidSlug(min = 1, max = 100)
 * private String categorySlug; // e.g., technology-news
 * }</pre>
 *
 * @see SlugValidator
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SlugValidator.class)
public @interface ValidSlug {
    /**
     * Minimum slug length.
     *
     * @return the minimum slug length
     */
    int min() default 1;

    /**
     * Maximum slug length.
     *
     * @return the maximum slug length
     */
    int max() default 100;

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
