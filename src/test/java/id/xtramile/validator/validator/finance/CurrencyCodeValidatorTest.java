package id.xtramile.validator.validator.finance;

import id.xtramile.validator.annotation.finance.ValidCurrencyCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CurrencyCodeValidatorTest {

    private static class CurrencyCodeDummy {
        @ValidCurrencyCode
        String defaultCurrency;
    }

    private CurrencyCodeValidator validator;

    private static ValidCurrencyCode getAnnotation(String fieldName) {
        try {
            Field f = CurrencyCodeDummy.class.getDeclaredField(fieldName);
            return f.getAnnotation(ValidCurrencyCode.class);
        } catch (NoSuchFieldException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
        validator = new CurrencyCodeValidator();
    }

    @Test
    void testValidCurrencyCodes() {
        validator.initialize(getAnnotation("defaultCurrency"));

        assertTrue(validator.isValid("USD", null)); // US Dollar
        assertTrue(validator.isValid("EUR", null)); // Euro
        assertTrue(validator.isValid("GBP", null)); // British Pound
        assertTrue(validator.isValid("JPY", null)); // Japanese Yen
        assertTrue(validator.isValid("CAD", null)); // Canadian Dollar
        assertTrue(validator.isValid("AUD", null)); // Australian Dollar
        assertTrue(validator.isValid("CHF", null)); // Swiss Franc
        assertTrue(validator.isValid("CNY", null)); // Chinese Yuan
    }

    @Test
    void testCaseInsensitiveCurrencyCodes() {
        validator.initialize(getAnnotation("defaultCurrency"));

        assertTrue(validator.isValid("usd", null)); // Lowercase
        assertTrue(validator.isValid("Usd", null)); // Mixed case
        assertTrue(validator.isValid("USD", null)); // Uppercase
        assertTrue(validator.isValid("eur", null)); // Lowercase
        assertTrue(validator.isValid("Eur", null)); // Mixed case
        assertTrue(validator.isValid("EUR", null)); // Uppercase
    }

    @Test
    void testInvalidCurrencyCodes() {
        validator.initialize(getAnnotation("defaultCurrency"));

        assertFalse(validator.isValid("XYZ", null)); // Non-existent currency
        assertFalse(validator.isValid("ABC", null)); // Non-existent currency
        assertFalse(validator.isValid("123", null)); // Numeric code
        assertFalse(validator.isValid("US", null)); // Too short
        assertFalse(validator.isValid("USDD", null)); // Too long
    }

    @Test
    void testCommonCurrencyCodes() {
        validator.initialize(getAnnotation("defaultCurrency"));

        // Major currencies
        assertTrue(validator.isValid("USD", null)); // US Dollar
        assertTrue(validator.isValid("EUR", null)); // Euro
        assertTrue(validator.isValid("GBP", null)); // British Pound Sterling
        assertTrue(validator.isValid("JPY", null)); // Japanese Yen
        assertTrue(validator.isValid("CHF", null)); // Swiss Franc
        assertTrue(validator.isValid("CAD", null)); // Canadian Dollar
        assertTrue(validator.isValid("AUD", null)); // Australian Dollar
        assertTrue(validator.isValid("NZD", null)); // New Zealand Dollar
    }

    @Test
    void testAsianCurrencyCodes() {
        validator.initialize(getAnnotation("defaultCurrency"));

        assertTrue(validator.isValid("CNY", null)); // Chinese Yuan
        assertTrue(validator.isValid("KRW", null)); // South Korean Won
        assertTrue(validator.isValid("SGD", null)); // Singapore Dollar
        assertTrue(validator.isValid("HKD", null)); // Hong Kong Dollar
        assertTrue(validator.isValid("INR", null)); // Indian Rupee
        assertTrue(validator.isValid("THB", null)); // Thai Baht
    }

    @Test
    void testEuropeanCurrencyCodes() {
        validator.initialize(getAnnotation("defaultCurrency"));

        assertTrue(validator.isValid("EUR", null)); // Euro
        assertTrue(validator.isValid("GBP", null)); // British Pound
        assertTrue(validator.isValid("CHF", null)); // Swiss Franc
        assertTrue(validator.isValid("SEK", null)); // Swedish Krona
        assertTrue(validator.isValid("NOK", null)); // Norwegian Krone
        assertTrue(validator.isValid("DKK", null)); // Danish Krone
    }

    @Test
    void testMiddleEastCurrencyCodes() {
        validator.initialize(getAnnotation("defaultCurrency"));

        assertTrue(validator.isValid("AED", null)); // UAE Dirham
        assertTrue(validator.isValid("SAR", null)); // Saudi Riyal
        assertTrue(validator.isValid("QAR", null)); // Qatari Riyal
        assertTrue(validator.isValid("KWD", null)); // Kuwaiti Dinar
        assertTrue(validator.isValid("BHD", null)); // Bahraini Dinar
    }

    @Test
    void testAfricanCurrencyCodes() {
        validator.initialize(getAnnotation("defaultCurrency"));

        assertTrue(validator.isValid("ZAR", null)); // South African Rand
        assertTrue(validator.isValid("EGP", null)); // Egyptian Pound
        assertTrue(validator.isValid("NGN", null)); // Nigerian Naira
        assertTrue(validator.isValid("KES", null)); // Kenyan Shilling
    }

    @Test
    void testSouthAmericanCurrencyCodes() {
        validator.initialize(getAnnotation("defaultCurrency"));

        assertTrue(validator.isValid("BRL", null)); // Brazilian Real
        assertTrue(validator.isValid("ARS", null)); // Argentine Peso
        assertTrue(validator.isValid("CLP", null)); // Chilean Peso
        assertTrue(validator.isValid("COP", null)); // Colombian Peso
        assertTrue(validator.isValid("MXN", null)); // Mexican Peso
    }

    @Test
    void testInvalidFormatCodes() {
        validator.initialize(getAnnotation("defaultCurrency"));

        assertFalse(validator.isValid("12", null)); // Numeric
        assertFalse(validator.isValid("US", null)); // Too short
        assertFalse(validator.isValid("USDD", null)); // Too long
        assertFalse(validator.isValid("U$D", null)); // Special character
        assertFalse(validator.isValid("US-D", null)); // Hyphen
        assertFalse(validator.isValid("U S D", null)); // Spaces
    }

    @Test
    void testBlankString() {
        validator.initialize(getAnnotation("defaultCurrency"));

        assertTrue(validator.isValid("", null));
        assertTrue(validator.isValid("   ", null));
    }

    @Test
    void testWhitespaceHandling() {
        validator.initialize(getAnnotation("defaultCurrency"));

        assertFalse(validator.isValid(" USD", null)); // Leading space
        assertFalse(validator.isValid("USD ", null)); // Trailing space
        assertFalse(validator.isValid("U S D", null)); // Spaces between letters
    }

    @Test
    void testSpecialCharacters() {
        validator.initialize(getAnnotation("defaultCurrency"));

        assertFalse(validator.isValid("US$", null)); // Dollar sign
        assertFalse(validator.isValid("€UR", null)); // Euro sign
        assertFalse(validator.isValid("£BP", null)); // Pound sign
        assertFalse(validator.isValid("¥PY", null)); // Yen sign
    }
}
