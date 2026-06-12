package id.xtramile.validator.validator.data;

import id.xtramile.validator.annotation.data.ValidPIN;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PinValidatorTest {

    private PinValidator validator;

    private static ValidPIN getAnnotation(String fieldName) {
        try {
            Field f = PinDummy.class.getDeclaredField(fieldName);
            return f.getAnnotation(ValidPIN.class);
        } catch (NoSuchFieldException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
        validator = new PinValidator();
    }

    @Test
    void testValidPins() {
        validator.initialize(getAnnotation("defaultPin"));

        assertTrue(validator.isValid("135792", null)); // no pattern
        assertTrue(validator.isValid("246813", null)); // no pattern
        assertTrue(validator.isValid("982341", null)); // no pattern
    }

    @Test
    void testInvalidLength() {
        validator.initialize(getAnnotation("defaultPin"));

        assertFalse(validator.isValid("12345", null)); // too short (5 < 6)
        assertFalse(validator.isValid("1234567", null)); // too long (7 > 6)
        assertFalse(validator.isValid("12345a", null)); // non-numeric
    }

    @Test
    void testTooManyRepetitions() {
        validator.initialize(getAnnotation("defaultPin"));

        assertFalse(validator.isValid("111111", null)); // all same digits
        assertFalse(validator.isValid("222222", null)); // all same digits
        assertFalse(validator.isValid("333333", null)); // all same digits
    }

    @Test
    void testSequentialPatterns() {
        validator.initialize(getAnnotation("defaultPin"));

        assertFalse(validator.isValid("123456", null)); // ascending sequence
        assertFalse(validator.isValid("654321", null)); // descending sequence
        assertFalse(validator.isValid("012345", null)); // ascending with zero start
        assertFalse(validator.isValid("098765", null)); // descending with zero start
        assertFalse(validator.isValid("567890", null)); // ascending with zero end
        assertFalse(validator.isValid("543210", null)); // descending with zero end
    }

    @Test
    void testValidRepetitionsAndSequences() {
        validator.initialize(getAnnotation("defaultPin"));

        assertTrue(validator.isValid("112233", null)); // 2 repetitions allowed
        assertTrue(validator.isValid("121212", null)); // alternating pattern
        assertTrue(validator.isValid("135246", null)); // no pattern
    }

    @Test
    void testCustomParameters() {
        validator.initialize(getAnnotation("customPin"));

        assertTrue(validator.isValid("1324", null)); // correct length, no pattern
        assertTrue(validator.isValid("1122", null)); // 2 repetitions allowed
        assertTrue(validator.isValid("1357", null)); // no sequential pattern

        assertFalse(validator.isValid("123", null)); // too short
        assertFalse(validator.isValid("12345", null)); // too long
        assertFalse(validator.isValid("1111", null)); // too many repetitions (4 > 2)
        assertFalse(validator.isValid("1234", null)); // too many sequential (4 > 2)
    }

    private static class PinDummy {
        @ValidPIN
        String defaultPin;

        @ValidPIN(length = 4, maxAllowedRepetitive = 2, maxAllowedSequential = 2)
        String customPin;
    }
}
