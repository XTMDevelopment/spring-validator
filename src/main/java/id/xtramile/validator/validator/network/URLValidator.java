package id.xtramile.validator.validator.network;

import id.xtramile.validator.annotation.network.ValidURL;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.net.URI;
import java.util.regex.Pattern;

import static id.xtramile.validator.util.ValidatorUtils.isBlank;

/**
 * Validator implementation for {@link ValidURL} annotation.
 * <p>
 * Validates URLs with optional HTTPS-only enforcement.
 * This validator ensures the URL follows proper format with valid
 * scheme, host, and optional protocol restrictions.
 *
 * <p>The validator performs the following operations:
 * <ul>
 * <li>Accepts null/blank values as valid</li>
 * <li>Validates URL format using regex patterns</li>
 * <li>Validates URI components (scheme, host)</li>
 * <li>Optionally enforces HTTPS-only protocol</li>
 * <li>Prevents invalid URL endings and formats</li>
 * </ul>
 *
 * @see ValidURL
 */
public class URLValidator implements ConstraintValidator<ValidURL, String> {
    private static final Pattern URL_PATTERN = Pattern.compile(
            "^https?://\\S+$"
    );
    private boolean httpsOnly;

    /**
     * Initializes the validator with the annotation parameters.
     *
     * @param annotation the ValidURL annotation instance
     */
    @Override
    public void initialize(ValidURL annotation) {
        this.httpsOnly = annotation.httpsOnly();
    }

    /**
     * Validates the URL format and optional HTTPS enforcement.
     *
     * @param value   the URL string to validate
     * @param context the constraint validator context
     * @return true if the URL is valid or is null/blank
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (isBlank(value)) return true;

        String trimmed = value.trim();
        if (trimmed.isEmpty()) return true;

        if (trimmed.endsWith("+") || trimmed.endsWith("-") || trimmed.endsWith("%") ||
                trimmed.endsWith("#") || trimmed.endsWith("@") || trimmed.endsWith("&") ||
                trimmed.endsWith("*")) {
            return false;
        }

        try {
            URI uri = URI.create(trimmed);
            String scheme = uri.getScheme();
            String host = uri.getHost();

            if (scheme == null || host == null || host.isEmpty()) return false;

            if (!URL_PATTERN.matcher(trimmed).matches()) return false;
            if (host.contains("//") || host.startsWith("/")) return false;
            if (httpsOnly) return scheme.equalsIgnoreCase("https");

            return scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https");

        } catch (Exception e) {
            return false;
        }
    }
}
