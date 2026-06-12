package id.xtramile.validator.validator.finance;

import id.xtramile.validator.annotation.finance.ValidCurrencyCode;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Currency;

import static id.xtramile.validator.util.ValidatorUtils.isBlank;

/**
 * Validator implementation for {@link ValidCurrencyCode} annotation.
 * <p>
 * Validates ISO 4217 currency codes using Java's Currency class.
 * This validator ensures the currency code follows the international standard
 * format and is recognized by the Java platform.
 * 
 * <p>The validator performs the following operations:
 * <ul>
 * <li>Accepts null/blank values as valid</li>
 * <li>Converts input to uppercase for validation</li>
 * <li>Uses Currency.getInstance() for validation</li>
 * <li>Handles validation exceptions gracefully</li>
 * </ul>
 * 
 * @see ValidCurrencyCode
 */
public class CurrencyCodeValidator implements ConstraintValidator<ValidCurrencyCode, String> {

    /**
     * Validates the currency code against ISO 4217 standard.
     * @param value the currency code string to validate
     * @param context the constraint validator context
     * @return true if the currency code is valid or is null/blank
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (isBlank(value)) return true;

        try {
            Currency.getInstance(value.toUpperCase());
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
