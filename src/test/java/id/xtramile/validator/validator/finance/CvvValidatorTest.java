package id.xtramile.validator.validator.finance;

import id.xtramile.validator.annotation.finance.ValidCVV;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CvvValidatorTest {

    private static class CvvDummy {
        @ValidCVV
        String defaultCvv;

        @ValidCVV()
        String allowFourDigitsCvv;

        @ValidCVV(allowFourDigits = false)
        String threeDigitsOnlyCvv;
    }

    private CvvValidator validator;

    private static ValidCVV getAnnotation(String fieldName) {
        try {
            Field f = CvvDummy.class.getDeclaredField(fieldName);
            return f.getAnnotation(ValidCVV.class);
        } catch (NoSuchFieldException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
        validator = new CvvValidator();
    }

    @Test
    void testValidThreeDigitCvv() {
        validator.initialize(getAnnotation("defaultCvv"));

        assertTrue(validator.isValid("123", null));
        assertTrue(validator.isValid("000", null));
        assertTrue(validator.isValid("999", null));
        assertTrue(validator.isValid("456", null));
    }

    @Test
    void testValidFourDigitCvv() {
        validator.initialize(getAnnotation("allowFourDigitsCvv"));

        assertTrue(validator.isValid("1234", null));
        assertTrue(validator.isValid("0000", null));
        assertTrue(validator.isValid("9999", null));
        assertTrue(validator.isValid("123", null)); // 3 digits also allowed
    }

    @Test
    void testThreeDigitsOnlyCvv() {
        validator.initialize(getAnnotation("threeDigitsOnlyCvv"));

        assertTrue(validator.isValid("123", null));
        assertTrue(validator.isValid("000", null));
        assertTrue(validator.isValid("999", null));
        assertFalse(validator.isValid("1234", null)); // 4 digits not allowed
    }

    @Test
    void testInvalidCvvLengths() {
        validator.initialize(getAnnotation("defaultCvv"));

        assertFalse(validator.isValid("12", null)); // Too short
        assertFalse(validator.isValid("1", null)); // Too short
        assertFalse(validator.isValid("12345", null)); // Too long
    }

    @Test
    void testInvalidCvvCharacters() {
        validator.initialize(getAnnotation("defaultCvv"));

        assertFalse(validator.isValid("12a", null)); // Contains letter
        assertFalse(validator.isValid("1-3", null)); // Contains dash
        assertFalse(validator.isValid("12 ", null)); // Contains space
        assertFalse(validator.isValid("12.3", null)); // Contains dot
        assertFalse(validator.isValid("abc", null)); // All letters
        assertFalse(validator.isValid("12@", null)); // Contains special character
    }

    @Test
    void testEdgeCaseLengths() {
        validator.initialize(getAnnotation("allowFourDigitsCvv"));

        assertTrue(validator.isValid("123", null)); // Exactly 3 digits
        assertTrue(validator.isValid("1234", null)); // Exactly 4 digits
        assertFalse(validator.isValid("12", null)); // 2 digits
        assertFalse(validator.isValid("12345", null)); // 5 digits
    }

    @Test
    void testZeroPaddedCvv() {
        validator.initialize(getAnnotation("defaultCvv"));

        assertTrue(validator.isValid("001", null));
        assertTrue(validator.isValid("010", null));
        assertTrue(validator.isValid("100", null));
    }

    @Test
    void testAllNinesCvv() {
        validator.initialize(getAnnotation("defaultCvv"));

        assertTrue(validator.isValid("999", null));
    }

    @Test
    void testMixedDigitCvv() {
        validator.initialize(getAnnotation("defaultCvv"));

        assertTrue(validator.isValid("123", null));
        assertTrue(validator.isValid("321", null));
        assertTrue(validator.isValid("456", null));
        assertTrue(validator.isValid("789", null));
    }

    @Test
    void testBlankString() {
        validator.initialize(getAnnotation("defaultCvv"));

        assertTrue(validator.isValid("", null));
        assertTrue(validator.isValid("   ", null));
    }

    @Test
    void testWhitespaceHandling() {
        validator.initialize(getAnnotation("defaultCvv"));

        assertFalse(validator.isValid(" 123", null)); // Leading space
        assertFalse(validator.isValid("123 ", null)); // Trailing space
        assertFalse(validator.isValid("1 23", null)); // Middle space
    }

    @Test
    void testDefaultAllowFourDigits() {
        validator.initialize(getAnnotation("defaultCvv"));

        // Default should allow 4 digits (allowFourDigits = true by default)
        assertTrue(validator.isValid("1234", null));
        assertTrue(validator.isValid("123", null));
    }

    @Test
    void testBoundaryValues() {
        validator.initialize(getAnnotation("allowFourDigitsCvv"));

        assertTrue(validator.isValid("000", null)); // Minimum 3-digit value
        assertTrue(validator.isValid("999", null)); // Maximum 3-digit value
        assertTrue(validator.isValid("0000", null)); // Minimum 4-digit value
        assertTrue(validator.isValid("9999", null)); // Maximum 4-digit value
    }
}
