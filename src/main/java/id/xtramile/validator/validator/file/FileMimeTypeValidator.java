package id.xtramile.validator.validator.file;

import id.xtramile.validator.annotation.file.ValidFileMimeType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import static id.xtramile.validator.util.ValidatorUtils.isBlank;

/**
 * Validator implementation for {@link ValidFileMimeType} annotation.
 * <p>
 * Validates MIME types for both String values and MultipartFile objects.
 * This validator ensures the MIME type matches one of the allowed types
 * with support for wildcard patterns and case-insensitive comparison.
 * 
 * <p>The validator performs the following operations:
 * <ul>
 * <li>Accepts null values as valid</li>
 * <li>Handles String MIME types and MultipartFile objects</li>
 * <li>Supports exact MIME type matching</li>
 * <li>Supports wildcard patterns (e.g., "image/*")</li>
 * <li>Supports case-insensitive comparison</li>
 * </ul>
 * 
 * @see ValidFileMimeType
 */
public class FileMimeTypeValidator implements ConstraintValidator<ValidFileMimeType, Object> {
    private String[] allowed;
    private boolean ignoreCase;

    /**
     * Initializes the validator with the annotation parameters.
     * @param annotation the ValidFileMimeType annotation instance
     */
    @Override
    public void initialize(ValidFileMimeType annotation) {
        this.ignoreCase = annotation.ignoreCase();
        this.allowed = annotation.allowed();
    }

    /**
     * Validates the MIME type against the allowed list.
     * @param value the file object (String or MultipartFile) to validate
     * @param context the constraint validator context
     * @return true if the MIME type is allowed or value is null/empty
     */
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) return true;

        String mime;

        if (value instanceof String s) {
            if (isBlank(s)) return true;
            mime = s;

        } else if (value instanceof MultipartFile mf) {
            if (mf.isEmpty()) return true;
            mime = mf.getContentType();

        } else {
            return true;
        }

        if (isBlank(mime)) return false;

        for (String rule : allowed) {
            if (matches(rule, mime)) return true;
        }

        return false;
    }

    /**
     * Checks if a MIME type matches a rule (exact or wildcard).
     * @param rule the rule to match against
     * @param mime the MIME type to check
     * @return true if the MIME type matches the rule
     */
    private boolean matches(String rule, String mime) {
        if (isBlank(rule) || isBlank(mime)) return false;

        String r = ignoreCase ? rule.toLowerCase() : rule;
        String m = ignoreCase ? mime.toLowerCase() : mime;

        if (!r.contains("*")) {
            return r.equals(m);
        }

        int slashR = r.indexOf('/');
        int slashM = m.indexOf('/');
        if (slashR <= 0 || slashM <= 0) return false;

        String typeR = r.substring(0, slashR);
        String subR = r.substring(slashR + 1);
        String typeM = m.substring(0, slashM);
        String subM = m.substring(slashM + 1);

        boolean typeMatch = typeR.equals(typeM);
        boolean subMatch = "*".equals(subR) || subR.equals(subM);
        return typeMatch && subMatch;
    }
}
