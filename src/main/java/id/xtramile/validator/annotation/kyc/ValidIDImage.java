package id.xtramile.validator.annotation.kyc;

import id.xtramile.validator.validator.kyc.IDImageValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates an uploaded ID document image with specific requirements for KYC compliance.
 * <p>
 * Validates MultipartFile for size, dimensions, aspect ratio, and MIME type.
 * Null/empty files are considered valid (ignored). This validator ensures ID images
 * meet KYC documentation standards.
 * 
 * <p>Example usage:
 * <pre>{@code
 * @ValidIDImage
 * private MultipartFile idDocument; // Standard KYC requirements
 * 
 * @ValidIDImage(maxMB = 10, minWidth = 800, minHeight = 600)
 * private MultipartFile highQualityId; // Custom requirements
 * }</pre>
 * 
 * @see IDImageValidator
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = IDImageValidator.class)
public @interface ValidIDImage {
    String message() default "{friendly.default}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    /**
     * Maximum file size in megabytes.
     */
    long maxMB() default 7;
    
    /**
     * Minimum image width in pixels.
     */
    int minWidth() default 600;
    
    /**
     * Minimum image height in pixels.
     */
    int minHeight() default 400;
    
    /**
     * Maximum image width in pixels.
     */
    int maxWidth() default 8000;
    
    /**
     * Maximum image height in pixels.
     */
    int maxHeight() default 8000;
    
    /**
     * Required aspect ratio (width/height). 0.0 means no requirement.
     */
    double aspectRatio() default 0.0;
    
    /**
     * Allowed MIME types for the image.
     */
    String[] mimeAllowed() default {"image/jpeg","image/png"};
}
