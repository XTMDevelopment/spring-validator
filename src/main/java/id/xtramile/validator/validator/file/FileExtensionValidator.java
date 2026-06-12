package id.xtramile.validator.validator.file;

import id.xtramile.validator.annotation.file.ValidFileExtension;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static id.xtramile.validator.util.ValidatorUtils.isBlank;

/**
 * Validator implementation for {@link ValidFileExtension} annotation.
 * <p>
 * Validates file extensions for both String filenames and MultipartFile objects.
 * This validator ensures the file extension is in the allowed list with optional
 * case-insensitive comparison.
 * 
 * <p>The validator performs the following operations:
 * <ul>
 * <li>Accepts null values as valid</li>
 * <li>Handles String filenames and MultipartFile objects</li>
 * <li>Extracts file extension from the filename</li>
 * <li>Validates against the allowed extensions list</li>
 * <li>Supports case-insensitive comparison</li>
 * </ul>
 * 
 * @see ValidFileExtension
 */
public class FileExtensionValidator implements ConstraintValidator<ValidFileExtension, Object> {
    private Set<String> allowed;
    private boolean ignoreCase;

    /**
     * Initializes the validator with the annotation parameters.
     * @param annotation the ValidFileExtension annotation instance
     */
    @Override
    public void initialize(ValidFileExtension annotation) {
        this.ignoreCase = annotation.ignoreCase();
        this.allowed = Stream.of(annotation.allowed())
                .map(ext -> ignoreCase ? ext.toLowerCase() : ext)
                .collect(Collectors.toSet());
    }

    /**
     * Validates the file extension against the allowed list.
     * @param value the file object (String or MultipartFile) to validate
     * @param context the constraint validator context
     * @return true if the file extension is allowed or value is null/empty
     */
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) return true;

        String fileName;

        if (value instanceof String) {
            String s = (String) value;
            if (isBlank(s)) return true;
            fileName = s;

        } else if (value instanceof MultipartFile) {
            MultipartFile mf = (MultipartFile) value;
            if (mf.isEmpty()) return true;
            fileName = mf.getOriginalFilename();

        } else {
            return true;
        }

        if (isBlank(fileName)) return false;

        int dot = fileName.lastIndexOf('.');
        if (dot < 0 || dot == fileName.length() - 1) return false;

        String ext = fileName.substring(dot + 1);
        String key = ignoreCase ? ext.toLowerCase() : ext;
        return allowed.contains(key);
    }
}
