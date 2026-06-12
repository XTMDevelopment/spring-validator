package id.xtramile.validator.validator.data;

import id.xtramile.validator.annotation.data.ValidHexColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HexColorValidatorTest {

    private HexColorValidator validator;

    private static ValidHexColor getAnnotation(String fieldName) {
        try {
            Field f = HexColorDummy.class.getDeclaredField(fieldName);
            return f.getAnnotation(ValidHexColor.class);
        } catch (NoSuchFieldException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
        validator = new HexColorValidator();
    }

    @Test
    void testValidLongFormHexColors() {
        validator.initialize(getAnnotation("defaultHexColor"));

        assertTrue(validator.isValid("#000000", null));
        assertTrue(validator.isValid("#FFFFFF", null));
        assertTrue(validator.isValid("#123456", null));
        assertTrue(validator.isValid("#abcdef", null));
        assertTrue(validator.isValid("#ABCDEF", null));
        assertTrue(validator.isValid("#AbCdEf", null));
    }

    @Test
    void testValidShortFormHexColors() {
        validator.initialize(getAnnotation("defaultHexColor"));

        assertTrue(validator.isValid("#000", null));
        assertTrue(validator.isValid("#FFF", null));
        assertTrue(validator.isValid("#123", null));
        assertTrue(validator.isValid("#abc", null));
        assertTrue(validator.isValid("#ABC", null));
        assertTrue(validator.isValid("#AbC", null));
    }

    @Test
    void testInvalidHexColors() {
        validator.initialize(getAnnotation("defaultHexColor"));

        assertFalse(validator.isValid("000000", null)); // missing #
        assertFalse(validator.isValid("#00000", null)); // too short (5 chars)
        assertFalse(validator.isValid("#0000000", null)); // too long (7 chars)
        assertFalse(validator.isValid("#GGGGGG", null)); // invalid characters
        assertFalse(validator.isValid("#12345G", null)); // invalid character
        assertFalse(validator.isValid("#", null)); // just #
        assertFalse(validator.isValid("##000000", null)); // double #
        assertFalse(validator.isValid("#000000 ", null)); // trailing space
        assertFalse(validator.isValid(" #000000", null)); // leading space
    }

    @Test
    void testShortFormAllowedTrue() {
        validator.initialize(getAnnotation("shortFormAllowed"));

        assertTrue(validator.isValid("#000000", null)); // long form
        assertTrue(validator.isValid("#000", null)); // short form
        assertTrue(validator.isValid("#FFF", null)); // short form
    }

    @Test
    void testShortFormAllowedFalse() {
        validator.initialize(getAnnotation("shortFormNotAllowed"));

        assertTrue(validator.isValid("#000000", null)); // long form
        assertFalse(validator.isValid("#000", null)); // short form not allowed
        assertFalse(validator.isValid("#FFF", null)); // short form not allowed
    }

    @Test
    void testBlankValues() {
        validator.initialize(getAnnotation("defaultHexColor"));

        assertTrue(validator.isValid("", null));
        assertTrue(validator.isValid("   ", null));
        assertTrue(validator.isValid(null, null));
    }

    @Test
    void testCaseInsensitive() {
        validator.initialize(getAnnotation("defaultHexColor"));

        assertTrue(validator.isValid("#abcdef", null));
        assertTrue(validator.isValid("#ABCDEF", null));
        assertTrue(validator.isValid("#AbCdEf", null));
        assertTrue(validator.isValid("#abc", null));
        assertTrue(validator.isValid("#ABC", null));
        assertTrue(validator.isValid("#AbC", null));
    }

    @Test
    void testCommonColors() {
        validator.initialize(getAnnotation("defaultHexColor"));

        assertTrue(validator.isValid("#FF0000", null)); // red
        assertTrue(validator.isValid("#00FF00", null)); // green
        assertTrue(validator.isValid("#0000FF", null)); // blue
        assertTrue(validator.isValid("#FFFF00", null)); // yellow
        assertTrue(validator.isValid("#FF00FF", null)); // magenta
        assertTrue(validator.isValid("#00FFFF", null)); // cyan
        assertTrue(validator.isValid("#000000", null)); // black
        assertTrue(validator.isValid("#FFFFFF", null)); // white
    }

    @Test
    void testEdgeCases() {
        validator.initialize(getAnnotation("defaultHexColor"));

        assertTrue(validator.isValid("#000000", null)); // minimum valid long form
        assertTrue(validator.isValid("#FFFFFF", null)); // maximum valid long form
        assertTrue(validator.isValid("#000", null)); // minimum valid short form
        assertTrue(validator.isValid("#FFF", null)); // maximum valid short form
    }

    private static class HexColorDummy {
        @ValidHexColor
        String defaultHexColor;

        @ValidHexColor()
        String shortFormAllowed;

        @ValidHexColor(shortFormAllowed = false)
        String shortFormNotAllowed;
    }
}
