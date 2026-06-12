package id.xtramile.validator.annotation.network;

import id.xtramile.validator.validator.network.IPv6AddressValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates an IPv6 address string.
 * <p>
 * Null/blank values are considered valid. This validator ensures the IP address
 * follows the correct IPv6 format (hexadecimal notation with colons).
 *
 * <p>Example usage:
 * <pre>{@code
 * @ValidIPv6Address
 * private String ipv6Address; // e.g., 2001:db8::1
 *
 * @ValidIPv6Address
 * private String serverIpv6; // e.g., ::1 (localhost)
 * }</pre>
 *
 * @see IPv6AddressValidator
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = IPv6AddressValidator.class)
public @interface ValidIPv6Address {
    /**
     * Default violation message template.
     *
     * @return the message template
     */
    String message() default "{friendly.default}";

    /**
     * Validation groups for conditional validation.
     *
     * @return the validation groups
     */
    Class<?>[] groups() default {};

    /**
     * Payload types for extensibility metadata.
     *
     * @return the payload types
     */
    Class<? extends Payload>[] payload() default {};
}
