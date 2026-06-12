package id.xtramile.validator.validator.data;

import id.xtramile.validator.annotation.data.ValidTaxID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TaxIdValidatorTest {

    private TaxIdValidator validator;

    private static ValidTaxID getAnnotation(String fieldName) {
        try {
            Field f = TaxIdDummy.class.getDeclaredField(fieldName);
            return f.getAnnotation(ValidTaxID.class);
        } catch (NoSuchFieldException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
        validator = new TaxIdValidator();
    }

    @Test
    void testValidIndonesianTaxIds() {
        validator.initialize(getAnnotation("defaultTaxId"));

        assertTrue(validator.isValid("123456789012345", null)); // 15 digits
        assertTrue(validator.isValid("1234567890123456", null)); // 16 digits
        assertTrue(validator.isValid("000000000000000", null)); // all zeros
        assertTrue(validator.isValid("123-456-789-012-345", null)); // with separators
        assertTrue(validator.isValid("123 456 789 012 345", null)); // with spaces
    }

    @Test
    void testInvalidIndonesianTaxIds() {
        validator.initialize(getAnnotation("defaultTaxId"));

        assertFalse(validator.isValid("12345678901234", null)); // too short (14 < 15)
        assertFalse(validator.isValid("12345678901234567", null)); // too long (17 > 16)
        assertFalse(validator.isValid("12345678901234a", null)); // contains letter
        assertFalse(validator.isValid("12345678901234-", null)); // ends with separator
    }

    @Test
    void testBlankValues() {
        validator.initialize(getAnnotation("defaultTaxId"));

        assertTrue(validator.isValid("", null));
        assertTrue(validator.isValid("   ", null));
        assertTrue(validator.isValid(null, null));
    }

    @Test
    void testExplicitIndonesianCountry() {
        validator.initialize(getAnnotation("indonesianTaxId"));

        assertTrue(validator.isValid("123456789012345", null));
        assertTrue(validator.isValid("1234567890123456", null));
        assertFalse(validator.isValid("12345678901234", null));
    }

    @Test
    void testNonIndonesianCountry() {
        validator.initialize(getAnnotation("usTaxId"));

        assertFalse(validator.isValid("123456789012345", null));
        assertFalse(validator.isValid("1234567890123456", null));
        assertFalse(validator.isValid("any-value", null));
    }

    @Test
    void testEdgeCases() {
        validator.initialize(getAnnotation("defaultTaxId"));

        assertTrue(validator.isValid("123456789012345", null)); // exactly 15 digits
        assertTrue(validator.isValid("1234567890123456", null)); // exactly 16 digits
        assertFalse(validator.isValid("12345678901234", null)); // 14 digits
        assertFalse(validator.isValid("12345678901234567", null)); // 17 digits
    }

    private static class TaxIdDummy {
        @ValidTaxID
        String defaultTaxId;

        @ValidTaxID()
        String indonesianTaxId;

        @ValidTaxID(country = "US")
        String usTaxId;
    }
}
