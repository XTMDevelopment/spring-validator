package id.xtramile.validator.validator.datetime;

import id.xtramile.validator.annotation.datetime.ValidDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DateTimeValidatorTest {

    private DateTimeValidator validator;

    private static ValidDateTime getAnnotation(String fieldName) {
        try {
            Field f = DateTimeDummy.class.getDeclaredField(fieldName);
            return f.getAnnotation(ValidDateTime.class);
        } catch (NoSuchFieldException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
        validator = new DateTimeValidator();
    }

    @Test
    void testValidDefaultDateTimes() {
        validator.initialize(getAnnotation("defaultDateTime"));

        assertTrue(validator.isValid("2023-12-25 10:30:00", null)); // standard format
        assertTrue(validator.isValid("2024-01-01 00:00:00", null)); // new year midnight
        assertTrue(validator.isValid("2023-02-28 23:59:59", null)); // end of day
        assertTrue(validator.isValid("2024-02-29 12:00:00", null)); // leap year
        assertTrue(validator.isValid("2023-06-15 15:45:30", null)); // mid year
    }

    @Test
    void testInvalidDefaultDateTimes() {
        validator.initialize(getAnnotation("defaultDateTime"));

        assertFalse(validator.isValid("2023-13-01 10:30:00", null)); // invalid month
        assertFalse(validator.isValid("2023-12-32 10:30:00", null)); // invalid day
        assertFalse(validator.isValid("2023-12-25 25:30:00", null)); // invalid hour
        assertFalse(validator.isValid("2023-12-25 10:60:00", null)); // invalid minute
        assertFalse(validator.isValid("2023-12-25 10:30:60", null)); // invalid second
        assertFalse(validator.isValid("2023/12/25 10:30:00", null)); // wrong separator
        assertFalse(validator.isValid("2023-12-25T10:30:00", null)); // ISO format
    }

    @Test
    void testCustomPatternDateTimes() {
        validator.initialize(getAnnotation("customPatternDateTime"));

        assertTrue(validator.isValid("25/12/2023 10:30:00", null)); // valid dd/MM/yyyy HH:mm:ss
        assertTrue(validator.isValid("01/01/2024 00:00:00", null)); // new year
        assertTrue(validator.isValid("29/02/2024 12:00:00", null)); // leap year

        assertFalse(validator.isValid("2023-12-25 10:30:00", null)); // wrong format
        assertFalse(validator.isValid("25-12-2023 10:30:00", null)); // wrong separator
        assertFalse(validator.isValid("25/13/2023 10:30:00", null)); // invalid month
        assertFalse(validator.isValid("32/12/2023 10:30:00", null)); // invalid day
    }

    @Test
    void testUSPatternDateTimes() {
        validator.initialize(getAnnotation("usPatternDateTime"));

        assertTrue(validator.isValid("12-25-2023 10:30", null)); // valid MM-dd-yyyy HH:mm
        assertTrue(validator.isValid("01-01-2024 00:00", null)); // new year
        assertTrue(validator.isValid("02-29-2024 12:00", null)); // leap year

        assertFalse(validator.isValid("2023-12-25 10:30", null)); // wrong format
        assertFalse(validator.isValid("25/12/2023 10:30", null)); // wrong format
        assertFalse(validator.isValid("13-25-2023 10:30", null)); // invalid month
        assertFalse(validator.isValid("12-32-2023 10:30", null)); // invalid day
    }

    @Test
    void testISOPatternDateTimes() {
        validator.initialize(getAnnotation("isoPatternDateTime"));

        assertTrue(validator.isValid("2023/12/25 10:30:00", null)); // valid yyyy/MM/dd HH:mm:ss
        assertTrue(validator.isValid("2024/01/01 00:00:00", null)); // new year
        assertTrue(validator.isValid("2024/02/29 12:00:00", null)); // leap year

        assertFalse(validator.isValid("2023-12-25 10:30:00", null)); // wrong separator
        assertFalse(validator.isValid("25/12/2023 10:30:00", null)); // wrong order
        assertFalse(validator.isValid("2023/13/25 10:30:00", null)); // invalid month
        assertFalse(validator.isValid("2023/12/32 10:30:00", null)); // invalid day
    }

    @Test
    void testBlankValues() {
        validator.initialize(getAnnotation("defaultDateTime"));

        assertTrue(validator.isValid("", null));
        assertTrue(validator.isValid("   ", null));
        assertTrue(validator.isValid(null, null));
    }

    @Test
    void testEdgeCases() {
        validator.initialize(getAnnotation("defaultDateTime"));

        // Test with spaces
        assertFalse(validator.isValid(" 2023-12-25 10:30:00", null)); // leading space
        assertFalse(validator.isValid("2023-12-25 10:30:00 ", null)); // trailing space
        assertFalse(validator.isValid("2023 -12-25 10:30:00", null)); // space in date
        assertFalse(validator.isValid("2023-12-25  10:30:00", null)); // double space

        // Test with invalid characters
        assertFalse(validator.isValid("2023-12-25T10:30:00", null)); // ISO format
        assertFalse(validator.isValid("2023-12-25 10:30:00Z", null)); // with timezone
        assertFalse(validator.isValid("2023-12-25 10:30:00+01:00", null)); // with timezone
    }

    @Test
    void testTimeBoundaries() {
        validator.initialize(getAnnotation("defaultDateTime"));

        // Valid time boundaries
        assertTrue(validator.isValid("2023-12-25 00:00:00", null)); // midnight
        assertTrue(validator.isValid("2023-12-25 23:59:59", null)); // end of day
        assertTrue(validator.isValid("2023-12-25 12:00:00", null)); // noon

        // Invalid time boundaries
        assertFalse(validator.isValid("2023-12-25 24:00:00", null)); // invalid hour
        assertFalse(validator.isValid("2023-12-25 10:60:00", null)); // invalid minute
        assertFalse(validator.isValid("2023-12-25 10:30:60", null)); // invalid second
        assertFalse(validator.isValid("2023-12-25 25:30:00", null)); // hour > 23
    }

    @Test
    void testRealWorldExamples() {
        validator.initialize(getAnnotation("defaultDateTime"));

        // Common datetime formats
        assertTrue(validator.isValid("2023-01-01 00:00:00", null)); // New Year midnight
        assertTrue(validator.isValid("2023-12-25 12:00:00", null)); // Christmas noon
        assertTrue(validator.isValid("2023-07-04 18:00:00", null)); // Independence Day evening
        assertTrue(validator.isValid("2023-11-23 14:30:00", null)); // Thanksgiving afternoon
        assertTrue(validator.isValid("2023-06-15 09:15:30", null)); // Mid year morning
    }

    @Test
    void testPatternVariations() {
        // Test different pattern configurations
        validator.initialize(getAnnotation("customPatternDateTime"));
        assertTrue(validator.isValid("25/12/2023 10:30:00", null));
        assertFalse(validator.isValid("2023-12-25 10:30:00", null));

        validator.initialize(getAnnotation("usPatternDateTime"));
        assertTrue(validator.isValid("12-25-2023 10:30", null));
        assertFalse(validator.isValid("25/12/2023 10:30:00", null));

        validator.initialize(getAnnotation("isoPatternDateTime"));
        assertTrue(validator.isValid("2023/12/25 10:30:00", null));
        assertFalse(validator.isValid("12-25-2023 10:30", null));
    }

    @Test
    void testInvalidFormats() {
        validator.initialize(getAnnotation("defaultDateTime"));

        // Various invalid formats
        assertFalse(validator.isValid("25-12-2023 10:30:00", null)); // wrong date order
        assertFalse(validator.isValid("2023/12/25 10:30:00", null)); // wrong separator
        assertFalse(validator.isValid("Dec 25, 2023 10:30:00", null)); // text format
        assertFalse(validator.isValid("25 Dec 2023 10:30:00", null)); // text format
        assertFalse(validator.isValid("2023.12.25 10:30:00", null)); // dot separator
        assertFalse(validator.isValid("20231225 103000", null)); // no separators
        assertFalse(validator.isValid("2023-12-25T10:30:00", null)); // ISO format
    }

    @Test
    void testTimeFormatVariations() {
        validator.initialize(getAnnotation("defaultDateTime"));

        // Test different time formats within the same pattern
        assertTrue(validator.isValid("2023-12-25 00:00:00", null)); // midnight
        assertTrue(validator.isValid("2023-12-25 12:00:00", null)); // noon
        assertTrue(validator.isValid("2023-12-25 23:59:59", null)); // end of day
        assertTrue(validator.isValid("2023-12-25 01:01:01", null)); // specific time
        assertTrue(validator.isValid("2023-12-25 15:30:45", null)); // afternoon time
    }

    @Test
    void testSmartParsingFallback() {
        validator.initialize(getAnnotation("defaultDateTime"));

        assertTrue(validator.isValid("2023-12-25 10:30:00", null)); // should work with strict
    }

    @Test
    void testInvalidTimeValues() {
        validator.initialize(getAnnotation("defaultDateTime"));

        // Test invalid time values that should be caught early
        assertFalse(validator.isValid("2023-12-25 24:00:00", null)); // 24:00
        assertFalse(validator.isValid("2023-12-25 10:60:00", null)); // :60:
        assertFalse(validator.isValid("2023-12-25 10:30:60", null)); // :60
    }

    @Test
    void testSpaceValidation() {
        validator.initialize(getAnnotation("defaultDateTime"));

        // Test space validation
        assertFalse(validator.isValid(" 2023-12-25 10:30:00", null)); // leading space
        assertFalse(validator.isValid("2023-12-25 10:30:00 ", null)); // trailing space
        assertFalse(validator.isValid("2023-12-25  10:30:00", null)); // double space
    }

    @Test
    void testLeapYearValidation() {
        validator.initialize(getAnnotation("defaultDateTime"));

        // Test leap year validation
        assertTrue(validator.isValid("2024-02-29 10:30:00", null)); // Valid leap year
        assertFalse(validator.isValid("2023-02-29 10:30:00", null)); // Invalid leap year
    }

    @Test
    void testDateTimeComponentsValidation() {
        validator.initialize(getAnnotation("defaultDateTime"));

        // Test date-time components validation
        assertTrue(validator.isValid("2023-12-25 10:30:00", null));
        // Invalid components should be caught by validateDateTimeComponents
    }

    @Test
    void testExceptionHandling() {
        validator.initialize(getAnnotation("defaultDateTime"));

        // Test exception handling
        assertFalse(validator.isValid("completely-invalid", null));
    }

    private static class DateTimeDummy {
        @ValidDateTime
        String defaultDateTime;

        @ValidDateTime(pattern = "dd/MM/yyyy HH:mm:ss")
        String customPatternDateTime;

        @ValidDateTime(pattern = "MM-dd-yyyy HH:mm")
        String usPatternDateTime;

        @ValidDateTime(pattern = "yyyy/MM/dd HH:mm:ss")
        String isoPatternDateTime;
    }
}
