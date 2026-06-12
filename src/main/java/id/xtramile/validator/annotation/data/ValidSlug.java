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
     */
    int min() default 1;

    /**
     * Maximum slug length.
     */
    int max() default 100;

    String message() default "{friendly.default}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
