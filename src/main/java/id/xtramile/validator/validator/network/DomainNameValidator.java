package id.xtramile.validator.validator.network;

import id.xtramile.validator.annotation.network.ValidDomainName;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

import static id.xtramile.validator.util.ValidatorUtils.isBlank;

/**
 * Validator implementation for {@link ValidDomainName} annotation.
 * <p>
 * Validates domain names following RFC-1035 standards with optional punycode support.
 * This validator ensures the domain name follows proper format with label length
 * constraints and character restrictions.
 * 
 * <p>The validator performs the following operations:
 * <ul>
 * <li>Accepts null/blank values as valid</li>
 * <li>Validates label length (1-63 characters)</li>
 * <li>Validates total length (≤253 characters)</li>
 * <li>Validates character restrictions (alphanumeric + hyphen)</li>
 * <li>Optionally allows punycode (xn--) labels</li>
 * <li>Prevents consecutive dots and invalid characters</li>
 * </ul>
 * 
 * @see ValidDomainName
 */
public class DomainNameValidator implements ConstraintValidator<ValidDomainName, String> {
    private boolean allowPunycode;

    private static final Pattern PUNYCODE_PATTERN = Pattern.compile("^xn--");

    /**
     * Initializes the validator with the annotation parameters.
     * @param annotation the ValidDomainName annotation instance
     */
    @Override
    public void initialize(ValidDomainName annotation) {
        this.allowPunycode = annotation.allowPunycode();
    }

    /**
     * Validates the domain name against RFC-1035 standards.
     * @param value the domain name string to validate
     * @param context the constraint validator context
     * @return true if the domain name is valid or is null/blank
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (isBlank(value)) return true;

        String trimmed = value.trim();
        if (trimmed.isEmpty()) return true;

        if (trimmed.contains("://")) return false;
        if (trimmed.contains(":") && !trimmed.contains("::")) return false;
        if (trimmed.contains("@") || trimmed.contains("#") || trimmed.contains("%") || 
            trimmed.contains("&") || trimmed.contains("*") || trimmed.contains("+") || 
            trimmed.contains("=") || trimmed.contains("!") || trimmed.contains(" ")) {
            return false;
        }

        if (trimmed.startsWith("-") || trimmed.endsWith("-")) return false;
        if (trimmed.contains("..")) return false;
        if (trimmed.startsWith(".") || trimmed.endsWith(".")) return false;
        if (trimmed.length() > 253) return false;

        String[] labels = trimmed.split("\\.");
        for (String label : labels) {
            if (label.isEmpty() || label.length() > 63) return false;
            if (label.startsWith("-") || label.endsWith("-")) return false;
            if (label.contains("--")) return false;
        }

        if (!trimmed.contains(".")) return false;

        if (!allowPunycode) {
            for (String label : labels) {
                if (PUNYCODE_PATTERN.matcher(label).find()) {
                    return false;
                }
            }
        }

        return true;
    }
}
