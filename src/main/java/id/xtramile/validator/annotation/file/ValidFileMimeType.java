package id.xtramile.validator.annotation.file;

import id.xtramile.validator.validator.file.FileMimeTypeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates that a MIME type or uploaded file's content type matches one of the allowed rules.
 * <p>
 * Supports exact match (e.g., "application/pdf") and wildcard subtype (e.g., "image/*").
 * Accepts String or MultipartFile. Null/blank String and empty MultipartFile are considered valid (ignored).
 * 
 * <p>Example usage:
 * <pre>{@code
 * // Exact type
 * @ValidFileMimeType(allowed = {"application/pdf"})
 * private String mime; // e.g., application/pdf
 * 
 * // Wildcard type for images
 * @ValidFileMimeType(allowed = {"image/*"})
 * private MultipartFile photo;
 * }</pre>
 * 
 * @see FileMimeTypeValidator
 */
@Documented
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FileMimeTypeValidator.class)
public @interface ValidFileMimeType {
    String message() default "{friendly.default}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    /**
     * The list of allowed MIME types (supports wildcards like "image/*").
     */
    String[] allowed();
    
    /**
     * Whether the comparison should be case-insensitive.
     */
    boolean ignoreCase() default true;
}
