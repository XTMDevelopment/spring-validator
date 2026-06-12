package id.xtramile.validator.annotation.network;

import id.xtramile.validator.validator.network.PortValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates a port number (integer or string) between 1 and 65535.
 * <p>
 * Null values are considered valid. This validator ensures port numbers
 * are within the valid range for network services.
 * 
 * <p>Example usage:
 * <pre>{@code
 * @ValidPort
 * private Integer port; // e.g., 8080
 * 
 * @ValidPort
 * private String portString; // e.g., "443"
 * }</pre>
 * 
 * @see PortValidator
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PortValidator.class)
public @interface ValidPort {
    String message() default "{friendly.default}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
