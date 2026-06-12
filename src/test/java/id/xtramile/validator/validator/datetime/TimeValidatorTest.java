package id.xtramile.validator.validator.datetime;

import id.xtramile.validator.annotation.datetime.ValidTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TimeValidatorTest {

    private static class TimeDummy {
        @ValidTime
        String defaultTime;

        @ValidTime(pattern = "HH:mm")
        String hourMinuteTime;

        @ValidTime(pattern = "h:mm a")
        String twelveHourTime;

        @ValidTime(pattern = "HH:mm:ss.SSS")
        String millisecondTime;
    }

    private TimeValidator validator;

    private static ValidTime getAnnotation(String fieldName) {
        try {
            Field f = TimeDummy.class.getDeclaredField(fieldName);
            return f.getAnnotation(ValidTime.class);
        } catch (NoSuchFieldException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
        validator = new TimeValidator();
    }

    @Test
    void testValidDefaultTimes() {
        validator.initialize(getAnnotation("defaultTime"));

        assertTrue(validator.isValid("10:30:00", null)); // standard format
        assertTrue(validator.isValid("00:00:00", null)); // midnight
        assertTrue(validator.isValid("23:59:59", null)); // end of day
        assertTrue(validator.isValid("12:00:00", null)); // noon
        assertTrue(validator.isValid("15:45:30", null)); // afternoon
    }

    @Test
    void testInvalidDefaultTimes() {
        validator.initialize(getAnnotation("defaultTime"));

        assertFalse(validator.isValid("25:30:00", null)); // invalid hour
        assertFalse(validator.isValid("10:60:00", null)); // invalid minute
        assertFalse(validator.isValid("10:30:60", null)); // invalid second
        assertFalse(validator.isValid("10:30", null)); // missing seconds
        assertFalse(validator.isValid("10:30:00:000", null)); // includes milliseconds
        assertFalse(validator.isValid("10-30-00", null)); // wrong separator
    }

    @Test
    void testHourMinuteTimes() {
        validator.initialize(getAnnotation("hourMinuteTime"));

        assertTrue(validator.isValid("10:30", null)); // valid HH:mm
        assertTrue(validator.isValid("00:00", null)); // midnight
        assertTrue(validator.isValid("23:59", null)); // end of day
        assertTrue(validator.isValid("12:00", null)); // noon

        assertFalse(validator.isValid("10:30:00", null)); // includes seconds
        assertFalse(validator.isValid("25:30", null)); // invalid hour
        assertFalse(validator.isValid("10:60", null)); // invalid minute
        assertFalse(validator.isValid("10-30", null)); // wrong separator
    }

    @Test
    void testTwelveHourTimes() {
        validator.initialize(getAnnotation("twelveHourTime"));

        assertTrue(validator.isValid("10:30 AM", null)); // valid 12-hour format
        assertTrue(validator.isValid("12:00 PM", null)); // noon
        assertTrue(validator.isValid("12:00 AM", null)); // midnight
        assertTrue(validator.isValid("1:30 PM", null)); // single digit hour
        assertTrue(validator.isValid("11:59 PM", null)); // end of day

        assertFalse(validator.isValid("10:30", null)); // missing AM/PM
        assertFalse(validator.isValid("13:30 PM", null)); // invalid hour for 12-hour format
        assertFalse(validator.isValid("10:60 AM", null)); // invalid minute
        assertFalse(validator.isValid("10:30am", null)); // lowercase AM/PM
    }

    @Test
    void testMillisecondTimes() {
        validator.initialize(getAnnotation("millisecondTime"));

        assertTrue(validator.isValid("10:30:00.000", null)); // valid with milliseconds
        assertTrue(validator.isValid("00:00:00.000", null)); // midnight
        assertTrue(validator.isValid("23:59:59.999", null)); // end of day
        assertTrue(validator.isValid("12:00:00.500", null)); // noon with milliseconds

        assertFalse(validator.isValid("10:30:00", null)); // missing milliseconds
        assertFalse(validator.isValid("10:30:00.0000", null)); // too many milliseconds
        assertFalse(validator.isValid("25:30:00.000", null)); // invalid hour
        assertFalse(validator.isValid("10:60:00.000", null)); // invalid minute
    }

    @Test
    void testBlankValues() {
        validator.initialize(getAnnotation("defaultTime"));

        assertTrue(validator.isValid("", null));
        assertTrue(validator.isValid("   ", null));
        assertTrue(validator.isValid(null, null));
    }

    @Test
    void testEdgeCases() {
        validator.initialize(getAnnotation("defaultTime"));

        // Test with spaces
        assertFalse(validator.isValid(" 10:30:00", null)); // leading space
        assertFalse(validator.isValid("10:30:00 ", null)); // trailing space
        assertFalse(validator.isValid("10 :30:00", null)); // space in time
        assertFalse(validator.isValid("10: 30:00", null)); // space in time

        // Test with invalid characters
        assertFalse(validator.isValid("10:30:00Z", null)); // with timezone
        assertFalse(validator.isValid("10:30:00+01:00", null)); // with timezone
        assertFalse(validator.isValid("10:30:00.000", null)); // with milliseconds
    }

    @Test
    void testTimeBoundaries() {
        validator.initialize(getAnnotation("defaultTime"));

        // Valid time boundaries
        assertTrue(validator.isValid("00:00:00", null)); // midnight
        assertTrue(validator.isValid("23:59:59", null)); // end of day
        assertTrue(validator.isValid("12:00:00", null)); // noon
        assertTrue(validator.isValid("01:01:01", null)); // specific time

        // Invalid time boundaries
        assertFalse(validator.isValid("24:00:00", null)); // invalid hour
        assertFalse(validator.isValid("10:60:00", null)); // invalid minute
        assertFalse(validator.isValid("10:30:60", null)); // invalid second
        assertFalse(validator.isValid("25:30:00", null)); // hour > 23
    }

    @Test
    void testTwelveHourBoundaries() {
        validator.initialize(getAnnotation("twelveHourTime"));

        // Valid 12-hour boundaries
        assertTrue(validator.isValid("12:00 AM", null)); // midnight
        assertTrue(validator.isValid("12:00 PM", null)); // noon
        assertTrue(validator.isValid("1:00 AM", null)); // 1 AM
        assertTrue(validator.isValid("11:59 PM", null)); // end of day

        // Invalid 12-hour boundaries
        assertFalse(validator.isValid("13:00 PM", null)); // invalid hour
        assertFalse(validator.isValid("12:60 PM", null)); // invalid minute
        assertFalse(validator.isValid("0:00 AM", null)); // invalid hour (should be 12)
    }

    @Test
    void testRealWorldExamples() {
        validator.initialize(getAnnotation("defaultTime"));

        // Common time formats
        assertTrue(validator.isValid("09:00:00", null)); // 9 AM
        assertTrue(validator.isValid("12:00:00", null)); // noon
        assertTrue(validator.isValid("15:30:00", null)); // 3:30 PM
        assertTrue(validator.isValid("18:00:00", null)); // 6 PM
        assertTrue(validator.isValid("21:45:00", null)); // 9:45 PM
    }

    @Test
    void testPatternVariations() {
        // Test different pattern configurations
        validator.initialize(getAnnotation("hourMinuteTime"));
        assertTrue(validator.isValid("10:30", null));
        assertFalse(validator.isValid("10:30:00", null));

        validator.initialize(getAnnotation("twelveHourTime"));
        assertTrue(validator.isValid("10:30 AM", null));
        assertFalse(validator.isValid("10:30", null));

        validator.initialize(getAnnotation("millisecondTime"));
        assertTrue(validator.isValid("10:30:00.000", null));
        assertFalse(validator.isValid("10:30:00", null));
    }

    @Test
    void testInvalidFormats() {
        validator.initialize(getAnnotation("defaultTime"));

        // Various invalid formats
        assertFalse(validator.isValid("10-30-00", null)); // wrong separator
        assertFalse(validator.isValid("10.30.00", null)); // dot separator
        assertFalse(validator.isValid("10 30 00", null)); // space separator
        assertFalse(validator.isValid("103000", null)); // no separators
        assertFalse(validator.isValid("10:30", null)); // missing seconds
        assertFalse(validator.isValid("10:30:00:000", null)); // too many parts
    }

    @Test
    void testMillisecondBoundaries() {
        validator.initialize(getAnnotation("millisecondTime"));

        // Valid millisecond boundaries
        assertTrue(validator.isValid("10:30:00.000", null)); // zero milliseconds
        assertTrue(validator.isValid("10:30:00.001", null)); // one millisecond
        assertTrue(validator.isValid("10:30:00.999", null)); // maximum milliseconds
        assertTrue(validator.isValid("10:30:00.500", null)); // half second

        // Invalid millisecond boundaries
        assertFalse(validator.isValid("10:30:00.1000", null)); // too many milliseconds
        assertFalse(validator.isValid("10:30:00.0000", null)); // too many milliseconds
        assertFalse(validator.isValid("10:30:00", null)); // missing milliseconds
    }
}
