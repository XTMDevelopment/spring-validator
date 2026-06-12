package id.xtramile.validator.validator.finance;

import id.xtramile.validator.annotation.finance.ValidCardExpiry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CardExpiryValidatorTest {

    private static class CardExpiryDummy {
        @ValidCardExpiry
        String defaultExpiry;

        @ValidCardExpiry()
        String futureExpiry;

        @ValidCardExpiry(mustBeFuture = false)
        String anyExpiry;
    }

    private CardExpiryValidator validator;

    private static ValidCardExpiry getAnnotation(String fieldName) {
        try {
            Field f = CardExpiryDummy.class.getDeclaredField(fieldName);
            return f.getAnnotation(ValidCardExpiry.class);
        } catch (NoSuchFieldException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
        validator = new CardExpiryValidator();
    }

    @Test
    void testValidExpiryFormats() {
        validator.initialize(getAnnotation("anyExpiry"));

        assertTrue(validator.isValid("12/27", null));
        assertTrue(validator.isValid("01/2027", null));
        assertTrue(validator.isValid("12 / 27", null)); // With spaces
        assertTrue(validator.isValid("01 / 2027", null)); // With spaces
    }

    @Test
    void testInvalidExpiryFormats() {
        validator.initialize(getAnnotation("defaultExpiry"));

        assertFalse(validator.isValid("13/27", null)); // Invalid month
        assertFalse(validator.isValid("00/27", null)); // Invalid month
        assertFalse(validator.isValid("12/7", null)); // Invalid year format
        assertFalse(validator.isValid("12/202", null)); // Invalid year format
        assertFalse(validator.isValid("1/27", null)); // Invalid month format
        assertFalse(validator.isValid("12/", null)); // Missing year
        assertFalse(validator.isValid("/27", null)); // Missing month
        assertFalse(validator.isValid("12-27", null)); // Wrong separator
        assertFalse(validator.isValid("12.27", null)); // Wrong separator
    }

    @Test
    void testFutureExpiryValidation() {
        validator.initialize(getAnnotation("futureExpiry"));

        // These should be valid if they're in the future
        assertTrue(validator.isValid("12/30", null)); // Far future
        assertTrue(validator.isValid("01/2027", null)); // Far future
        
        // These might be invalid if they're in the past (depending on current date)
        // Note: These tests might need adjustment based on when they're run
        assertTrue(validator.isValid("12/27", null)); // Should be valid if current date is before Dec 2027
    }

    @Test
    void testAnyExpiryValidation() {
        validator.initialize(getAnnotation("anyExpiry"));

        // Should accept any valid format regardless of date
        assertTrue(validator.isValid("01/20", null)); // Past date
        assertTrue(validator.isValid("12/27", null)); // Future date
        assertTrue(validator.isValid("06/2020", null)); // Past date
        assertTrue(validator.isValid("12/2027", null)); // Future date
    }

    @Test
    void testMonthBoundaries() {
        validator.initialize(getAnnotation("anyExpiry"));

        assertTrue(validator.isValid("01/27", null)); // January
        assertTrue(validator.isValid("12/27", null)); // December
        assertFalse(validator.isValid("00/27", null)); // Month 0
        assertFalse(validator.isValid("13/27", null)); // Month 13
    }

    @Test
    void testYearFormats() {
        validator.initialize(getAnnotation("defaultExpiry"));

        assertTrue(validator.isValid("12/27", null)); // 2-digit year
        assertTrue(validator.isValid("12/2027", null)); // 4-digit year
        assertFalse(validator.isValid("12/7", null)); // 1-digit year
        assertFalse(validator.isValid("12/202", null)); // 3-digit year
        assertFalse(validator.isValid("12/20227", null)); // 5-digit year
    }

    @Test
    void testWhitespaceHandling() {
        validator.initialize(getAnnotation("defaultExpiry"));

        assertTrue(validator.isValid(" 12/27 ", null)); // Leading/trailing spaces
        assertTrue(validator.isValid("12 / 27", null)); // Space around separator
        assertTrue(validator.isValid("12  /  27", null)); // Multiple spaces around separator
    }

    @Test
    void testEdgeCaseMonths() {
        validator.initialize(getAnnotation("anyExpiry"));

        assertTrue(validator.isValid("01/27", null)); // January
        assertTrue(validator.isValid("06/27", null)); // June
        assertTrue(validator.isValid("09/27", null)); // September
        assertTrue(validator.isValid("12/27", null)); // December
    }

    @Test
    void testBlankString() {
        validator.initialize(getAnnotation("defaultExpiry"));

        assertTrue(validator.isValid("", null));
        assertTrue(validator.isValid("   ", null));
    }

    @Test
    void testInvalidCharacters() {
        validator.initialize(getAnnotation("defaultExpiry"));

        assertFalse(validator.isValid("12a/27", null)); // Letter in month
        assertFalse(validator.isValid("12/2a7", null)); // Letter in year
        assertFalse(validator.isValid("ab/cd", null)); // All letters
        assertFalse(validator.isValid("12/25/2027", null)); // Too many parts
    }

    @Test
    void testSeparatorVariations() {
        validator.initialize(getAnnotation("defaultExpiry"));

        assertTrue(validator.isValid("12/27", null)); // Forward slash
        assertFalse(validator.isValid("12-27", null)); // Dash
        assertFalse(validator.isValid("12.27", null)); // Dot
        assertFalse(validator.isValid("12:27", null)); // Colon
        assertFalse(validator.isValid("12 27", null)); // Space only
    }
}
