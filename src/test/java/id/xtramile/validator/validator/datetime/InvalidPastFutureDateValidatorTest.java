package id.xtramile.validator.validator.datetime;

import id.xtramile.validator.annotation.datetime.InvalidPastFutureDate;
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

public class InvalidPastFutureDateValidatorTest {

    private static class InvalidPastFutureDateDummy {
        @InvalidPastFutureDate(toleranceHours = 2)
        String defaultInvalidPastFutureDate;

        @InvalidPastFutureDate(pattern = "yyyy-MM-dd", toleranceHours = 48)
        String toleranceInvalidPastFutureDate;

        @InvalidPastFutureDate(pattern = "dd/MM/yyyy", toleranceHours = 24)
        String customPatternInvalidPastFutureDate;

        @InvalidPastFutureDate(pattern = "yyyy-MM-dd HH:mm:ss", toleranceHours = 2)
        String dateTimeInvalidPastFutureDate;

        @InvalidPastFutureDate(toleranceHours = 2)
        String iso8601InvalidPastFutureDate;
    }

    private InvalidPastFutureDateValidator validator;

    private static InvalidPastFutureDate getAnnotation(String fieldName) {
        try {
            Field f = InvalidPastFutureDateDummy.class.getDeclaredField(fieldName);
            return f.getAnnotation(InvalidPastFutureDate.class);

        } catch (NoSuchFieldException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
        validator = new InvalidPastFutureDateValidator();
    }

    @Test
    void testValidISO8601DatesWithinRange() {
        validator.initialize(getAnnotation("defaultInvalidPastFutureDate"));

        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime nowUtc = now.withZoneSameInstant(ZoneId.of("UTC"));
        assertTrue(validator.isValid(nowUtc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX")), null));

        ZonedDateTime oneHourAgoUtc = now.minusHours(1).withZoneSameInstant(ZoneId.of("UTC"));
        assertTrue(validator.isValid(oneHourAgoUtc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX")), null));
    }

    @Test
    void testInvalidISO8601PastDates() {
        validator.initialize(getAnnotation("defaultInvalidPastFutureDate"));

        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime farPastUtc = now.minusDays(10).withZoneSameInstant(ZoneId.of("UTC")); // beyond default tolerance
        assertFalse(validator.isValid(farPastUtc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX")), null));

        ZonedDateTime veryFarPastUtc = now.minusDays(30).withZoneSameInstant(ZoneId.of("UTC")); // very far past
        assertFalse(validator.isValid(veryFarPastUtc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX")), null));

        ZonedDateTime lastYearUtc = now.minusYears(1).withZoneSameInstant(ZoneId.of("UTC")); // last year
        assertFalse(validator.isValid(lastYearUtc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX")), null));
    }

    @Test
    void testInvalidISO8601FutureDates() {
        validator.initialize(getAnnotation("defaultInvalidPastFutureDate"));

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
    void testToleranceInvalidPastFutureDates() {
        validator.initialize(getAnnotation("toleranceInvalidPastFutureDate"));

        LocalDate withinTolerance = LocalDate.now().minusDays(1); // within 48 hours tolerance
        assertTrue(validator.isValid(withinTolerance.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));

        LocalDate atTolerance = LocalDate.now().minusDays(2); // exactly at tolerance (approximately)
        assertTrue(validator.isValid(atTolerance.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));

        LocalDate beyondTolerance = LocalDate.now().minusDays(3); // beyond 48 hours tolerance
        assertFalse(validator.isValid(beyondTolerance.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));

        LocalDate future = LocalDate.now().plusDays(1);
        assertFalse(validator.isValid(future.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));
    }

    @Test
    void testCustomPatternInvalidPastFutureDates() {
        validator.initialize(getAnnotation("customPatternInvalidPastFutureDate"));

        LocalDate withinTolerance = LocalDate.now().minusDays(0); // within 24 hours tolerance
        assertTrue(validator.isValid(withinTolerance.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), null));

        LocalDate atTolerance = LocalDate.now().minusDays(1); // approximately at tolerance
        assertTrue(validator.isValid(atTolerance.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), null));

        LocalDate beyondTolerance = LocalDate.now().minusDays(2); // beyond 24 hours tolerance
        assertFalse(validator.isValid(beyondTolerance.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), null));

        LocalDate future = LocalDate.now().plusDays(1);
        assertFalse(validator.isValid(future.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), null));
    }

    @Test
    void testDateTimeInvalidPastFutureDates() {
        validator.initialize(getAnnotation("dateTimeInvalidPastFutureDate"));

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime withinTolerance = now.minusHours(1); // within 2 hours tolerance
        assertTrue(validator.isValid(withinTolerance.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), null));

        LocalDateTime nearTolerance = now.minusHours(2).plusSeconds(1); // just within 2 hours tolerance
        assertTrue(validator.isValid(nearTolerance.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), null));

        LocalDateTime beyondTolerance = LocalDateTime.now().minusHours(3); // beyond 2 hours tolerance
        assertFalse(validator.isValid(beyondTolerance.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), null));

        LocalDateTime future = LocalDateTime.now().plusHours(1);
        assertFalse(validator.isValid(future.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), null));
    }

    @Test
    void testISO8601WithTolerance() {
        validator.initialize(getAnnotation("iso8601InvalidPastFutureDate"));

        ZonedDateTime now = ZonedDateTime.now();

        ZonedDateTime nowUtc = now.withZoneSameInstant(ZoneId.of("UTC"));
        assertTrue(validator.isValid(nowUtc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX")), null));

        ZonedDateTime oneHourAgoUtc = now.minusHours(1).withZoneSameInstant(ZoneId.of("UTC"));
        assertTrue(validator.isValid(oneHourAgoUtc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX")), null));

        ZonedDateTime twoHoursAgoUtc = now.minusHours(2).plusSeconds(1).withZoneSameInstant(ZoneId.of("UTC"));
        assertTrue(validator.isValid(twoHoursAgoUtc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX")), null));

        ZonedDateTime threeHoursAgoUtc = now.minusHours(3).withZoneSameInstant(ZoneId.of("UTC"));
        assertFalse(validator.isValid(threeHoursAgoUtc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX")), null));

        ZonedDateTime oneHourFutureUtc = now.plusHours(1).withZoneSameInstant(ZoneId.of("UTC"));
        assertFalse(validator.isValid(oneHourFutureUtc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX")), null));
    }

    @Test
    void testBlankValues() {
        validator.initialize(getAnnotation("defaultInvalidPastFutureDate"));

        assertTrue(validator.isValid("", null));
        assertTrue(validator.isValid("   ", null));
        assertTrue(validator.isValid(null, null));
    }

    @Test
    void testInvalidDateFormats() {
        validator.initialize(getAnnotation("defaultInvalidPastFutureDate"));

        assertFalse(validator.isValid("invalid-date", null));
        assertFalse(validator.isValid("2023-13-01T10:00:00Z", null)); // invalid month
        assertFalse(validator.isValid("2023-12-32T10:00:00Z", null)); // invalid day
        assertFalse(validator.isValid("2023/12/25T10:00:00Z", null)); // wrong separator
        assertFalse(validator.isValid("25-12-2023T10:00:00Z", null)); // wrong order
        assertFalse(validator.isValid("2023-12-25", null)); // missing time and timezone
    }

    @Test
    void testEdgeCases() {
        validator.initialize(getAnnotation("defaultInvalidPastFutureDate"));

        ZonedDateTime nowUtc = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC"));
        String todayDate = nowUtc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX"));
        assertFalse(validator.isValid(" " + todayDate, null)); // leading space
        assertFalse(validator.isValid(todayDate + " ", null)); // trailing space
        assertFalse(validator.isValid("2023-12-25T10:00:00 Z", null)); // space before timezone
    }

    @Test
    void testRealWorldExamples() {
        validator.initialize(getAnnotation("defaultInvalidPastFutureDate"));

        ZonedDateTime nowUtc = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC"));
        assertTrue(validator.isValid(nowUtc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX")), null));

        ZonedDateTime oneHourAgoUtc = ZonedDateTime.now().minusHours(1).withZoneSameInstant(ZoneId.of("UTC"));
        assertTrue(validator.isValid(oneHourAgoUtc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX")), null));

        assertFalse(validator.isValid("2028-01-01T00:00:00Z", null)); // New Year 2028
        assertFalse(validator.isValid("2027-12-25T00:00:00Z", null)); // Christmas 2027

        assertFalse(validator.isValid("2020-01-01T00:00:00Z", null)); // New Year 2020
        assertFalse(validator.isValid("2022-12-25T00:00:00Z", null)); // Christmas 2022
    }

    @Test
    void testToleranceBoundaries() {
        validator.initialize(getAnnotation("toleranceInvalidPastFutureDate"));

        LocalDate today = LocalDate.now();
        LocalDate withinTolerance = today.minusDays(1); // 1 day ago (within 48 hours)
        LocalDate atTolerance = today.minusDays(2); // 2 days ago (at tolerance - 48/24 = 2 days)
        LocalDate beyondTolerance = today.minusDays(3); // 3 days ago (beyond tolerance)

        assertTrue(validator.isValid(withinTolerance.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));
        assertTrue(validator.isValid(atTolerance.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));
        assertFalse(validator.isValid(beyondTolerance.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));

        LocalDate future = today.plusDays(1);
        assertFalse(validator.isValid(future.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));
    }

    @Test
    void testPatternVariations() {
        validator.initialize(getAnnotation("customPatternInvalidPastFutureDate"));
        LocalDate today = LocalDate.now();
        assertTrue(validator.isValid(today.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), null));
        assertFalse(validator.isValid(today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));

        validator.initialize(getAnnotation("dateTimeInvalidPastFutureDate"));
        LocalDateTime todayDateTime = LocalDateTime.now();
        assertTrue(validator.isValid(todayDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), null));
        assertFalse(validator.isValid(todayDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), null));
    }

    @Test
    void testLeapYearInvalidPastFutureDates() {
        validator.initialize(getAnnotation("defaultInvalidPastFutureDate"));

        assertFalse(validator.isValid("2020-02-29T00:00:00Z", null)); // 2020 is leap year
        assertFalse(validator.isValid("2016-02-29T00:00:00Z", null)); // 2016 is leap year
        assertFalse(validator.isValid("2012-02-29T00:00:00Z", null)); // 2012 is leap year

        assertFalse(validator.isValid("2024-02-29T00:00:00Z", null)); // 2024 is leap year (if future)
        assertFalse(validator.isValid("2028-02-29T00:00:00Z", null)); // 2028 is leap year
        assertFalse(validator.isValid("2032-02-29T00:00:00Z", null)); // 2032 is leap year
    }

    @Test
    void testDateTimeWithTimezones() {
        validator.initialize(getAnnotation("dateTimeInvalidPastFutureDate"));

        ZonedDateTime today = ZonedDateTime.now();
        String todayDateTime = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        assertTrue(validator.isValid(todayDateTime, null));

        ZonedDateTime future = ZonedDateTime.now().plusDays(1);
        String futureDateTime = future.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        assertFalse(validator.isValid(futureDateTime, null));

        ZonedDateTime past = ZonedDateTime.now().minusDays(2);
        String pastDateTime = past.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        assertFalse(validator.isValid(pastDateTime, null));
    }

    @Test
    void testBoundaryConditions() {
        validator.initialize(getAnnotation("defaultInvalidPastFutureDate"));

        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime tomorrowUtc = now.plusDays(1).withZoneSameInstant(ZoneId.of("UTC"));
        ZonedDateTime yesterdayUtc = now.minusDays(1).withZoneSameInstant(ZoneId.of("UTC"));
        ZonedDateTime nowUtc = now.withZoneSameInstant(ZoneId.of("UTC"));

        assertTrue(validator.isValid(nowUtc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX")), null));
        assertFalse(validator.isValid(tomorrowUtc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX")), null));
        assertFalse(validator.isValid(yesterdayUtc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX")), null));
    }

    @Test
    void testCombinedLogic() {
        validator.initialize(getAnnotation("defaultInvalidPastFutureDate"));

        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime tomorrowUtc = now.plusDays(1).withZoneSameInstant(ZoneId.of("UTC"));
        ZonedDateTime oneHourAgoUtc = now.minusHours(1).withZoneSameInstant(ZoneId.of("UTC"));
        ZonedDateTime lastWeekUtc = now.minusDays(7).withZoneSameInstant(ZoneId.of("UTC"));
        ZonedDateTime lastMonthUtc = now.minusMonths(1).withZoneSameInstant(ZoneId.of("UTC"));
        ZonedDateTime nowUtc = now.withZoneSameInstant(ZoneId.of("UTC"));

        assertTrue(validator.isValid(nowUtc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX")), null));
        assertTrue(validator.isValid(oneHourAgoUtc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX")), null));
        assertFalse(validator.isValid(tomorrowUtc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX")), null));
        assertFalse(validator.isValid(lastWeekUtc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX")), null));
        assertFalse(validator.isValid(lastMonthUtc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX")), null));
    }

    @Test
    void testValidateDateTimeWithoutTimezoneSmartParsing() {
        validator.initialize(getAnnotation("dateTimeInvalidPastFutureDate"));

        // Test smart parsing fallback when strict parsing fails
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime recent = now.minusHours(1);
        String recentStr = recent.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        assertTrue(validator.isValid(recentStr, null));

        // Test with invalid leap year in smart parsing
        assertFalse(validator.isValid("2023-02-29 10:30:00", null)); // Invalid leap year
    }

    @Test
    void testValidateDateOnlySmartParsing() {
        validator.initialize(getAnnotation("toleranceInvalidPastFutureDate"));

        // Test smart parsing fallback for date only
        LocalDate today = LocalDate.now();
        LocalDate recent = today.minusDays(1);
        assertTrue(validator.isValid(recent.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));

        // Test with invalid leap year in smart parsing
        assertFalse(validator.isValid("2023-02-29", null)); // Invalid leap year
    }

    @Test
    void testValidateDefaultWithZonedDateTimeStrict() throws NoSuchFieldException {
        // Test validateDefault path with ZonedDateTime using strict parsing
        class TestDummy {
            @InvalidPastFutureDate(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", toleranceHours = 2)
            String field;
        }
        
        Field f = TestDummy.class.getDeclaredField("field");
        InvalidPastFutureDate annotation = f.getAnnotation(InvalidPastFutureDate.class);
        validator.initialize(annotation);
        
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime recent = now.minusHours(1);
        String recentStr = recent.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"));
        assertTrue(validator.isValid(recentStr, null));
        
        ZonedDateTime future = now.plusHours(1);
        String futureStr = future.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"));
        assertFalse(validator.isValid(futureStr, null));
        
        ZonedDateTime tooPast = now.minusHours(3);
        String tooPastStr = tooPast.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"));
        assertFalse(validator.isValid(tooPastStr, null));
    }

    @Test
    void testValidateDefaultWithLocalDateTimeStrict() throws NoSuchFieldException {
        class TestDummy {
            @InvalidPastFutureDate(pattern = "yyyy-MM-dd'T'HH:mm:ss", toleranceHours = 2)
            String field;
        }
        
        Field f = TestDummy.class.getDeclaredField("field");
        InvalidPastFutureDate annotation = f.getAnnotation(InvalidPastFutureDate.class);
        validator.initialize(annotation);
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime recent = now.minusHours(1);
        String recentStr = recent.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        assertTrue(validator.isValid(recentStr, null));
        
        LocalDateTime future = now.plusHours(1);
        String futureStr = future.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        assertFalse(validator.isValid(futureStr, null));
    }

    @Test
    void testValidateDefaultWithLocalDateStrict() throws NoSuchFieldException {
        class TestDummy {
            @InvalidPastFutureDate(pattern = "yyyy-MM-dd", toleranceHours = 48)
            String field;
        }
        
        Field f = TestDummy.class.getDeclaredField("field");
        InvalidPastFutureDate annotation = f.getAnnotation(InvalidPastFutureDate.class);
        validator.initialize(annotation);
        
        LocalDate today = LocalDate.now();
        LocalDate recent = today.minusDays(1);
        assertTrue(validator.isValid(recent.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));
        
        LocalDate future = today.plusDays(1);
        assertFalse(validator.isValid(future.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));
    }

    @Test
    void testValidateDefaultSmartParsingWithZonedDateTime() throws NoSuchFieldException {
        class TestDummy {
            @InvalidPastFutureDate(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", toleranceHours = 2)
            String field;
        }
        
        Field f = TestDummy.class.getDeclaredField("field");
        InvalidPastFutureDate annotation = f.getAnnotation(InvalidPastFutureDate.class);
        validator.initialize(annotation);
        
        // Test smart parsing in validateDefault with ZonedDateTime and leap year check
        assertFalse(validator.isValid("2023-02-29T10:30:00+07:00", null)); // Invalid leap year
        
        // Test valid leap year in smart parsing
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime validLeap = ZonedDateTime.of(2024, 2, 29, 10, 30, 0, 0, now.getZone());
        if (validLeap.isBefore(now) && validLeap.isAfter(now.minusHours(2))) {
            assertTrue(validator.isValid(validLeap.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")), null));
        }
        
        // Test valid date within tolerance
        ZonedDateTime recent = now.minusHours(1);
        String recentStr = recent.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"));
        assertTrue(validator.isValid(recentStr, null));
    }

    @Test
    void testValidateDefaultSmartParsingWithLocalDateTime() throws NoSuchFieldException {
        class TestDummy {
            @InvalidPastFutureDate(pattern = "yyyy-MM-dd'T'HH:mm:ss", toleranceHours = 2)
            String field;
        }
        
        Field f = TestDummy.class.getDeclaredField("field");
        InvalidPastFutureDate annotation = f.getAnnotation(InvalidPastFutureDate.class);
        validator.initialize(annotation);
        
        // Test smart parsing in validateDefault with LocalDateTime and leap year check
        assertFalse(validator.isValid("2023-02-29T10:30:00", null)); // Invalid leap year
        
        // Test valid date within tolerance
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime recent = now.minusHours(1);
        String recentStr = recent.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        assertTrue(validator.isValid(recentStr, null));
    }

    @Test
    void testValidateDefaultSmartParsingWithLocalDate() throws NoSuchFieldException {
        class TestDummy {
            @InvalidPastFutureDate(pattern = "yyyy-MM-dd", toleranceHours = 48)
            String field;
        }
        
        Field f = TestDummy.class.getDeclaredField("field");
        InvalidPastFutureDate annotation = f.getAnnotation(InvalidPastFutureDate.class);
        validator.initialize(annotation);
        
        // Test smart parsing in validateDefault with LocalDate and leap year check
        assertFalse(validator.isValid("2023-02-29", null)); // Invalid leap year
        
        // Test valid date within tolerance
        LocalDate today = LocalDate.now();
        LocalDate recent = today.minusDays(1);
        assertTrue(validator.isValid(recent.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));
    }

    @Test
    void testExceptionHandling() {
        validator.initialize(getAnnotation("defaultInvalidPastFutureDate"));
        
        // Test exception handling in isValid
        assertFalse(validator.isValid("completely-invalid", null));
    }

    @Test
    void testValidateDefaultWithUnsupportedTemporalType() throws NoSuchFieldException {
        class TestDummy {
            @InvalidPastFutureDate(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", toleranceHours = 2)
            String field;
        }
        
        Field f = TestDummy.class.getDeclaredField("field");
        InvalidPastFutureDate annotation = f.getAnnotation(InvalidPastFutureDate.class);
        validator.initialize(annotation);
        
        // Test when parseBest returns a type that's not ZonedDateTime, LocalDateTime, or LocalDate
        // This should trigger the "pattern" violation message
        assertFalse(validator.isValid("invalid", null));
    }

    @Test
    void testValidateDateTimeWithoutTimezoneStrictParsing() {
        validator.initialize(getAnnotation("dateTimeInvalidPastFutureDate"));
        
        // Test strict parsing path
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime withinTolerance = now.minusHours(1);
        assertTrue(validator.isValid(withinTolerance.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), null));
        
        LocalDateTime beyondTolerance = now.minusHours(3);
        assertFalse(validator.isValid(beyondTolerance.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), null));
        
        LocalDateTime future = now.plusHours(1);
        assertFalse(validator.isValid(future.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), null));
    }

    @Test
    void testValidateDateTimeWithoutTimezoneSmartParsingLeapYear() {
        validator.initialize(getAnnotation("dateTimeInvalidPastFutureDate"));
        
        // Test smart parsing with valid leap year
        LocalDateTime validLeap = LocalDateTime.of(2024, 2, 29, 10, 30, 0);
        LocalDateTime now = LocalDateTime.now();
        if (validLeap.isBefore(now) && validLeap.isAfter(now.minusHours(2))) {
            assertTrue(validator.isValid(validLeap.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), null));
        }
        
        // Test smart parsing with invalid leap year
        assertFalse(validator.isValid("2023-02-29 10:30:00", null));
    }

    @Test
    void testValidateDateOnlyStrictParsing() {
        validator.initialize(getAnnotation("toleranceInvalidPastFutureDate"));
        
        // Test strict parsing path
        LocalDate today = LocalDate.now();
        LocalDate withinTolerance = today.minusDays(1);
        assertTrue(validator.isValid(withinTolerance.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));
        
        LocalDate beyondTolerance = today.minusDays(3);
        assertFalse(validator.isValid(beyondTolerance.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));
        
        LocalDate future = today.plusDays(1);
        assertFalse(validator.isValid(future.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));
    }

    @Test
    void testValidateDateOnlySmartParsingLeapYear() {
        validator.initialize(getAnnotation("toleranceInvalidPastFutureDate"));
        
        // Test smart parsing with invalid leap year
        assertFalse(validator.isValid("2023-02-29", null));
        
        // Test with a past valid leap year that's within tolerance (48 hours = 2 days)
        LocalDate today = LocalDate.now();
        LocalDate pastLeap = LocalDate.of(2020, 2, 29);
        // If it's too far in the past (beyond 2 days), it should be invalid
        // 2020-02-29 is way past, so it should be invalid
        assertFalse(validator.isValid(pastLeap.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));
        
        // Test with a recent date within tolerance
        LocalDate recent = today.minusDays(1);
        assertTrue(validator.isValid(recent.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));
    }

    @Test
    void testValidateDefaultSmartParsingException() throws NoSuchFieldException {
        class TestDummy {
            @InvalidPastFutureDate(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", toleranceHours = 2)
            String field;
        }
        
        Field f = TestDummy.class.getDeclaredField("field");
        InvalidPastFutureDate annotation = f.getAnnotation(InvalidPastFutureDate.class);
        validator.initialize(annotation);
        
        // Test exception in smart parsing
        assertFalse(validator.isValid("completely-invalid", null));
    }

    @Test
    void testBoundaryConditionsAtTolerance() {
        validator.initialize(getAnnotation("dateTimeInvalidPastFutureDate"));
        
        LocalDateTime now = LocalDateTime.now();
        // Within tolerance (1 hour ago)
        LocalDateTime withinTolerance = now.minusHours(1);
        assertTrue(validator.isValid(withinTolerance.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), null));
        
        // Just beyond tolerance (more than 2 hours ago)
        LocalDateTime justBeyondTolerance = now.minusHours(2).minusSeconds(1);
        assertFalse(validator.isValid(justBeyondTolerance.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), null));
        
        // Exactly at tolerance might be edge case due to timing, so test with a value clearly within
        LocalDateTime clearlyWithin = now.minusHours(1).minusMinutes(30);
        assertTrue(validator.isValid(clearlyWithin.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), null));
    }

    @Test
    void testBoundaryConditionsAtNow() {
        validator.initialize(getAnnotation("dateTimeInvalidPastFutureDate"));
        
        LocalDateTime now = LocalDateTime.now();
        // Exactly at now
        assertTrue(validator.isValid(now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), null));
        
        // Just after now (future)
        LocalDateTime justFuture = now.plusSeconds(1);
        assertFalse(validator.isValid(justFuture.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), null));
    }

    @Test
    void testValidateDateTimeWithoutTimezoneStrictParsingFuture() {
        validator.initialize(getAnnotation("dateTimeInvalidPastFutureDate"));
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime future = now.plusHours(1);
        assertFalse(validator.isValid(future.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), null));
    }

    @Test
    void testValidateDateTimeWithoutTimezoneStrictParsingTooPast() {
        validator.initialize(getAnnotation("dateTimeInvalidPastFutureDate"));
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tooPast = now.minusHours(3);
        assertFalse(validator.isValid(tooPast.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), null));
    }

    @Test
    void testValidateDateOnlyStrictParsingFuture() {
        validator.initialize(getAnnotation("toleranceInvalidPastFutureDate"));
        
        LocalDate today = LocalDate.now();
        LocalDate future = today.plusDays(1);
        assertFalse(validator.isValid(future.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));
    }

    @Test
    void testValidateDateOnlyStrictParsingTooPast() {
        validator.initialize(getAnnotation("toleranceInvalidPastFutureDate"));
        
        LocalDate today = LocalDate.now();
        LocalDate tooPast = today.minusDays(3);
        assertFalse(validator.isValid(tooPast.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));
    }

    @Test
    void testValidateDefaultStrictParsingZonedDateTimeFuture() throws NoSuchFieldException {
        class TestDummy {
            @InvalidPastFutureDate(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", toleranceHours = 2)
            String field;
        }
        
        Field f = TestDummy.class.getDeclaredField("field");
        InvalidPastFutureDate annotation = f.getAnnotation(InvalidPastFutureDate.class);
        validator.initialize(annotation);
        
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime future = now.plusHours(1);
        String futureStr = future.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"));
        assertFalse(validator.isValid(futureStr, null));
    }

    @Test
    void testValidateDefaultStrictParsingZonedDateTimeTooPast() throws NoSuchFieldException {
        class TestDummy {
            @InvalidPastFutureDate(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", toleranceHours = 2)
            String field;
        }
        
        Field f = TestDummy.class.getDeclaredField("field");
        InvalidPastFutureDate annotation = f.getAnnotation(InvalidPastFutureDate.class);
        validator.initialize(annotation);
        
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime tooPast = now.minusHours(3);
        String tooPastStr = tooPast.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"));
        assertFalse(validator.isValid(tooPastStr, null));
    }

    @Test
    void testValidateDefaultStrictParsingLocalDateTimeFuture() throws NoSuchFieldException {
        class TestDummy {
            @InvalidPastFutureDate(pattern = "yyyy-MM-dd'T'HH:mm:ss", toleranceHours = 2)
            String field;
        }
        
        Field f = TestDummy.class.getDeclaredField("field");
        InvalidPastFutureDate annotation = f.getAnnotation(InvalidPastFutureDate.class);
        validator.initialize(annotation);
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime future = now.plusHours(1);
        String futureStr = future.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        assertFalse(validator.isValid(futureStr, null));
    }

    @Test
    void testValidateDefaultStrictParsingLocalDateTimeTooPast() throws NoSuchFieldException {
        class TestDummy {
            @InvalidPastFutureDate(pattern = "yyyy-MM-dd'T'HH:mm:ss", toleranceHours = 2)
            String field;
        }
        
        Field f = TestDummy.class.getDeclaredField("field");
        InvalidPastFutureDate annotation = f.getAnnotation(InvalidPastFutureDate.class);
        validator.initialize(annotation);
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tooPast = now.minusHours(3);
        String tooPastStr = tooPast.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        assertFalse(validator.isValid(tooPastStr, null));
    }

    @Test
    void testValidateDefaultStrictParsingLocalDateFuture() throws NoSuchFieldException {
        class TestDummy {
            @InvalidPastFutureDate(pattern = "yyyy-MM-dd", toleranceHours = 48)
            String field;
        }
        
        Field f = TestDummy.class.getDeclaredField("field");
        InvalidPastFutureDate annotation = f.getAnnotation(InvalidPastFutureDate.class);
        validator.initialize(annotation);
        
        LocalDate today = LocalDate.now();
        LocalDate future = today.plusDays(1);
        assertFalse(validator.isValid(future.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));
    }

    @Test
    void testValidateDefaultStrictParsingLocalDateTooPast() throws NoSuchFieldException {
        class TestDummy {
            @InvalidPastFutureDate(pattern = "yyyy-MM-dd", toleranceHours = 48)
            String field;
        }
        
        Field f = TestDummy.class.getDeclaredField("field");
        InvalidPastFutureDate annotation = f.getAnnotation(InvalidPastFutureDate.class);
        validator.initialize(annotation);
        
        LocalDate today = LocalDate.now();
        LocalDate tooPast = today.minusDays(3);
        assertFalse(validator.isValid(tooPast.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));
    }

    @Test
    void testValidateDefaultSmartParsingZonedDateTimeFuture() throws NoSuchFieldException {
        class TestDummy {
            @InvalidPastFutureDate(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", toleranceHours = 2)
            String field;
        }
        
        Field f = TestDummy.class.getDeclaredField("field");
        InvalidPastFutureDate annotation = f.getAnnotation(InvalidPastFutureDate.class);
        validator.initialize(annotation);
        
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime future = now.plusHours(1);
        String futureStr = future.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"));
        assertFalse(validator.isValid(futureStr, null));
    }

    @Test
    void testValidateDefaultSmartParsingZonedDateTimeTooPast() throws NoSuchFieldException {
        class TestDummy {
            @InvalidPastFutureDate(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", toleranceHours = 2)
            String field;
        }
        
        Field f = TestDummy.class.getDeclaredField("field");
        InvalidPastFutureDate annotation = f.getAnnotation(InvalidPastFutureDate.class);
        validator.initialize(annotation);
        
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime tooPast = now.minusHours(3);
        String tooPastStr = tooPast.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"));
        assertFalse(validator.isValid(tooPastStr, null));
    }

    @Test
    void testValidateDefaultSmartParsingLocalDateTimeFuture() throws NoSuchFieldException {
        class TestDummy {
            @InvalidPastFutureDate(pattern = "yyyy-MM-dd'T'HH:mm:ss", toleranceHours = 2)
            String field;
        }
        
        Field f = TestDummy.class.getDeclaredField("field");
        InvalidPastFutureDate annotation = f.getAnnotation(InvalidPastFutureDate.class);
        validator.initialize(annotation);
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime future = now.plusHours(1);
        String futureStr = future.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        assertFalse(validator.isValid(futureStr, null));
    }

    @Test
    void testValidateDefaultSmartParsingLocalDateTimeTooPast() throws NoSuchFieldException {
        class TestDummy {
            @InvalidPastFutureDate(pattern = "yyyy-MM-dd'T'HH:mm:ss", toleranceHours = 2)
            String field;
        }
        
        Field f = TestDummy.class.getDeclaredField("field");
        InvalidPastFutureDate annotation = f.getAnnotation(InvalidPastFutureDate.class);
        validator.initialize(annotation);
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tooPast = now.minusHours(3);
        String tooPastStr = tooPast.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        assertFalse(validator.isValid(tooPastStr, null));
    }

    @Test
    void testValidateDefaultSmartParsingLocalDateFuture() throws NoSuchFieldException {
        class TestDummy {
            @InvalidPastFutureDate(pattern = "yyyy-MM-dd", toleranceHours = 48)
            String field;
        }
        
        Field f = TestDummy.class.getDeclaredField("field");
        InvalidPastFutureDate annotation = f.getAnnotation(InvalidPastFutureDate.class);
        validator.initialize(annotation);
        
        LocalDate today = LocalDate.now();
        LocalDate future = today.plusDays(1);
        assertFalse(validator.isValid(future.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));
    }

    @Test
    void testValidateDefaultSmartParsingLocalDateTooPast() throws NoSuchFieldException {
        class TestDummy {
            @InvalidPastFutureDate(pattern = "yyyy-MM-dd", toleranceHours = 48)
            String field;
        }
        
        Field f = TestDummy.class.getDeclaredField("field");
        InvalidPastFutureDate annotation = f.getAnnotation(InvalidPastFutureDate.class);
        validator.initialize(annotation);
        
        LocalDate today = LocalDate.now();
        LocalDate tooPast = today.minusDays(3);
        assertFalse(validator.isValid(tooPast.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));
    }

    @Test
    void testValidateDefaultSmartParsingZonedDateTimeValidLeapYear() throws NoSuchFieldException {
        class TestDummy {
            @InvalidPastFutureDate(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", toleranceHours = 8760)
            String field;
        }
        
        Field f = TestDummy.class.getDeclaredField("field");
        InvalidPastFutureDate annotation = f.getAnnotation(InvalidPastFutureDate.class);
        validator.initialize(annotation);
        
        // Test with valid leap year that's within tolerance (365 days = 8760 hours)
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime validLeap = ZonedDateTime.of(2024, 2, 29, 10, 30, 0, 0, now.getZone());
        if (validLeap.isBefore(now) && validLeap.isAfter(now.minusHours(8760))) {
            String validLeapStr = validLeap.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"));
            assertTrue(validator.isValid(validLeapStr, null));
        }
    }

    @Test
    void testValidateDefaultSmartParsingLocalDateTimeValidLeapYear() throws NoSuchFieldException {
        class TestDummy {
            @InvalidPastFutureDate(pattern = "yyyy-MM-dd'T'HH:mm:ss", toleranceHours = 8760)
            String field;
        }
        
        Field f = TestDummy.class.getDeclaredField("field");
        InvalidPastFutureDate annotation = f.getAnnotation(InvalidPastFutureDate.class);
        validator.initialize(annotation);
        
        // Test with valid leap year that's within tolerance
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime validLeap = LocalDateTime.of(2024, 2, 29, 10, 30, 0);
        if (validLeap.isBefore(now) && validLeap.isAfter(now.minusHours(8760))) {
            String validLeapStr = validLeap.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
            assertTrue(validator.isValid(validLeapStr, null));
        }
    }

    @Test
    void testValidateDefaultSmartParsingLocalDateValidLeapYear() throws NoSuchFieldException {
        class TestDummy {
            @InvalidPastFutureDate(pattern = "yyyy-MM-dd", toleranceHours = 8760)
            String field;
        }
        
        Field f = TestDummy.class.getDeclaredField("field");
        InvalidPastFutureDate annotation = f.getAnnotation(InvalidPastFutureDate.class);
        validator.initialize(annotation);
        
        // Test with valid leap year that's within tolerance
        LocalDate today = LocalDate.now();
        LocalDate validLeap = LocalDate.of(2024, 2, 29);
        if (validLeap.isBefore(today) || validLeap.equals(today)) {
            if (!validLeap.isBefore(today.minusDays(365))) {
                assertTrue(validator.isValid(validLeap.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));
            }
        }
    }
}
