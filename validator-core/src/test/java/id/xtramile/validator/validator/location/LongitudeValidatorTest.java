package id.xtramile.validator.validator.location;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LongitudeValidatorTest {

    private LongitudeValidator validator;

    @BeforeEach
    void setUp() {
        validator = new LongitudeValidator();
    }

    @Test
    void testValidLongitudeValues() {
        assertTrue(validator.isValid("0", null)); // Prime Meridian
        assertTrue(validator.isValid("180", null)); // International Date Line
        assertTrue(validator.isValid("-180", null)); // International Date Line
        assertTrue(validator.isValid("45.5", null)); // Valid decimal
        assertTrue(validator.isValid("-45.5", null)); // Valid negative decimal
        assertTrue(validator.isValid("179.999", null)); // Just below 180
        assertTrue(validator.isValid("-179.999", null)); // Just above -180
    }

    @Test
    void testInvalidLongitudeValues() {
        assertFalse(validator.isValid("180.1", null)); // Above 180
        assertFalse(validator.isValid("-180.1", null)); // Below -180
        assertFalse(validator.isValid("181", null)); // Above 180
        assertFalse(validator.isValid("-181", null)); // Below -180
        assertFalse(validator.isValid("360", null)); // Invalid longitude
        assertFalse(validator.isValid("-360", null)); // Invalid longitude
        assertFalse(validator.isValid("720", null)); // Invalid longitude
        assertFalse(validator.isValid("-720", null)); // Invalid longitude
    }

    @Test
    void testInvalidFormatValues() {
        assertFalse(validator.isValid("abc", null)); // Non-numeric
        assertFalse(validator.isValid("45.5.5", null)); // Multiple decimals
        assertFalse(validator.isValid("45,5", null)); // Comma instead of decimal
        assertFalse(validator.isValid("45 30", null)); // Space in number
        assertFalse(validator.isValid("45°", null)); // Degree symbol
        assertFalse(validator.isValid("45E", null)); // Direction indicator
        assertFalse(validator.isValid("45.5.5.5", null)); // Multiple decimals
    }

    @Test
    void testEdgeCaseValues() {
        assertTrue(validator.isValid("0.0", null)); // Zero with decimal
        assertTrue(validator.isValid("0.000001", null)); // Very small decimal
        assertTrue(validator.isValid("-0.000001", null)); // Very small negative decimal
        assertTrue(validator.isValid("179.999999", null)); // Very close to 180
        assertTrue(validator.isValid("-179.999999", null)); // Very close to -180
    }

    @Test
    void testBoundaryValues() {
        assertTrue(validator.isValid("180.0", null)); // Exactly 180
        assertTrue(validator.isValid("-180.0", null)); // Exactly -180
        assertFalse(validator.isValid("180.000001", null)); // Just above 180
        assertFalse(validator.isValid("-180.000001", null)); // Just below -180
    }

    @Test
    void testScientificNotation() {
        assertTrue(validator.isValid("1.8E2", null)); // 180 in scientific notation
        assertTrue(validator.isValid("-1.8E2", null)); // -180 in scientific notation
        assertTrue(validator.isValid("1.5E2", null)); // 150 in scientific notation
        assertTrue(validator.isValid("-1.5E2", null)); // -150 in scientific notation
        assertFalse(validator.isValid("1.81E2", null)); // 181 in scientific notation (invalid)
        assertFalse(validator.isValid("-1.81E2", null)); // -181 in scientific notation (invalid)
    }

    @Test
    void testLeadingTrailingSpaces() {
        assertTrue(validator.isValid(" 45.5 ", null)); // Leading and trailing spaces
        assertTrue(validator.isValid("  180  ", null)); // Multiple spaces
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
        assertTrue(validator.isValid("179.999999999", null)); // High precision near 180
        assertTrue(validator.isValid("-179.999999999", null)); // High precision near -180
    }

    @Test
    void testCommonLongitudeValues() {
        assertTrue(validator.isValid("0", null)); // Greenwich
        assertTrue(validator.isValid("120", null)); // Jakarta
        assertTrue(validator.isValid("-74", null)); // New York
        assertTrue(validator.isValid("2.3522", null)); // Paris
        assertTrue(validator.isValid("139.6917", null)); // Tokyo
        assertTrue(validator.isValid("-0.1276", null)); // London
    }
}
