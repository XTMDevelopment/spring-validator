package id.xtramile.validator.validator.data;

import id.xtramile.validator.annotation.data.ValidPassword;
import id.xtramile.validator.enums.PasswordType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PasswordValidatorTest {

    private static class PasswordDummy {
        @ValidPassword
        String defaultPassword;

        @ValidPassword(min = 6, type = PasswordType.ANY)
        String anyPassword;

        @ValidPassword(type = PasswordType.ALPHANUMERIC)
        String alphanumericPassword;

        @ValidPassword(min = 10, type = PasswordType.LETTER_DIGIT)
        String letterDigitPassword;

        @ValidPassword(min = 12, type = PasswordType.LETTER_MIXED_CASE)
        String mixedCasePassword;

        @ValidPassword(type = PasswordType.FULL)
        String fullPassword;

        @ValidPassword(min = 6, type = PasswordType.STRONG_3_OF_4)
        String strongPassword;
    }

    private PasswordValidator validator;

    private static ValidPassword getAnnotation(String fieldName) {
        try {
            Field f = PasswordDummy.class.getDeclaredField(fieldName);
            return f.getAnnotation(ValidPassword.class);
        } catch (NoSuchFieldException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
        validator = new PasswordValidator();
    }

    @Test
    void testBlankValues() {
        validator.initialize(getAnnotation("defaultPassword"));

        assertTrue(validator.isValid("", null));
        assertTrue(validator.isValid(null, null));
    }

    @Test
    void testAnyPasswordType() {
        validator.initialize(getAnnotation("anyPassword"));

        assertTrue(validator.isValid("password123", null));
        assertTrue(validator.isValid("123456", null));
        assertTrue(validator.isValid("abcdef", null));
        assertTrue(validator.isValid("!@#$%^&*()", null));

        assertFalse(validator.isValid("12345", null)); // too short (5 < 6)
        assertFalse(validator.isValid("pass word", null)); // contains whitespace
    }

    @Test
    void testAlphanumericPasswordType() {
        validator.initialize(getAnnotation("alphanumericPassword"));

        assertTrue(validator.isValid("password123", null));
        assertTrue(validator.isValid("12345678", null));
        assertTrue(validator.isValid("abcdefgh", null));

        assertFalse(validator.isValid("password!", null)); // contains symbol
        assertFalse(validator.isValid("pass word", null)); // contains whitespace
        assertFalse(validator.isValid("1234567", null)); // too short (7 < 8)
    }

    @Test
    void testLetterDigitPasswordType() {
        validator.initialize(getAnnotation("letterDigitPassword"));

        assertTrue(validator.isValid("password123", null));
        assertTrue(validator.isValid("abc123def456", null));
        assertTrue(validator.isValid("a1b2c3d4e5f6", null));

        assertFalse(validator.isValid("password", null)); // no digits
        assertFalse(validator.isValid("1234567890", null)); // no letters
        assertFalse(validator.isValid("pass123", null)); // too short (7 < 10)
    }

    @Test
    void testMixedCasePasswordType() {
        validator.initialize(getAnnotation("mixedCasePassword"));

        assertTrue(validator.isValid("Password1234", null)); // 12 chars with mixed case
        assertTrue(validator.isValid("AbCdEfGhIjKl", null));
        assertTrue(validator.isValid("AaBbCcDdEeFf", null));

        assertFalse(validator.isValid("password1234", null)); // no uppercase
        assertFalse(validator.isValid("PASSWORD1234", null)); // no lowercase
        assertFalse(validator.isValid("Pass123", null)); // too short (7 < 12)
    }

    @Test
    void testFullPasswordType() {
        validator.initialize(getAnnotation("fullPassword"));

        assertTrue(validator.isValid("Password123!", null));
        assertTrue(validator.isValid("AbC123!@#", null));
        assertTrue(validator.isValid("MyPass123$", null));

        assertFalse(validator.isValid("Password123", null)); // no symbol
        assertFalse(validator.isValid("password123!", null)); // no uppercase
        assertFalse(validator.isValid("PASSWORD123!", null)); // no lowercase
        assertFalse(validator.isValid("Password!", null)); // no digit
        assertFalse(validator.isValid("Pas123!", null)); // too short (7 < 8)
    }

    @Test
    void testStrongPasswordType() {
        validator.initialize(getAnnotation("strongPassword"));

        // 3 of 4: lower, upper, digit, symbol
        assertTrue(validator.isValid("Password123", null)); // lower, upper, digit
        assertTrue(validator.isValid("password123!", null)); // lower, digit, symbol
        assertTrue(validator.isValid("PASSWORD123!", null)); // upper, digit, symbol
        assertTrue(validator.isValid("Password!", null)); // lower, upper, symbol
        assertTrue(validator.isValid("Password123!", null)); // all 4

        assertFalse(validator.isValid("password", null)); // only lower (1 of 4)
        assertFalse(validator.isValid("password123", null)); // lower, digit (2 of 4)
        assertFalse(validator.isValid("pass", null)); // too short (4 < 6)
    }

    @Test
    void testWhitespaceRejection() {
        validator.initialize(getAnnotation("anyPassword"));

        assertFalse(validator.isValid("pass word", null));
        assertFalse(validator.isValid(" password", null));
        assertFalse(validator.isValid("password ", null));
        assertFalse(validator.isValid("pass word 123", null));
    }

    @Test
    void testMinimumLength() {
        validator.initialize(getAnnotation("anyPassword"));

        assertTrue(validator.isValid("123456", null)); // exactly min
        assertTrue(validator.isValid("1234567", null)); // above min

        assertFalse(validator.isValid("12345", null)); // below min
    }

    @Test
    void testDefaultPasswordType() {
        validator.initialize(getAnnotation("defaultPassword"));

        // Default is FULL type with min=8
        assertTrue(validator.isValid("Password123!", null));
        assertFalse(validator.isValid("Password123", null)); // no symbol
        assertFalse(validator.isValid("Pas123!", null)); // too short
    }
}
