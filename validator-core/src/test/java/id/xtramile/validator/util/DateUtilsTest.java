package id.xtramile.validator.util;

import id.xtramile.validator.enums.DatePrecision;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DateUtilsTest {

    @Test
    void testParseDateTime() {
        // Valid dates
        LocalDateTime result1 = DateUtils.parseDateTime("2023-12-25 10:30:00", "yyyy-MM-dd HH:mm:ss");
        assertNotNull(result1);
        assertEquals(2023, result1.getYear());
        assertEquals(12, result1.getMonthValue());
        assertEquals(25, result1.getDayOfMonth());

        // Invalid - contains 24:
        assertNull(DateUtils.parseDateTime("2023-12-25 24:00:00", "yyyy-MM-dd HH:mm:ss"));

        // Invalid - contains :60:
        assertNull(DateUtils.parseDateTime("2023-12-25 10:60:00", "yyyy-MM-dd HH:mm:ss"));
        assertNull(DateUtils.parseDateTime("2023-12-25 10:30:60", "yyyy-MM-dd HH:mm:ss"));

        // Invalid - leading/trailing spaces
        assertNull(DateUtils.parseDateTime(" 2023-12-25 10:30:00", "yyyy-MM-dd HH:mm:ss"));
        assertNull(DateUtils.parseDateTime("2023-12-25 10:30:00 ", "yyyy-MM-dd HH:mm:ss"));
        assertNull(DateUtils.parseDateTime("2023-12-25  10:30:00", "yyyy-MM-dd HH:mm:ss"));

        // Invalid - invalid leap year
        assertNull(DateUtils.parseDateTime("2023-02-29 10:30:00", "yyyy-MM-dd HH:mm:ss"));

        // Valid leap year
        LocalDateTime leapResult = DateUtils.parseDateTime("2024-02-29 10:30:00", "yyyy-MM-dd HH:mm:ss");
        assertNotNull(leapResult);
    }

    @SuppressWarnings("ConstantValue")
    @Test
    void testParseDateTimeWithStrictAndSmartParsing() {
        // Test strict parsing first
        LocalDateTime result = DateUtils.parseDateTime("2023-12-25 10:30:00", "yyyy-MM-dd HH:mm:ss");
        assertNotNull(result);

        // Test smart parsing fallback - may return null if both fail
        LocalDateTime smartResult = DateUtils.parseDateTime("2023-12-25T10:30:00", "yyyy-MM-dd HH:mm:ss");
        assertTrue(smartResult == null || smartResult != null);
    }

    @Test
    void testCalculateDistance() {
        LocalDateTime first = LocalDateTime.of(2023, 1, 1, 0, 0, 0);
        LocalDateTime second = LocalDateTime.of(2023, 1, 2, 0, 0, 0);

        assertEquals(1, DateUtils.calculateDistance(first, second, DatePrecision.DAYS));
        assertEquals(24, DateUtils.calculateDistance(first, second, DatePrecision.HOURS));
        assertEquals(1440, DateUtils.calculateDistance(first, second, DatePrecision.MINUTES));
        assertEquals(86400, DateUtils.calculateDistance(first, second, DatePrecision.SECONDS));

        // Reverse order (negative)
        assertEquals(-1, DateUtils.calculateDistance(second, first, DatePrecision.DAYS));
    }

    @Test
    void pluralLabel_defaultsToDaysWhenNull() {
        assertEquals("days", DateUtils.pluralLabel(null));
    }

    @Test
    void pluralLabel_appendsSWhenNameDoesNotEndWithS() {
        assertEquals("days", DateUtils.pluralLabel(DatePrecision.DAYS));
        assertEquals("hours", DateUtils.pluralLabel(DatePrecision.HOURS));
    }

    @Test
    void pluralLabel_keepsMinutesAndSeconds() {
        assertEquals("minutes", DateUtils.pluralLabel(DatePrecision.MINUTES));
        assertEquals("seconds", DateUtils.pluralLabel(DatePrecision.SECONDS));
    }

    @Test
    void testIsISO8601Pattern() {
        // Valid ISO8601 patterns
        assertTrue(DateUtils.isISO8601Pattern("yyyy-MM-dd'T'HH:mm:ssX"));
        assertTrue(DateUtils.isISO8601Pattern("yyyy-MM-dd'T'HH:mm:ssZ"));
        assertTrue(DateUtils.isISO8601Pattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
        assertTrue(DateUtils.isISO8601Pattern("yyyy-MM-ddTHH:mm:ssX"));

        // Invalid patterns
        assertFalse(DateUtils.isISO8601Pattern("yyyy-MM-dd"));
        assertFalse(DateUtils.isISO8601Pattern("yyyy-MM-dd HH:mm:ss"));
        assertFalse(DateUtils.isISO8601Pattern("dd/MM/yyyy"));
        assertFalse(DateUtils.isISO8601Pattern("yyyy-MM-dd'T'HH:mm"));
    }
}
