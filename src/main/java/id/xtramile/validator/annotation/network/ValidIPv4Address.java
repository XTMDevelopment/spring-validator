package id.xtramile.validator.annotation.network;

import id.xtramile.validator.validator.network.IPv4AddressValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates an IPv4 address string.
 * <p>
 * Null/blank values are considered valid. This validator ensures the IP address
 * follows the correct IPv4 format (dotted decimal notation).
 *
 * <p>Example usage:
 * <pre>{@code
 * @ValidIPv4Address
 * private String ipv4Address; // e.g., 192.168.1.1
 *
 * @ValidIPv4Address
 * private String gateway; // e.g., 10.0.0.1
 * }</pre>
 *
 * @see IPv4AddressValidator
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = IPv4AddressValidator.class)
public @interface ValidIPv4Address {
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
