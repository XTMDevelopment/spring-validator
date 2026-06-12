package id.xtramile.validator.validator.network;

import id.xtramile.validator.annotation.network.ValidIPv4Address;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import static id.xtramile.validator.util.ValidatorUtils.isBlank;

/**
 * Validator implementation for {@link ValidIPv4Address} annotation.
 * <p>
 * Validates IPv4 addresses in dotted decimal notation.
 * This validator ensures the IPv4 address follows the standard format
 * with four octets in the range 0-255.
 * 
 * <p>The validator performs the following operations:
 * <ul>
 * <li>Accepts null/blank values as valid</li>
 * <li>Validates four octet format</li>
 * <li>Validates each octet is 1-3 digits</li>
 * <li>Validates octet range (0-255)</li>
 * <li>Handles parsing exceptions gracefully</li>
 * </ul>
 * 
 * @see ValidIPv4Address
 */
public class IPv4AddressValidator implements ConstraintValidator<ValidIPv4Address, String> {

    /**
     * Validates the IPv4 address format and octet ranges.
     * @param value the IPv4 address string to validate
     * @param context the constraint validator context
     * @return true if the IPv4 address is valid or is null/blank
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (isBlank(value)) return true;

        String trimmed = value.trim();

        String[] parts = trimmed.split("\\.", -1);
        if (parts.length != 4) return false;

        for (String part : parts) {
            if (part.isEmpty()) return false;
            if (!part.matches("^\\d{1,3}$")) return false;

            int n = Integer.parseInt(part);
            if (n < 0 || n > 255) return false;
        }

        return true;
    }
}
