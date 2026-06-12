package id.xtramile.validator.annotation.network;

import id.xtramile.validator.validator.network.CIDRValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates a CIDR (Classless Inter-Domain Routing) notation string.
 * <p>
 * Null/blank values are considered valid. This validator ensures the CIDR notation
 * follows the correct format for network address and subnet mask.
 *
 * <p>Example usage:
 * <pre>{@code
 * @ValidCIDR
 * private String networkRange; // e.g., 192.168.1.0/24
 *
 * @ValidCIDR
 * private String subnet; // e.g., 10.0.0.0/8
 * }</pre>
 *
 * @see CIDRValidator
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CIDRValidator.class)
public @interface ValidCIDR {
    String message() default "{friendly.default}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
