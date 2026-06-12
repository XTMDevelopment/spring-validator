package id.xtramile.validator.validator.finance;

import id.xtramile.validator.annotation.finance.ValidSwiftCode;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import static id.xtramile.validator.util.ValidatorUtils.isBlank;

/**
 * Validator implementation for {@link ValidSwiftCode} annotation.
 * <p>
 * Validates SWIFT/BIC (Bank Identifier Code) format.
 * This validator ensures the SWIFT code follows the international standard
 * format with proper bank, country, location, and optional branch codes.
 * 
 * <p>The validator performs the following operations:
 * <ul>
 * <li>Accepts null/blank values as valid</li>
 * <li>Validates 8 or 11 character length</li>
 * <li>Validates bank code (4 letters)</li>
 * <li>Validates country code (2 letters)</li>
 * <li>Validates location code (2 letters/digits)</li>
 * <li>Validates optional branch code (3 letters/digits)</li>
 * </ul>
 * 
 * @see ValidSwiftCode
 */
public class SwiftCodeValidator implements ConstraintValidator<ValidSwiftCode, String> {

    /**
     * Validates the SWIFT/BIC code format.
     * @param value the SWIFT code string to validate
     * @param context the constraint validator context
     * @return true if the SWIFT code is valid or is null/blank
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (isBlank(value)) return true;

        String v = value.toUpperCase().trim();
        if (!(v.length() == 8 || v.length() == 11)) return false;

        String bank = v.substring(0, 4);
        String country = v.substring(4, 6);
        String location = v.substring(6, 8);
        String branch = v.length() == 11 ? v.substring(8) : null;

        if (!bank.matches("^[A-Z]{4}$")) return false;
        if (!country.matches("^[A-Z]{2}$")) return false;
        if (!location.matches("^[A-Z0-9]{2}$")) return false;
        return branch == null || branch.matches("^[A-Z0-9]{3}$");
    }
}
