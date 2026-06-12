package id.xtramile.validator.annotation.network;

import id.xtramile.validator.validator.network.MacAddressValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates a MAC address string.
 * <p>
 * Null/blank values are considered valid. This validator ensures the MAC address
 * follows the correct format for hardware addresses.
 *
 * <p>Example usage:
 * <pre>{@code
 * @ValidMacAddress
 * private String macAddress; // e.g., 00:1B:44:11:3A:B7
 *
 * @ValidMacAddress
 * private String deviceMac; // e.g., AA:BB:CC:DD:EE:FF
 * }</pre>
 *
 * @see MacAddressValidator
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MacAddressValidator.class)
public @interface ValidMacAddress {
    String message() default "{friendly.default}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
