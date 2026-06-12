package id.xtramile.validator.validator.network;

import id.xtramile.validator.annotation.network.ValidIPv6Address;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

import static id.xtramile.validator.util.ValidatorUtils.isBlank;

/**
 * Validator implementation for {@link ValidIPv6Address} annotation.
 * <p>
 * Validates IPv6 addresses in various formats including compressed notation.
 * This validator ensures the IPv6 address follows the standard format
 * with support for compressed notation and IPv4-mapped addresses.
 *
 * <p>The validator performs the following operations:
 * <ul>
 * <li>Accepts null/blank values as valid</li>
 * <li>Validates IPv6 format using regex patterns</li>
 * <li>Supports compressed notation (::)</li>
 * <li>Handles IPv4-mapped IPv6 addresses</li>
 * <li>Uses InetAddress for additional validation</li>
 * </ul>
 *
 * @see ValidIPv6Address
 */
public class IPv6AddressValidator implements ConstraintValidator<ValidIPv6Address, String> {

    private static final Pattern IPv6_PATTERN = Pattern.compile(
            "^(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$|" +
                    "^::1$|" +
                    "^::$|" +
                    "^(?:[0-9a-fA-F]{1,4}:)*::(?:[0-9a-fA-F]{1,4}:)*[0-9a-fA-F]{1,4}$|" +
                    "^(?:[0-9a-fA-F]{1,4}:)*::(?:[0-9a-fA-F]{1,4}:)*$|" +
                    "^(?:[0-9a-fA-F]{1,4}:)*::(?:[0-9a-fA-F]{1,4}:)*[0-9a-fA-F]{1,4}$|" +
                    "^::ffff:(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$"
    );

    /**
     * Validates the IPv6 address format and components.
     *
     * @param value   the IPv6 address string to validate
     * @param context the constraint validator context
     * @return true if the IPv6 address is valid or is null/blank
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (isBlank(value)) return true;

        String trimmed = value.trim();
        if (trimmed.isEmpty()) return true;

        if (trimmed.startsWith("::ffff:") && trimmed.contains(".")) {
            String ipv4Part = trimmed.substring(7);
            if (!isValidIPv4Address(ipv4Part)) {
                return false;
            }

            return IPv6_PATTERN.matcher(trimmed).matches();
        }

        try {
            InetAddress address = InetAddress.getByName(trimmed);
            return address instanceof Inet6Address;
        } catch (UnknownHostException e) {
            return false;
        }
    }

    /**
     * Validates IPv4 address format for IPv4-mapped IPv6 addresses.
     *
     * @param value the IPv4 address string to validate
     * @return true if the IPv4 address is valid
     */
    private boolean isValidIPv4Address(String value) {
        try {
            String[] parts = value.split("\\.");
            if (parts.length != 4) return false;

            for (String part : parts) {
                if (part.isEmpty()) return false;
                int num = Integer.parseInt(part);
                if (num < 0 || num > 255) return false;
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
