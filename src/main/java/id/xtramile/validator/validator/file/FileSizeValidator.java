package id.xtramile.validator.validator.file;

import id.xtramile.validator.annotation.file.ValidFileSize;
import id.xtramile.validator.enums.Group;
import id.xtramile.validator.util.MessageUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import static id.xtramile.validator.util.ValidatorUtils.formatFileSize;
import static id.xtramile.validator.util.ValidatorUtils.mbToBytes;

/**
 * Validator implementation for {@link ValidFileSize} annotation.
 * <p>
 * Validates file size for MultipartFile and byte array objects.
 * This validator ensures the file size is within the specified range
 * with support for both byte and megabyte limits.
 * 
 * <p>The validator performs the following operations:
 * <ul>
 * <li>Accepts null values as valid</li>
 * <li>Handles MultipartFile and byte array objects</li>
 * <li>Validates size against min and max constraints</li>
 * <li>Supports both byte and megabyte limits</li>
 * <li>Ignores unsupported types (returns true)</li>
 * </ul>
 * 
 * @see ValidFileSize
 */
public class FileSizeValidator implements ConstraintValidator<ValidFileSize, Object> {
    private long min;
    private long max;

    /**
     * Initializes the validator with the annotation parameters.
     * @param annotation the ValidFileSize annotation instance
     */
    @Override
    public void initialize(ValidFileSize annotation) {
        this.min = Math.max(0, annotation.minBytes());

        long maxBytes = annotation.maxBytes();
        long maxFromMB = mbToBytes(annotation.maxMB());
        
        // If maxBytes is -1 (no limit) but maxMB is set, use maxMB
        if (maxBytes < 0 && maxFromMB >= 0) {
            this.max = maxFromMB;
        } else {
            this.max = maxBytes;
        }

        if (this.max >= 0 && this.max < this.min) {
            this.max = this.min;
        }
    }

    /**
     * Validates the file size against the configured constraints.
     * @param value the file object to validate
     * @param context the constraint validator context
     * @return true if the file size is within limits or value is null/empty
     */
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) return true;

        long size;
        if (value instanceof MultipartFile) {
            MultipartFile mf = (MultipartFile) value;
            if (mf.isEmpty()) return true;
            size = mf.getSize();

        } else if (value instanceof byte[]) {
            byte[] bytes = (byte[]) value;
            if (bytes.length == 0) return true;
            size = bytes.length;

        } else if (value instanceof CharSequence) {
            // Base64 or similar in a string
            return true;

        } else {
            // Unsupported type
            return true;
        }

        if (size < min) {
            String sizeText = formatFileSize(size);
            MessageUtils.buildViolation(context, Group.FILE, "file-size.min", sizeText);
            return false;
        }

        if (max >= 0 && size > max) {
            String sizeText = formatFileSize(size);
            MessageUtils.buildViolation(context, Group.FILE, "file-size.max", sizeText);
            return false;
        }

        return true;
    }
}
