package id.xtramile.validator.validator.datetime;

import id.xtramile.validator.annotation.datetime.ValidFutureDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FutureDateValidatorTest {

    private static class FutureDateDummy {
        @ValidFutureDate
        String defaultFutureDate;

        @ValidFutureDate(pattern = "dd/MM/yyyy")
        String customPatternFutureDate;

        @ValidFutureDate(pattern = "yyyy-MM-dd HH:mm:ss")
        String dateTimeFutureDate;
    }

    private FutureDateValidator validator;

    private static ValidFutureDate getAnnotation(String fieldName) {
        try {
            Field f = FutureDateDummy.class.getDeclaredField(fieldName);
            return f.getAnnotation(ValidFutureDate.class);
        } catch (NoSuchFieldException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
        validator = new FutureDateValidator();
    }

    @Test
    void testValidFutureDates() {
        validator.initialize(getAnnotation("defaultFutureDate"));

        // Test with dates in the future
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        assertTrue(validator.isValid(tomorrow.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));

        LocalDate nextWeek = LocalDate.now().plusWeeks(1);
        assertTrue(validator.isValid(nextWeek.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));

        LocalDate nextMonth = LocalDate.now().plusMonths(1);
        assertTrue(validator.isValid(nextMonth.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));

        LocalDate nextYear = LocalDate.now().plusYears(1);
        assertTrue(validator.isValid(nextYear.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));
    }

    @Test
    void testInvalidFutureDates() {
        validator.initialize(getAnnotation("defaultFutureDate"));

        // Test with dates in the past
        LocalDate yesterday = LocalDate.now().minusDays(1);
        assertFalse(validator.isValid(yesterday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));

        LocalDate lastWeek = LocalDate.now().minusWeeks(1);
        assertFalse(validator.isValid(lastWeek.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));

        LocalDate lastMonth = LocalDate.now().minusMonths(1);
        assertFalse(validator.isValid(lastMonth.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));

        LocalDate lastYear = LocalDate.now().minusYears(1);
        assertFalse(validator.isValid(lastYear.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));
    }

    @Test
    void testCustomPatternFutureDates() {
        validator.initialize(getAnnotation("customPatternFutureDate"));

        // Test with custom pattern (dd/MM/yyyy)
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        assertTrue(validator.isValid(tomorrow.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), null));

        LocalDate nextWeek = LocalDate.now().plusWeeks(1);
        assertTrue(validator.isValid(nextWeek.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), null));

        // Test with past dates
        LocalDate yesterday = LocalDate.now().minusDays(1);
        assertFalse(validator.isValid(yesterday.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), null));
    }

    @Test
    void testDateTimeFutureDates() {
        validator.initialize(getAnnotation("dateTimeFutureDate"));

        // Test with future datetime
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
        assertTrue(validator.isValid(tomorrow.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), null));

        LocalDateTime nextHour = LocalDateTime.now().plusHours(1);
        assertTrue(validator.isValid(nextHour.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), null));

        // Test with past datetime
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        assertFalse(validator.isValid(yesterday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), null));

        LocalDateTime lastHour = LocalDateTime.now().minusHours(1);
        assertFalse(validator.isValid(lastHour.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), null));
    }

    @Test
    void testBlankValues() {
        validator.initialize(getAnnotation("defaultFutureDate"));

        assertTrue(validator.isValid("", null));
        assertTrue(validator.isValid("   ", null));
        assertTrue(validator.isValid(null, null));
    }

    @Test
    void testInvalidDateFormats() {
        validator.initialize(getAnnotation("defaultFutureDate"));

        // Test with invalid date formats
        assertFalse(validator.isValid("invalid-date", null));
        assertFalse(validator.isValid("2023-13-01", null)); // invalid month
        assertFalse(validator.isValid("2023-12-32", null)); // invalid day
        assertFalse(validator.isValid("2023/12/25", null)); // wrong separator
        assertFalse(validator.isValid("25-12-2023", null)); // wrong order
    }

    @Test
    void testEdgeCases() {
        validator.initialize(getAnnotation("defaultFutureDate"));

        // Test with spaces
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        String futureDate = tomorrow.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        assertFalse(validator.isValid(" " + futureDate, null)); // leading space
        assertFalse(validator.isValid(futureDate + " ", null)); // trailing space
        assertFalse(validator.isValid("2023 -12-25", null)); // space in date
    }

    @Test
    void testRealWorldExamples() {
        validator.initialize(getAnnotation("defaultFutureDate"));

        // Test with specific future dates
        assertTrue(validator.isValid("2028-01-01", null)); // New Year 2028
        assertTrue(validator.isValid("2027-12-25", null)); // Christmas 2027
        assertTrue(validator.isValid("2027-07-04", null)); // Independence Day 2027
        assertTrue(validator.isValid("2027-06-15", null)); // Mid year 2027

        // Test with past dates
        assertFalse(validator.isValid("2020-01-01", null)); // New Year 2020
        assertFalse(validator.isValid("2022-12-25", null)); // Christmas 2022
        assertFalse(validator.isValid("2023-07-04", null)); // Independence Day 2023 (if past)
        assertFalse(validator.isValid("2023-06-15", null)); // Mid year 2023 (if past)
    }

    @Test
    void testPatternVariations() {
        // Test different pattern configurations
        validator.initialize(getAnnotation("customPatternFutureDate"));
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        assertTrue(validator.isValid(tomorrow.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), null));
        assertFalse(validator.isValid(tomorrow.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));

        validator.initialize(getAnnotation("dateTimeFutureDate"));
        LocalDateTime tomorrowDateTime = LocalDateTime.now().plusDays(1);
        assertTrue(validator.isValid(tomorrowDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), null));
        assertFalse(validator.isValid(tomorrowDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), null));
    }

    @Test
    void testLeapYearFutureDates() {
        validator.initialize(getAnnotation("defaultFutureDate"));

        // Test leap year dates
        assertTrue(validator.isValid("2028-02-29", null)); // 2028 is leap year
        assertTrue(validator.isValid("2032-02-29", null)); // 2032 is leap year
        assertTrue(validator.isValid("2036-02-29", null)); // 2036 is leap year

        // Test non-leap year dates
        assertFalse(validator.isValid("2023-02-29", null)); // 2023 is not leap year
        assertFalse(validator.isValid("2025-02-29", null)); // 2025 is not leap year
        assertFalse(validator.isValid("2026-02-29", null)); // 2026 is not leap year
    }

    @Test
    void testDateTimeWithTimezones() {
        validator.initialize(getAnnotation("dateTimeFutureDate"));

        // Test with timezone-aware datetime
        ZonedDateTime tomorrow = ZonedDateTime.now().plusDays(1);
        String futureDateTime = tomorrow.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        assertTrue(validator.isValid(futureDateTime, null));

        ZonedDateTime yesterday = ZonedDateTime.now().minusDays(1);
        String pastDateTime = yesterday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        assertFalse(validator.isValid(pastDateTime, null));
    }

    @Test
    void testBoundaryConditions() {
        validator.initialize(getAnnotation("defaultFutureDate"));

        // Test with dates very close to today
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        LocalDate yesterday = today.minusDays(1);

        // Today should be invalid (not future)
        assertFalse(validator.isValid(today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));

        // Tomorrow should be valid (future)
        assertTrue(validator.isValid(tomorrow.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));

        // Yesterday should be invalid (past)
        assertFalse(validator.isValid(yesterday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));
    }

    @Test
    void testStrictParsingWithZonedDateTime() throws NoSuchFieldException {
        class TestDummy {
            @ValidFutureDate(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
            String field;
        }
        
        Field f = TestDummy.class.getDeclaredField("field");
        ValidFutureDate annotation = f.getAnnotation(ValidFutureDate.class);
        validator.initialize(annotation);
        
        ZonedDateTime future = ZonedDateTime.now().plusDays(1);
        ZonedDateTime past = ZonedDateTime.now().minusDays(1);
        
        String futureStr = future.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"));
        String pastStr = past.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"));
        
        assertTrue(validator.isValid(futureStr, null));
        assertFalse(validator.isValid(pastStr, null));
    }

    @Test
    void testStrictParsingWithZonedDateTimeLeapYear() throws NoSuchFieldException {
        class TestDummy {
            @ValidFutureDate(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
            String field;
        }
        
        Field f = TestDummy.class.getDeclaredField("field");
        ValidFutureDate annotation = f.getAnnotation(ValidFutureDate.class);
        validator.initialize(annotation);
        
        // Test valid leap year in future
        ZonedDateTime validLeap = ZonedDateTime.of(2028, 2, 29, 10, 30, 0, 0, ZonedDateTime.now().getZone());
        if (validLeap.isAfter(ZonedDateTime.now())) {
            String validLeapStr = validLeap.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"));
            assertTrue(validator.isValid(validLeapStr, null));
        }
        
        // Test invalid leap year
        assertFalse(validator.isValid("2023-02-29T10:30:00+07:00", null));
    }

    @Test
    void testStrictParsingWithLocalDateTimeLeapYear() {
        validator.initialize(getAnnotation("dateTimeFutureDate"));
        
        // Test valid leap year in future
        LocalDateTime validLeap = LocalDateTime.of(2028, 2, 29, 10, 30, 0);
        if (validLeap.isAfter(LocalDateTime.now())) {
            assertTrue(validator.isValid(validLeap.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), null));
        }
        
        // Test invalid leap year
        assertFalse(validator.isValid("2023-02-29 10:30:00", null));
    }

    @Test
    void testStrictParsingWithLocalDateLeapYear() {
        validator.initialize(getAnnotation("defaultFutureDate"));
        
        // Test valid leap year in future
        LocalDate validLeap = LocalDate.of(2028, 2, 29);
        if (validLeap.isAfter(LocalDate.now())) {
            assertTrue(validator.isValid(validLeap.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));
        }
        
        // Test invalid leap year
        assertFalse(validator.isValid("2023-02-29", null));
    }

    @Test
    void testStrictParsingWithLocalDateTime() {
        validator.initialize(getAnnotation("dateTimeFutureDate"));
        
        LocalDateTime future = LocalDateTime.now().plusDays(1);
        LocalDateTime past = LocalDateTime.now().minusDays(1);
        
        assertTrue(validator.isValid(future.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), null));
        assertFalse(validator.isValid(past.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), null));
    }

    @Test
    void testStrictParsingWithLocalDate() {
        validator.initialize(getAnnotation("defaultFutureDate"));
        
        LocalDate future = LocalDate.now().plusDays(1);
        LocalDate past = LocalDate.now().minusDays(1);
        
        assertTrue(validator.isValid(future.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));
        assertFalse(validator.isValid(past.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));
    }

    @Test
    void testUnsupportedTemporalType() {
        validator.initialize(getAnnotation("defaultFutureDate"));
        
        // Test when parseBest returns unsupported type
        assertFalse(validator.isValid("invalid", null));
    }

    @Test
    void testExceptionHandling() {
        validator.initialize(getAnnotation("defaultFutureDate"));
        
        // Test exception handling
        assertFalse(validator.isValid("completely-invalid", null));
    }

    @Test
    void testBoundaryConditionExactlyNow() {
        validator.initialize(getAnnotation("dateTimeFutureDate"));
        
        // Test exactly at now (should be invalid - not future)
        LocalDateTime now = LocalDateTime.now();
        assertFalse(validator.isValid(now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), null));
    }

    @Test
    void testDateTimeParseException() {
        validator.initialize(getAnnotation("defaultFutureDate"));
        
        // Test DateTimeParseException handling
        assertFalse(validator.isValid("invalid-format", null));
    }

    @Test
    void testStrictParsingZonedDateTimePast() throws NoSuchFieldException {
        class TestDummy {
            @ValidFutureDate(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
            String field;
        }
        
        Field f = TestDummy.class.getDeclaredField("field");
        ValidFutureDate annotation = f.getAnnotation(ValidFutureDate.class);
        validator.initialize(annotation);
        
        ZonedDateTime past = ZonedDateTime.now().minusDays(1);
        String pastStr = past.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"));
        assertFalse(validator.isValid(pastStr, null));
    }

    @Test
    void testStrictParsingZonedDateTimeFuture() throws NoSuchFieldException {
        class TestDummy {
            @ValidFutureDate(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
            String field;
        }
        
        Field f = TestDummy.class.getDeclaredField("field");
        ValidFutureDate annotation = f.getAnnotation(ValidFutureDate.class);
        validator.initialize(annotation);
        
        ZonedDateTime future = ZonedDateTime.now().plusDays(1);
        String futureStr = future.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"));
        assertTrue(validator.isValid(futureStr, null));
    }

    @Test
    void testStrictParsingLocalDateTimePast() {
        validator.initialize(getAnnotation("dateTimeFutureDate"));
        
        LocalDateTime past = LocalDateTime.now().minusDays(1);
        assertFalse(validator.isValid(past.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), null));
    }

    @Test
    void testStrictParsingLocalDateTimeFuture() {
        validator.initialize(getAnnotation("dateTimeFutureDate"));
        
        LocalDateTime future = LocalDateTime.now().plusDays(1);
        assertTrue(validator.isValid(future.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), null));
    }

    @Test
    void testStrictParsingLocalDatePast() {
        validator.initialize(getAnnotation("defaultFutureDate"));
        
        LocalDate past = LocalDate.now().minusDays(1);
        assertFalse(validator.isValid(past.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));
    }

    @Test
    void testStrictParsingLocalDateFuture() {
        validator.initialize(getAnnotation("defaultFutureDate"));
        
        LocalDate future = LocalDate.now().plusDays(1);
        assertTrue(validator.isValid(future.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));
    }

    @Test
    void testStrictParsingZonedDateTimeValidLeapYearFuture() throws NoSuchFieldException {
        class TestDummy {
            @ValidFutureDate(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
            String field;
        }
        
        Field f = TestDummy.class.getDeclaredField("field");
        ValidFutureDate annotation = f.getAnnotation(ValidFutureDate.class);
        validator.initialize(annotation);
        
        // Test with valid leap year in future
        ZonedDateTime futureLeap = ZonedDateTime.of(2028, 2, 29, 10, 30, 0, 0, ZonedDateTime.now().getZone());
        if (futureLeap.isAfter(ZonedDateTime.now())) {
            String futureLeapStr = futureLeap.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"));
            assertTrue(validator.isValid(futureLeapStr, null));
        }
    }

    @Test
    void testStrictParsingLocalDateTimeValidLeapYearFuture() {
        validator.initialize(getAnnotation("dateTimeFutureDate"));
        
        // Test with valid leap year in future
        LocalDateTime futureLeap = LocalDateTime.of(2028, 2, 29, 10, 30, 0);
        if (futureLeap.isAfter(LocalDateTime.now())) {
            assertTrue(validator.isValid(futureLeap.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), null));
        }
    }

    @Test
    void testStrictParsingLocalDateValidLeapYearFuture() {
        validator.initialize(getAnnotation("defaultFutureDate"));
        
        // Test with valid leap year in future
        LocalDate futureLeap = LocalDate.of(2028, 2, 29);
        if (futureLeap.isAfter(LocalDate.now())) {
            assertTrue(validator.isValid(futureLeap.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));
        }
    }
}
