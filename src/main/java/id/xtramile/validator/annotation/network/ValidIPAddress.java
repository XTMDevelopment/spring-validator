package id.xtramile.validator.annotation.network;

import id.xtramile.validator.validator.network.IPAddressValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates an IP address string (IPv4 or IPv6).
 * <p>
 * Null/blank values are considered valid. This validator ensures the IP address
 * follows the correct format for either IPv4 or IPv6 protocols.
 * 
 * <p>Example usage:
 * <pre>{@code
 * @ValidIPAddress
 * private String ipAddress; // e.g., 192.168.1.1 or 2001:db8::1
 * 
 * @ValidIPAddress
 * private String serverIp; // e.g., 10.0.0.1
 * }</pre>
 * 
 * @see IPAddressValidator
 */
@Documented
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = IPAddressValidator.class)
public @interface ValidIPAddress {
    String message() default "{friendly.default}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
