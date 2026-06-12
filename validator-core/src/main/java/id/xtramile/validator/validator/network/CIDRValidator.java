package id.xtramile.validator.validator.network;

import id.xtramile.validator.annotation.network.ValidCIDR;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

import static id.xtramile.validator.util.ValidatorUtils.isBlank;

/**
 * Validator implementation for {@link ValidCIDR} annotation.
 * <p>
 * Validates CIDR (Classless Inter-Domain Routing) notation for both IPv4 and IPv6.
 * This validator ensures the CIDR block follows the correct format with proper
 * IP address and prefix length validation.
 *
 * <p>The validator performs the following operations:
 * <ul>
 * <li>Accepts null/blank values as valid</li>
 * <li>Validates CIDR format (IP/prefix)</li>
 * <li>Validates IPv4 octets (0-255)</li>
 * <li>Validates IPv6 addresses using InetAddress</li>
 * <li>Validates prefix length (0-32 for IPv4, 0-128 for IPv6)</li>
 * </ul>
 *
 * @see ValidCIDR
 */
public class CIDRValidator implements ConstraintValidator<ValidCIDR, String> {

    private static final Pattern CIDR_PATTERN = Pattern.compile("^[^/]+/[0-9]+$");

    /**
     * Validates the CIDR notation format and components.
     *
     * @param value   the CIDR string to validate
     * @param context the constraint validator context
     * @return true if the CIDR notation is valid or is null/blank
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (isBlank(value)) return true;

        String trimmed = value.trim();
        if (trimmed.isEmpty()) return true;

        if (!CIDR_PATTERN.matcher(trimmed).matches()) return false;

        String[] parts = trimmed.split("/");
        if (parts.length != 2) return false;

        String ipPart = parts[0].trim();
        String prefixPart = parts[1].trim();

        if (ipPart.contains(".")) {
            String[] octets = ipPart.split("\\.");
            if (octets.length != 4) return false;

            for (String octet : octets) {
                if (octet.isEmpty()) return false;
                try {
                    int num = Integer.parseInt(octet);
                    if (num < 0 || num > 255) return false;
                } catch (NumberFormatException e) {
                    return false;
                }
            }

        } else {
            try {
                InetAddress address = InetAddress.getByName(ipPart);
                if (!(address instanceof java.net.Inet6Address)) {
                    return false;
                }
            } catch (UnknownHostException ex) {
                return false;
            }
        }

        try {
            int prefix = Integer.parseInt(prefixPart);

            boolean isIPv4 = ipPart.contains(".");
            int maxPrefix = isIPv4 ? 32 : 128;

            if (prefix < 0 || prefix > maxPrefix) return false;
        } catch (NumberFormatException ex) {
            return false;
        }

        return true;
    }
}
