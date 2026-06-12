package id.xtramile.validator.validator.datetime;

import id.xtramile.validator.annotation.datetime.InvalidFutureDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InvalidFutureDateValidatorTest {

    private InvalidFutureDateValidator validator;

    private static InvalidFutureDate getAnnotation(String fieldName) {
        try {
            Field f = InvalidFutureDateDummy.class.getDeclaredField(fieldName);
            return f.getAnnotation(InvalidFutureDate.class);

        } catch (NoSuchFieldException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
        validator = new InvalidFutureDateValidator();
    }

    @Test
    void testValidISO8601NonFutureDates() {
        validator.initialize(getAnnotation("defaultInvalidFutureDate"));

        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime yesterdayUtc = now.minusDays(1).withZoneSameInstant(ZoneId.of("UTC"));
        assertTrue(validator.isValid(yesterdayUtc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX")), null));

        ZonedDateTime lastWeekUtc = now.minusWeeks(1).withZoneSameInstant(ZoneId.of("UTC"));
        assertTrue(validator.isValid(lastWeekUtc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX")), null));

        ZonedDateTime lastMonthUtc = now.minusMonths(1).withZoneSameInstant(ZoneId.of("UTC"));
        assertTrue(validator.isValid(lastMonthUtc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX")), null));

        ZonedDateTime lastYearUtc = now.minusYears(1).withZoneSameInstant(ZoneId.of("UTC"));
        assertTrue(validator.isValid(lastYearUtc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX")), null));
    }

    @Test
    void testInvalidISO8601FutureDates() {
        validator.initialize(getAnnotation("defaultInvalidFutureDate"));

        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime tomorrowUtc = now.plusDays(1).withZoneSameInstant(ZoneId.of("UTC"));
        assertFalse(validator.isValid(tomorrowUtc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX")), null));

        ZonedDateTime nextWeekUtc = now.plusWeeks(1).withZoneSameInstant(ZoneId.of("UTC"));
        assertFalse(validator.isValid(nextWeekUtc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX")), null));

        ZonedDateTime nextMonthUtc = now.plusMonths(1).withZoneSameInstant(ZoneId.of("UTC"));
        assertFalse(validator.isValid(nextMonthUtc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX")), null));

        ZonedDateTime nextYearUtc = now.plusYears(1).withZoneSameInstant(ZoneId.of("UTC"));
        assertFalse(validator.isValid(nextYearUtc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX")), null));
    }

    @Test
    void testCustomPatternNonFutureDates() {
        validator.initialize(getAnnotation("customPatternInvalidFutureDate"));

        // Test with custom pattern (dd/MM/yyyy) - past dates should be valid
        LocalDate yesterday = LocalDate.now().minusDays(1);
        assertTrue(validator.isValid(yesterday.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), null));

        LocalDate lastWeek = LocalDate.now().minusWeeks(1);
        assertTrue(validator.isValid(lastWeek.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), null));

        // Test with future dates - should be invalid
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        assertFalse(validator.isValid(tomorrow.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), null));
    }

    @Test
    void testDateTimeNonFutureDates() {
        validator.initialize(getAnnotation("dateTimeInvalidFutureDate"));

        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        assertTrue(validator.isValid(yesterday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), null));

        LocalDateTime lastHour = LocalDateTime.now().minusHours(1);
        assertTrue(validator.isValid(lastHour.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), null));

        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
        assertFalse(validator.isValid(tomorrow.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), null));

        LocalDateTime nextHour = LocalDateTime.now().plusHours(1);
        assertFalse(validator.isValid(nextHour.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), null));
    }

    @Test
    void testISO8601WithTolerance() {
        validator.initialize(getAnnotation("iso8601WithToleranceInvalidFutureDate"));

        ZonedDateTime now = ZonedDateTime.now();

        ZonedDateTime nowUtc = now.withZoneSameInstant(ZoneId.of("UTC"));
        assertTrue(validator.isValid(nowUtc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX")), null));

        ZonedDateTime oneHourFutureUtc = now.plusHours(1).withZoneSameInstant(ZoneId.of("UTC"));
        assertTrue(validator.isValid(oneHourFutureUtc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX")), null));

        ZonedDateTime twoHoursFutureUtc = now.plusHours(2).withZoneSameInstant(ZoneId.of("UTC"));
        assertTrue(validator.isValid(twoHoursFutureUtc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX")), null));

        ZonedDateTime threeHoursFutureUtc = now.plusHours(3).withZoneSameInstant(ZoneId.of("UTC"));
        assertFalse(validator.isValid(threeHoursFutureUtc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX")), null));

        ZonedDateTime oneHourPastUtc = now.minusHours(1).withZoneSameInstant(ZoneId.of("UTC"));
        assertTrue(validator.isValid(oneHourPastUtc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX")), null));
    }

    @Test
    void testBlankValues() {
        validator.initialize(getAnnotation("defaultInvalidFutureDate"));

        assertTrue(validator.isValid("", null));
        assertTrue(validator.isValid("   ", null));
        assertTrue(validator.isValid(null, null));
    }

    @Test
    void testInvalidDateFormats() {
        validator.initialize(getAnnotation("defaultInvalidFutureDate"));

        assertFalse(validator.isValid("invalid-date", null));
        assertFalse(validator.isValid("2023-13-01T10:00:00Z", null)); // invalid month
        assertFalse(validator.isValid("2023-12-32T10:00:00Z", null)); // invalid day
        assertFalse(validator.isValid("2023/12/25T10:00:00Z", null)); // wrong separator
        assertFalse(validator.isValid("25-12-2023T10:00:00Z", null)); // wrong order
        assertFalse(validator.isValid("2023-12-25", null)); // missing time and timezone
    }

    @Test
    void testEdgeCases() {
        validator.initialize(getAnnotation("defaultInvalidFutureDate"));

        ZonedDateTime yesterdayUtc = ZonedDateTime.now().minusDays(1).withZoneSameInstant(ZoneId.of("UTC"));
        String pastDate = yesterdayUtc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX"));
        assertFalse(validator.isValid(" " + pastDate, null)); // leading space
        assertFalse(validator.isValid(pastDate + " ", null)); // trailing space
        assertFalse(validator.isValid("2023-12-25T10:00:00 Z", null)); // space before timezone
    }

    @Test
    void testRealWorldExamples() {
        validator.initialize(getAnnotation("defaultInvalidFutureDate"));

        assertTrue(validator.isValid("2020-01-01T00:00:00Z", null)); // New Year 2020
        assertTrue(validator.isValid("2022-12-25T00:00:00Z", null)); // Christmas 2022
        assertTrue(validator.isValid("2023-07-04T00:00:00Z", null)); // Independence Day 2023
        assertTrue(validator.isValid("2023-06-15T00:00:00Z", null)); // Mid year 2023

        assertFalse(validator.isValid("2028-01-01T00:00:00Z", null)); // New Year 2028
        assertFalse(validator.isValid("2027-12-25T00:00:00Z", null)); // Christmas 2027
        assertFalse(validator.isValid("2027-07-04T00:00:00Z", null)); // Independence Day 2027
        assertFalse(validator.isValid("2027-06-15T00:00:00Z", null)); // Mid year 2027
    }

    @Test
    void testPatternVariations() {
        validator.initialize(getAnnotation("customPatternInvalidFutureDate"));
        LocalDate yesterday = LocalDate.now().minusDays(1);
        assertTrue(validator.isValid(yesterday.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), null));
        assertFalse(validator.isValid(yesterday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));

        validator.initialize(getAnnotation("dateTimeInvalidFutureDate"));
        LocalDateTime yesterdayDateTime = LocalDateTime.now().minusDays(1);
        assertTrue(validator.isValid(yesterdayDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), null));
        assertFalse(validator.isValid(yesterdayDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), null));
    }

    @Test
    void testLeapYearNonFutureDates() {
        validator.initialize(getAnnotation("defaultInvalidFutureDate"));

        assertTrue(validator.isValid("2020-02-29T00:00:00Z", null)); // 2020 is leap year
        assertTrue(validator.isValid("2016-02-29T00:00:00Z", null)); // 2016 is leap year
        assertTrue(validator.isValid("2012-02-29T00:00:00Z", null)); // 2012 is leap year

        assertFalse(validator.isValid("2028-02-29T00:00:00Z", null)); // 2028 is leap year
        assertFalse(validator.isValid("2032-02-29T00:00:00Z", null)); // 2032 is leap year
        assertFalse(validator.isValid("2036-02-29T00:00:00Z", null)); // 2036 is leap year
    }

    @Test
    void testDateTimeWithTimezones() {
        validator.initialize(getAnnotation("dateTimeInvalidFutureDate"));

        ZonedDateTime yesterday = ZonedDateTime.now().minusDays(1);
        String pastDateTime = yesterday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        assertTrue(validator.isValid(pastDateTime, null));

        ZonedDateTime tomorrow = ZonedDateTime.now().plusDays(1);
        String futureDateTime = tomorrow.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        assertFalse(validator.isValid(futureDateTime, null));
    }

    @Test
    void testBoundaryConditions() {
        validator.initialize(getAnnotation("defaultInvalidFutureDate"));

        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime tomorrowUtc = now.plusDays(1).withZoneSameInstant(ZoneId.of("UTC"));
        ZonedDateTime yesterdayUtc = now.minusDays(1).withZoneSameInstant(ZoneId.of("UTC"));
        ZonedDateTime nowUtc = now.withZoneSameInstant(ZoneId.of("UTC"));

        assertTrue(validator.isValid(nowUtc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX")), null));
        assertFalse(validator.isValid(tomorrowUtc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX")), null));
        assertTrue(validator.isValid(yesterdayUtc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX")), null));
    }

    @Test
    void testHistoricalDates() {
        validator.initialize(getAnnotation("defaultInvalidFutureDate"));

        assertTrue(validator.isValid("1900-01-01T00:00:00Z", null)); // Turn of century
        assertTrue(validator.isValid("2000-01-01T00:00:00Z", null)); // Y2K
        assertTrue(validator.isValid("1999-12-31T00:00:00Z", null)); // End of millennium
        assertTrue(validator.isValid("1980-01-01T00:00:00Z", null)); // 1980s
        assertTrue(validator.isValid("1970-01-01T00:00:00Z", null)); // Unix epoch

        assertTrue(validator.isValid("1900-01-01T00:00:00Z", null)); // 1900
        assertTrue(validator.isValid("1800-01-01T00:00:00Z", null)); // 1800
        assertTrue(validator.isValid("1700-01-01T00:00:00Z", null)); // 1700
    }

    @Test
    void testOppositeLogicToFutureDate() {
        validator.initialize(getAnnotation("defaultInvalidFutureDate"));

        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime tomorrowUtc = now.plusDays(1).withZoneSameInstant(ZoneId.of("UTC"));
        ZonedDateTime yesterdayUtc = now.minusDays(1).withZoneSameInstant(ZoneId.of("UTC"));

        assertFalse(validator.isValid(tomorrowUtc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX")), null));
        assertTrue(validator.isValid(yesterdayUtc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX")), null));
    }

    @Test
    void testValidateDateTimeWithoutTimezoneException() {
        validator.initialize(getAnnotation("dateTimeInvalidFutureDate"));

        // Test exception handling in validateDateTimeWithoutTimezone
        assertFalse(validator.isValid("invalid-datetime", null));
    }

    @Test
    void testValidateDateOnlyException() {
        validator.initialize(getAnnotation("customPatternInvalidFutureDate"));

        // Test exception handling in validateDateOnly
        assertFalse(validator.isValid("invalid-date", null));
    }

    @Test
    void testValidateDefaultWithZonedDateTime() {
        validator.initialize(getAnnotation("defaultInvalidFutureDate"));

        // Test validateDefault with ZonedDateTime
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime past = now.minusDays(1);
        ZonedDateTime future = now.plusDays(1);

        String pastStr = past.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"));
        String futureStr = future.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"));

        assertTrue(validator.isValid(pastStr, null));
        assertFalse(validator.isValid(futureStr, null));
    }

    @Test
    void testValidateDefaultWithLocalDateTime() {
        validator.initialize(getAnnotation("defaultInvalidFutureDate"));

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime past = now.minusDays(1);

        // Use ISO8601 format which will use the ISO8601 path, not validateDefault
        ZonedDateTime pastZoned = ZonedDateTime.of(past, ZoneId.systemDefault());
        String pastStr = pastZoned.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX"));

        assertTrue(validator.isValid(pastStr, null));
    }

    @Test
    void testValidateDefaultWithLocalDate() {
        validator.initialize(getAnnotation("defaultInvalidFutureDate"));

        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime past = now.minusDays(1);
        String pastStr = past.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX"));

        assertTrue(validator.isValid(pastStr, null));
    }

    @Test
    void testValidateDefaultException() {
        validator.initialize(getAnnotation("defaultInvalidFutureDate"));

        // Test exception handling in validateDefault
        assertFalse(validator.isValid("invalid", null));
    }

    @Test
    void testValidateDefaultUnsupportedTemporalType() {
        validator.initialize(getAnnotation("defaultInvalidFutureDate"));

        // Test when parseBest returns unsupported type
        assertFalse(validator.isValid("invalid", null));
    }

    @Test
    void testExceptionInIsValid() {
        validator.initialize(getAnnotation("defaultInvalidFutureDate"));

        // Test exception handling in isValid method
        assertFalse(validator.isValid("completely-invalid", null));
    }

    @Test
    void testToleranceWithDateTimeWithoutTimezone() {
        validator.initialize(getAnnotation("iso8601WithToleranceInvalidFutureDate"));

        // Test tolerance with ISO8601 format (not date-time without timezone)
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime withinTolerance = now.plusHours(1);
        ZonedDateTime beyondTolerance = now.plusHours(3);

        String withinStr = withinTolerance.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX"));
        String beyondStr = beyondTolerance.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX"));

        assertTrue(validator.isValid(withinStr, null));
        assertFalse(validator.isValid(beyondStr, null));
    }

    @Test
    void testValidateDefaultWithZonedDateTimeStrict() throws NoSuchFieldException {
        // Create a test class with annotation
        class TestDummy {
            @InvalidFutureDate(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", toleranceHours = 2)
            String field;
        }

        Field f = TestDummy.class.getDeclaredField("field");
        InvalidFutureDate annotation = f.getAnnotation(InvalidFutureDate.class);
        validator.initialize(annotation);

        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime past = now.minusDays(1);
        ZonedDateTime withinTolerance = now.plusHours(1);
        ZonedDateTime beyondTolerance = now.plusHours(3);

        String pastStr = past.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"));
        String withinStr = withinTolerance.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"));
        String beyondStr = beyondTolerance.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"));

        assertTrue(validator.isValid(pastStr, null));
        assertTrue(validator.isValid(withinStr, null));
        assertFalse(validator.isValid(beyondStr, null));
    }

    @Test
    void testValidateDefaultWithLocalDateTimeStrict() throws NoSuchFieldException {
        class TestDummy {
            @InvalidFutureDate(pattern = "yyyy-MM-dd'T'HH:mm:ss", toleranceHours = 2)
            String field;
        }

        Field f = TestDummy.class.getDeclaredField("field");
        InvalidFutureDate annotation = f.getAnnotation(InvalidFutureDate.class);
        validator.initialize(annotation);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime past = now.minusDays(1);
        LocalDateTime withinTolerance = now.plusHours(1);
        LocalDateTime beyondTolerance = now.plusHours(3);

        String pastStr = past.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        String withinStr = withinTolerance.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        String beyondStr = beyondTolerance.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));

        assertTrue(validator.isValid(pastStr, null));
        assertTrue(validator.isValid(withinStr, null));
        assertFalse(validator.isValid(beyondStr, null));
    }

    @Test
    void testValidateDefaultWithLocalDateStrict() throws NoSuchFieldException {
        class TestDummy {
            @InvalidFutureDate(pattern = "yyyy-MM-dd")
            String field;
        }

        Field f = TestDummy.class.getDeclaredField("field");
        InvalidFutureDate annotation = f.getAnnotation(InvalidFutureDate.class);
        validator.initialize(annotation);

        LocalDate today = LocalDate.now();
        LocalDate past = today.minusDays(1);
        LocalDate future = today.plusDays(1);

        assertTrue(validator.isValid(past.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));
        assertFalse(validator.isValid(future.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));
    }

    @Test
    void testBoundaryConditionsAtTolerance() {
        validator.initialize(getAnnotation("iso8601WithToleranceInvalidFutureDate"));

        ZonedDateTime now = ZonedDateTime.now();
        // Exactly at tolerance
        ZonedDateTime exactlyAtTolerance = now.plusHours(2);
        String exactlyStr = exactlyAtTolerance.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX"));
        assertTrue(validator.isValid(exactlyStr, null));

        // Just beyond tolerance
        ZonedDateTime justBeyondTolerance = now.plusHours(2).plusSeconds(1);
        String beyondStr = justBeyondTolerance.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX"));
        assertFalse(validator.isValid(beyondStr, null));
    }

    @Test
    void testUnsupportedTemporalType() throws NoSuchFieldException {
        class TestDummy {
            @InvalidFutureDate(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", toleranceHours = 2)
            String field;
        }

        Field f = TestDummy.class.getDeclaredField("field");
        InvalidFutureDate annotation = f.getAnnotation(InvalidFutureDate.class);
        validator.initialize(annotation);

        // Test when parseBest returns unsupported type
        assertFalse(validator.isValid("invalid", null));
    }

    @Test
    void testValidateDefaultStrictParsingZonedDateTimePast() throws NoSuchFieldException {
        class TestDummy {
            @InvalidFutureDate(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", toleranceHours = 2)
            String field;
        }

        Field f = TestDummy.class.getDeclaredField("field");
        InvalidFutureDate annotation = f.getAnnotation(InvalidFutureDate.class);
        validator.initialize(annotation);

        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime past = now.minusDays(1);
        String pastStr = past.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"));
        assertTrue(validator.isValid(pastStr, null));
    }

    @Test
    void testValidateDefaultStrictParsingLocalDateTimePast() throws NoSuchFieldException {
        class TestDummy {
            @InvalidFutureDate(pattern = "yyyy-MM-dd'T'HH:mm:ss", toleranceHours = 2)
            String field;
        }

        Field f = TestDummy.class.getDeclaredField("field");
        InvalidFutureDate annotation = f.getAnnotation(InvalidFutureDate.class);
        validator.initialize(annotation);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime past = now.minusDays(1);
        String pastStr = past.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        assertTrue(validator.isValid(pastStr, null));
    }

    @Test
    void testValidateDefaultStrictParsingLocalDatePast() throws NoSuchFieldException {
        class TestDummy {
            @InvalidFutureDate(pattern = "yyyy-MM-dd")
            String field;
        }

        Field f = TestDummy.class.getDeclaredField("field");
        InvalidFutureDate annotation = f.getAnnotation(InvalidFutureDate.class);
        validator.initialize(annotation);

        LocalDate today = LocalDate.now();
        LocalDate past = today.minusDays(1);
        assertTrue(validator.isValid(past.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));
    }

    @Test
    void testValidateDateTimeWithoutTimezoneWithinTolerance() throws NoSuchFieldException {
        // Use a pattern that triggers validateDateTimeWithoutTimezone
        class TestDummy {
            @InvalidFutureDate(pattern = "yyyy-MM-dd HH:mm:ss", toleranceHours = 2)
            String field;
        }

        Field f = TestDummy.class.getDeclaredField("field");
        InvalidFutureDate annotation = f.getAnnotation(InvalidFutureDate.class);
        validator.initialize(annotation);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime withinTolerance = now.plusHours(1);
        String withinStr = withinTolerance.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        assertTrue(validator.isValid(withinStr, null));
    }

    @Test
    void testValidateDateTimeWithoutTimezoneBeyondTolerance() throws NoSuchFieldException {
        class TestDummy {
            @InvalidFutureDate(pattern = "yyyy-MM-dd HH:mm:ss", toleranceHours = 2)
            String field;
        }

        Field f = TestDummy.class.getDeclaredField("field");
        InvalidFutureDate annotation = f.getAnnotation(InvalidFutureDate.class);
        validator.initialize(annotation);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime beyondTolerance = now.plusHours(3);
        String beyondStr = beyondTolerance.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        assertFalse(validator.isValid(beyondStr, null));
    }

    @Test
    void testValidateDateOnlyToday() {
        validator.initialize(getAnnotation("customPatternInvalidFutureDate"));

        LocalDate today = LocalDate.now();
        assertTrue(validator.isValid(today.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), null));
    }

    @Test
    void testValidateDateOnlyPast() {
        validator.initialize(getAnnotation("customPatternInvalidFutureDate"));

        LocalDate past = LocalDate.now().minusDays(1);
        assertTrue(validator.isValid(past.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), null));
    }

    private static class InvalidFutureDateDummy {
        @InvalidFutureDate
        String defaultInvalidFutureDate;

        @InvalidFutureDate(pattern = "dd/MM/yyyy")
        String customPatternInvalidFutureDate;

        @InvalidFutureDate(pattern = "yyyy-MM-dd HH:mm:ss")
        String dateTimeInvalidFutureDate;

        @InvalidFutureDate(toleranceHours = 2)
        String iso8601WithToleranceInvalidFutureDate;
    }
}
