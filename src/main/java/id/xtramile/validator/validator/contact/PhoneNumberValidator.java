package id.xtramile.validator.validator.contact;

import id.xtramile.validator.annotation.contact.ValidPhoneNumber;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

import static id.xtramile.validator.util.ValidatorUtils.isBlank;

/**
 * Validator implementation for {@link ValidPhoneNumber} annotation.
 * <p>
 * Validates an Indonesian phone number in MSISDN format.
 * This validator ensures the phone number follows Indonesian mobile number standards
 * with proper country code and digit constraints.
 * 
 * <p>The validator performs the following checks:
 * <ul>
 * <li>Accepts null/blank values as valid</li>
 * <li>Validates Indonesian MSISDN format: starts with 62</li>
 * <li>Ensures 8-11 additional digits after country code</li>
 * <li>First digit after country code must be 1-9 (E.164 compliant)</li>
 * </ul>
 * 
 * <p>Example: 6281234567890
 * 
 * @see ValidPhoneNumber
 */
public class PhoneNumberValidator implements ConstraintValidator<ValidPhoneNumber, String> {
    private static final Pattern REGEX = Pattern.compile("^62[1-9]\\d{8,11}$");

    /**
     * Validates the Indonesian phone number format.
     * @param value the phone number string to validate
     * @param context the constraint validator context
     * @return true if the phone number is valid or is null/blank
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (isBlank(value)) return true;
        return REGEX.matcher(value).matches();
    }
}
