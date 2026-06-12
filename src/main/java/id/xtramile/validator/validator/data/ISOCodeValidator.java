package id.xtramile.validator.validator.data;

import id.xtramile.validator.annotation.data.ValidISOCode;
import id.xtramile.validator.enums.Group;
import id.xtramile.validator.enums.ISOType;
import id.xtramile.validator.util.MessageUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.Currency;
import java.util.Locale;

import static id.xtramile.validator.util.ValidatorUtils.isBlank;

/**
 * Validator implementation for {@link ValidISOCode} annotation.
 * <p>
 * Validates common ISO codes depending on the selected type.
 * This validator ensures the string follows the correct ISO standard format
 * for currency codes, country codes, or language codes.
 *
 * <p>The validator performs the following operations:
 * <ul>
 * <li>Accepts null/blank values as valid</li>
 * <li>Validates currency codes against ISO 4217 standard</li>
 * <li>Validates country codes against ISO 3166-1 standard</li>
 * <li>Validates language codes against ISO 639 standard</li>
 * </ul>
 *
 * @see ValidISOCode
 * @see ISOType
 */
public class ISOCodeValidator implements ConstraintValidator<ValidISOCode, String> {
    private ISOType type;

    /**
     * Initializes the validator with the annotation parameters.
     *
     * @param annotation the ValidISOCode annotation instance
     */
    @Override
    public void initialize(ValidISOCode annotation) {
        this.type = annotation.value();
    }

    /**
     * Validates the ISO code against the configured standard.
     *
     * @param value   the ISO code string to validate
     * @param context the constraint validator context
     * @return true if the code is valid according to the ISO standard or is null/blank
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (isBlank(value)) return true;

        boolean isValid;
        String messageKey = switch (type) {
            case CURRENCY -> {
                isValid = checkCurrency(value);
                yield "iso-code.currency";
            }
            case COUNTRY_ALPHA2 -> {
                isValid = checkCountryAlpha2(value);
                yield "iso-code.country";
            }
            case COUNTRY_ALPHA3 -> {
                isValid = checkCountryAlpha3(value);
                yield "iso-code.country";
            }
            case LANGUAGE -> {
                isValid = checkLanguage(value);
                yield "iso-code.language";
            }
            default -> {
                isValid = false;
                yield "iso-code";
            }
        };

        if (!isValid) {
            MessageUtils.buildViolation(context, Group.DATA, messageKey);
        }

        return isValid;
    }

    private boolean checkCurrency(String value) {
        return Currency.getAvailableCurrencies()
                .stream()
                .anyMatch(cur -> cur.getCurrencyCode().equalsIgnoreCase(value));
    }

    private boolean checkCountryAlpha2(String value) {
        return Arrays.stream(Locale.getISOCountries())
                .anyMatch(cc -> cc.equalsIgnoreCase(value));
    }

    private boolean checkCountryAlpha3(String value) {
        return Arrays.stream(Locale.getISOCountries())
                .map(code -> new Locale("", code).getISO3Country())
                .anyMatch(cc3 -> cc3.equalsIgnoreCase(value));
    }

    private boolean checkLanguage(String value) {
        return Arrays.stream(Locale.getISOLanguages())
                .anyMatch(lang -> lang.equalsIgnoreCase(value));
    }
}
