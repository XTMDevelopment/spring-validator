package id.xtramile.validator.validator.contact;

import id.xtramile.validator.annotation.contact.ValidOtp;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

import static id.xtramile.validator.util.ValidatorUtils.isBlank;

/**
 * Validator implementation for {@link ValidOtp} annotation.
 * <p>
 * Validates an OTP (One-Time Password) string with configurable length and character set.
 * This validator ensures the OTP follows the specified format with either numeric-only
 * or alphanumeric characters.
 *
 * <p>The validator performs the following checks:
 * <ul>
 * <li>Accepts null/blank values as valid</li>
 * <li>Validates exact length as specified</li>
 * <li>Supports numeric-only or alphanumeric character sets</li>
 * <li>Uses regex pattern matching for validation</li>
 * </ul>
 *
 * @see ValidOtp
 */
public class OtpValidator implements ConstraintValidator<ValidOtp, String> {
    private Pattern pattern;

    /**
     * Initializes the validator with the annotation parameters.
     *
     * @param annotation the ValidOtp annotation instance
     */
    @Override
    public void initialize(ValidOtp annotation) {
        String cls = annotation.alphabets() ? "[a-zA-Z0-9]" : "\\d";
        this.pattern = Pattern.compile("^" + cls + "{" + annotation.length() + "}$");
    }

    /**
     * Validates the OTP string against the configured format.
     *
     * @param value   the OTP string to validate
     * @param context the constraint validator context
     * @return true if the OTP is valid or is null/blank
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (isBlank(value)) return true;
        return pattern.matcher(value).matches();
    }
}
