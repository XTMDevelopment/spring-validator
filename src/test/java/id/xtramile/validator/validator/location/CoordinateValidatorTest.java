package id.xtramile.validator.validator.location;

import id.xtramile.validator.annotation.location.ValidCoordinates;
import id.xtramile.validator.support.ValidatorTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CoordinateValidatorTest {

    private static class CoordinateDummy {
        @ValidCoordinates
        String defaultCoordinates;

        @ValidCoordinates(flipCoordinates = true)
        String flippedCoordinates;
    }

    private CoordinateValidator validator;

    private static ValidCoordinates getAnnotation(String fieldName) {
        try {
            Field f = CoordinateDummy.class.getDeclaredField(fieldName);
            return f.getAnnotation(ValidCoordinates.class);
        } catch (NoSuchFieldException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
        validator = new CoordinateValidator();
        ValidatorTestSupport.initializeValidator(validator, CoordinateDummy.class, "defaultCoordinates", ValidCoordinates.class);
    }

    @Test
    void testValidDefaultCoordinates() {
        validator.initialize(getAnnotation("defaultCoordinates"));

        assertTrue(validator.isValid("0,0", null)); // Equator, Prime Meridian
        assertTrue(validator.isValid("45.5,120.5", null)); // Valid lat,lon
        assertTrue(validator.isValid("-45.5,-120.5", null)); // Valid negative lat,lon
        assertTrue(validator.isValid("90,180", null)); // North Pole, Date Line
        assertTrue(validator.isValid("-90,-180", null)); // South Pole, Date Line
        assertTrue(validator.isValid("89.999,179.999", null)); // Near boundaries
        assertTrue(validator.isValid("-89.999,-179.999", null)); // Near boundaries
    }

    @Test
    void testValidFlippedCoordinates() {
        validator.initialize(getAnnotation("flippedCoordinates"));

        assertTrue(validator.isValid("0,0", null)); // Equator, Prime Meridian
        assertTrue(validator.isValid("120.5,45.5", null)); // Valid lon,lat
        assertTrue(validator.isValid("-120.5,-45.5", null)); // Valid negative lon,lat
        assertTrue(validator.isValid("180,90", null)); // Date Line, North Pole
        assertTrue(validator.isValid("-180,-90", null)); // Date Line, South Pole
        assertTrue(validator.isValid("179.999,89.999", null)); // Near boundaries
        assertTrue(validator.isValid("-179.999,-89.999", null)); // Near boundaries
    }

    @Test
    void testInvalidDefaultCoordinates() {
        validator.initialize(getAnnotation("defaultCoordinates"));

        assertFalse(validator.isValid("91,180", null)); // Invalid latitude
        assertFalse(validator.isValid("-91,-180", null)); // Invalid latitude
        assertFalse(validator.isValid("45,181", null)); // Invalid longitude
        assertFalse(validator.isValid("-45,-181", null)); // Invalid longitude
        assertFalse(validator.isValid("91,181", null)); // Both invalid
        assertFalse(validator.isValid("-91,-181", null)); // Both invalid
    }

    @Test
    void testInvalidFlippedCoordinates() {
        validator.initialize(getAnnotation("flippedCoordinates"));

        assertFalse(validator.isValid("180,91", null)); // Invalid latitude (flipped)
        assertFalse(validator.isValid("-180,-91", null)); // Invalid latitude (flipped)
        assertFalse(validator.isValid("181,45", null)); // Invalid longitude (flipped)
        assertFalse(validator.isValid("-181,-45", null)); // Invalid longitude (flipped)
        assertFalse(validator.isValid("181,91", null)); // Both invalid (flipped)
        assertFalse(validator.isValid("-181,-91", null)); // Both invalid (flipped)
    }

    @Test
    void testInvalidFormat() {
        validator.initialize(getAnnotation("defaultCoordinates"));

        assertFalse(validator.isValid("45.5", null)); // Single coordinate
        assertFalse(validator.isValid("45.5,120.5,30.5", null)); // Three coordinates
        assertFalse(validator.isValid("45.5;120.5", null)); // Wrong separator
        assertFalse(validator.isValid("45.5 120.5", null)); // Space separator
        assertFalse(validator.isValid("45.5|120.5", null)); // Pipe separator
    }

    @Test
    void testInvalidCoordinateFormat() {
        validator.initialize(getAnnotation("defaultCoordinates"));

        assertFalse(validator.isValid("abc,def", null)); // Non-numeric
        assertFalse(validator.isValid("45.5.5,120.5", null)); // Multiple decimals
        assertFalse(validator.isValid("45.5,120.5.5", null)); // Multiple decimals
        assertFalse(validator.isValid("45,5,120,5", null)); // Comma in number
        assertFalse(validator.isValid("45°30',120°30'", null)); // Degree symbols
        assertFalse(validator.isValid("45N,120E", null)); // Direction indicators
    }

    @Test
    void testWhitespaceHandling() {
        validator.initialize(getAnnotation("defaultCoordinates"));

        assertTrue(validator.isValid(" 45.5 , 120.5 ", null)); // Spaces around comma
        assertTrue(validator.isValid("\t45.5\t,\t120.5\t", null)); // Tabs
        assertTrue(validator.isValid("\n45.5\n,\n120.5\n", null)); // Newlines
        assertTrue(validator.isValid("  45.5  ,  120.5  ", null)); // Multiple spaces
    }

    @Test
    void testBoundaryValues() {
        validator.initialize(getAnnotation("defaultCoordinates"));

        assertTrue(validator.isValid("90,180", null)); // Maximum values
        assertTrue(validator.isValid("-90,-180", null)); // Minimum values
        assertTrue(validator.isValid("90.0,180.0", null)); // Maximum with decimal
        assertTrue(validator.isValid("-90.0,-180.0", null)); // Minimum with decimal
        assertFalse(validator.isValid("90.000001,180.000001", null)); // Just over maximum
        assertFalse(validator.isValid("-90.000001,-180.000001", null)); // Just under minimum
    }

    @Test
    void testScientificNotation() {
        validator.initialize(getAnnotation("defaultCoordinates"));

        assertTrue(validator.isValid("9.0E1,1.8E2", null)); // 90,180 in scientific notation
        assertTrue(validator.isValid("-9.0E1,-1.8E2", null)); // -90,-180 in scientific notation
        assertTrue(validator.isValid("4.5E1,1.2E2", null)); // 45,120 in scientific notation
        assertTrue(validator.isValid("-4.5E1,-1.2E2", null)); // -45,-120 in scientific notation
    }

    @Test
    void testHighPrecision() {
        validator.initialize(getAnnotation("defaultCoordinates"));

        assertTrue(validator.isValid("45.123456789,120.123456789", null)); // High precision
        assertTrue(validator.isValid("-45.123456789,-120.123456789", null)); // High precision negative
        assertTrue(validator.isValid("89.999999999,179.999999999", null)); // High precision near boundaries
        assertTrue(validator.isValid("-89.999999999,-179.999999999", null)); // High precision near boundaries
    }

    @Test
    void testCommonCoordinatePairs() {
        validator.initialize(getAnnotation("defaultCoordinates"));

        assertTrue(validator.isValid("0,0", null)); // Greenwich
        assertTrue(validator.isValid("-6.2088,106.8456", null)); // Jakarta
        assertTrue(validator.isValid("40.7128,-74.0060", null)); // New York
        assertTrue(validator.isValid("48.8566,2.3522", null)); // Paris
        assertTrue(validator.isValid("35.6762,139.6503", null)); // Tokyo
        assertTrue(validator.isValid("51.5074,-0.1278", null)); // London
    }

    @Test
    void testFlippedCommonCoordinates() {
        validator.initialize(getAnnotation("flippedCoordinates"));

        assertTrue(validator.isValid("0,0", null)); // Greenwich (same when flipped)
        assertTrue(validator.isValid("106.8456,-6.2088", null)); // Jakarta (flipped)
        assertTrue(validator.isValid("-74.0060,40.7128", null)); // New York (flipped)
        assertTrue(validator.isValid("2.3522,48.8566", null)); // Paris (flipped)
        assertTrue(validator.isValid("139.6503,35.6762", null)); // Tokyo (flipped)
        assertTrue(validator.isValid("-0.1278,51.5074", null)); // London (flipped)
    }
}
