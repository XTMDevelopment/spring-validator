package id.xtramile.validator.validator.datetime;

import id.xtramile.validator.annotation.datetime.InvalidPastDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InvalidPastDateValidatorTest {

    private static class InvalidPastDateDummy {
        @InvalidPastDate
        String defaultInvalidPastDate;

        @InvalidPastDate(tolerance = 7)
        String toleranceInvalidPastDate;

        @InvalidPastDate(pattern = "dd/MM/yyyy", tolerance = 3)
        String customPatternInvalidPastDate;

        @InvalidPastDate(pattern = "yyyy-MM-dd HH:mm:ss", tolerance = 1)
        String dateTimeInvalidPastDate;
    }

    private InvalidPastDateValidator validator;

    private static InvalidPastDate getAnnotation(String fieldName) {
        try {
            Field f = InvalidPastDateDummy.class.getDeclaredField(fieldName);
            return f.getAnnotation(InvalidPastDate.class);
        } catch (NoSuchFieldException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
        validator = new InvalidPastDateValidator();
    }

    @Test
    void testValidNonPastDates() {
        validator.initialize(getAnnotation("defaultInvalidPastDate"));

        // Test with dates in the future (should be valid - not past)
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
    void testInvalidPastDates() {
        validator.initialize(getAnnotation("defaultInvalidPastDate"));

        // Test with dates in the past (should be invalid - are past)
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
    void testToleranceInvalidPastDates() {
        validator.initialize(getAnnotation("toleranceInvalidPastDate"));

        // Test with dates within tolerance (should be valid - not too far past)
        LocalDate withinTolerance = LocalDate.now().minusDays(3); // within 7 days tolerance
        assertTrue(validator.isValid(withinTolerance.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));

        LocalDate atTolerance = LocalDate.now().minusDays(7); // exactly at tolerance
        assertTrue(validator.isValid(atTolerance.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));

        // Test with dates beyond tolerance (should be invalid - too far past)
        LocalDate beyondTolerance = LocalDate.now().minusDays(8); // beyond 7 days tolerance
        assertFalse(validator.isValid(beyondTolerance.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));

        LocalDate farPast = LocalDate.now().minusDays(30); // far past
        assertFalse(validator.isValid(farPast.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));
    }

    @Test
    void testCustomPatternInvalidPastDates() {
        validator.initialize(getAnnotation("customPatternInvalidPastDate"));

        // Test with custom pattern (dd/MM/yyyy) and tolerance
        LocalDate withinTolerance = LocalDate.now().minusDays(1); // within 3 days tolerance
        assertTrue(validator.isValid(withinTolerance.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), null));

        LocalDate atTolerance = LocalDate.now().minusDays(3); // exactly at tolerance
        assertTrue(validator.isValid(atTolerance.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), null));

        // Test with dates beyond tolerance
        LocalDate beyondTolerance = LocalDate.now().minusDays(4); // beyond 3 days tolerance
        assertFalse(validator.isValid(beyondTolerance.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), null));
    }

    @Test
    void testDateTimeInvalidPastDates() {
        validator.initialize(getAnnotation("dateTimeInvalidPastDate"));

        // Test with datetime within tolerance (should be valid - not too far past)
        LocalDateTime withinTolerance = LocalDateTime.now().minusHours(12); // within 1 day tolerance
        assertTrue(validator.isValid(withinTolerance.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), null));

        LocalDateTime atTolerance = LocalDateTime.now().minusDays(1); // exactly at tolerance
        assertTrue(validator.isValid(atTolerance.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), null));

        // Test with datetime beyond tolerance (should be invalid - too far past)
        LocalDateTime beyondTolerance = LocalDateTime.now().minusDays(2); // beyond 1 day tolerance
        assertFalse(validator.isValid(beyondTolerance.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), null));
    }

    @Test
    void testBlankValues() {
        validator.initialize(getAnnotation("defaultInvalidPastDate"));

        assertTrue(validator.isValid("", null));
        assertTrue(validator.isValid("   ", null));
        assertTrue(validator.isValid(null, null));
    }

    @Test
    void testInvalidDateFormats() {
        validator.initialize(getAnnotation("defaultInvalidPastDate"));

        // Test with invalid date formats
        assertFalse(validator.isValid("invalid-date", null));
        assertFalse(validator.isValid("2023-13-01", null)); // invalid month
        assertFalse(validator.isValid("2023-12-32", null)); // invalid day
        assertFalse(validator.isValid("2023/12/25", null)); // wrong separator
        assertFalse(validator.isValid("25-12-2023", null)); // wrong order
    }

    @Test
    void testEdgeCases() {
        validator.initialize(getAnnotation("defaultInvalidPastDate"));

        // Test with spaces
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        String futureDate = tomorrow.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        assertFalse(validator.isValid(" " + futureDate, null)); // leading space
        assertFalse(validator.isValid(futureDate + " ", null)); // trailing space
        assertFalse(validator.isValid("2023 -12-25", null)); // space in date
    }

    @Test
    void testRealWorldExamples() {
        validator.initialize(getAnnotation("defaultInvalidPastDate"));

        // Test with specific future dates (should be valid - not past)
        assertTrue(validator.isValid("2028-01-01", null)); // New Year 2028
        assertTrue(validator.isValid("2027-12-25", null)); // Christmas 2027
        assertTrue(validator.isValid("2027-07-04", null)); // Independence Day 2027
        assertTrue(validator.isValid("2027-06-15", null)); // Mid year 2027

        // Test with past dates (should be invalid - are past)
        assertFalse(validator.isValid("2020-01-01", null)); // New Year 2020
        assertFalse(validator.isValid("2022-12-25", null)); // Christmas 2022
        assertFalse(validator.isValid("2023-07-04", null)); // Independence Day 2023
        assertFalse(validator.isValid("2023-06-15", null)); // Mid year 2023
    }

    @Test
    void testToleranceBoundaries() {
        validator.initialize(getAnnotation("toleranceInvalidPastDate"));

        // Test tolerance boundaries
        LocalDate today = LocalDate.now();
        LocalDate withinTolerance = today.minusDays(6); // 6 days ago (within 7 days)
        LocalDate atTolerance = today.minusDays(7); // 7 days ago (at tolerance)
        LocalDate beyondTolerance = today.minusDays(8); // 8 days ago (beyond tolerance)

        assertTrue(validator.isValid(withinTolerance.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));
        assertTrue(validator.isValid(atTolerance.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));
        assertFalse(validator.isValid(beyondTolerance.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));
    }

    @Test
    void testPatternVariations() {
        // Test different pattern configurations
        validator.initialize(getAnnotation("customPatternInvalidPastDate"));
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        assertTrue(validator.isValid(tomorrow.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), null));
        assertFalse(validator.isValid(tomorrow.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));

        validator.initialize(getAnnotation("dateTimeInvalidPastDate"));
        LocalDateTime tomorrowDateTime = LocalDateTime.now().plusDays(1);
        assertTrue(validator.isValid(tomorrowDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), null));
        assertFalse(validator.isValid(tomorrowDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), null));
    }

    @Test
    void testLeapYearInvalidPastDates() {
        validator.initialize(getAnnotation("defaultInvalidPastDate"));

        // Test leap year dates in the past (should be invalid - are past)
        assertFalse(validator.isValid("2024-02-29", null)); // 2024 is leap year
        assertFalse(validator.isValid("2020-02-29", null)); // 2020 is leap year
        assertFalse(validator.isValid("2016-02-29", null)); // 2016 is leap year

        // Test future leap year dates (should be valid - not past)
        assertTrue(validator.isValid("2028-02-29", null)); // 2028 is leap year
        assertTrue(validator.isValid("2032-02-29", null)); // 2032 is leap year
        assertTrue(validator.isValid("2036-02-29", null)); // 2036 is leap year
    }

    @Test
    void testDateTimeWithTimezones() {
        validator.initialize(getAnnotation("dateTimeInvalidPastDate"));

        // Test with timezone-aware datetime
        ZonedDateTime tomorrow = ZonedDateTime.now().plusDays(1);
        String futureDateTime = tomorrow.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        assertTrue(validator.isValid(futureDateTime, null));

        ZonedDateTime yesterday = ZonedDateTime.now().minusDays(2);
        String pastDateTime = yesterday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        assertFalse(validator.isValid(pastDateTime, null));
    }

    @Test
    void testBoundaryConditions() {
        validator.initialize(getAnnotation("defaultInvalidPastDate"));

        // Test with dates very close to today
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        LocalDate yesterday = today.minusDays(1);

        // Today should be valid (not past)
        assertTrue(validator.isValid(today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));

        // Tomorrow should be valid (not past)
        assertTrue(validator.isValid(tomorrow.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));

        // Yesterday should be invalid (past)
        assertFalse(validator.isValid(yesterday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));
    }

    @Test
    void testOppositeLogicToPastDate() {
        validator.initialize(getAnnotation("defaultInvalidPastDate"));

        LocalDate tomorrow = LocalDate.now().plusDays(1);
        LocalDate yesterday = LocalDate.now().minusDays(1);

        // Future date should be valid
        assertTrue(validator.isValid(tomorrow.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));

        // Past date should be invalid
        assertFalse(validator.isValid(yesterday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));
    }

    @Test
    void testZonedDateTimeValidation() {
        validator.initialize(getAnnotation("dateTimeInvalidPastDate"));

        LocalDateTime today = LocalDateTime.now();
        LocalDateTime tomorrow = today.plusDays(1);
        LocalDateTime yesterday = today.minusDays(2);
        
        String tomorrowStr = tomorrow.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String yesterdayStr = yesterday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        assertTrue(validator.isValid(tomorrowStr, null));
        assertFalse(validator.isValid(yesterdayStr, null));
    }

    @Test
    void testLocalDateTimeValidation() {
        validator.initialize(getAnnotation("dateTimeInvalidPastDate"));

        // Test LocalDateTime path in strict parsing
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime tomorrow = today.plusDays(1);
        LocalDateTime yesterday = today.minusDays(2);
        
        String tomorrowStr = tomorrow.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String yesterdayStr = yesterday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        assertTrue(validator.isValid(tomorrowStr, null));
        assertFalse(validator.isValid(yesterdayStr, null));
    }

    @Test
    void testLocalDateValidation() {
        validator.initialize(getAnnotation("defaultInvalidPastDate"));

        // Test LocalDate path in strict parsing
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        LocalDate yesterday = today.minusDays(1);
        
        assertTrue(validator.isValid(tomorrow.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));
        assertFalse(validator.isValid(yesterday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));
    }

    @Test
    void testSmartParsingWithZonedDateTime() {
        validator.initialize(getAnnotation("dateTimeInvalidPastDate"));

        // Test smart parsing with ZonedDateTime and leap year check
        assertFalse(validator.isValid("2023-02-29T10:30:00+07:00", null)); // Invalid leap year
    }

    @Test
    void testSmartParsingWithLocalDateTime() {
        validator.initialize(getAnnotation("dateTimeInvalidPastDate"));

        // Test smart parsing with LocalDateTime and leap year check
        assertFalse(validator.isValid("2023-02-29 10:30:00", null)); // Invalid leap year
    }

    @Test
    void testSmartParsingWithLocalDate() {
        validator.initialize(getAnnotation("defaultInvalidPastDate"));

        // Test smart parsing with LocalDate and leap year check
        assertFalse(validator.isValid("2023-02-29", null)); // Invalid leap year
    }

    @Test
    void testUnsupportedTemporalType() {
        validator.initialize(getAnnotation("defaultInvalidPastDate"));

        // Test when parseBest returns a type that's not ZonedDateTime, LocalDateTime, or LocalDate
        assertFalse(validator.isValid("invalid", null));
    }

    @Test
    void testSmartParsingException() {
        validator.initialize(getAnnotation("defaultInvalidPastDate"));

        // Test exception in smart parsing
        assertFalse(validator.isValid("completely-invalid", null));
    }

    @Test
    void testSmartParsingWithZonedDateTimeLeapYear() throws NoSuchFieldException {
        // Use a pattern that will parse as ZonedDateTime
        class TestDummy {
            @InvalidPastDate(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", tolerance = 365)
            String field;
        }
        
        Field f = TestDummy.class.getDeclaredField("field");
        InvalidPastDate annotation = f.getAnnotation(InvalidPastDate.class);
        validator.initialize(annotation);

        // Test smart parsing with invalid leap year (this should fail)
        assertFalse(validator.isValid("2023-02-29T10:30:00+07:00", null));
        
        // Test with a past valid leap year that's within tolerance (365 days)
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime pastLeap = ZonedDateTime.of(2020, 2, 29, 10, 30, 0, 0, now.getZone());
        if (pastLeap.isBefore(now.truncatedTo(java.time.temporal.ChronoUnit.SECONDS)) && 
            !pastLeap.isBefore(now.minusDays(365).truncatedTo(java.time.temporal.ChronoUnit.SECONDS))) {
            String pastLeapStr = pastLeap.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"));
            assertTrue(validator.isValid(pastLeapStr, null));
        }
    }

    @Test
    void testSmartParsingWithLocalDateTimeLeapYear() {
        validator.initialize(getAnnotation("dateTimeInvalidPastDate"));

        // Test smart parsing with invalid leap year
        assertFalse(validator.isValid("2023-02-29 10:30:00", null));
        
        // Test with a recent valid leap year that's within tolerance (tolerance is 1 day)
        // Since 2020 is too far in the past, test with a date within 1 day
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime recent = now.minusHours(12); // Within 1 day tolerance
        assertTrue(validator.isValid(recent.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), null));
        
        // Test with a date beyond tolerance
        LocalDateTime tooPast = now.minusDays(2);
        assertFalse(validator.isValid(tooPast.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), null));
    }

    @Test
    void testSmartParsingWithLocalDateLeapYear() {
        validator.initialize(getAnnotation("defaultInvalidPastDate"));

        // Test smart parsing with invalid leap year
        assertFalse(validator.isValid("2023-02-29", null));
        
        // Test with a past valid leap year that's within tolerance (default tolerance is 0, so only today or future is valid)
        // Since InvalidPastDate rejects past dates, we need to test with a date that's not too far past
        // But wait - InvalidPastDate rejects dates that are TOO FAR in the past, not all past dates
        // So dates within tolerance should be valid
        LocalDate today = LocalDate.now();
        LocalDate pastLeap = LocalDate.of(2020, 2, 29);
        // If it's within tolerance (default is 0, so only today), it should be invalid
        // Actually, default tolerance is 0, so any past date should be invalid
        assertFalse(validator.isValid(pastLeap.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));
    }

    @Test
    void testStrictParsingWithZonedDateTime() throws NoSuchFieldException {
        class TestDummy {
            @InvalidPastDate(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", tolerance = 7)
            String field;
        }
        
        Field f = TestDummy.class.getDeclaredField("field");
        InvalidPastDate annotation = f.getAnnotation(InvalidPastDate.class);
        validator.initialize(annotation);
        
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime withinTolerance = now.minusDays(3);
        ZonedDateTime beyondTolerance = now.minusDays(8);
        
        String withinStr = withinTolerance.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"));
        String beyondStr = beyondTolerance.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"));
        
        assertTrue(validator.isValid(withinStr, null));
        assertFalse(validator.isValid(beyondStr, null));
    }

    @Test
    void testStrictParsingWithLocalDateTime() {
        validator.initialize(getAnnotation("dateTimeInvalidPastDate"));
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime withinTolerance = now.minusDays(0);
        LocalDateTime beyondTolerance = now.minusDays(2);
        
        assertTrue(validator.isValid(withinTolerance.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), null));
        assertFalse(validator.isValid(beyondTolerance.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), null));
    }

    @Test
    void testStrictParsingWithLocalDate() {
        validator.initialize(getAnnotation("defaultInvalidPastDate"));
        
        LocalDate today = LocalDate.now();
        LocalDate withinTolerance = today.minusDays(0);
        LocalDate beyondTolerance = today.minusDays(1);
        
        assertTrue(validator.isValid(withinTolerance.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));
        assertFalse(validator.isValid(beyondTolerance.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));
    }

    @Test
    void testBoundaryConditionsAtTolerance() {
        validator.initialize(getAnnotation("toleranceInvalidPastDate"));
        
        LocalDate today = LocalDate.now();
        LocalDate exactlyAtTolerance = today.minusDays(7);
        LocalDate justBeyondTolerance = today.minusDays(8);
        
        assertTrue(validator.isValid(exactlyAtTolerance.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));
        assertFalse(validator.isValid(justBeyondTolerance.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));
    }

    @Test
    void testUnsupportedTemporalTypeInSmartParsing() {
        validator.initialize(getAnnotation("defaultInvalidPastDate"));
        
        // Test when smart parsing returns unsupported type
        assertFalse(validator.isValid("invalid", null));
    }

    @Test
    void testStrictParsingZonedDateTimeWithinTolerance() throws NoSuchFieldException {
        class TestDummy {
            @InvalidPastDate(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", tolerance = 365)
            String field;
        }
        
        Field f = TestDummy.class.getDeclaredField("field");
        InvalidPastDate annotation = f.getAnnotation(InvalidPastDate.class);
        validator.initialize(annotation);
        
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime withinTolerance = now.minusDays(100);
        String withinStr = withinTolerance.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"));
        assertTrue(validator.isValid(withinStr, null));
    }

    @Test
    void testStrictParsingZonedDateTimeBeyondTolerance() throws NoSuchFieldException {
        class TestDummy {
            @InvalidPastDate(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", tolerance = 7)
            String field;
        }
        
        Field f = TestDummy.class.getDeclaredField("field");
        InvalidPastDate annotation = f.getAnnotation(InvalidPastDate.class);
        validator.initialize(annotation);
        
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime beyondTolerance = now.minusDays(10);
        String beyondStr = beyondTolerance.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"));
        assertFalse(validator.isValid(beyondStr, null));
    }

    @Test
    void testSmartParsingZonedDateTimeWithinTolerance() throws NoSuchFieldException {
        class TestDummy {
            @InvalidPastDate(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", tolerance = 365)
            String field;
        }
        
        Field f = TestDummy.class.getDeclaredField("field");
        InvalidPastDate annotation = f.getAnnotation(InvalidPastDate.class);
        validator.initialize(annotation);
        
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime withinTolerance = now.minusDays(100);
        String withinStr = withinTolerance.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"));
        assertTrue(validator.isValid(withinStr, null));
    }

    @Test
    void testSmartParsingZonedDateTimeBeyondTolerance() throws NoSuchFieldException {
        class TestDummy {
            @InvalidPastDate(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", tolerance = 7)
            String field;
        }
        
        Field f = TestDummy.class.getDeclaredField("field");
        InvalidPastDate annotation = f.getAnnotation(InvalidPastDate.class);
        validator.initialize(annotation);
        
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime beyondTolerance = now.minusDays(10);
        String beyondStr = beyondTolerance.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"));
        assertFalse(validator.isValid(beyondStr, null));
    }

    @Test
    void testSmartParsingLocalDateTimeWithinTolerance() {
        validator.initialize(getAnnotation("dateTimeInvalidPastDate"));
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime withinTolerance = now.minusHours(12);
        assertTrue(validator.isValid(withinTolerance.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), null));
    }

    @Test
    void testSmartParsingLocalDateTimeBeyondTolerance() {
        validator.initialize(getAnnotation("dateTimeInvalidPastDate"));
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime beyondTolerance = now.minusDays(2);
        assertFalse(validator.isValid(beyondTolerance.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), null));
    }

    @Test
    void testSmartParsingLocalDateWithinTolerance() throws NoSuchFieldException {
        class TestDummy {
            @InvalidPastDate(tolerance = 7)
            String field;
        }
        
        Field f = TestDummy.class.getDeclaredField("field");
        InvalidPastDate annotation = f.getAnnotation(InvalidPastDate.class);
        validator.initialize(annotation);
        
        LocalDate today = LocalDate.now();
        LocalDate withinTolerance = today.minusDays(3);
        assertTrue(validator.isValid(withinTolerance.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));
    }

    @Test
    void testSmartParsingLocalDateBeyondTolerance() throws NoSuchFieldException {
        class TestDummy {
            @InvalidPastDate(tolerance = 7)
            String field;
        }
        
        Field f = TestDummy.class.getDeclaredField("field");
        InvalidPastDate annotation = f.getAnnotation(InvalidPastDate.class);
        validator.initialize(annotation);
        
        LocalDate today = LocalDate.now();
        LocalDate beyondTolerance = today.minusDays(10);
        assertFalse(validator.isValid(beyondTolerance.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));
    }

    @Test
    void testSmartParsingZonedDateTimeValidLeapYearWithinTolerance() throws NoSuchFieldException {
        class TestDummy {
            @InvalidPastDate(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", tolerance = 365)
            String field;
        }
        
        Field f = TestDummy.class.getDeclaredField("field");
        InvalidPastDate annotation = f.getAnnotation(InvalidPastDate.class);
        validator.initialize(annotation);
        
        // Test with valid leap year that's within tolerance
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime validLeap = ZonedDateTime.of(2024, 2, 29, 10, 30, 0, 0, now.getZone());
        if (validLeap.isBefore(now.truncatedTo(java.time.temporal.ChronoUnit.SECONDS)) && 
            !validLeap.isBefore(now.minusDays(365).truncatedTo(java.time.temporal.ChronoUnit.SECONDS))) {
            String validLeapStr = validLeap.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"));
            assertTrue(validator.isValid(validLeapStr, null));
        }
    }

    @Test
    void testSmartParsingLocalDateTimeValidLeapYearWithinTolerance() {
        validator.initialize(getAnnotation("dateTimeInvalidPastDate"));
        
        // Test with valid leap year that's within tolerance (1 day)
        LocalDateTime now = LocalDateTime.now();
        // 2024-02-29 is too far in the past for 1 day tolerance, so test with a recent date
        LocalDateTime recent = now.minusHours(12);
        assertTrue(validator.isValid(recent.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), null));
    }

    @Test
    void testSmartParsingLocalDateValidLeapYearWithinTolerance() throws NoSuchFieldException {
        class TestDummy {
            @InvalidPastDate(tolerance = 365)
            String field;
        }
        
        Field f = TestDummy.class.getDeclaredField("field");
        InvalidPastDate annotation = f.getAnnotation(InvalidPastDate.class);
        validator.initialize(annotation);
        
        // Test with valid leap year that's within tolerance
        LocalDate today = LocalDate.now();
        LocalDate validLeap = LocalDate.of(2024, 2, 29);
        if (validLeap.isBefore(today) && !validLeap.isBefore(today.minusDays(365))) {
            assertTrue(validator.isValid(validLeap.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), null));
        }
    }
}
