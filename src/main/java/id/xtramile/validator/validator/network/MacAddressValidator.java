package id.xtramile.validator.validator.network;

import id.xtramile.validator.annotation.network.ValidMacAddress;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

import static id.xtramile.validator.util.ValidatorUtils.isBlank;

/**
 * Validator implementation for {@link ValidMacAddress} annotation.
 * <p>
 * Validates MAC addresses in standard format with colons or hyphens.
 * This validator ensures the MAC address follows the standard format
 * with 6 pairs of hexadecimal digits separated by colons or hyphens.
 * 
 * <p>The validator performs the following operations:
 * <li>Accepts null/blank values as valid</li>
 * <li>Validates 6 pairs of hexadecimal digits</li>
 * <li>Supports colon (:) and hyphen (-) separators</li>
 * <li>Uses regex pattern matching for validation</li>
 * </ul>
 * 
 * @see ValidMacAddress
 */
public class MacAddressValidator implements ConstraintValidator<ValidMacAddress, String> {
    private static final Pattern REGEX = Pattern.compile("^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$");

    /**
     * Validates the MAC address format.
     * @param value the MAC address string to validate
     * @param context the constraint validator context
     * @return true if the MAC address is valid or is null/blank
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (isBlank(value)) return true;

        String trimmed = value.trim();
        return REGEX.matcher(trimmed).matches();
    }
}
