package id.xtramile.validator.annotation.contact;

import id.xtramile.validator.validator.contact.EmailDomainValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates that the email's domain part is one of the allowed domains.
 * <p>
 * Case sensitivity can be configured. Null/blank values are considered valid.
 * This validator checks the domain part of an email address against a whitelist.
 *
 * <p>Example usage:
 * <pre>{@code
 * @ValidEmailDomain(allowed = {"example.com", "mycorp.co"}, ignoreCase = true)
 * private String corporateEmail;
 *
 * @ValidEmailDomain(allowed = {"company.com"})
 * private String workEmail;
 * }</pre>
 *
 * @see EmailDomainValidator
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EmailDomainValidator.class)
public @interface ValidEmailDomain {
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

    /**
     * The list of allowed email domains.
     *
     * @return the array of allowed email domains
     */
    String[] allowed();

    /**
     * Whether the comparison should be case-insensitive.
     *
     * @return the whether the comparison should be case-insensitive
     */
    boolean ignoreCase() default true;
}
