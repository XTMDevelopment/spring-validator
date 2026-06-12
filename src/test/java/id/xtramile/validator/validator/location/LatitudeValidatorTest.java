package id.xtramile.validator.validator.location;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LatitudeValidatorTest {

    private LatitudeValidator validator;

    @BeforeEach
    void setUp() {
        validator = new LatitudeValidator();
    }

    @Test
    void testValidLatitudeValues() {
        assertTrue(validator.isValid("0", null)); // Equator
        assertTrue(validator.isValid("90", null)); // North Pole
        assertTrue(validator.isValid("-90", null)); // South Pole
        assertTrue(validator.isValid("45.5", null)); // Valid decimal
        assertTrue(validator.isValid("-45.5", null)); // Valid negative decimal
        assertTrue(validator.isValid("89.999", null)); // Just below North Pole
        assertTrue(validator.isValid("-89.999", null)); // Just above South Pole
    }

    @Test
    void testInvalidLatitudeValues() {
        assertFalse(validator.isValid("90.1", null)); // Above North Pole
        assertFalse(validator.isValid("-90.1", null)); // Below South Pole
        assertFalse(validator.isValid("91", null)); // Above North Pole
        assertFalse(validator.isValid("-91", null)); // Below South Pole
        assertFalse(validator.isValid("180", null)); // Invalid latitude
        assertFalse(validator.isValid("-180", null)); // Invalid latitude
        assertFalse(validator.isValid("360", null)); // Invalid latitude
        assertFalse(validator.isValid("-360", null)); // Invalid latitude
    }

    @Test
    void testInvalidFormatValues() {
        assertFalse(validator.isValid("abc", null)); // Non-numeric
        assertFalse(validator.isValid("45.5.5", null)); // Multiple decimals
        assertFalse(validator.isValid("45,5", null)); // Comma instead of decimal
        assertFalse(validator.isValid("45 30", null)); // Space in number
        assertFalse(validator.isValid("45°", null)); // Degree symbol
        assertFalse(validator.isValid("45N", null)); // Direction indicator
        assertFalse(validator.isValid("45.5.5.5", null)); // Multiple decimals
    }

    @Test
    void testEdgeCaseValues() {
        assertTrue(validator.isValid("0.0", null)); // Zero with decimal
        assertTrue(validator.isValid("0.000001", null)); // Very small decimal
        assertTrue(validator.isValid("-0.000001", null)); // Very small negative decimal
        assertTrue(validator.isValid("89.999999", null)); // Very close to North Pole
        assertTrue(validator.isValid("-89.999999", null)); // Very close to South Pole
    }

    @Test
    void testBoundaryValues() {
        assertTrue(validator.isValid("90.0", null)); // Exactly North Pole
        assertTrue(validator.isValid("-90.0", null)); // Exactly South Pole
        assertFalse(validator.isValid("90.000001", null)); // Just above North Pole
        assertFalse(validator.isValid("-90.000001", null)); // Just below South Pole
    }

    @Test
    void testScientificNotation() {
        assertTrue(validator.isValid("9.0E1", null)); // 90 in scientific notation
        assertTrue(validator.isValid("-9.0E1", null)); // -90 in scientific notation
        assertFalse(validator.isValid("1.0E2", null)); // 100 in scientific notation (invalid)
        assertFalse(validator.isValid("-1.0E2", null)); // -100 in scientific notation (invalid)
    }

    @Test
    void testLeadingTrailingSpaces() {
        assertTrue(validator.isValid(" 45.5 ", null)); // Leading and trailing spaces
        assertTrue(validator.isValid("  90  ", null)); // Multiple spaces
        assertTrue(validator.isValid("\t45.5\t", null)); // Tabs
        assertTrue(validator.isValid("\n45.5\n", null)); // Newlines
    }

    @Test
    void testSpecialCharacters() {
        assertFalse(validator.isValid("45.5+", null)); // Plus sign
        assertFalse(validator.isValid("45.5-", null)); // Minus in wrong position
        assertFalse(validator.isValid("45.5%", null)); // Percentage
        assertFalse(validator.isValid("45.5#", null)); // Hash symbol
    }

    @Test
    void testVeryLargeNumbers() {
        assertFalse(validator.isValid("999999", null)); // Very large number
        assertFalse(validator.isValid("-999999", null)); // Very large negative number
        assertFalse(validator.isValid("1000000", null)); // Million
        assertFalse(validator.isValid("-1000000", null)); // Negative million
    }

    @Test
    void testDecimalPrecision() {
        assertTrue(validator.isValid("45.123456789", null)); // High precision
        assertTrue(validator.isValid("-45.123456789", null)); // High precision negative
        assertTrue(validator.isValid("89.999999999", null)); // High precision near pole
        assertTrue(validator.isValid("-89.999999999", null)); // High precision near pole
    }
}
