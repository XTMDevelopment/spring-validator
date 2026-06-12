package id.xtramile.validator.validator.data;

import id.xtramile.validator.annotation.data.ValidBase64;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Base64;

import static id.xtramile.validator.util.ValidatorUtils.isBlank;

/**
 * Validator implementation for {@link ValidBase64} annotation.
 * <p>
 * Validates a Base64-encoded string using either standard or URL-safe Base64 alphabet.
 * This validator ensures the string is properly Base64 encoded and can be decoded successfully.
 *
 * <p>The validator performs the following operations:
 * <ul>
 * <li>Accepts null/blank values as valid</li>
 * <li>Validates using URL-safe Base64 decoder when urlSafe=true</li>
 * <li>Validates using standard Base64 decoder when urlSafe=false</li>
 * <li>Handles padding for standard Base64 when needed</li>
 * </ul>
 *
 * @see ValidBase64
 */
public class Base64Validator implements ConstraintValidator<ValidBase64, String> {
    private boolean urlSafe;

    /**
     * Initializes the validator with the annotation parameters.
     *
     * @param annotation the ValidBase64 annotation instance
     */
    @Override
    public void initialize(ValidBase64 annotation) {
        this.urlSafe = annotation.urlSafe();
    }

    /**
     * Validates the Base64 string using the configured alphabet.
     *
     * @param value   the Base64 string to validate
     * @param context the constraint validator context
     * @return true if the string is valid Base64 or is null/blank
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (isBlank(value)) return true;

        try {
            if (urlSafe) {
                // URL-safe Base64 doesn't require padding
                byte[] bytes = Base64.getUrlDecoder().decode(value);
                return bytes != null;

            } else {
                // For standard Base64, try direct decoding first
                try {
                    byte[] bytes = Base64.getDecoder().decode(value);
                    return bytes != null;

                } catch (IllegalArgumentException e) {
                    // If fails, try adding padding
                    String paddedValue = value;
                    int remainder = value.length() % 4;
                    if (remainder > 0) {
                        paddedValue += "=".repeat(4 - remainder);
                        try {
                            byte[] bytes = Base64.getDecoder().decode(paddedValue);
                            return bytes != null;

                        } catch (IllegalArgumentException e2) {
                            return false;
                        }
                    }

                    return false;
                }
            }

        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
