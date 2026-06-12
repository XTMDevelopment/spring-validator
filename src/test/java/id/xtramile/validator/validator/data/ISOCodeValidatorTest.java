package id.xtramile.validator.validator.data;

import id.xtramile.validator.annotation.data.ValidISOCode;
import id.xtramile.validator.enums.ISOType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ISOCodeValidatorTest {

    private static class ISOCodeDummy {
        @ValidISOCode(value = ISOType.CURRENCY)
        String currencyCode;

        @ValidISOCode(value = ISOType.COUNTRY_ALPHA2)
        String countryAlpha2Code;

        @ValidISOCode(value = ISOType.COUNTRY_ALPHA3)
        String countryAlpha3Code;

        @ValidISOCode(value = ISOType.LANGUAGE)
        String languageCode;
    }

    private ISOCodeValidator validator;

    private static ValidISOCode getAnnotation(String fieldName) {
        try {
            Field f = ISOCodeDummy.class.getDeclaredField(fieldName);
            return f.getAnnotation(ValidISOCode.class);
        } catch (NoSuchFieldException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
        validator = new ISOCodeValidator();
    }

    @Test
    void testValidCurrencyCodes() {
        validator.initialize(getAnnotation("currencyCode"));

        assertTrue(validator.isValid("USD", null)); // US Dollar
        assertTrue(validator.isValid("EUR", null)); // Euro
        assertTrue(validator.isValid("GBP", null)); // British Pound
        assertTrue(validator.isValid("JPY", null)); // Japanese Yen
        assertTrue(validator.isValid("IDR", null)); // Indonesian Rupiah
        assertTrue(validator.isValid("usd", null)); // case insensitive
        assertTrue(validator.isValid("Usd", null)); // mixed case
    }

    @Test
    void testInvalidCurrencyCodes() {
        validator.initialize(getAnnotation("currencyCode"));

        assertFalse(validator.isValid("XYZ", null)); // non-existent currency
        assertFalse(validator.isValid("US", null)); // too short
        assertFalse(validator.isValid("USDD", null)); // too long
    }

    @Test
    void testValidCountryAlpha2Codes() {
        validator.initialize(getAnnotation("countryAlpha2Code"));

        assertTrue(validator.isValid("US", null)); // United States
        assertTrue(validator.isValid("ID", null)); // Indonesia
        assertTrue(validator.isValid("GB", null)); // United Kingdom
        assertTrue(validator.isValid("JP", null)); // Japan
        assertTrue(validator.isValid("us", null)); // case insensitive
        assertTrue(validator.isValid("Us", null)); // mixed case
    }

    @Test
    void testInvalidCountryAlpha2Codes() {
        validator.initialize(getAnnotation("countryAlpha2Code"));

        assertFalse(validator.isValid("XX", null)); // non-existent country
        assertFalse(validator.isValid("U", null)); // too short
        assertFalse(validator.isValid("USA", null)); // too long
    }

    @Test
    void testValidCountryAlpha3Codes() {
        validator.initialize(getAnnotation("countryAlpha3Code"));

        assertTrue(validator.isValid("USA", null)); // United States
        assertTrue(validator.isValid("IDN", null)); // Indonesia
        assertTrue(validator.isValid("GBR", null)); // United Kingdom
        assertTrue(validator.isValid("JPN", null)); // Japan
        assertTrue(validator.isValid("usa", null)); // case insensitive
        assertTrue(validator.isValid("Usa", null)); // mixed case
    }

    @Test
    void testInvalidCountryAlpha3Codes() {
        validator.initialize(getAnnotation("countryAlpha3Code"));

        assertFalse(validator.isValid("XXX", null)); // non-existent country
        assertFalse(validator.isValid("US", null)); // too short
        assertFalse(validator.isValid("USAA", null)); // too long
    }

    @Test
    void testValidLanguageCodes() {
        validator.initialize(getAnnotation("languageCode"));

        assertTrue(validator.isValid("en", null)); // English
        assertTrue(validator.isValid("id", null)); // Indonesian
        assertTrue(validator.isValid("es", null)); // Spanish
        assertTrue(validator.isValid("fr", null)); // French
        assertTrue(validator.isValid("de", null)); // German
        assertTrue(validator.isValid("EN", null)); // case insensitive
        assertTrue(validator.isValid("En", null)); // mixed case
    }

    @Test
    void testInvalidLanguageCodes() {
        validator.initialize(getAnnotation("languageCode"));

        assertFalse(validator.isValid("xx", null)); // non-existent language
        assertFalse(validator.isValid("e", null)); // too short
        assertFalse(validator.isValid("eng", null)); // too long
    }

    @Test
    void testBlankValues() {
        validator.initialize(getAnnotation("currencyCode"));

        assertTrue(validator.isValid("", null));
        assertTrue(validator.isValid("   ", null));
        assertTrue(validator.isValid(null, null));
    }

    @Test
    void testCaseInsensitiveValidation() {
        validator.initialize(getAnnotation("currencyCode"));

        assertTrue(validator.isValid("USD", null));
        assertTrue(validator.isValid("usd", null));
        assertTrue(validator.isValid("Usd", null));
        assertTrue(validator.isValid("uSd", null));
    }

    @Test
    void testCommonValidCodes() {
        // Test some common valid codes for each type
        validator.initialize(getAnnotation("currencyCode"));
        assertTrue(validator.isValid("USD", null));
        assertTrue(validator.isValid("EUR", null));

        validator.initialize(getAnnotation("countryAlpha2Code"));
        assertTrue(validator.isValid("US", null));
        assertTrue(validator.isValid("ID", null));

        validator.initialize(getAnnotation("countryAlpha3Code"));
        assertTrue(validator.isValid("USA", null));
        assertTrue(validator.isValid("IDN", null));

        validator.initialize(getAnnotation("languageCode"));
        assertTrue(validator.isValid("en", null));
        assertTrue(validator.isValid("id", null));
    }
}
