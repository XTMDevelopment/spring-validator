package id.xtramile.validator.annotation.network;

import id.xtramile.validator.validator.network.URLValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates a URL string according to RFC standards.
 * <p>
 * Null/blank values are considered valid. This validator ensures URLs
 * follow the correct format and optionally enforces HTTPS-only protocols.
 * 
 * <p>Example usage:
 * <pre>{@code
 * @ValidURL
 * private String website; // e.g., https://example.com
 * 
 * @ValidURL(httpsOnly = true)
 * private String secureUrl; // e.g., https://api.example.com
 * }</pre>
 * 
 * @see URLValidator
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = URLValidator.class)
public @interface ValidURL {
    String message() default "{friendly.default}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    /**
     * Whether to enforce HTTPS-only URLs.
     */
    boolean httpsOnly() default false;
}
