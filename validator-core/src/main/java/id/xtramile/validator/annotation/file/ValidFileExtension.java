package id.xtramile.validator.annotation.file;

import id.xtramile.validator.validator.file.FileExtensionValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates that a filename or uploaded file has an allowed extension.
 * <p>
 * Supports validating String (filename) or MultipartFile. Null values, empty strings,
 * and empty MultipartFile are considered valid (ignored).
 *
 * <p>Example usage:
 * <pre>{@code
 * // On a String filename
 * @ValidFileExtension(allowed = {"jpg", "jpeg", "png"})
 * private String avatarFileName; // e.g., photo.png
 *
 * // On a MultipartFile (e.g., in Spring MVC request DTO)
 * @ValidFileExtension(allowed = {"pdf"}, ignoreCase = true)
 * private MultipartFile document;
 * }</pre>
 *
 * @see FileExtensionValidator
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FileExtensionValidator.class)
public @interface ValidFileExtension {
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

    /**
     * The list of allowed file extensions.
     *
     * @return the array of allowed file extensions
     */
    String[] allowed();

    /**
     * Whether the comparison should be case-insensitive.
     *
     * @return the whether the comparison should be case-insensitive
     */
    boolean ignoreCase() default true;
}
