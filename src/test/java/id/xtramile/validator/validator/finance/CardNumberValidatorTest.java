package id.xtramile.validator.validator.finance;

import id.xtramile.validator.annotation.finance.ValidCardNumber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CardNumberValidatorTest {

    private static class CardNumberDummy {
        @ValidCardNumber
        String defaultCard;

        @ValidCardNumber()
        String stripSeparatorsCard;

        @ValidCardNumber(stripSeparators = false)
        String noStripCard;
    }

    private CardNumberValidator validator;

    private static ValidCardNumber getAnnotation(String fieldName) {
        try {
            Field f = CardNumberDummy.class.getDeclaredField(fieldName);
            return f.getAnnotation(ValidCardNumber.class);
        } catch (NoSuchFieldException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
        validator = new CardNumberValidator();
    }

    @Test
    void testValidCardNumbers() {
        validator.initialize(getAnnotation("defaultCard"));

        // Valid Luhn algorithm test numbers
        assertTrue(validator.isValid("4532015112830366", null)); // Visa
        assertTrue(validator.isValid("5555555555554444", null)); // Mastercard
        assertTrue(validator.isValid("378282246310005", null));  // American Express
        assertTrue(validator.isValid("6011111111111117", null)); // Discover
    }

    @Test
    void testValidCardNumbersWithSeparators() {
        validator.initialize(getAnnotation("stripSeparatorsCard"));

        assertTrue(validator.isValid("4532-0151-1283-0366", null));
        assertTrue(validator.isValid("5555 5555 5555 4444", null));
        assertTrue(validator.isValid("3782-8224-6310-005", null));
        assertTrue(validator.isValid("6011 1111 1111 1117", null));
    }

    @Test
    void testInvalidCardNumbers() {
        validator.initialize(getAnnotation("defaultCard"));

        assertFalse(validator.isValid("4532015112830367", null)); // Invalid Luhn
        assertFalse(validator.isValid("1234567890123456", null)); // Invalid Luhn
        assertFalse(validator.isValid("453201511283036", null));  // Too short
        assertFalse(validator.isValid("45320151128303666", null)); // Too long
    }

    @Test
    void testNoStripSeparators() {
        validator.initialize(getAnnotation("noStripCard"));

        assertTrue(validator.isValid("4532015112830366", null)); // No separators
        assertFalse(validator.isValid("4532-0151-1283-0366", null)); // With separators (not stripped)
        assertFalse(validator.isValid("5555 5555 5555 4444", null)); // With spaces (not stripped)
    }

    @Test
    void testInvalidLengths() {
        validator.initialize(getAnnotation("defaultCard"));

        assertFalse(validator.isValid("12345678901", null)); // 11 digits
        assertFalse(validator.isValid("12345678901234567890", null)); // 20 digits
        assertFalse(validator.isValid("123456789012345678901", null)); // 21 digits
    }

    @Test
    void testNonNumericCharacters() {
        validator.initialize(getAnnotation("defaultCard"));

        assertFalse(validator.isValid("4532a15112830366", null));
        assertTrue(validator.isValid("4532-0151-1283-0366", null));
        assertTrue(validator.isValid("4532 0151 1283 0366", null));
    }

    @Test
    void testEdgeCaseLengths() {
        validator.initialize(getAnnotation("defaultCard"));

        // Test minimum length (12 digits) - using valid Luhn numbers
        assertTrue(validator.isValid("400000000002", null)); // Valid 12-digit number
        // Test maximum length (19 digits) - using valid Luhn numbers  
        assertTrue(validator.isValid("4000000000000000006", null)); // Valid 19-digit number
    }

    @Test
    void testLuhnAlgorithmEdgeCases() {
        validator.initialize(getAnnotation("defaultCard"));

        // Test numbers that should pass Luhn algorithm
        assertTrue(validator.isValid("4111111111111111", null)); // Valid test number
        assertTrue(validator.isValid("4000000000000002", null)); // Valid test number
        
        // Test numbers that should fail Luhn algorithm
        assertFalse(validator.isValid("4111111111111112", null)); // Invalid test number
        assertFalse(validator.isValid("4000000000000001", null)); // Invalid test number
    }

    @Test
    void testMixedSeparators() {
        validator.initialize(getAnnotation("stripSeparatorsCard"));

        assertTrue(validator.isValid("4532-0151 1283-0366", null)); // Mixed separators
        assertTrue(validator.isValid("5555 5555-5555 4444", null)); // Mixed separators
    }

    @Test
    void testBlankString() {
        validator.initialize(getAnnotation("defaultCard"));

        assertTrue(validator.isValid("", null));
        assertTrue(validator.isValid("   ", null));
    }
}
