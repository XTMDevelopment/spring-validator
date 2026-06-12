package id.xtramile.validator.validator.datetime;

import id.xtramile.validator.annotation.datetime.ValidPastDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PastDateValidatorTest {

    private PastDateValidator validator;

    private static ValidPastDate getAnnotation(String fieldName) {
        try {
            Field f = PastDateDummy.class.getDeclaredField(fieldName);
            return f.getAnnotation(ValidPastDate.class);
        } catch (NoSuchFieldException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
        validator = new PastDateValidator();
    }

    @Test
    void testValidPastDates() {
        validator.initialize(getAnnotation("defaultPastDate"));

        // Test with dates in the past
        LocalDate yesterday = LocalDate.now().minusDays(1);
        assertTrue(validator.isValid(yesterday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));

        LocalDate lastWeek = LocalDate.now().minusWeeks(1);
        assertTrue(validator.isValid(lastWeek.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));

        LocalDate lastMonth = LocalDate.now().minusMonths(1);
        assertTrue(validator.isValid(lastMonth.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));

        LocalDate lastYear = LocalDate.now().minusYears(1);
        assertTrue(validator.isValid(lastYear.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));
    }

    @Test
    void testInvalidPastDates() {
        validator.initialize(getAnnotation("defaultPastDate"));

        // Test with dates in the future
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        assertFalse(validator.isValid(tomorrow.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));

        LocalDate nextWeek = LocalDate.now().plusWeeks(1);
        assertFalse(validator.isValid(nextWeek.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));

        LocalDate nextMonth = LocalDate.now().plusMonths(1);
        assertFalse(validator.isValid(nextMonth.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));

        LocalDate nextYear = LocalDate.now().plusYears(1);
        assertFalse(validator.isValid(nextYear.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));
    }

    @Test
    void testCustomPatternPastDates() {
        validator.initialize(getAnnotation("customPatternPastDate"));

        // Test with custom pattern (dd/MM/yyyy)
        LocalDate yesterday = LocalDate.now().minusDays(1);
        assertTrue(validator.isValid(yesterday.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), null));

        LocalDate lastWeek = LocalDate.now().minusWeeks(1);
        assertTrue(validator.isValid(lastWeek.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), null));

        // Test with future dates
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        assertFalse(validator.isValid(tomorrow.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), null));
    }

    @Test
    void testDateTimePastDates() {
        validator.initialize(getAnnotation("dateTimePastDate"));

        // Test with past datetime
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        assertTrue(validator.isValid(yesterday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), null));

        LocalDateTime lastHour = LocalDateTime.now().minusHours(1);
        assertTrue(validator.isValid(lastHour.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), null));

        // Test with future datetime
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
        assertFalse(validator.isValid(tomorrow.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), null));

        LocalDateTime nextHour = LocalDateTime.now().plusHours(1);
        assertFalse(validator.isValid(nextHour.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), null));
    }

    @Test
    void testBlankValues() {
        validator.initialize(getAnnotation("defaultPastDate"));

        assertTrue(validator.isValid("", null));
        assertTrue(validator.isValid("   ", null));
        assertTrue(validator.isValid(null, null));
    }

    @Test
    void testInvalidDateFormats() {
        validator.initialize(getAnnotation("defaultPastDate"));

        // Test with invalid date formats
        assertFalse(validator.isValid("invalid-date", null));
        assertFalse(validator.isValid("2023-13-01", null)); // invalid month
        assertFalse(validator.isValid("2023-12-32", null)); // invalid day
        assertFalse(validator.isValid("2023/12/25", null)); // wrong separator
        assertFalse(validator.isValid("25-12-2023", null)); // wrong order
    }

    @Test
    void testEdgeCases() {
        validator.initialize(getAnnotation("defaultPastDate"));

        // Test with spaces
        LocalDate yesterday = LocalDate.now().minusDays(1);
        String pastDate = yesterday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        assertFalse(validator.isValid(" " + pastDate, null)); // leading space
        assertFalse(validator.isValid(pastDate + " ", null)); // trailing space
        assertFalse(validator.isValid("2023 -12-25", null)); // space in date
    }

    @Test
    void testRealWorldExamples() {
        validator.initialize(getAnnotation("defaultPastDate"));

        // Test with specific past dates
        assertTrue(validator.isValid("2020-01-01", null)); // New Year 2020
        assertTrue(validator.isValid("2022-12-25", null)); // Christmas 2022
        assertTrue(validator.isValid("2023-07-04", null)); // Independence Day 2023
        assertTrue(validator.isValid("2023-06-15", null)); // Mid year 2023

        // Test with future dates
        assertFalse(validator.isValid("2028-01-01", null)); // New Year 2028
        assertFalse(validator.isValid("2027-12-25", null)); // Christmas 2027
        assertFalse(validator.isValid("2027-07-04", null)); // Independence Day 2027
        assertFalse(validator.isValid("2027-06-15", null)); // Mid year 2027
    }

    @Test
    void testPatternVariations() {
        // Test different pattern configurations
        validator.initialize(getAnnotation("customPatternPastDate"));
        LocalDate yesterday = LocalDate.now().minusDays(1);
        assertTrue(validator.isValid(yesterday.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), null));
        assertFalse(validator.isValid(yesterday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));

        validator.initialize(getAnnotation("dateTimePastDate"));
        LocalDateTime yesterdayDateTime = LocalDateTime.now().minusDays(1);
        assertTrue(validator.isValid(yesterdayDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), null));
        assertFalse(validator.isValid(yesterdayDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), null));
    }

    @Test
    void testLeapYearPastDates() {
        validator.initialize(getAnnotation("defaultPastDate"));

        // Test leap year dates in the past
        assertTrue(validator.isValid("2020-02-29", null)); // 2020 is leap year
        assertTrue(validator.isValid("2016-02-29", null)); // 2016 is leap year
        assertTrue(validator.isValid("2012-02-29", null)); // 2012 is leap year

        // Test non-leap year dates
        assertFalse(validator.isValid("2021-02-29", null)); // 2021 is not leap year
        assertFalse(validator.isValid("2019-02-29", null)); // 2019 is not leap year
        assertFalse(validator.isValid("2018-02-29", null)); // 2018 is not leap year
    }

    @Test
    void testDateTimeWithTimezones() {
        validator.initialize(getAnnotation("dateTimePastDate"));

        // Test with timezone-aware datetime
        ZonedDateTime yesterday = ZonedDateTime.now().minusDays(1);
        String pastDateTime = yesterday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        assertTrue(validator.isValid(pastDateTime, null));

        ZonedDateTime tomorrow = ZonedDateTime.now().plusDays(1);
        String futureDateTime = tomorrow.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        assertFalse(validator.isValid(futureDateTime, null));
    }

    @Test
    void testBoundaryConditions() {
        validator.initialize(getAnnotation("defaultPastDate"));

        // Test with dates very close to today
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        LocalDate yesterday = today.minusDays(1);

        // Today should be invalid (not past)
        assertFalse(validator.isValid(today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));

        // Tomorrow should be invalid (future)
        assertFalse(validator.isValid(tomorrow.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));

        // Yesterday should be valid (past)
        assertTrue(validator.isValid(yesterday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));
    }

    @Test
    void testHistoricalDates() {
        validator.initialize(getAnnotation("defaultPastDate"));

        // Test with historical dates
        assertTrue(validator.isValid("1900-01-01", null)); // Turn of century
        assertTrue(validator.isValid("2000-01-01", null)); // Y2K
        assertTrue(validator.isValid("1999-12-31", null)); // End of millennium
        assertTrue(validator.isValid("1980-01-01", null)); // 1980s
        assertTrue(validator.isValid("1970-01-01", null)); // Unix epoch

        // Test with very old dates
        assertTrue(validator.isValid("1900-01-01", null)); // 1900
        assertTrue(validator.isValid("1800-01-01", null)); // 1800
        assertTrue(validator.isValid("1700-01-01", null)); // 1700
    }

    @Test
    void testSmartParsingWithZonedDateTime() throws NoSuchFieldException {
        class TestDummy {
            @ValidPastDate(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
            String field;
        }

        Field f = TestDummy.class.getDeclaredField("field");
        ValidPastDate annotation = f.getAnnotation(ValidPastDate.class);
        validator.initialize(annotation);

        ZonedDateTime past = ZonedDateTime.now().minusDays(1);
        ZonedDateTime future = ZonedDateTime.now().plusDays(1);

        String pastStr = past.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"));
        String futureStr = future.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"));

        assertTrue(validator.isValid(pastStr, null));
        assertFalse(validator.isValid(futureStr, null));
    }

    @Test
    void testSmartParsingWithZonedDateTimeLeapYear() throws NoSuchFieldException {
        class TestDummy {
            @ValidPastDate(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
            String field;
        }

        Field f = TestDummy.class.getDeclaredField("field");
        ValidPastDate annotation = f.getAnnotation(ValidPastDate.class);
        validator.initialize(annotation);

        // Test valid leap year in past
        ZonedDateTime validLeap = ZonedDateTime.of(2024, 2, 29, 10, 30, 0, 0, ZonedDateTime.now().getZone());
        if (validLeap.isBefore(ZonedDateTime.now())) {
            String validLeapStr = validLeap.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"));
            assertTrue(validator.isValid(validLeapStr, null));
        }

        // Test invalid leap year
        assertFalse(validator.isValid("2023-02-29T10:30:00+07:00", null));
    }

    @Test
    void testSmartParsingWithLocalDateTimeLeapYear() {
        validator.initialize(getAnnotation("dateTimePastDate"));

        // Test valid leap year in past
        LocalDateTime validLeap = LocalDateTime.of(2024, 2, 29, 10, 30, 0);
        if (validLeap.isBefore(LocalDateTime.now())) {
            assertTrue(validator.isValid(validLeap.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), null));
        }

        // Test invalid leap year
        assertFalse(validator.isValid("2023-02-29 10:30:00", null));
    }

    @Test
    void testSmartParsingWithLocalDateLeapYear() {
        validator.initialize(getAnnotation("defaultPastDate"));

        // Test valid leap year in past
        LocalDate validLeap = LocalDate.of(2024, 2, 29);
        if (validLeap.isBefore(LocalDate.now())) {
            assertTrue(validator.isValid(validLeap.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));
        }

        // Test invalid leap year
        assertFalse(validator.isValid("2023-02-29", null));
    }

    @Test
    void testStrictParsingWithZonedDateTime() throws NoSuchFieldException {
        class TestDummy {
            @ValidPastDate(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
            String field;
        }

        Field f = TestDummy.class.getDeclaredField("field");
        ValidPastDate annotation = f.getAnnotation(ValidPastDate.class);
        validator.initialize(annotation);

        ZonedDateTime past = ZonedDateTime.now().minusDays(1);
        ZonedDateTime future = ZonedDateTime.now().plusDays(1);

        String pastStr = past.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"));
        String futureStr = future.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"));

        assertTrue(validator.isValid(pastStr, null));
        assertFalse(validator.isValid(futureStr, null));
    }

    @Test
    void testStrictParsingWithLocalDateTime() {
        validator.initialize(getAnnotation("dateTimePastDate"));

        LocalDateTime past = LocalDateTime.now().minusDays(1);
        LocalDateTime future = LocalDateTime.now().plusDays(1);

        assertTrue(validator.isValid(past.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), null));
        assertFalse(validator.isValid(future.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), null));
    }

    @Test
    void testStrictParsingWithLocalDate() {
        validator.initialize(getAnnotation("defaultPastDate"));

        LocalDate past = LocalDate.now().minusDays(1);
        LocalDate future = LocalDate.now().plusDays(1);

        assertTrue(validator.isValid(past.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));
        assertFalse(validator.isValid(future.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));
    }

    @Test
    void testUnsupportedTemporalType() {
        validator.initialize(getAnnotation("defaultPastDate"));

        // Test when parseBest returns unsupported type
        assertFalse(validator.isValid("invalid", null));
    }

    @Test
    void testSmartParsingException() {
        validator.initialize(getAnnotation("defaultPastDate"));

        // Test exception in smart parsing
        assertFalse(validator.isValid("completely-invalid", null));
    }

    @Test
    void testBoundaryConditionExactlyNow() {
        validator.initialize(getAnnotation("dateTimePastDate"));

        // Test exactly at now (should be invalid - not past)
        // However, due to timing, the now value might be slightly different when parsed
        // So we test with a value that's definitely in the past
        LocalDateTime past = LocalDateTime.now().minusSeconds(1);
        assertTrue(validator.isValid(past.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), null));

        // Test with a future value
        LocalDateTime future = LocalDateTime.now().plusSeconds(1);
        assertFalse(validator.isValid(future.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), null));
    }

    @Test
    void testStrictParsingZonedDateTimePast() throws NoSuchFieldException {
        class TestDummy {
            @ValidPastDate(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
            String field;
        }

        Field f = TestDummy.class.getDeclaredField("field");
        ValidPastDate annotation = f.getAnnotation(ValidPastDate.class);
        validator.initialize(annotation);

        ZonedDateTime past = ZonedDateTime.now().minusDays(1);
        ZonedDateTime future = ZonedDateTime.now().plusDays(1);

        String pastStr = past.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"));
        String futureStr = future.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"));

        assertTrue(validator.isValid(pastStr, null));
        assertFalse(validator.isValid(futureStr, null));
    }

    @Test
    void testStrictParsingZonedDateTimeFuture() throws NoSuchFieldException {
        class TestDummy {
            @ValidPastDate(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
            String field;
        }

        Field f = TestDummy.class.getDeclaredField("field");
        ValidPastDate annotation = f.getAnnotation(ValidPastDate.class);
        validator.initialize(annotation);

        ZonedDateTime future = ZonedDateTime.now().plusDays(1);
        String futureStr = future.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"));
        assertFalse(validator.isValid(futureStr, null));
    }

    @Test
    void testStrictParsingLocalDateTimePast() {
        validator.initialize(getAnnotation("dateTimePastDate"));

        LocalDateTime past = LocalDateTime.now().minusDays(1);
        LocalDateTime future = LocalDateTime.now().plusDays(1);

        assertTrue(validator.isValid(past.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), null));
        assertFalse(validator.isValid(future.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), null));
    }

    @Test
    void testStrictParsingLocalDateTimeFuture() {
        validator.initialize(getAnnotation("dateTimePastDate"));

        LocalDateTime future = LocalDateTime.now().plusDays(1);
        assertFalse(validator.isValid(future.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), null));
    }

    @Test
    void testStrictParsingLocalDatePast() {
        validator.initialize(getAnnotation("defaultPastDate"));

        LocalDate past = LocalDate.now().minusDays(1);
        LocalDate future = LocalDate.now().plusDays(1);

        assertTrue(validator.isValid(past.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));
        assertFalse(validator.isValid(future.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));
    }

    @Test
    void testStrictParsingLocalDateFuture() {
        validator.initialize(getAnnotation("defaultPastDate"));

        LocalDate future = LocalDate.now().plusDays(1);
        assertFalse(validator.isValid(future.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));
    }

    @Test
    void testSmartParsingZonedDateTimePast() throws NoSuchFieldException {
        class TestDummy {
            @ValidPastDate(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
            String field;
        }

        Field f = TestDummy.class.getDeclaredField("field");
        ValidPastDate annotation = f.getAnnotation(ValidPastDate.class);
        validator.initialize(annotation);

        ZonedDateTime past = ZonedDateTime.now().minusDays(1);
        String pastStr = past.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"));
        assertTrue(validator.isValid(pastStr, null));
    }

    @Test
    void testSmartParsingZonedDateTimeFuture() throws NoSuchFieldException {
        class TestDummy {
            @ValidPastDate(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
            String field;
        }

        Field f = TestDummy.class.getDeclaredField("field");
        ValidPastDate annotation = f.getAnnotation(ValidPastDate.class);
        validator.initialize(annotation);

        ZonedDateTime future = ZonedDateTime.now().plusDays(1);
        String futureStr = future.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"));
        assertFalse(validator.isValid(futureStr, null));
    }

    @Test
    void testSmartParsingLocalDateTimePast() {
        validator.initialize(getAnnotation("dateTimePastDate"));

        LocalDateTime past = LocalDateTime.now().minusDays(1);
        assertTrue(validator.isValid(past.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), null));
    }

    @Test
    void testSmartParsingLocalDateTimeFuture() {
        validator.initialize(getAnnotation("dateTimePastDate"));

        LocalDateTime future = LocalDateTime.now().plusDays(1);
        assertFalse(validator.isValid(future.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), null));
    }

    @Test
    void testSmartParsingLocalDatePast() {
        validator.initialize(getAnnotation("defaultPastDate"));

        LocalDate past = LocalDate.now().minusDays(1);
        assertTrue(validator.isValid(past.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));
    }

    @Test
    void testSmartParsingLocalDateFuture() {
        validator.initialize(getAnnotation("defaultPastDate"));

        LocalDate future = LocalDate.now().plusDays(1);
        assertFalse(validator.isValid(future.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));
    }

    private static class PastDateDummy {
        @ValidPastDate
        String defaultPastDate;

        @ValidPastDate(pattern = "dd/MM/yyyy")
        String customPatternPastDate;

        @ValidPastDate(pattern = "yyyy-MM-dd HH:mm:ss")
        String dateTimePastDate;
    }
}
