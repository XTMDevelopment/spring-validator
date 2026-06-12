package id.xtramile.validator.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordTypeTest {

    @Test
    void values_returnsAllConstantsInOrder() {
        assertArrayEquals(
                new PasswordType[]{
                        PasswordType.ANY,
                        PasswordType.ALPHANUMERIC,
                        PasswordType.LETTER_DIGIT,
                        PasswordType.LETTER_MIXED_CASE,
                        PasswordType.FULL,
                        PasswordType.STRONG_3_OF_4
                },
                PasswordType.values()
        );
    }

    @Test
    void valueOf_resolvesKnownNames() {
        assertEquals(PasswordType.ANY, PasswordType.valueOf("ANY"));
        assertEquals(PasswordType.ALPHANUMERIC, PasswordType.valueOf("ALPHANUMERIC"));
        assertEquals(PasswordType.LETTER_DIGIT, PasswordType.valueOf("LETTER_DIGIT"));
        assertEquals(PasswordType.LETTER_MIXED_CASE, PasswordType.valueOf("LETTER_MIXED_CASE"));
        assertEquals(PasswordType.FULL, PasswordType.valueOf("FULL"));
        assertEquals(PasswordType.STRONG_3_OF_4, PasswordType.valueOf("STRONG_3_OF_4"));
    }

    @Test
    void valueOf_rejectsInvalidName() {
        assertThrows(IllegalArgumentException.class, () -> PasswordType.valueOf("INVALID_PASSWORD_TYPE"));
    }

    @Test
    void valueOf_rejectsNull() {
        assertThrows(NullPointerException.class, () -> PasswordType.valueOf(null));
    }
}
