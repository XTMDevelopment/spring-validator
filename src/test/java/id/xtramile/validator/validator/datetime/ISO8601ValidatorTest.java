package id.xtramile.validator.validator.datetime;

import id.xtramile.validator.annotation.datetime.ValidISO8601;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ISO8601ValidatorTest {

    private ISO8601Validator validator;

    private static ValidISO8601 getAnnotation(String fieldName) {
        try {
            Field f = ISO8601Dummy.class.getDeclaredField(fieldName);
            return f.getAnnotation(ValidISO8601.class);
        } catch (NoSuchFieldException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
        validator = new ISO8601Validator();
    }

    @Test
    void testValidISO8601Formats() {
        validator.initialize(getAnnotation("iso8601Field"));

        // Basic ISO8601 formats
        assertTrue(validator.isValid("2023-12-25T10:30:00Z", null)); // UTC with Z
        assertTrue(validator.isValid("2023-12-25T10:30:00+00:00", null)); // UTC with offset
        assertTrue(validator.isValid("2023-12-25T10:30:00+01:00", null)); // positive offset
        assertTrue(validator.isValid("2023-12-25T10:30:00-05:00", null)); // negative offset
        assertTrue(validator.isValid("2023-12-25T10:30:00.000Z", null)); // with milliseconds
    }

    @Test
    void testValidISO8601WithTimezones() {
        validator.initialize(getAnnotation("iso8601Field"));

        // Different timezone formats
        assertTrue(validator.isValid("2023-12-25T10:30:00+01:30", null)); // half hour offset
        assertTrue(validator.isValid("2023-12-25T10:30:00+14:00", null)); // maximum positive offset
        assertTrue(validator.isValid("2023-12-25T10:30:00-12:00", null)); // maximum negative offset
        assertTrue(validator.isValid("2023-12-25T10:30:00+00:30", null)); // 30 minute offset
        assertTrue(validator.isValid("2023-12-25T10:30:00-00:30", null)); // negative 30 minute offset
    }

    @Test
    void testValidISO8601WithMilliseconds() {
        validator.initialize(getAnnotation("iso8601Field"));

        // With different precision levels
        assertTrue(validator.isValid("2023-12-25T10:30:00.000Z", null)); // 3 digits
        assertTrue(validator.isValid("2023-12-25T10:30:00.00Z", null)); // 2 digits
        assertTrue(validator.isValid("2023-12-25T10:30:00.0Z", null)); // 1 digit
        assertTrue(validator.isValid("2023-12-25T10:30:00.123456Z", null)); // 6 digits
        assertTrue(validator.isValid("2023-12-25T10:30:00.123456789Z", null)); // 9 digits
    }

    @Test
    void testInvalidISO8601Formats() {
        validator.initialize(getAnnotation("iso8601Field"));

        // Invalid formats
        assertFalse(validator.isValid("2023-12-25 10:30:00", null)); // space instead of T
        assertFalse(validator.isValid("2023/12/25T10:30:00Z", null)); // wrong date separator
        assertFalse(validator.isValid("2023-12-25T10:30:00", null)); // missing timezone
        assertFalse(validator.isValid("2023-12-25T25:30:00Z", null)); // invalid hour
        assertFalse(validator.isValid("2023-12-25T10:60:00Z", null)); // invalid minute
        assertFalse(validator.isValid("2023-12-25T10:30:60Z", null)); // invalid second
        assertFalse(validator.isValid("2023-13-25T10:30:00Z", null)); // invalid month
        assertFalse(validator.isValid("2023-12-32T10:30:00Z", null)); // invalid day
    }

    @Test
    void testBlankValues() {
        validator.initialize(getAnnotation("iso8601Field"));

        assertTrue(validator.isValid("", null));
        assertTrue(validator.isValid("   ", null));
        assertTrue(validator.isValid(null, null));
    }

    @Test
    void testEdgeCases() {
        validator.initialize(getAnnotation("iso8601Field"));

        // Test with spaces
        assertFalse(validator.isValid(" 2023-12-25T10:30:00Z", null)); // leading space
        assertFalse(validator.isValid("2023-12-25T10:30:00Z ", null)); // trailing space
        assertFalse(validator.isValid("2023-12-25 T10:30:00Z", null)); // space before T
        assertFalse(validator.isValid("2023-12-25T 10:30:00Z", null)); // space after T

        // Test with invalid characters
        assertFalse(validator.isValid("2023-12-25T10:30:00Z+01:00", null)); // Z and offset
        assertFalse(validator.isValid("2023-12-25T10:30:00+01:00Z", null)); // offset and Z
        assertFalse(validator.isValid("2023-12-25T10:30:00+01:00+02:00", null)); // double offset
    }

    @Test
    void testLeapYearISO8601() {
        validator.initialize(getAnnotation("iso8601Field"));

        // Valid leap years
        assertTrue(validator.isValid("2024-02-29T10:30:00Z", null)); // 2024 is leap year
        assertTrue(validator.isValid("2020-02-29T10:30:00Z", null)); // 2020 is leap year
        assertTrue(validator.isValid("2000-02-29T10:30:00Z", null)); // 2000 is leap year

        // Invalid leap years
        assertFalse(validator.isValid("2023-02-29T10:30:00Z", null)); // 2023 is not leap year
        assertFalse(validator.isValid("2021-02-29T10:30:00Z", null)); // 2021 is not leap year
        assertFalse(validator.isValid("1900-02-29T10:30:00Z", null)); // 1900 is not leap year
    }

    @Test
    void testRealWorldExamples() {
        validator.initialize(getAnnotation("iso8601Field"));

        // Common ISO8601 examples
        assertTrue(validator.isValid("2023-01-01T00:00:00Z", null)); // New Year UTC
        assertTrue(validator.isValid("2023-12-25T12:00:00Z", null)); // Christmas noon UTC
        assertTrue(validator.isValid("2023-06-15T15:30:45.123Z", null)); // Mid year with milliseconds
        assertTrue(validator.isValid("2023-07-04T18:00:00-05:00", null)); // US Independence Day EST
        assertTrue(validator.isValid("2023-11-23T14:30:00+01:00", null)); // European time
    }

    @Test
    void testTimezoneVariations() {
        validator.initialize(getAnnotation("iso8601Field"));

        // Different timezone representations
        assertTrue(validator.isValid("2023-12-25T10:30:00Z", null)); // UTC with Z
        assertTrue(validator.isValid("2023-12-25T10:30:00+00:00", null)); // UTC with +00:00
        assertTrue(validator.isValid("2023-12-25T10:30:00-00:00", null)); // UTC with -00:00
        assertTrue(validator.isValid("2023-12-25T10:30:00+01:00", null)); // CET
        assertTrue(validator.isValid("2023-12-25T10:30:00-08:00", null)); // PST
        assertTrue(validator.isValid("2023-12-25T10:30:00+09:00", null)); // JST
    }

    @Test
    void testInvalidTimezoneFormats() {
        validator.initialize(getAnnotation("iso8601Field"));

        // Invalid timezone formats
        assertFalse(validator.isValid("2023-12-25T10:30:00+1:00", null)); // single digit hour
        assertFalse(validator.isValid("2023-12-25T10:30:00+01:0", null)); // single digit minute
        assertFalse(validator.isValid("2023-12-25T10:30:00+25:00", null)); // invalid hour
        assertFalse(validator.isValid("2023-12-25T10:30:00+01:60", null)); // invalid minute
        assertFalse(validator.isValid("2023-12-25T10:30:00+01", null)); // missing minutes
    }

    @Test
    void testMillisecondPrecision() {
        validator.initialize(getAnnotation("iso8601Field"));

        // Different millisecond precision levels
        assertTrue(validator.isValid("2023-12-25T10:30:00Z", null)); // no milliseconds
        assertTrue(validator.isValid("2023-12-25T10:30:00.0Z", null)); // 1 digit
        assertTrue(validator.isValid("2023-12-25T10:30:00.00Z", null)); // 2 digits
        assertTrue(validator.isValid("2023-12-25T10:30:00.000Z", null)); // 3 digits
        assertTrue(validator.isValid("2023-12-25T10:30:00.0000Z", null)); // 4 digits
        assertTrue(validator.isValid("2023-12-25T10:30:00.00000Z", null)); // 5 digits
        assertTrue(validator.isValid("2023-12-25T10:30:00.000000Z", null)); // 6 digits
        assertTrue(validator.isValid("2023-12-25T10:30:00.000000000Z", null)); // 9 digits
    }

    @Test
    void testDateBoundaries() {
        validator.initialize(getAnnotation("iso8601Field"));

        // Valid date boundaries
        assertTrue(validator.isValid("2023-01-01T00:00:00Z", null)); // January 1st
        assertTrue(validator.isValid("2023-12-31T23:59:59Z", null)); // December 31st
        assertTrue(validator.isValid("2023-02-28T10:30:00Z", null)); // February 28th
        assertTrue(validator.isValid("2024-02-29T10:30:00Z", null)); // February 29th (leap year)

        // Invalid date boundaries
        assertFalse(validator.isValid("2023-13-01T10:30:00Z", null)); // invalid month
        assertFalse(validator.isValid("2023-12-32T10:30:00Z", null)); // invalid day
        assertFalse(validator.isValid("2023-02-30T10:30:00Z", null)); // invalid day for February
        assertFalse(validator.isValid("2023-04-31T10:30:00Z", null)); // invalid day for April
    }

    private static class ISO8601Dummy {
        @ValidISO8601
        String iso8601Field;
    }
}
