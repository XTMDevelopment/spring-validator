package id.xtramile.validator.annotation.data;

import id.xtramile.validator.enums.ISOType;
import id.xtramile.validator.validator.data.ISOCodeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates common ISO codes depending on the selected type.
 * <p>
 * Supports various ISO standards including currency codes, country codes, and language codes.
 * Null/blank values are considered valid.
 *
 * <p>Supported ISO types:
 * <ul>
 * <li>CURRENCY: ISO 4217 currency code (e.g., USD, IDR)</li>
 * <li>COUNTRY_ALPHA2: ISO 3166-1 alpha-2 country code (e.g., US, ID)</li>
 * <li>COUNTRY_ALPHA3: ISO 3166-1 alpha-3 country code (e.g., USA, IDN)</li>
 * <li>LANGUAGE: ISO 639 language code (e.g., en, id)</li>
 * </ul>
 *
 * <p>Example usage:
 * <pre>{@code
 * @ValidISOCode(ISOType.CURRENCY)
 * private String currencyCode; // e.g., IDR
 *
 * @ValidISOCode(ISOType.COUNTRY_ALPHA2)
 * private String country; // e.g., ID
 * }</pre>
 *
 * @see ISOCodeValidator
 * @see ISOType
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ISOCodeValidator.class)
public @interface ValidISOCode {
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
     * The ISO standard type to validate against.
     *
     * @return the ISO standard type to validate against
     */
    ISOType value();
}
