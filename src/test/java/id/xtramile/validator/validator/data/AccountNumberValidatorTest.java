package id.xtramile.validator.validator.data;

import id.xtramile.validator.annotation.data.ValidAccountNumber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AccountNumberValidatorTest {

    private static class AccountNumberDummy {
        @ValidAccountNumber
        String defaultAccountNumber;

        @ValidAccountNumber(min = 10, max = 15)
        String customAccountNumber;
    }

    private AccountNumberValidator validator;

    private static ValidAccountNumber getAnnotation(String fieldName) {
        try {
            Field f = AccountNumberDummy.class.getDeclaredField(fieldName);
            return f.getAnnotation(ValidAccountNumber.class);
        } catch (NoSuchFieldException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
        validator = new AccountNumberValidator();
    }

    @Test
    void testValidAccountNumbers() {
        validator.initialize(getAnnotation("defaultAccountNumber"));

        assertTrue(validator.isValid("12345678", null));
        assertTrue(validator.isValid("12345678901234567890", null));
        assertTrue(validator.isValid("00000000", null));
    }

    @Test
    void testInvalidLength() {
        validator.initialize(getAnnotation("defaultAccountNumber"));

        assertFalse(validator.isValid("1234567", null)); // too short (7 < 8)
        assertFalse(validator.isValid("123456789012345678901", null)); // too long (21 > 20)
    }

    @Test
    void testNonNumericCharacters() {
        validator.initialize(getAnnotation("defaultAccountNumber"));

        assertFalse(validator.isValid("1234567a", null));
        assertFalse(validator.isValid("1234567-", null));
        assertFalse(validator.isValid("1234567 ", null));
        assertFalse(validator.isValid("1234567.", null));
    }

    @Test
    void testBlankValues() {
        validator.initialize(getAnnotation("defaultAccountNumber"));

        assertTrue(validator.isValid("", null));
        assertTrue(validator.isValid("   ", null));
        assertTrue(validator.isValid(null, null));
    }

    @Test
    void testCustomMinMax() {
        validator.initialize(getAnnotation("customAccountNumber"));

        assertTrue(validator.isValid("1234567890", null)); // exactly min
        assertTrue(validator.isValid("123456789012345", null)); // exactly max
        assertTrue(validator.isValid("12345678901", null)); // in between

        assertFalse(validator.isValid("123456789", null)); // too short (9 < 10)
        assertFalse(validator.isValid("1234567890123456", null)); // too long (16 > 15)
    }
}
