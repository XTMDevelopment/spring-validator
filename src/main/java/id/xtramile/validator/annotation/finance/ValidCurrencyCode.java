package id.xtramile.validator.annotation.finance;

import id.xtramile.validator.validator.finance.CurrencyCodeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates a 3-letter ISO 4217 currency code (e.g., "USD", "EUR", "IDR").
 * <p>
 * Null/blank values are considered valid. This validator ensures the currency code
 * follows the ISO 4217 standard format.
 *
 * <p>Example usage:
 * <pre>{@code
 * @ValidCurrencyCode
 * private String currency; // e.g., USD, EUR, IDR
 *
 * @ValidCurrencyCode
 * private String baseCurrency; // e.g., USD
 * }</pre>
 *
 * @see CurrencyCodeValidator
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CurrencyCodeValidator.class)
public @interface ValidCurrencyCode {
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
