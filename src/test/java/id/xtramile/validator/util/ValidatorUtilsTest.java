package id.xtramile.validator.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ValidatorUtilsTest {

    @Test
    void testIsBlank() {
        assertTrue(ValidatorUtils.isBlank(null));
        assertTrue(ValidatorUtils.isBlank(""));
        assertTrue(ValidatorUtils.isBlank("   "));
        assertTrue(ValidatorUtils.isBlank("\t\n"));
        assertFalse(ValidatorUtils.isBlank("test"));
        assertFalse(ValidatorUtils.isBlank("  test  "));
    }

    @Test
    void testIsPresent() {
        // Null
        assertFalse(ValidatorUtils.isPresent(null));
        
        // String
        assertFalse(ValidatorUtils.isPresent(""));
        assertFalse(ValidatorUtils.isPresent("   "));
        assertTrue(ValidatorUtils.isPresent("test"));
        
        // Collection
        Collection<String> emptyList = new ArrayList<>();
        Collection<String> nonEmptyList = new ArrayList<>();
        nonEmptyList.add("item");
        assertFalse(ValidatorUtils.isPresent(emptyList));
        assertTrue(ValidatorUtils.isPresent(nonEmptyList));
        
        // Map
        Map<String, String> emptyMap = new HashMap<>();
        Map<String, String> nonEmptyMap = new HashMap<>();
        nonEmptyMap.put("key", "value");
        assertFalse(ValidatorUtils.isPresent(emptyMap));
        assertTrue(ValidatorUtils.isPresent(nonEmptyMap));
        
        // Array
        assertFalse(ValidatorUtils.isPresent(new String[0]));
        assertTrue(ValidatorUtils.isPresent(new String[]{"item"}));
        assertTrue(ValidatorUtils.isPresent(new int[]{1, 2, 3}));
        
        // Other objects
        assertTrue(ValidatorUtils.isPresent(123));
        assertTrue(ValidatorUtils.isPresent(new Object()));
    }

    @Test
    void testMbToBytes() {
        assertEquals(0, ValidatorUtils.mbToBytes(0));
        assertEquals(1048576, ValidatorUtils.mbToBytes(1));
        assertEquals(10485760, ValidatorUtils.mbToBytes(10));
        assertEquals(-1, ValidatorUtils.mbToBytes(-1));
        assertEquals(-1, ValidatorUtils.mbToBytes(-100));
    }

    @Test
    void testFormatFileSize() {
        assertEquals("1 byte", ValidatorUtils.formatFileSize(1));
        assertEquals("2 byte", ValidatorUtils.formatFileSize(2)); // Note: implementation uses "byte" not "bytes"
        assertEquals("1 kilobyte", ValidatorUtils.formatFileSize(1024)); // 1024 bytes = 1 kilobyte
        assertEquals("1 megabyte", ValidatorUtils.formatFileSize(1024 * 1024));
        assertEquals("1 gigabyte", ValidatorUtils.formatFileSize(1024 * 1024 * 1024));
        assertEquals("1024 gigabyte", ValidatorUtils.formatFileSize(1024L * 1024 * 1024 * 1024)); // 1 terabyte = 1024 gigabytes
    }

    @Test
    void testFormatFileSizeWithRemainder() {
        String result = ValidatorUtils.formatFileSize(1500);
        assertNotNull(result);
        assertTrue(result.contains("byte"));
    }

    @Test
    void testValidateLeapYear() {
        // Valid leap years
        assertTrue(ValidatorUtils.validateLeapYear("2024-02-29"));
        assertTrue(ValidatorUtils.validateLeapYear("2020-02-29"));
        assertTrue(ValidatorUtils.validateLeapYear("2000-02-29"));
        
        // Invalid leap years
        assertFalse(ValidatorUtils.validateLeapYear("2023-02-29"));
        assertFalse(ValidatorUtils.validateLeapYear("2021-02-29"));
        assertFalse(ValidatorUtils.validateLeapYear("1900-02-29"));
        
        // Non-February dates
        assertTrue(ValidatorUtils.validateLeapYear("2023-01-15"));
        assertTrue(ValidatorUtils.validateLeapYear("2023-03-15"));
        
        // Invalid format
        assertTrue(ValidatorUtils.validateLeapYear("invalid"));
        assertTrue(ValidatorUtils.validateLeapYear("2023"));
        assertTrue(ValidatorUtils.validateLeapYear(null));
    }

    @Test
    void testValidateDateComponents() {
        // Valid dates
        assertTrue(ValidatorUtils.validateDateComponents(LocalDate.of(2023, 1, 31)));
        assertTrue(ValidatorUtils.validateDateComponents(LocalDate.of(2024, 2, 29))); // Leap year
        assertTrue(ValidatorUtils.validateDateComponents(LocalDate.of(2023, 2, 28)));

        assertTrue(ValidatorUtils.validateDateComponents(LocalDate.of(2023, 4, 30)));
    }

    @Test
    void testValidateDateTimeComponents() {
        LocalDateTime validDateTime = LocalDateTime.of(2023, 1, 15, 10, 30);
        assertTrue(ValidatorUtils.validateDateTimeComponents(validDateTime));
        
        LocalDateTime leapYearDateTime = LocalDateTime.of(2024, 2, 29, 12, 0);
        assertTrue(ValidatorUtils.validateDateTimeComponents(leapYearDateTime));
    }

    @Test
    void testGetDaysInMonth() {
        // 31-day months
        assertEquals(31, ValidatorUtils.getDaysInMonth(2023, 1));
        assertEquals(31, ValidatorUtils.getDaysInMonth(2023, 3));
        assertEquals(31, ValidatorUtils.getDaysInMonth(2023, 5));
        assertEquals(31, ValidatorUtils.getDaysInMonth(2023, 7));
        assertEquals(31, ValidatorUtils.getDaysInMonth(2023, 8));
        assertEquals(31, ValidatorUtils.getDaysInMonth(2023, 10));
        assertEquals(31, ValidatorUtils.getDaysInMonth(2023, 12));
        
        // 30-day months
        assertEquals(30, ValidatorUtils.getDaysInMonth(2023, 4));
        assertEquals(30, ValidatorUtils.getDaysInMonth(2023, 6));
        assertEquals(30, ValidatorUtils.getDaysInMonth(2023, 9));
        assertEquals(30, ValidatorUtils.getDaysInMonth(2023, 11));
        
        // February
        assertEquals(28, ValidatorUtils.getDaysInMonth(2023, 2)); // Non-leap year
        assertEquals(29, ValidatorUtils.getDaysInMonth(2024, 2)); // Leap year
        assertEquals(29, ValidatorUtils.getDaysInMonth(2000, 2)); // Century leap year
        assertEquals(28, ValidatorUtils.getDaysInMonth(1900, 2)); // Century non-leap year
        
        // Invalid month
        assertEquals(0, ValidatorUtils.getDaysInMonth(2023, 0));
        assertEquals(0, ValidatorUtils.getDaysInMonth(2023, 13));
    }

    @Test
    void testValidateEmail() {
        // Valid emails
        assertTrue(ValidatorUtils.validateEmail("test@example.com"));
        assertTrue(ValidatorUtils.validateEmail("user.name@domain.co.uk"));
        assertTrue(ValidatorUtils.validateEmail("user+tag@example.com"));
        
        // Invalid emails
        assertFalse(ValidatorUtils.validateEmail("noat.com"));
        assertFalse(ValidatorUtils.validateEmail("@domain.com"));
        assertFalse(ValidatorUtils.validateEmail("user@"));
        assertFalse(ValidatorUtils.validateEmail("user..name@domain.com"));
        assertFalse(ValidatorUtils.validateEmail("user@domain..com"));
        assertFalse(ValidatorUtils.validateEmail("user name@domain.com"));
        assertFalse(ValidatorUtils.validateEmail("user@domain com"));
        assertFalse(ValidatorUtils.validateEmail("user@domain"));
        assertFalse(ValidatorUtils.validateEmail("user@.domain.com"));
        assertFalse(ValidatorUtils.validateEmail("user@domain.com."));
        assertFalse(ValidatorUtils.validateEmail("-user@domain.com"));
        assertFalse(ValidatorUtils.validateEmail("user-@domain.com"));
        assertFalse(ValidatorUtils.validateEmail(".user@domain.com"));
        assertFalse(ValidatorUtils.validateEmail("user.@domain.com"));
    }

    @Test
    void testValidateISO8601ForFutureDate() {
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime future = now.plusHours(2);
        ZonedDateTime past = now.minusHours(2);
        
        String futureStr = future.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        String pastStr = past.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        
        // Valid future date (within tolerance)
        assertTrue(ValidatorUtils.validateISO8601ForFutureDate(futureStr, "yyyy-MM-dd'T'HH:mm:ssXXX", 3));
        
        // Invalid future date (beyond tolerance)
        ZonedDateTime tooFuture = now.plusHours(5);
        String tooFutureStr = tooFuture.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        assertFalse(ValidatorUtils.validateISO8601ForFutureDate(tooFutureStr, "yyyy-MM-dd'T'HH:mm:ssXXX", 3));
        
        // Past date should be valid
        assertTrue(ValidatorUtils.validateISO8601ForFutureDate(pastStr, "yyyy-MM-dd'T'HH:mm:ssXXX", 3));
        
        // Invalid format
        assertFalse(ValidatorUtils.validateISO8601ForFutureDate("invalid", "yyyy-MM-dd'T'HH:mm:ssXXX", 3));
    }

    @Test
    void testValidateISO8601ForPastFutureDate() {
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime recent = now.minusHours(1);
        ZonedDateTime tooPast = now.minusHours(5);
        ZonedDateTime future = now.plusHours(1);
        
        String recentStr = recent.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        String tooPastStr = tooPast.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        String futureStr = future.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        
        // Valid recent date (within tolerance)
        assertTrue(ValidatorUtils.validateISO8601ForPastFutureDate(recentStr, "yyyy-MM-dd'T'HH:mm:ssXXX", 3));
        
        // Invalid - too past
        assertFalse(ValidatorUtils.validateISO8601ForPastFutureDate(tooPastStr, "yyyy-MM-dd'T'HH:mm:ssXXX", 3));
        
        // Invalid - future
        assertFalse(ValidatorUtils.validateISO8601ForPastFutureDate(futureStr, "yyyy-MM-dd'T'HH:mm:ssXXX", 3));
        
        // Invalid format
        assertFalse(ValidatorUtils.validateISO8601ForPastFutureDate("invalid", "yyyy-MM-dd'T'HH:mm:ssXXX", 3));
    }
}
