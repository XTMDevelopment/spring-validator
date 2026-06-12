package id.xtramile.validator.validator.contact;

import id.xtramile.validator.annotation.contact.ValidContactNumber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ContactNumberValidatorTest {

    private ContactNumberValidator validator;

    private static ValidContactNumber getAnnotation(String fieldName) {
        try {
            Field f = ContactNumberDummy.class.getDeclaredField(fieldName);
            return f.getAnnotation(ValidContactNumber.class);
        } catch (NoSuchFieldException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
        validator = new ContactNumberValidator();
    }

    @Test
    void testValidContactNumbers() {
        validator.initialize(getAnnotation("contactNumberField"));

        // Valid E.164-ish format: optional '+', then 7-15 digits; first digit must be 1-9
        assertTrue(validator.isValid("+6281234567890", null)); // with +, 15 digits
        assertTrue(validator.isValid("6281234567890", null)); // without +, 15 digits
        assertTrue(validator.isValid("+12025550123", null)); // US number with +
        assertTrue(validator.isValid("12025550123", null)); // US number without +
        assertTrue(validator.isValid("+1234567890", null)); // 10 digits with +
        assertTrue(validator.isValid("1234567890", null)); // 10 digits without +
        assertTrue(validator.isValid("+123456789", null)); // 9 digits with +
        assertTrue(validator.isValid("123456789", null)); // 9 digits without +
        assertTrue(validator.isValid("+12345678", null)); // 8 digits with +
        assertTrue(validator.isValid("12345678", null)); // 8 digits without +
        assertTrue(validator.isValid("+1234567", null)); // 7 digits with +
        assertTrue(validator.isValid("1234567", null)); // 7 digits without +
    }

    @Test
    void testInvalidContactNumbers() {
        validator.initialize(getAnnotation("contactNumberField"));

        // Too short (less than 7 digits)
        assertFalse(validator.isValid("+123456", null)); // 6 digits with +
        assertFalse(validator.isValid("123456", null)); // 6 digits without +
        assertFalse(validator.isValid("+12345", null)); // 5 digits with +
        assertFalse(validator.isValid("12345", null)); // 5 digits without +

        // Too long (more than 15 digits)
        assertFalse(validator.isValid("+1234567890123456", null)); // 16 digits with +
        assertFalse(validator.isValid("1234567890123456", null)); // 16 digits without +

        // Starts with 0 (invalid first digit)
        assertFalse(validator.isValid("+0123456789", null)); // starts with 0
        assertFalse(validator.isValid("0123456789", null)); // starts with 0
        assertFalse(validator.isValid("+06281234567890", null)); // starts with 0 after +
        assertFalse(validator.isValid("06281234567890", null)); // starts with 0

        // Contains non-digits
        assertFalse(validator.isValid("+123456789a", null)); // contains letter
        assertFalse(validator.isValid("123456789a", null)); // contains letter
        assertFalse(validator.isValid("+123456789-", null)); // contains hyphen
        assertFalse(validator.isValid("123456789-", null)); // contains hyphen
        assertFalse(validator.isValid("+123456789 ", null)); // contains space
        assertFalse(validator.isValid("123456789 ", null)); // contains space
    }

    @Test
    void testBlankValues() {
        validator.initialize(getAnnotation("contactNumberField"));

        assertTrue(validator.isValid("", null));
        assertTrue(validator.isValid("   ", null));
        assertTrue(validator.isValid(null, null));
    }

    @Test
    void testEdgeCases() {
        validator.initialize(getAnnotation("contactNumberField"));

        // Test with spaces
        assertFalse(validator.isValid(" +1234567890", null)); // leading space
        assertFalse(validator.isValid("+1234567890 ", null)); // trailing space
        assertFalse(validator.isValid("+123 456 7890", null)); // spaces in middle
        assertFalse(validator.isValid(" 1234567890", null)); // leading space without +
        assertFalse(validator.isValid("1234567890 ", null)); // trailing space without +

        // Test with special characters
        assertFalse(validator.isValid("+123-456-7890", null)); // hyphens
        assertFalse(validator.isValid("123-456-7890", null)); // hyphens without +
        assertFalse(validator.isValid("+123.456.7890", null)); // dots
        assertFalse(validator.isValid("123.456.7890", null)); // dots without +
        assertFalse(validator.isValid("+123(456)7890", null)); // parentheses
        assertFalse(validator.isValid("123(456)7890", null)); // parentheses without +
    }

    @Test
    void testMinimumLength() {
        validator.initialize(getAnnotation("contactNumberField"));

        // Minimum valid length (7 digits)
        assertTrue(validator.isValid("+1234567", null)); // exactly 7 digits with +
        assertTrue(validator.isValid("1234567", null)); // exactly 7 digits without +
        assertFalse(validator.isValid("+123456", null)); // 6 digits with + (too short)
        assertFalse(validator.isValid("123456", null)); // 6 digits without + (too short)
    }

    @Test
    void testMaximumLength() {
        validator.initialize(getAnnotation("contactNumberField"));

        // Maximum valid length (15 digits)
        assertTrue(validator.isValid("+123456789012345", null)); // exactly 15 digits with +
        assertTrue(validator.isValid("123456789012345", null)); // exactly 15 digits without +
        assertFalse(validator.isValid("+1234567890123456", null)); // 16 digits with + (too long)
        assertFalse(validator.isValid("1234567890123456", null)); // 16 digits without + (too long)
    }

    @Test
    void testFirstDigitValidation() {
        validator.initialize(getAnnotation("contactNumberField"));

        // Valid first digits (1-9)
        assertTrue(validator.isValid("+1234567890", null)); // starts with 1
        assertTrue(validator.isValid("+2234567890", null)); // starts with 2
        assertTrue(validator.isValid("+3234567890", null)); // starts with 3
        assertTrue(validator.isValid("+4234567890", null)); // starts with 4
        assertTrue(validator.isValid("+5234567890", null)); // starts with 5
        assertTrue(validator.isValid("+6234567890", null)); // starts with 6
        assertTrue(validator.isValid("+7234567890", null)); // starts with 7
        assertTrue(validator.isValid("+8234567890", null)); // starts with 8
        assertTrue(validator.isValid("+9234567890", null)); // starts with 9

        // Invalid first digit (0)
        assertFalse(validator.isValid("+0234567890", null)); // starts with 0
        assertFalse(validator.isValid("0234567890", null)); // starts with 0 without +
    }

    @Test
    void testRealWorldExamples() {
        validator.initialize(getAnnotation("contactNumberField"));

        // Common international numbers
        assertTrue(validator.isValid("+6281234567890", null)); // Indonesian number
        assertTrue(validator.isValid("6281234567890", null)); // Indonesian number without +
        assertTrue(validator.isValid("+12025550123", null)); // US number
        assertTrue(validator.isValid("12025550123", null)); // US number without +
        assertTrue(validator.isValid("+44123456789", null)); // UK number
        assertTrue(validator.isValid("44123456789", null)); // UK number without +

        // Invalid real-world examples
        assertFalse(validator.isValid("+06281234567890", null)); // Indonesian with leading 0
        assertFalse(validator.isValid("06281234567890", null)); // Indonesian with leading 0
        assertFalse(validator.isValid("+012025550123", null)); // US with leading 0
        assertFalse(validator.isValid("012025550123", null)); // US with leading 0
    }

    @Test
    void testBoundaryValues() {
        validator.initialize(getAnnotation("contactNumberField"));

        // Test exact boundaries
        assertTrue(validator.isValid("+1234567", null)); // 7 digits (minimum)
        assertTrue(validator.isValid("1234567", null)); // 7 digits without + (minimum)
        assertTrue(validator.isValid("+123456789012345", null)); // 15 digits (maximum)
        assertTrue(validator.isValid("123456789012345", null)); // 15 digits without + (maximum)

        // Test just outside boundaries
        assertFalse(validator.isValid("+123456", null)); // 6 digits (1 less than minimum)
        assertFalse(validator.isValid("123456", null)); // 6 digits without + (1 less than minimum)
        assertFalse(validator.isValid("+1234567890123456", null)); // 16 digits (1 more than maximum)
        assertFalse(validator.isValid("1234567890123456", null)); // 16 digits without + (1 more than maximum)
    }

    @Test
    void testSpecialCases() {
        validator.initialize(getAnnotation("contactNumberField"));

        // Test with all same digits
        assertTrue(validator.isValid("+1111111111", null)); // all 1s
        assertTrue(validator.isValid("1111111111", null)); // all 1s without +
        assertTrue(validator.isValid("+9999999999", null)); // all 9s
        assertTrue(validator.isValid("9999999999", null)); // all 9s without +

        // Test with leading zeros (should be invalid)
        assertFalse(validator.isValid("+0111111111", null)); // starts with 0
        assertFalse(validator.isValid("0111111111", null)); // starts with 0 without +
    }

    @Test
    void testFormatValidation() {
        validator.initialize(getAnnotation("contactNumberField"));

        // Valid formats
        assertTrue(validator.isValid("+1234567890", null)); // standard format with +
        assertTrue(validator.isValid("1234567890", null)); // standard format without +
        assertTrue(validator.isValid("+123456789012345", null)); // long format with +
        assertTrue(validator.isValid("123456789012345", null)); // long format without +

        // Invalid formats
        assertFalse(validator.isValid("+1234567890123456", null)); // too long
        assertFalse(validator.isValid("1234567890123456", null)); // too long without +
        assertFalse(validator.isValid("+123456", null)); // too short
        assertFalse(validator.isValid("123456", null)); // too short without +
        assertFalse(validator.isValid("+0123456789", null)); // starts with 0
        assertFalse(validator.isValid("0123456789", null)); // starts with 0 without +
    }

    private static class ContactNumberDummy {
        @ValidContactNumber
        String contactNumberField;
    }
}
