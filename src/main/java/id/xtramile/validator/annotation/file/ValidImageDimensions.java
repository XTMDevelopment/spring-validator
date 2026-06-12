package id.xtramile.validator.annotation.file;

import id.xtramile.validator.validator.file.ImageDimensionsValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates uploaded image dimensions and optional aspect ratio on a MultipartFile.
 * <p>
 * Null/empty files are considered valid (ignored).
 * This validator ensures images meet specific dimension requirements.
 *
 * <p>Example usage:
 * <pre>{@code
 * // Image must be at least 600x400 and at most 8000x8000
 * @ValidImageDimensions(minWidth = 600, minHeight = 400, maxWidth = 8000, maxHeight = 8000)
 * private MultipartFile image;
 *
 * // Enforce 1:1 aspect ratio
 * @ValidImageDimensions(aspectRatio = 1.0)
 * private MultipartFile avatar;
 * }</pre>
 *
 * @see ImageDimensionsValidator
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ImageDimensionsValidator.class)
public @interface ValidImageDimensions {
    String message() default "{friendly.default}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Minimum image width in pixels.
     */
    int minWidth() default 0;

    /**
     * Minimum image height in pixels.
     */
    int minHeight() default 0;

    /**
     * Maximum image width in pixels.
     */
    int maxWidth() default Integer.MAX_VALUE;

    /**
     * Maximum image height in pixels.
     */
    int maxHeight() default Integer.MAX_VALUE;

    /**
     * If > 0, enforce exact aspect ratio = width/height (e.g., 1.7777 for 16:9).
     */
    double aspectRatio() default 0.0;
}
