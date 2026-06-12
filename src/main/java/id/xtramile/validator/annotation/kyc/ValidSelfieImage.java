package id.xtramile.validator.annotation.kyc;

import id.xtramile.validator.validator.kyc.SelfieImageValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates an uploaded selfie image with specific requirements for KYC compliance.
 * <p>
 * Validates MultipartFile for size, dimensions, aspect ratio, and MIME type.
 * Null/empty files are considered valid (ignored). This validator ensures selfie images
 * meet KYC documentation standards for identity verification.
 *
 * <p>Example usage:
 * <pre>{@code
 * @ValidSelfieImage
 * private MultipartFile selfie; // Standard KYC selfie requirements
 *
 * @ValidSelfieImage(maxMB = 3, aspectRatio = 1.0)
 * private MultipartFile squareSelfie; // Square format selfie
 * }</pre>
 *
 * @see SelfieImageValidator
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SelfieImageValidator.class)
public @interface ValidSelfieImage {
    String message() default "{friendly.default}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Maximum file size in megabytes.
     */
    long maxMB() default 5;

    /**
     * Minimum image width in pixels.
     */
    int minWidth() default 256;

    /**
     * Minimum image height in pixels.
     */
    int minHeight() default 256;

    /**
     * Maximum image width in pixels.
     */
    int maxWidth() default 4096;

    /**
     * Maximum image height in pixels.
     */
    int maxHeight() default 4096;

    /**
     * Required aspect ratio (width/height). 1.0 means square format.
     */
    double aspectRatio() default 1.0;

    /**
     * Allowed MIME types for the image.
     */
    String[] mimeAllowed() default {"image/jpeg", "image/png"};
}
