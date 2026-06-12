package id.xtramile.validator.validator.network;

import id.xtramile.validator.annotation.network.ValidIPAddress;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import static id.xtramile.validator.util.ValidatorUtils.isBlank;

/**
 * Validator implementation for {@link ValidIPAddress} annotation.
 * <p>
 * Validates general IP addresses (both IPv4 and IPv6).
 * This validator delegates to IPv4 and IPv6 validators to ensure
 * the address is valid in either format.
 *
 * <p>The validator performs the following operations:
 * <ul>
 * <li>Accepts null/blank values as valid</li>
 * <li>Delegates to IPv4 validator first</li>
 * <li>Falls back to IPv6 validator if IPv4 fails</li>
 * <li>Returns true if either format is valid</li>
 * </ul>
 *
 * @see ValidIPAddress
 */
public class IPAddressValidator implements ConstraintValidator<ValidIPAddress, String> {
    /**
     * Validates the IP address against IPv4 and IPv6 formats.
     *
     * @param value   the IP address string to validate
     * @param context the constraint validator context
     * @return true if the IP address is valid or is null/blank
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        IPv4AddressValidator iPv4AddressValidator = new IPv4AddressValidator();
        IPv6AddressValidator iPv6AddressValidator = new IPv6AddressValidator();

        if (isBlank(value)) return true;

        String trimmed = value.trim();
        if (trimmed.isEmpty()) return true;

        if (iPv4AddressValidator.isValid(value, context)) return true;
        return iPv6AddressValidator.isValid(value, context);
    }
}
