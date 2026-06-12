package id.xtramile.validator.validator.data;

import id.xtramile.validator.annotation.data.ValidUsername;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UsernameValidatorTest {

    private static class UsernameDummy {
        @ValidUsername
        String defaultUsername;

        @ValidUsername(min = 3, max = 10)
        String customUsername;
    }

    private UsernameValidator validator;

    private static ValidUsername getAnnotation(String fieldName) {
        try {
            Field f = UsernameDummy.class.getDeclaredField(fieldName);
            return f.getAnnotation(ValidUsername.class);
        } catch (NoSuchFieldException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
        validator = new UsernameValidator();
    }

    @Test
    void testValidUsernames() {
        validator.initialize(getAnnotation("defaultUsername"));

        assertTrue(validator.isValid("john", null));
        assertTrue(validator.isValid("john_doe", null));
        assertTrue(validator.isValid("user123", null));
        assertTrue(validator.isValid("test.user", null));
        assertTrue(validator.isValid("a".repeat(20), null)); // exactly max length
    }

    @Test
    void testInvalidLength() {
        validator.initialize(getAnnotation("defaultUsername"));

        assertFalse(validator.isValid("jo", null)); // too short (2 < 4)
        assertFalse(validator.isValid("a".repeat(21), null)); // too long (21 > 20)
    }

    @Test
    void testInvalidCharacters() {
        validator.initialize(getAnnotation("defaultUsername"));

        assertFalse(validator.isValid("john-doe", null)); // hyphen not allowed
        assertFalse(validator.isValid("john@doe", null)); // @ not allowed
        assertFalse(validator.isValid("john doe", null)); // space not allowed
        assertFalse(validator.isValid("john#doe", null)); // # not allowed
        assertFalse(validator.isValid("john+doe", null)); // + not allowed
    }

    @Test
    void testTrimming() {
        validator.initialize(getAnnotation("defaultUsername"));

        assertTrue(validator.isValid("  john  ", null));
        assertTrue(validator.isValid("  user123  ", null));
    }

    @Test
    void testBlankValues() {
        validator.initialize(getAnnotation("defaultUsername"));

        assertTrue(validator.isValid("", null));
        assertTrue(validator.isValid("   ", null));
        assertTrue(validator.isValid(null, null));
    }

    @Test
    void testCustomMinMax() {
        validator.initialize(getAnnotation("customUsername"));

        assertTrue(validator.isValid("abc", null)); // exactly min
        assertTrue(validator.isValid("1234567890", null)); // exactly max
        assertTrue(validator.isValid("user123", null)); // in between

        assertFalse(validator.isValid("ab", null)); // too short (2 < 3)
        assertFalse(validator.isValid("12345678901", null)); // too long (11 > 10)
    }

    @Test
    void testEdgeCases() {
        validator.initialize(getAnnotation("defaultUsername"));

        assertTrue(validator.isValid("a1b2", null)); // minimum valid
        assertTrue(validator.isValid("a1b2c3d4e5f6g7h8i9j0", null)); // exactly 20 chars
        assertFalse(validator.isValid("a1b2c3d4e5f6g7h8i9j0k", null)); // 21 chars
    }
}
