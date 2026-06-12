package id.xtramile.validator.validator.data;

import id.xtramile.validator.annotation.data.ValidNationalID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NationalIdValidatorTest {

    private static class NationalIdDummy {
        @ValidNationalID
        String defaultNationalId;

        @ValidNationalID()
        String indonesianNationalId;

        @ValidNationalID(country = "US")
        String usNationalId;
    }

    private NationalIdValidator validator;

    private static ValidNationalID getAnnotation(String fieldName) {
        try {
            Field f = NationalIdDummy.class.getDeclaredField(fieldName);
            return f.getAnnotation(ValidNationalID.class);
        } catch (NoSuchFieldException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
        validator = new NationalIdValidator();
    }

    @Test
    void testValidIndonesianNIK() {
        validator.initialize(getAnnotation("defaultNationalId"));

        assertTrue(validator.isValid("1234567890123456", null)); // exactly 16 digits
        assertTrue(validator.isValid("0000000000000000", null)); // all zeros
        assertTrue(validator.isValid("1234567890123456", null)); // valid NIK
    }

    @Test
    void testInvalidIndonesianNIK() {
        validator.initialize(getAnnotation("defaultNationalId"));

        assertFalse(validator.isValid("123456789012345", null)); // too short (15 < 16)
        assertFalse(validator.isValid("12345678901234567", null)); // too long (17 > 16)
        assertFalse(validator.isValid("123456789012345a", null)); // contains letter
        assertFalse(validator.isValid("12345678901234-6", null)); // contains separator
    }

    @Test
    void testBlankValues() {
        validator.initialize(getAnnotation("defaultNationalId"));

        assertTrue(validator.isValid("", null));
        assertTrue(validator.isValid("   ", null));
        assertTrue(validator.isValid(null, null));
    }

    @Test
    void testExplicitIndonesianCountry() {
        validator.initialize(getAnnotation("indonesianNationalId"));

        assertTrue(validator.isValid("1234567890123456", null));
        assertFalse(validator.isValid("123456789012345", null));
    }

    @Test
    void testNonIndonesianCountry() {
        validator.initialize(getAnnotation("usNationalId"));

        assertFalse(validator.isValid("1234567890123456", null));
        assertFalse(validator.isValid("any-value", null));
    }

    @Test
    void testEdgeCases() {
        validator.initialize(getAnnotation("defaultNationalId"));

        assertTrue(validator.isValid("1234567890123456", null)); // exactly 16 digits
        assertFalse(validator.isValid("123456789012345", null)); // 15 digits
        assertFalse(validator.isValid("12345678901234567", null)); // 17 digits
    }

    @Test
    void testRealWorldNIKExamples() {
        validator.initialize(getAnnotation("defaultNationalId"));

        // These are example NIKs (not real ones)
        assertTrue(validator.isValid("1234567890123456", null));
        assertTrue(validator.isValid("9876543210987654", null));
        assertTrue(validator.isValid("1111111111111111", null));
    }
}
