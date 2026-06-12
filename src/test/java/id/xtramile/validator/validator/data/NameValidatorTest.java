package id.xtramile.validator.validator.data;

import id.xtramile.validator.annotation.data.ValidName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NameValidatorTest {

    private static class NameDummy {
        @ValidName
        String defaultName;

        @ValidName(min = 2, max = 10, allowedSymbols = {".", "'", "-"})
        String customName;

        @ValidName(allowDigits = true)
        String nameWithDigits;
    }

    private NameValidator validator;

    private static ValidName getAnnotation(String fieldName) {
        try {
            Field f = NameDummy.class.getDeclaredField(fieldName);
            return f.getAnnotation(ValidName.class);
        } catch (NoSuchFieldException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
        validator = new NameValidator();
    }

    @Test
    void testAllowedCommonNames() {
        validator.initialize(getAnnotation("defaultName"));

        assertTrue(validator.isValid("John Doe", null));
        assertTrue(validator.isValid("O'Connor", null));
        assertTrue(validator.isValid("A...B", null));
    }

    @Test
    void testTooShortAndTooLong() {
        validator.initialize(getAnnotation("defaultName"));

        assertFalse(validator.isValid("Jo", null));
        assertFalse(validator.isValid("  A   ", null));
        assertFalse(validator.isValid("A".repeat(101), null));
    }

    @Test
    void testInvalidChars() {
        validator.initialize(getAnnotation("defaultName"));

        assertFalse(validator.isValid("John_ Doe", null));
        assertFalse(validator.isValid("John3", null));
        assertFalse(validator.isValid("John🙂", null));
    }

    @Test
    void testBlank() {
        validator.initialize(getAnnotation("defaultName"));

        assertTrue(validator.isValid("", null));
        assertTrue(validator.isValid("   ", null));
    }

    @Test
    void testTrimming() {
        validator.initialize(getAnnotation("defaultName"));

        assertTrue(validator.isValid("  John Doe   ", null));
    }

    @Test
    void testCustomSymbols() {
        validator.initialize(getAnnotation("customName"));
        // min=2, max=10, allowedSymbols={"-","'", "."}

        assertTrue(validator.isValid("A.B", null));
        assertTrue(validator.isValid("Jo-An", null));
        assertTrue(validator.isValid("O'Neal", null));

        assertFalse(validator.isValid("Jo_An", null));
    }

    @Test
    void testAllowDigitsDisabled() {
        validator.initialize(getAnnotation("defaultName"));
        // allowDigits defaults to false

        assertFalse(validator.isValid("John3", null));
        assertFalse(validator.isValid("Mary123", null));
        assertFalse(validator.isValid("Test1Name", null));
    }

    @Test
    void testAllowDigitsEnabled() {
        validator.initialize(getAnnotation("nameWithDigits"));
        // allowDigits = true

        assertTrue(validator.isValid("John3", null));
        assertTrue(validator.isValid("Mary123", null));
        assertTrue(validator.isValid("Test1Name", null));
        assertTrue(validator.isValid("O'Connor2", null));
        assertTrue(validator.isValid("A.B.123", null));
        
        // Still validates length and other constraints
        assertFalse(validator.isValid("A1", null)); // too short
        assertFalse(validator.isValid("A".repeat(101) + "1", null)); // too long
    }
}
