package id.xtramile.validator.validator.contact;

import id.xtramile.validator.annotation.contact.ValidPhoneNumber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PhoneNumberValidatorTest {

    private PhoneNumberValidator validator;

    private static ValidPhoneNumber getAnnotation(String fieldName) {
        try {
            Field f = PhoneNumberDummy.class.getDeclaredField(fieldName);
            return f.getAnnotation(ValidPhoneNumber.class);
        } catch (NoSuchFieldException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
        validator = new PhoneNumberValidator();
    }

    @Test
    void testValidIndonesianPhoneNumbers() {
        validator.initialize(getAnnotation("phoneNumberField"));

        // Valid Indonesian MSISDN: 62 + [1-9] + 8..11 more digits (9..12 digits after 62)
        assertTrue(validator.isValid("62812345678901", null)); // 12 digits after 62 (max)
        assertTrue(validator.isValid("6281234567890", null)); // 11 digits after 62
        assertTrue(validator.isValid("628123456789", null)); // 10 digits after 62
        assertTrue(validator.isValid("62812345678", null)); // 9 digits after 62 (min)
    }

    @Test
    void testInvalidIndonesianPhoneNumbers() {
        validator.initialize(getAnnotation("phoneNumberField"));

        // Too short (less than 9 digits after 62: need [1-9] plus 8..11 further digits)
        assertFalse(validator.isValid("6281234567", null)); // 8 digits after 62
        assertFalse(validator.isValid("628123456", null)); // 7 digits after 62
        assertFalse(validator.isValid("62812345", null)); // 6 digits after 62
        assertFalse(validator.isValid("6281234", null)); // 5 digits after 62
        assertFalse(validator.isValid("628123", null)); // 4 digits after 62

        // Too long (more than 12 digits after 62)
        assertFalse(validator.isValid("628123456789012", null)); // 13 digits after 62
        assertFalse(validator.isValid("6281234567890123", null)); // 14 digits after 62

        // Doesn't start with 62
        assertFalse(validator.isValid("81234567890", null)); // missing 62
        assertFalse(validator.isValid("6181234567890", null)); // wrong country code

        // Contains non-digits
        assertFalse(validator.isValid("628123456789a", null)); // contains letter
        assertFalse(validator.isValid("628123456789-", null)); // contains hyphen
        assertFalse(validator.isValid("628123456789 ", null)); // contains space
        assertFalse(validator.isValid("628123456789.", null)); // contains dot
    }

    @Test
    void testBlankValues() {
        validator.initialize(getAnnotation("phoneNumberField"));

        assertTrue(validator.isValid("", null));
        assertTrue(validator.isValid("   ", null));
        assertTrue(validator.isValid(null, null));
    }

    @Test
    void testEdgeCases() {
        validator.initialize(getAnnotation("phoneNumberField"));

        // Test with spaces
        assertFalse(validator.isValid(" 6281234567890", null)); // leading space
        assertFalse(validator.isValid("6281234567890 ", null)); // trailing space
        assertFalse(validator.isValid("62 81234567890", null)); // space in middle

        // Test with special characters
        assertFalse(validator.isValid("+6281234567890", null)); // plus sign
        assertFalse(validator.isValid("6281234567890-", null)); // trailing hyphen
        assertFalse(validator.isValid("-6281234567890", null)); // leading hyphen
        assertFalse(validator.isValid("6281234567890.", null)); // trailing dot
    }

    @Test
    void testMinimumLength() {
        validator.initialize(getAnnotation("phoneNumberField"));

        // Minimum valid: [1-9] + 8 digits → 9 digits after 62 (11 characters total)
        assertTrue(validator.isValid("62812345678", null)); // exactly 9 digits after 62
        assertFalse(validator.isValid("6281234567", null)); // 8 digits after 62 (too short)
    }

    @Test
    void testMaximumLength() {
        validator.initialize(getAnnotation("phoneNumberField"));

        // Maximum valid: [1-9] + 11 digits → 12 digits after 62 (14 characters total)
        assertTrue(validator.isValid("62812345678901", null)); // exactly 12 digits after 62
        assertFalse(validator.isValid("628123456789012", null)); // 13 digits after 62 (too long)
    }

    @Test
    void testRealWorldExamples() {
        validator.initialize(getAnnotation("phoneNumberField"));

        // Common Indonesian mobile numbers
        assertTrue(validator.isValid("6281234567890", null)); // typical mobile
        assertTrue(validator.isValid("628123456789", null)); // shorter mobile
        assertTrue(validator.isValid("62812345678", null)); // shortest valid length

        // Invalid examples
        assertFalse(validator.isValid("81234567890", null)); // missing country code
        assertFalse(validator.isValid("+6281234567890", null)); // with plus sign
        assertFalse(validator.isValid("628123456789012", null)); // too long (13 digits after 62)
    }

    @Test
    void testBoundaryValues() {
        validator.initialize(getAnnotation("phoneNumberField"));

        // Test exact boundaries
        assertTrue(validator.isValid("62812345678", null)); // 9 digits after 62 (minimum)
        assertTrue(validator.isValid("62812345678901", null)); // 12 digits after 62 (maximum)

        // Test just outside boundaries
        assertFalse(validator.isValid("6281234567", null)); // 8 digits after 62 (1 less than minimum)
        assertFalse(validator.isValid("628123456789012", null)); // 13 digits after 62 (1 more than maximum)
    }

    @Test
    void testSpecialCases() {
        validator.initialize(getAnnotation("phoneNumberField"));

        // Test with leading zeros (should be invalid as per E.164)
        assertFalse(validator.isValid("6201234567890", null)); // starts with 0 after country code
        assertFalse(validator.isValid("620123456789", null)); // starts with 0 after country code

        // Test with all same digits
        assertTrue(validator.isValid("62111111111", null)); // all 1s
        assertFalse(validator.isValid("62000000000", null)); // all 0s (invalid due to leading zero)
    }

    @Test
    void testFormatValidation() {
        validator.initialize(getAnnotation("phoneNumberField"));

        // Valid formats
        assertTrue(validator.isValid("6281234567890", null)); // standard format
        assertTrue(validator.isValid("628123456789", null)); // shorter format
        assertTrue(validator.isValid("62812345678", null)); // minimum format

        // Invalid formats
        assertFalse(validator.isValid("628123456789012", null)); // too long
        assertFalse(validator.isValid("628123456", null)); // too short
        assertFalse(validator.isValid("81234567890", null)); // missing country code
        assertFalse(validator.isValid("6281234567890a", null)); // contains letter
    }

    private static class PhoneNumberDummy {
        @ValidPhoneNumber
        String phoneNumberField;
    }
}
