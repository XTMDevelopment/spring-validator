package id.xtramile.validator.annotation.network;

import id.xtramile.validator.validator.network.DomainNameValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates a domain name string according to RFC standards.
 * <p>
 * Null/blank values are considered valid. This validator ensures domain names
 * follow the correct format and optionally supports internationalized domain names.
 * 
 * <p>Example usage:
 * <pre>{@code
 * @ValidDomainName
 * private String domain; // e.g., example.com
 * 
 * @ValidDomainName(allowPunycode = false)
 * private String strictDomain; // e.g., mycompany.co
 * }</pre>
 * 
 * @see DomainNameValidator
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DomainNameValidator.class)
public @interface ValidDomainName {
    String message() default "{friendly.default}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    /**
     * Allow internationalized (IDN) names (punycode like xn--).
     */
    boolean allowPunycode() default true;
}
