package id.xtramile.validator.validator.datetime;

import id.xtramile.validator.annotation.datetime.ValidDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DateValidatorTest {

    private DateValidator validator;

    private static ValidDate getAnnotation(String fieldName) {
        try {
            Field f = DateDummy.class.getDeclaredField(fieldName);
            return f.getAnnotation(ValidDate.class);
        } catch (NoSuchFieldException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
        validator = new DateValidator();
    }

    @Test
    void testValidDefaultDates() {
        validator.initialize(getAnnotation("defaultDate"));

        assertTrue(validator.isValid("2023-12-25", null)); // standard format
        assertTrue(validator.isValid("2024-01-01", null)); // new year
        assertTrue(validator.isValid("2023-02-28", null)); // leap year
        assertTrue(validator.isValid("2024-02-29", null)); // leap year
        assertTrue(validator.isValid("2023-06-15", null)); // mid year
    }

    @Test
    void testInvalidDefaultDates() {
        validator.initialize(getAnnotation("defaultDate"));

        assertFalse(validator.isValid("2023-13-01", null)); // invalid month
        assertFalse(validator.isValid("2023-12-32", null)); // invalid day
        assertFalse(validator.isValid("2023-02-29", null)); // invalid leap year
        assertFalse(validator.isValid("2023/12/25", null)); // wrong separator
        assertFalse(validator.isValid("25-12-2023", null)); // wrong order
        assertFalse(validator.isValid("2023-12-25 10:30:00", null)); // includes time
    }

    @Test
    void testCustomPatternDates() {
        validator.initialize(getAnnotation("customPatternDate"));

        assertTrue(validator.isValid("25/12/2023", null)); // valid dd/MM/yyyy
        assertTrue(validator.isValid("01/01/2024", null)); // new year
        assertTrue(validator.isValid("29/02/2024", null)); // leap year

        assertFalse(validator.isValid("2023-12-25", null)); // wrong format
        assertFalse(validator.isValid("25-12-2023", null)); // wrong separator
        assertFalse(validator.isValid("25/13/2023", null)); // invalid month
        assertFalse(validator.isValid("32/12/2023", null)); // invalid day
    }

    @Test
    void testUSPatternDates() {
        validator.initialize(getAnnotation("usPatternDate"));

        assertTrue(validator.isValid("12-25-2023", null)); // valid MM-dd-yyyy
        assertTrue(validator.isValid("01-01-2024", null)); // new year
        assertTrue(validator.isValid("02-29-2024", null)); // leap year

        assertFalse(validator.isValid("2023-12-25", null)); // wrong format
        assertFalse(validator.isValid("25/12/2023", null)); // wrong format
        assertFalse(validator.isValid("13-25-2023", null)); // invalid month
        assertFalse(validator.isValid("12-32-2023", null)); // invalid day
    }

    @Test
    void testISOPatternDates() {
        validator.initialize(getAnnotation("isoPatternDate"));

        assertTrue(validator.isValid("2023/12/25", null)); // valid yyyy/MM/dd
        assertTrue(validator.isValid("2024/01/01", null)); // new year
        assertTrue(validator.isValid("2024/02/29", null)); // leap year

        assertFalse(validator.isValid("2023-12-25", null)); // wrong separator
        assertFalse(validator.isValid("25/12/2023", null)); // wrong order
        assertFalse(validator.isValid("2023/13/25", null)); // invalid month
        assertFalse(validator.isValid("2023/12/32", null)); // invalid day
    }

    @Test
    void testBlankValues() {
        validator.initialize(getAnnotation("defaultDate"));

        assertTrue(validator.isValid("", null));
        assertTrue(validator.isValid("   ", null));
        assertTrue(validator.isValid(null, null));
    }

    @Test
    void testEdgeCases() {
        validator.initialize(getAnnotation("defaultDate"));

        // Test with spaces
        assertFalse(validator.isValid(" 2023-12-25", null)); // leading space
        assertFalse(validator.isValid("2023-12-25 ", null)); // trailing space
        assertFalse(validator.isValid("2023 -12-25", null)); // space in middle

        // Test with invalid characters
        assertFalse(validator.isValid("2023-12-25T10:30:00", null)); // includes time
        assertFalse(validator.isValid("2023-12-25Z", null)); // includes timezone
        assertFalse(validator.isValid("2023-12-25+01:00", null)); // includes timezone
    }

    @Test
    void testLeapYearValidation() {
        validator.initialize(getAnnotation("defaultDate"));

        // Valid leap years
        assertTrue(validator.isValid("2024-02-29", null)); // 2024 is leap year
        assertTrue(validator.isValid("2020-02-29", null)); // 2020 is leap year
        assertTrue(validator.isValid("2000-02-29", null)); // 2000 is leap year

        // Invalid leap years
        assertFalse(validator.isValid("2023-02-29", null)); // 2023 is not leap year
        assertFalse(validator.isValid("2021-02-29", null)); // 2021 is not leap year
        assertFalse(validator.isValid("1900-02-29", null)); // 1900 is not leap year (century rule)
    }

    @Test
    void testMonthBoundaries() {
        validator.initialize(getAnnotation("defaultDate"));

        // Test month boundaries
        assertTrue(validator.isValid("2023-01-31", null)); // January has 31 days
        assertFalse(validator.isValid("2023-01-32", null)); // January doesn't have 32 days
        assertTrue(validator.isValid("2023-04-30", null)); // April has 30 days
        assertFalse(validator.isValid("2023-04-31", null)); // April doesn't have 31 days
        assertTrue(validator.isValid("2023-12-31", null)); // December has 31 days
        assertFalse(validator.isValid("2023-12-32", null)); // December doesn't have 32 days
    }

    @Test
    void testRealWorldExamples() {
        validator.initialize(getAnnotation("defaultDate"));

        // Common date formats
        assertTrue(validator.isValid("2023-01-01", null)); // New Year
        assertTrue(validator.isValid("2023-12-25", null)); // Christmas
        assertTrue(validator.isValid("2023-07-04", null)); // Independence Day
        assertTrue(validator.isValid("2023-11-23", null)); // Thanksgiving
        assertTrue(validator.isValid("2023-06-15", null)); // Mid year
    }

    @Test
    void testPatternVariations() {
        // Test different pattern configurations
        validator.initialize(getAnnotation("customPatternDate"));
        assertTrue(validator.isValid("25/12/2023", null));
        assertFalse(validator.isValid("2023-12-25", null));

        validator.initialize(getAnnotation("usPatternDate"));
        assertTrue(validator.isValid("12-25-2023", null));
        assertFalse(validator.isValid("25/12/2023", null));

        validator.initialize(getAnnotation("isoPatternDate"));
        assertTrue(validator.isValid("2023/12/25", null));
        assertFalse(validator.isValid("12-25-2023", null));
    }

    @Test
    void testInvalidFormats() {
        validator.initialize(getAnnotation("defaultDate"));

        // Various invalid formats
        assertFalse(validator.isValid("25-12-2023", null)); // wrong order
        assertFalse(validator.isValid("2023/12/25", null)); // wrong separator
        assertFalse(validator.isValid("Dec 25, 2023", null)); // text format
        assertFalse(validator.isValid("25 Dec 2023", null)); // text format
        assertFalse(validator.isValid("2023.12.25", null)); // dot separator
        assertFalse(validator.isValid("20231225", null)); // no separators
    }

    @Test
    void testInvalidDateFormatWithIncompleteParts() {
        validator.initialize(getAnnotation("defaultDate"));

        // Test when value contains "-" but parts.length < 3
        assertFalse(validator.isValid("2023-12", null)); // only 2 parts
        assertFalse(validator.isValid("2023", null)); // only 1 part
        assertFalse(validator.isValid("-", null)); // just separator
    }

    @Test
    void testInvalidDateFormatWithNonNumericParts() {
        validator.initialize(getAnnotation("defaultDate"));

        // Test when parts cannot be parsed as integers (NumberFormatException)
        assertFalse(validator.isValid("abc-12-25", null)); // non-numeric year
        assertFalse(validator.isValid("2023-abc-25", null)); // non-numeric month
        assertFalse(validator.isValid("2023-12-abc", null)); // non-numeric day
    }

    @Test
    void testInvalidDateFormatWithInvalidDayInMonth() {
        validator.initialize(getAnnotation("defaultDate"));

        // Test when day exceeds days in month
        assertFalse(validator.isValid("2023-01-32", null)); // January has 31 days
        assertFalse(validator.isValid("2023-02-30", null)); // February has 28/29 days
        assertFalse(validator.isValid("2023-04-31", null)); // April has 30 days
        assertFalse(validator.isValid("2023-06-31", null)); // June has 30 days
        assertFalse(validator.isValid("2023-09-31", null)); // September has 30 days
        assertFalse(validator.isValid("2023-11-31", null)); // November has 30 days
    }

    @Test
    void testSmartParsingFallback() {
        validator.initialize(getAnnotation("defaultDate"));

        assertTrue(validator.isValid("2023-12-25", null)); // should work with strict
    }

    @Test
    void testDateTimeParseExceptionHandling() {
        validator.initialize(getAnnotation("defaultDate"));

        // Test completely invalid date strings that will throw DateTimeParseException
        assertFalse(validator.isValid("not-a-date", null));
        assertFalse(validator.isValid("12345", null));
    }

    @Test
    void testPatternNotYyyyMmDd() {
        // Test with pattern that is not "yyyy-MM-dd" but value contains "-"
        validator.initialize(getAnnotation("usPatternDate")); // MM-dd-yyyy

        // Should not trigger the special yyyy-MM-dd logic
        assertTrue(validator.isValid("12-25-2023", null));
        assertFalse(validator.isValid("2023-12-25", null)); // wrong format for this pattern
    }

    private static class DateDummy {
        @ValidDate
        String defaultDate;

        @ValidDate(pattern = "dd/MM/yyyy")
        String customPatternDate;

        @ValidDate(pattern = "MM-dd-yyyy")
        String usPatternDate;

        @ValidDate(pattern = "yyyy/MM/dd")
        String isoPatternDate;
    }
}
