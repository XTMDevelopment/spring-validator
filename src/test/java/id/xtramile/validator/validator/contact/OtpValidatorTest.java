package id.xtramile.validator.validator.contact;

import id.xtramile.validator.annotation.contact.ValidOtp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OtpValidatorTest {

    private OtpValidator validator;

    private static ValidOtp getAnnotation(String fieldName) {
        try {
            Field f = OtpDummy.class.getDeclaredField(fieldName);
            return f.getAnnotation(ValidOtp.class);
        } catch (NoSuchFieldException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
        validator = new OtpValidator();
    }

    @Test
    void testValidDefaultOtp() {
        validator.initialize(getAnnotation("defaultOtp"));

        assertTrue(validator.isValid("123456", null)); // exactly 6 digits
        assertTrue(validator.isValid("000000", null)); // all zeros
        assertTrue(validator.isValid("999999", null)); // all nines
        assertTrue(validator.isValid("123456", null)); // mixed digits
    }

    @Test
    void testInvalidDefaultOtp() {
        validator.initialize(getAnnotation("defaultOtp"));

        assertFalse(validator.isValid("12345", null)); // too short (5 < 6)
        assertFalse(validator.isValid("1234567", null)); // too long (7 > 6)
        assertFalse(validator.isValid("12345a", null)); // contains letter
        assertFalse(validator.isValid("12345-", null)); // contains special character
        assertFalse(validator.isValid("12345 ", null)); // contains space
    }

    @Test
    void testFourDigitOtp() {
        validator.initialize(getAnnotation("fourDigitOtp"));

        assertTrue(validator.isValid("1234", null)); // exactly 4 digits
        assertTrue(validator.isValid("0000", null)); // all zeros
        assertTrue(validator.isValid("9999", null)); // all nines

        assertFalse(validator.isValid("123", null)); // too short (3 < 4)
        assertFalse(validator.isValid("12345", null)); // too long (5 > 4)
        assertFalse(validator.isValid("123a", null)); // contains letter
    }

    @Test
    void testEightDigitOtp() {
        validator.initialize(getAnnotation("eightDigitOtp"));

        assertTrue(validator.isValid("12345678", null)); // exactly 8 digits
        assertTrue(validator.isValid("00000000", null)); // all zeros
        assertTrue(validator.isValid("99999999", null)); // all nines

        assertFalse(validator.isValid("1234567", null)); // too short (7 < 8)
        assertFalse(validator.isValid("123456789", null)); // too long (9 > 8)
        assertFalse(validator.isValid("1234567a", null)); // contains letter
    }

    @Test
    void testAlphanumericOtp() {
        validator.initialize(getAnnotation("alphanumericOtp"));

        assertTrue(validator.isValid("123456", null)); // all digits
        assertTrue(validator.isValid("abcdef", null)); // all letters
        assertTrue(validator.isValid("ABC123", null)); // mixed case
        assertTrue(validator.isValid("a1b2c3", null)); // alternating
        assertTrue(validator.isValid("A1B2C3", null)); // uppercase alternating

        assertFalse(validator.isValid("12345", null)); // too short (5 < 6)
        assertFalse(validator.isValid("1234567", null)); // too long (7 > 6)
        assertFalse(validator.isValid("12345-", null)); // contains special character
        assertFalse(validator.isValid("12345 ", null)); // contains space
    }

    @Test
    void testFourCharOtp() {
        validator.initialize(getAnnotation("fourCharOtp"));

        assertTrue(validator.isValid("1234", null)); // all digits
        assertTrue(validator.isValid("abcd", null)); // all letters
        assertTrue(validator.isValid("A1B2", null)); // mixed case
        assertTrue(validator.isValid("a1b2", null)); // lowercase mixed

        assertFalse(validator.isValid("123", null)); // too short (3 < 4)
        assertFalse(validator.isValid("12345", null)); // too long (5 > 4)
        assertFalse(validator.isValid("123-", null)); // contains special character
    }

    @Test
    void testBlankValues() {
        validator.initialize(getAnnotation("defaultOtp"));

        assertTrue(validator.isValid("", null));
        assertTrue(validator.isValid("   ", null));
        assertTrue(validator.isValid(null, null));
    }

    @Test
    void testEdgeCases() {
        validator.initialize(getAnnotation("defaultOtp"));

        // Test with leading/trailing spaces
        assertFalse(validator.isValid(" 123456", null)); // leading space
        assertFalse(validator.isValid("123456 ", null)); // trailing space
        assertFalse(validator.isValid(" 123456 ", null)); // both spaces

        // Test with special characters
        assertFalse(validator.isValid("12345-", null)); // hyphen
        assertFalse(validator.isValid("12345.", null)); // dot
        assertFalse(validator.isValid("12345@", null)); // at symbol
        assertFalse(validator.isValid("12345#", null)); // hash
    }

    @Test
    void testAlphanumericEdgeCases() {
        validator.initialize(getAnnotation("alphanumericOtp"));

        // Test case sensitivity
        assertTrue(validator.isValid("ABC123", null)); // uppercase
        assertTrue(validator.isValid("abc123", null)); // lowercase
        assertTrue(validator.isValid("AbC123", null)); // mixed case

        // Test with spaces and special characters
        assertFalse(validator.isValid("ABC12 ", null)); // trailing space
        assertFalse(validator.isValid(" ABC123", null)); // leading space
        assertFalse(validator.isValid("ABC-123", null)); // hyphen
        assertFalse(validator.isValid("ABC.123", null)); // dot
    }

    @Test
    void testRealWorldExamples() {
        validator.initialize(getAnnotation("defaultOtp"));

        // Common OTP examples
        assertTrue(validator.isValid("123456", null)); // 6-digit OTP
        assertTrue(validator.isValid("000000", null)); // all zeros
        assertTrue(validator.isValid("999999", null)); // all nines

        // Invalid examples
        assertFalse(validator.isValid("12345", null)); // too short
        assertFalse(validator.isValid("1234567", null)); // too long
        assertFalse(validator.isValid("12345a", null)); // contains letter
    }

    @Test
    void testAlphanumericRealWorldExamples() {
        validator.initialize(getAnnotation("alphanumericOtp"));

        // Common alphanumeric OTP examples
        assertTrue(validator.isValid("ABC123", null)); // uppercase letters + numbers
        assertTrue(validator.isValid("abc123", null)); // lowercase letters + numbers
        assertTrue(validator.isValid("123ABC", null)); // numbers + uppercase letters
        assertTrue(validator.isValid("A1B2C3", null)); // alternating pattern

        // Invalid examples
        assertFalse(validator.isValid("ABC12", null)); // too short
        assertFalse(validator.isValid("ABC1234", null)); // too long
        assertFalse(validator.isValid("ABC-123", null)); // contains special character
    }

    @Test
    void testLengthVariations() {
        // Test different length configurations
        validator.initialize(getAnnotation("fourDigitOtp"));
        assertTrue(validator.isValid("1234", null));
        assertFalse(validator.isValid("123", null));
        assertFalse(validator.isValid("12345", null));

        validator.initialize(getAnnotation("eightDigitOtp"));
        assertTrue(validator.isValid("12345678", null));
        assertFalse(validator.isValid("1234567", null));
        assertFalse(validator.isValid("123456789", null));
    }

    private static class OtpDummy {
        @ValidOtp
        String defaultOtp;

        @ValidOtp(length = 4)
        String fourDigitOtp;

        @ValidOtp(length = 8)
        String eightDigitOtp;

        @ValidOtp(alphabets = true)
        String alphanumericOtp;

        @ValidOtp(length = 4, alphabets = true)
        String fourCharOtp;
    }
}
