package id.xtramile.validator.validator.location;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RtRwValidatorTest {

    private RtRwValidator validator;

    @BeforeEach
    void setUp() {
        validator = new RtRwValidator();
    }

    @Test
    void testValidRtRwValues() {
        assertTrue(validator.isValid("000", null)); // Minimum value
        assertTrue(validator.isValid("999", null)); // Maximum value
        assertTrue(validator.isValid("123", null)); // Random valid value
        assertTrue(validator.isValid("456", null)); // Another valid value
        assertTrue(validator.isValid("789", null)); // Another valid value
        assertTrue(validator.isValid("001", null)); // Leading zero
        assertTrue(validator.isValid("010", null)); // Leading zero
        assertTrue(validator.isValid("100", null)); // No leading zero
    }

    @Test
    void testInvalidRtRwValues() {
        assertFalse(validator.isValid("12", null)); // Too short
        assertFalse(validator.isValid("1234", null)); // Too long
        assertFalse(validator.isValid("12a", null)); // Contains letter
        assertFalse(validator.isValid("a12", null)); // Starts with letter
        assertFalse(validator.isValid("1a2", null)); // Letter in middle
        assertFalse(validator.isValid("12-", null)); // Contains hyphen
        assertFalse(validator.isValid("12 ", null)); // Contains space
        assertFalse(validator.isValid("12.", null)); // Contains dot
    }

    @Test
    void testEdgeCaseLengths() {
        assertFalse(validator.isValid("1", null)); // Single digit
        assertFalse(validator.isValid("12", null)); // Two digits
        assertTrue(validator.isValid("123", null)); // Three digits (valid)
        assertFalse(validator.isValid("1234", null)); // Four digits
        assertFalse(validator.isValid("12345", null)); // Five digits
        assertFalse(validator.isValid("123456", null)); // Six digits
    }

    @Test
    void testNumericBoundaries() {
        assertTrue(validator.isValid("000", null)); // Minimum value
        assertTrue(validator.isValid("999", null)); // Maximum value
        assertTrue(validator.isValid("500", null)); // Middle value
        assertTrue(validator.isValid("001", null)); // Just above minimum
        assertTrue(validator.isValid("998", null)); // Just below maximum
    }

    @Test
    void testSpecialCharacters() {
        assertFalse(validator.isValid("123+", null)); // Plus sign
        assertFalse(validator.isValid("123-", null)); // Minus sign
        assertFalse(validator.isValid("123%", null)); // Percentage
        assertFalse(validator.isValid("123#", null)); // Hash symbol
        assertFalse(validator.isValid("123@", null)); // At symbol
        assertFalse(validator.isValid("123&", null)); // Ampersand
        assertFalse(validator.isValid("123*", null)); // Asterisk
        assertFalse(validator.isValid("123(", null)); // Parenthesis
    }

    @Test
    void testWhitespaceHandling() {
        assertTrue(validator.isValid(" 123 ", null)); // Leading and trailing spaces
        assertTrue(validator.isValid("\t123\t", null)); // Tabs
        assertTrue(validator.isValid("\n123\n", null)); // Newlines
        assertTrue(validator.isValid("  123  ", null)); // Multiple spaces
    }

    @Test
    void testMixedCharacters() {
        assertFalse(validator.isValid("12a", null)); // Letter at end
        assertFalse(validator.isValid("a12", null)); // Letter at start
        assertFalse(validator.isValid("1a2", null)); // Letter in middle
        assertFalse(validator.isValid("1a3", null)); // Letter in middle
        assertFalse(validator.isValid("a23", null)); // Letter at start
        assertFalse(validator.isValid("12a", null)); // Letter at end
    }

    @Test
    void testCommonInvalidFormats() {
        assertFalse(validator.isValid("123-456", null)); // Hyphenated format
        assertFalse(validator.isValid("123 456", null)); // Space separated
        assertFalse(validator.isValid("123.456", null)); // Dot separated
        assertFalse(validator.isValid("123/456", null)); // Slash separated
        assertFalse(validator.isValid("123\\456", null)); // Backslash separated
    }

    @Test
    void testRealWorldExamples() {
        assertTrue(validator.isValid("001", null)); // Common RT/RW number
        assertTrue(validator.isValid("010", null)); // Common RT/RW number
        assertTrue(validator.isValid("100", null)); // Common RT/RW number
        assertTrue(validator.isValid("500", null)); // Common RT/RW number
        assertTrue(validator.isValid("999", null)); // Common RT/RW number
    }

    @Test
    void testLeadingZeros() {
        assertTrue(validator.isValid("000", null)); // All zeros
        assertTrue(validator.isValid("001", null)); // Leading zeros
        assertTrue(validator.isValid("010", null)); // Leading zeros
        assertTrue(validator.isValid("100", null)); // No leading zeros
        assertTrue(validator.isValid("999", null)); // No leading zeros
    }

    @Test
    void testNumericRange() {
        assertTrue(validator.isValid("000", null)); // Minimum
        assertTrue(validator.isValid("001", null)); // Just above minimum
        assertTrue(validator.isValid("100", null)); // Middle range
        assertTrue(validator.isValid("500", null)); // Middle range
        assertTrue(validator.isValid("998", null)); // Just below maximum
        assertTrue(validator.isValid("999", null)); // Maximum
    }

    @Test
    void testInvalidNumericFormats() {
        assertFalse(validator.isValid("12.3", null)); // Decimal
        assertFalse(validator.isValid("12,3", null)); // Comma decimal
        assertFalse(validator.isValid("12e3", null)); // Scientific notation
        assertFalse(validator.isValid("12E3", null)); // Scientific notation uppercase
        assertFalse(validator.isValid("0x123", null)); // Hexadecimal
        assertFalse(validator.isValid("0b101", null)); // Binary
    }
}
