package id.xtramile.validator.validator.datetime;

import id.xtramile.validator.annotation.datetime.DateAfter;
import id.xtramile.validator.enums.DatePrecision;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DateAfterValidatorTest {

    @DateAfter(first = "endDate", second = "startDate")
        private record DateAfterDummy(String startDate, String endDate) {
    }

    @DateAfter(first = "endDate", second = "startDate", maxDistance = 30, precision = DatePrecision.DAYS)
        private record DateAfterWithDistanceDummy(String startDate, String endDate) {
    }

    @DateAfter(first = "endDate", second = "startDate", maxDistance = 24, precision = DatePrecision.HOURS)
        private record DateAfterWithHoursDummy(String startDate, String endDate) {
    }

    @DateAfter(first = "endDate", second = "startDate", maxDistance = 60, precision = DatePrecision.MINUTES)
        private record DateAfterWithMinutesDummy(String startDate, String endDate) {
    }

    @DateAfter(first = "endDate", second = "startDate", maxDistance = 3600, precision = DatePrecision.SECONDS)
        private record DateAfterWithSecondsDummy(String startDate, String endDate) {
    }

    @DateAfter(first = "endDate", second = "startDate", pattern = "dd/MM/yyyy HH:mm:ss")
        private record DateAfterCustomPatternDummy(String startDate, String endDate) {
    }

    private DateAfterValidator validator;

    @BeforeEach
    void setUp() {
        validator = new DateAfterValidator();
        validator.initialize(DateAfterDummy.class.getAnnotation(DateAfter.class));
    }

    @Test
    void testValidDateAfter() {
        assertTrue(validator.isValid(new DateAfterDummy("2023-01-01 10:00:00", "2023-01-01 11:00:00"), null));
        assertTrue(validator.isValid(new DateAfterDummy("2023-01-01 00:00:00", "2023-01-02 00:00:00"), null));
        assertTrue(validator.isValid(new DateAfterDummy("2023-01-01 10:00:00", "2023-12-31 23:59:59"), null));
        assertTrue(validator.isValid(new DateAfterDummy("2023-06-15 12:30:00", "2023-06-15 12:30:01"), null)); // 1 second difference
    }

    @Test
    void testInvalidDateNotAfter() {
        assertFalse(validator.isValid(new DateAfterDummy("2023-01-01 11:00:00", "2023-01-01 10:00:00"), null));
        assertFalse(validator.isValid(new DateAfterDummy("2023-01-02 00:00:00", "2023-01-01 00:00:00"), null));
        assertFalse(validator.isValid(new DateAfterDummy("2023-12-31 23:59:59", "2023-01-01 10:00:00"), null));
        assertFalse(validator.isValid(new DateAfterDummy("2023-01-01 10:00:00", "2023-01-01 10:00:00"), null)); // Equal dates
    }

    @Test
    void testNullAndBlankValues() {
        assertTrue(validator.isValid(new DateAfterDummy(null, null), null));
        assertTrue(validator.isValid(new DateAfterDummy("", ""), null));
        assertTrue(validator.isValid(new DateAfterDummy("   ", "   "), null));
        assertTrue(validator.isValid(new DateAfterDummy(null, "2023-01-01 11:00:00"), null));
        assertTrue(validator.isValid(new DateAfterDummy("2023-01-01 10:00:00", null), null));
        assertTrue(validator.isValid(null, null)); // Null bean
    }

    @Test
    void testInvalidDateFormats() {
        assertFalse(validator.isValid(new DateAfterDummy("invalid-date", "2023-01-01 11:00:00"), null));
        assertFalse(validator.isValid(new DateAfterDummy("2023-01-01 10:00:00", "invalid-date"), null));
        assertFalse(validator.isValid(new DateAfterDummy("2023-13-01 10:00:00", "2023-01-01 11:00:00"), null)); // Invalid month
        assertFalse(validator.isValid(new DateAfterDummy("2023-01-01 10:00:00", "2023-01-32 11:00:00"), null)); // Invalid day
        assertFalse(validator.isValid(new DateAfterDummy("2023-01-01 24:00:00", "2023-01-01 11:00:00"), null)); // Invalid hour
        assertFalse(validator.isValid(new DateAfterDummy("2023-01-01 10:60:00", "2023-01-01 11:00:00"), null)); // Invalid minute
        assertFalse(validator.isValid(new DateAfterDummy("2023-01-01 10:00:60", "2023-01-01 11:00:00"), null)); // Invalid second
    }

    @Test
    void testMaxDistanceWithDays() {
        validator.initialize(DateAfterWithDistanceDummy.class.getAnnotation(DateAfter.class));
        
        // Valid: within 30 days
        assertTrue(validator.isValid(new DateAfterWithDistanceDummy("2023-01-01 10:00:00", "2023-01-31 10:00:00"), null)); // Exactly 30 days
        assertTrue(validator.isValid(new DateAfterWithDistanceDummy("2023-01-01 10:00:00", "2023-01-15 10:00:00"), null)); // 14 days
        
        // Invalid: exceeds 30 days
        assertFalse(validator.isValid(new DateAfterWithDistanceDummy("2023-01-01 10:00:00", "2023-02-01 10:00:00"), null)); // 31 days
        assertFalse(validator.isValid(new DateAfterWithDistanceDummy("2023-01-01 10:00:00", "2023-02-05 10:00:00"), null)); // 35 days
    }

    @Test
    void testMaxDistanceWithHours() {
        validator.initialize(DateAfterWithHoursDummy.class.getAnnotation(DateAfter.class));
        
        // Valid: within 24 hours
        assertTrue(validator.isValid(new DateAfterWithHoursDummy("2023-01-01 10:00:00", "2023-01-02 10:00:00"), null)); // Exactly 24 hours
        assertTrue(validator.isValid(new DateAfterWithHoursDummy("2023-01-01 10:00:00", "2023-01-01 12:00:00"), null)); // 2 hours
        
        // Invalid: exceeds 24 hours
        assertFalse(validator.isValid(new DateAfterWithHoursDummy("2023-01-01 10:00:00", "2023-01-02 11:00:00"), null)); // 25 hours
        assertFalse(validator.isValid(new DateAfterWithHoursDummy("2023-01-01 10:00:00", "2023-01-03 10:00:00"), null)); // 48 hours
    }

    @Test
    void testMaxDistanceWithMinutes() {
        validator.initialize(DateAfterWithMinutesDummy.class.getAnnotation(DateAfter.class));
        
        // Valid: within 60 minutes
        assertTrue(validator.isValid(new DateAfterWithMinutesDummy("2023-01-01 10:00:00", "2023-01-01 11:00:00"), null)); // Exactly 60 minutes
        assertTrue(validator.isValid(new DateAfterWithMinutesDummy("2023-01-01 10:00:00", "2023-01-01 10:30:00"), null)); // 30 minutes
        
        // Invalid: exceeds 60 minutes
        assertFalse(validator.isValid(new DateAfterWithMinutesDummy("2023-01-01 10:00:00", "2023-01-01 11:01:00"), null)); // 61 minutes
        assertFalse(validator.isValid(new DateAfterWithMinutesDummy("2023-01-01 10:00:00", "2023-01-01 12:00:00"), null)); // 120 minutes
    }

    @Test
    void testMaxDistanceWithSeconds() {
        validator.initialize(DateAfterWithSecondsDummy.class.getAnnotation(DateAfter.class));
        
        // Valid: within 3600 seconds (1 hour)
        assertTrue(validator.isValid(new DateAfterWithSecondsDummy("2023-01-01 10:00:00", "2023-01-01 11:00:00"), null)); // Exactly 3600 seconds
        assertTrue(validator.isValid(new DateAfterWithSecondsDummy("2023-01-01 10:00:00", "2023-01-01 10:30:00"), null)); // 1800 seconds
        
        // Invalid: exceeds 3600 seconds
        assertFalse(validator.isValid(new DateAfterWithSecondsDummy("2023-01-01 10:00:00", "2023-01-01 11:00:01"), null)); // 3601 seconds
        assertFalse(validator.isValid(new DateAfterWithSecondsDummy("2023-01-01 10:00:00", "2023-01-01 11:30:00"), null)); // 5400 seconds
    }

    @Test
    void testCustomPattern() {
        validator.initialize(DateAfterCustomPatternDummy.class.getAnnotation(DateAfter.class));
        
        assertTrue(validator.isValid(new DateAfterCustomPatternDummy("01/01/2023 10:00:00", "01/01/2023 11:00:00"), null));
        assertTrue(validator.isValid(new DateAfterCustomPatternDummy("15/06/2023 12:30:00", "16/06/2023 12:30:00"), null));
        
        assertFalse(validator.isValid(new DateAfterCustomPatternDummy("01/01/2023 11:00:00", "01/01/2023 10:00:00"), null));
        assertFalse(validator.isValid(new DateAfterCustomPatternDummy("2023-01-01 10:00:00", "2023-01-01 11:00:00"), null)); // Wrong format
    }

    @Test
    void testLeapYear() {
        assertTrue(validator.isValid(new DateAfterDummy("2024-02-28 10:00:00", "2024-02-29 10:00:00"), null)); // Leap year
        assertTrue(validator.isValid(new DateAfterDummy("2024-02-29 10:00:00", "2024-03-01 10:00:00"), null)); // Leap year day
        
        assertFalse(validator.isValid(new DateAfterDummy("2023-02-28 10:00:00", "2023-02-29 10:00:00"), null)); // Not leap year
    }

    @Test
    void testEdgeCases() {
        // Same date, different times
        assertTrue(validator.isValid(new DateAfterDummy("2023-01-01 10:00:00", "2023-01-01 10:00:01"), null)); // 1 second difference
        assertTrue(validator.isValid(new DateAfterDummy("2023-01-01 10:00:00", "2023-01-01 10:01:00"), null)); // 1 minute difference
        
        // Different dates
        assertTrue(validator.isValid(new DateAfterDummy("2023-01-01 23:59:59", "2023-01-02 00:00:00"), null)); // End of day to start of next
        assertTrue(validator.isValid(new DateAfterDummy("2023-12-31 23:59:59", "2024-01-01 00:00:00"), null)); // Year boundary
    }

    @Test
    void testDistanceAtBoundary() {
        validator.initialize(DateAfterWithDistanceDummy.class.getAnnotation(DateAfter.class));
        
        // Exactly at the boundary (30 days)
        assertTrue(validator.isValid(new DateAfterWithDistanceDummy("2023-01-01 10:00:00", "2023-01-31 10:00:00"), null));
        
        // With DAYS precision, 30 days + 1 second still counts as 30 days (truncated)
        // So this is still valid
        assertTrue(validator.isValid(new DateAfterWithDistanceDummy("2023-01-01 10:00:00", "2023-01-31 10:00:01"), null)); // 30 days + 1 second
        
        // Actually over the boundary (31 days)
        assertFalse(validator.isValid(new DateAfterWithDistanceDummy("2023-01-01 10:00:00", "2023-02-01 10:00:00"), null)); // 31 days
    }

    @Test
    void testNonStringFields() {
        // Non-string fields should be considered valid (validator returns true for non-strings)
        @DateAfter(first = "field1", second = "field2")
                record NonStringDummy(Integer field1, Integer field2) {
        }
        
        validator.initialize(NonStringDummy.class.getAnnotation(DateAfter.class));
        assertTrue(validator.isValid(new NonStringDummy(1, 2), null));
        assertTrue(validator.isValid(new NonStringDummy(null, null), null));
    }

    @Test
    void testRealWorldScenarios() {
        // Event booking: start before end
        assertTrue(validator.isValid(new DateAfterDummy("2023-06-15 09:00:00", "2023-06-15 17:00:00"), null)); // 8-hour event
        assertTrue(validator.isValid(new DateAfterDummy("2023-06-15 09:00:00", "2023-06-16 17:00:00"), null)); // Multi-day event
        
        // Document expiry: expiry after issue
        assertTrue(validator.isValid(new DateAfterDummy("2023-01-01 00:00:00", "2025-12-31 23:59:59"), null)); // Long validity
        assertTrue(validator.isValid(new DateAfterDummy("2023-01-01 00:00:00", "2023-01-02 00:00:00"), null)); // Short validity
    }

    @Test
    void testVerySmallTimeDifferences() {
        // Test with very small differences (seconds)
        assertTrue(validator.isValid(new DateAfterDummy("2023-01-01 10:00:00", "2023-01-01 10:00:01"), null)); // 1 second
        assertTrue(validator.isValid(new DateAfterDummy("2023-01-01 10:00:00", "2023-01-01 10:00:30"), null)); // 30 seconds
        
        // Test precision with seconds
        validator.initialize(DateAfterWithSecondsDummy.class.getAnnotation(DateAfter.class));
        assertTrue(validator.isValid(new DateAfterWithSecondsDummy("2023-01-01 10:00:00", "2023-01-01 10:00:30"), null)); // 30 seconds
        assertFalse(validator.isValid(new DateAfterWithSecondsDummy("2023-01-01 10:00:00", "2023-01-01 11:00:01"), null)); // 3601 seconds
    }

    @Test
    void testWhitespaceHandling() {
        // Leading/trailing spaces should be invalid
        assertFalse(validator.isValid(new DateAfterDummy(" 2023-01-01 10:00:00", "2023-01-01 11:00:00"), null)); // Leading space
        assertFalse(validator.isValid(new DateAfterDummy("2023-01-01 10:00:00", "2023-01-01 11:00:00 "), null)); // Trailing space
        assertFalse(validator.isValid(new DateAfterDummy("2023-01-01 10:00:00", "2023-01-01  11:00:00"), null)); // Double space
    }
}

