package id.xtramile.validator.validator.location;

import id.xtramile.validator.annotation.location.ValidPostalCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PostalCodeValidatorTest {

    private static class PostalCodeDummy {
        @ValidPostalCode
        String defaultPostalCode;

        @ValidPostalCode()
        String indonesiaPostalCode;

        @ValidPostalCode(country = "MY")
        String malaysiaPostalCode;

        @ValidPostalCode()
        Integer intIndonesiaPostalCode;

        @ValidPostalCode(country = "US")
        String unsupportedCountry;
    }

    private PostalCodeValidator validator;

    private static ValidPostalCode getAnnotation(String fieldName) {
        try {
            Field f = PostalCodeDummy.class.getDeclaredField(fieldName);
            return f.getAnnotation(ValidPostalCode.class);
        } catch (NoSuchFieldException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
        validator = new PostalCodeValidator();
    }

    @Test
    void testValidIndonesiaPostalCodes() {
        validator.initialize(getAnnotation("indonesiaPostalCode"));

        assertTrue(validator.isValid("12345", null)); // Valid 5-digit postal code
        assertTrue(validator.isValid("00000", null)); // All zeros
        assertTrue(validator.isValid("99999", null)); // All nines
        assertTrue(validator.isValid("12345", null)); // Random valid code
        assertTrue(validator.isValid("54321", null)); // Another valid code
    }

    @Test
    void testValidMalaysiaPostalCodes() {
        validator.initialize(getAnnotation("malaysiaPostalCode"));

        assertTrue(validator.isValid("12345", null)); // Valid 5-digit postal code
        assertTrue(validator.isValid("00000", null)); // All zeros
        assertTrue(validator.isValid("99999", null)); // All nines
        assertTrue(validator.isValid("12345", null)); // Random valid code
        assertTrue(validator.isValid("54321", null)); // Another valid code
    }

    @Test
    void testValidIndonesiaPostalCodesInteger() {
        validator.initialize(getAnnotation("intIndonesiaPostalCode"));

        assertTrue(validator.isValid(12345, null)); // Valid 5-digit postal code
        assertTrue(validator.isValid(0, null)); // All zeros
        assertTrue(validator.isValid(99999, null)); // All nines
        assertTrue(validator.isValid(12345, null)); // Random valid code
        assertTrue(validator.isValid(54321, null)); // Another valid code
    }

    @Test
    void testValidDefaultPostalCodes() {
        validator.initialize(getAnnotation("defaultPostalCode"));

        assertTrue(validator.isValid("12345", null)); // Valid 5-digit postal code (defaults to ID)
        assertTrue(validator.isValid("00000", null)); // All zeros
        assertTrue(validator.isValid("99999", null)); // All nines
    }

    @Test
    void testInvalidIndonesiaPostalCodes() {
        validator.initialize(getAnnotation("indonesiaPostalCode"));

        assertFalse(validator.isValid("1234", null)); // Too short
        assertFalse(validator.isValid("123456", null)); // Too long
        assertFalse(validator.isValid("1234a", null)); // Contains letter
        assertFalse(validator.isValid("a1234", null)); // Starts with letter
        assertFalse(validator.isValid("12-34", null)); // Contains hyphen
        assertFalse(validator.isValid("12 34", null)); // Contains space
        assertFalse(validator.isValid("12.34", null)); // Contains dot
    }

    @Test
    void testInvalidMalaysiaPostalCodes() {
        validator.initialize(getAnnotation("malaysiaPostalCode"));

        assertFalse(validator.isValid("1234", null)); // Too short
        assertFalse(validator.isValid("123456", null)); // Too long
        assertFalse(validator.isValid("1234a", null)); // Contains letter
        assertFalse(validator.isValid("a1234", null)); // Starts with letter
        assertFalse(validator.isValid("12-34", null)); // Contains hyphen
        assertFalse(validator.isValid("12 34", null)); // Contains space
        assertFalse(validator.isValid("12.34", null)); // Contains dot
    }

    @Test
    void testUnsupportedCountry() {
        validator.initialize(getAnnotation("unsupportedCountry"));

        assertFalse(validator.isValid("12345", null)); // Valid format but unsupported country
        assertFalse(validator.isValid("00000", null)); // Valid format but unsupported country
        assertFalse(validator.isValid("99999", null)); // Valid format but unsupported country
    }

    @Test
    void testWhitespaceHandling() {
        validator.initialize(getAnnotation("indonesiaPostalCode"));

        assertTrue(validator.isValid(" 12345 ", null)); // Leading and trailing spaces
        assertTrue(validator.isValid("\t12345\t", null)); // Tabs
        assertTrue(validator.isValid("\n12345\n", null)); // Newlines
        assertTrue(validator.isValid("  12345  ", null)); // Multiple spaces
    }

    @Test
    void testSpecialCharacters() {
        validator.initialize(getAnnotation("indonesiaPostalCode"));

        assertFalse(validator.isValid("12345+", null)); // Plus sign
        assertFalse(validator.isValid("12345-", null)); // Minus sign
        assertFalse(validator.isValid("12345%", null)); // Percentage
        assertFalse(validator.isValid("12345#", null)); // Hash symbol
        assertFalse(validator.isValid("12345@", null)); // At symbol
        assertFalse(validator.isValid("12345&", null)); // Ampersand
    }

    @Test
    void testMixedCharacters() {
        validator.initialize(getAnnotation("indonesiaPostalCode"));

        assertFalse(validator.isValid("1234a", null)); // Letter at end
        assertFalse(validator.isValid("a1234", null)); // Letter at start
        assertFalse(validator.isValid("12a34", null)); // Letter in middle
        assertFalse(validator.isValid("1a234", null)); // Letter in middle
        assertFalse(validator.isValid("123a4", null)); // Letter in middle
    }

    @Test
    void testEdgeCaseLengths() {
        validator.initialize(getAnnotation("indonesiaPostalCode"));

        assertFalse(validator.isValid("1", null)); // Single digit
        assertFalse(validator.isValid("12", null)); // Two digits
        assertFalse(validator.isValid("123", null)); // Three digits
        assertFalse(validator.isValid("1234", null)); // Four digits
        assertTrue(validator.isValid("12345", null)); // Five digits (valid)
        assertFalse(validator.isValid("123456", null)); // Six digits
        assertFalse(validator.isValid("1234567", null)); // Seven digits
        assertFalse(validator.isValid("12345678", null)); // Eight digits
    }

    @Test
    void testNumericBoundaries() {
        validator.initialize(getAnnotation("indonesiaPostalCode"));

        assertTrue(validator.isValid("00000", null)); // Minimum value
        assertTrue(validator.isValid("99999", null)); // Maximum value
        assertTrue(validator.isValid("12345", null)); // Middle value
        assertTrue(validator.isValid("50000", null)); // Half value
    }

    @Test
    void testCommonInvalidFormats() {
        validator.initialize(getAnnotation("indonesiaPostalCode"));

        assertFalse(validator.isValid("12345-6789", null)); // US format
        assertFalse(validator.isValid("SW1A 1AA", null)); // UK format
        assertFalse(validator.isValid("12345-1234", null)); // Extended format
        assertFalse(validator.isValid("12345 1234", null)); // Space separated
        assertFalse(validator.isValid("12345.1234", null)); // Dot separated
    }

    @Test
    void testRealWorldExamples() {
        validator.initialize(getAnnotation("indonesiaPostalCode"));

        assertTrue(validator.isValid("12110", null)); // Jakarta postal code
        assertTrue(validator.isValid("40111", null)); // Bandung postal code
        assertTrue(validator.isValid("55161", null)); // Yogyakarta postal code
        assertTrue(validator.isValid("80234", null)); // Bali postal code
        assertTrue(validator.isValid("60111", null)); // Surabaya postal code
    }
}
