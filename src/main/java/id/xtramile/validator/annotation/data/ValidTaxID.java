package id.xtramile.validator.annotation.data;

import id.xtramile.validator.validator.data.TaxIdValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates a tax identification number for the configured country.
 * <p>
 * Currently supported: ID (Indonesia), accepts 15–16 digits (non-digits ignored before length check).
 * Null/blank values are considered valid.
 *
 * <p>Example usage:
 * <pre>{@code
 * @ValidTaxID // defaults to ID (Indonesia)
 * private String npwp;
 *
 * @ValidTaxID(country = "ID")
 * private String taxId; // Indonesian NPWP
 * }</pre>
 *
 * @see TaxIdValidator
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TaxIdValidator.class)
public @interface ValidTaxID {
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
     * The country code for tax ID validation.
     *
     * @return the country code for tax ID validation
     */
    String country() default "ID";
}
