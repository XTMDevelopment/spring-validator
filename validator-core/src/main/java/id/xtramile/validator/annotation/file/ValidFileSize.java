package id.xtramile.validator.annotation.file;

import id.xtramile.validator.validator.file.FileSizeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates the size of a file.
 * <p>
 * Supports MultipartFile and byte[]. Strings are ignored by the validator.
 * Empty/null values are considered valid (ignored). Size must be between minBytes and max (inclusive).
 * When maxMB >= 0, it acts as a convenience to specify the maximum in megabytes.
 *
 * <p>Example usage:
 * <pre>{@code
 * // Max 5 MB, any minimum
 * @ValidFileSize(maxMB = 5)
 * private MultipartFile attachment;
 *
 * // Between 1 KB and 1 MB (bytes)
 * @ValidFileSize(minBytes = 1024, maxBytes = 1_048_576)
 * private byte[] payload;
 * }</pre>
 *
 * @see FileSizeValidator
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FileSizeValidator.class)
public @interface ValidFileSize {
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
     * Minimum bytes (inclusive). Use 0 for "no minimum".
     *
     * @return the minimum bytes (inclusive). Use 0 for "no minimum"
     */
    long minBytes() default 0;

    /**
     * Maximum bytes (inclusive). Set to -1 for "no maximum".
     *
     * @return the maximum bytes (inclusive). Set to -1 for "no maximum"
     */
    long maxBytes() default -1;

    /**
     * Optional convenience: if >= 0, overrides maxBytes as MB.
     *
     * @return the optional convenience: if >= 0, overrides maxBytes as MB
     */
    long maxMB() default -1;
}
