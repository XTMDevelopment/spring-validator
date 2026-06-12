package id.xtramile.validator.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PasswordTypeTest {

    @Test
    void testAllEnumValues() {
        PasswordType[] values = PasswordType.values();
        
        assertEquals(6, values.length);
        assertTrue(containsEnumValue(values, PasswordType.ANY));
        assertTrue(containsEnumValue(values, PasswordType.ALPHANUMERIC));
        assertTrue(containsEnumValue(values, PasswordType.LETTER_DIGIT));
        assertTrue(containsEnumValue(values, PasswordType.LETTER_MIXED_CASE));
        assertTrue(containsEnumValue(values, PasswordType.FULL));
        assertTrue(containsEnumValue(values, PasswordType.STRONG_3_OF_4));
    }

    @Test
    void testValueOf() {
        assertEquals(PasswordType.ANY, PasswordType.valueOf("ANY"));
        assertEquals(PasswordType.ALPHANUMERIC, PasswordType.valueOf("ALPHANUMERIC"));
        assertEquals(PasswordType.LETTER_DIGIT, PasswordType.valueOf("LETTER_DIGIT"));
        assertEquals(PasswordType.LETTER_MIXED_CASE, PasswordType.valueOf("LETTER_MIXED_CASE"));
        assertEquals(PasswordType.FULL, PasswordType.valueOf("FULL"));
        assertEquals(PasswordType.STRONG_3_OF_4, PasswordType.valueOf("STRONG_3_OF_4"));
    }

    @Test
    void testValueOfInvalidName() {
        assertThrows(IllegalArgumentException.class, () -> PasswordType.valueOf("INVALID_PASSWORD_TYPE"));
    }

    @Test
    void testValueOfNull() {
        assertThrows(NullPointerException.class, () -> PasswordType.valueOf(null));
    }

    @Test
    void testOrdinalValues() {
        assertEquals(0, PasswordType.ANY.ordinal());
        assertEquals(1, PasswordType.ALPHANUMERIC.ordinal());
        assertEquals(2, PasswordType.LETTER_DIGIT.ordinal());
        assertEquals(3, PasswordType.LETTER_MIXED_CASE.ordinal());
        assertEquals(4, PasswordType.FULL.ordinal());
        assertEquals(5, PasswordType.STRONG_3_OF_4.ordinal());
    }

    @Test
    void testNameMethod() {
        assertEquals("ANY", PasswordType.ANY.name());
        assertEquals("ALPHANUMERIC", PasswordType.ALPHANUMERIC.name());
        assertEquals("LETTER_DIGIT", PasswordType.LETTER_DIGIT.name());
        assertEquals("LETTER_MIXED_CASE", PasswordType.LETTER_MIXED_CASE.name());
        assertEquals("FULL", PasswordType.FULL.name());
        assertEquals("STRONG_3_OF_4", PasswordType.STRONG_3_OF_4.name());
    }

    @Test
    void testToStringMethod() {
        assertEquals("ANY", PasswordType.ANY.toString());
        assertEquals("ALPHANUMERIC", PasswordType.ALPHANUMERIC.toString());
        assertEquals("LETTER_DIGIT", PasswordType.LETTER_DIGIT.toString());
        assertEquals("LETTER_MIXED_CASE", PasswordType.LETTER_MIXED_CASE.toString());
        assertEquals("FULL", PasswordType.FULL.toString());
        assertEquals("STRONG_3_OF_4", PasswordType.STRONG_3_OF_4.toString());
    }

    @Test
    void testEnumComparison() {
        assertTrue(PasswordType.ANY.compareTo(PasswordType.ALPHANUMERIC) < 0);
        assertTrue(PasswordType.ALPHANUMERIC.compareTo(PasswordType.LETTER_DIGIT) < 0);
        assertTrue(PasswordType.LETTER_DIGIT.compareTo(PasswordType.LETTER_MIXED_CASE) < 0);
        assertTrue(PasswordType.LETTER_MIXED_CASE.compareTo(PasswordType.FULL) < 0);
        assertTrue(PasswordType.FULL.compareTo(PasswordType.STRONG_3_OF_4) < 0);
    }

    @Test
    void testEnumEquality() {
        assertNotEquals(PasswordType.ANY, PasswordType.ALPHANUMERIC);
        assertNotEquals(PasswordType.ANY, null);
    }

    @Test
    void testEnumHashCode() {
        assertNotEquals(PasswordType.ANY.hashCode(), PasswordType.ALPHANUMERIC.hashCode());
    }

    @Test
    void testEnumValuesAreUnique() {
        PasswordType[] values = PasswordType.values();
        for (int i = 0; i < values.length; i++) {
            for (int j = i + 1; j < values.length; j++) {
                assertNotEquals(values[i], values[j], 
                    "Enum values should be unique: " + values[i] + " and " + values[j]);
            }
        }
    }

    @Test
    void testEnumNamesAreNotNullOrEmpty() {
        for (PasswordType passwordType : PasswordType.values()) {
            assertNotNull(passwordType.name(), "Enum name should not be null");
            assertFalse(passwordType.name().trim().isEmpty(), "Enum name should not be empty");
        }
    }

    @Test
    void testEnumValuesIteration() {
        PasswordType[] expectedValues = {
            PasswordType.ANY, 
            PasswordType.ALPHANUMERIC, 
            PasswordType.LETTER_DIGIT, 
            PasswordType.LETTER_MIXED_CASE, 
            PasswordType.FULL, 
            PasswordType.STRONG_3_OF_4
        };
        PasswordType[] actualValues = PasswordType.values();
        
        assertEquals(expectedValues.length, actualValues.length);
        for (int i = 0; i < expectedValues.length; i++) {
            assertEquals(expectedValues[i], actualValues[i]);
        }
    }

    @Test
    void testEnumSwitchStatementCompatibility() {
        for (PasswordType passwordType : PasswordType.values()) {
            String result;

            switch (passwordType) {
                case ANY:
                    result = "any";
                    break;

                case ALPHANUMERIC:
                    result = "alphanumeric";
                    break;

                case LETTER_DIGIT:
                    result = "letter_digit";
                    break;

                case LETTER_MIXED_CASE:
                    result = "letter_mixed_case";
                    break;

                case FULL:
                    result = "full";
                    break;

                case STRONG_3_OF_4:
                    result = "strong_3_of_4";
                    break;

                default:
                    result = "";
            }

            assertNotNull(result);
            assertFalse(result.trim().isEmpty());
        }
    }

    @Test
    void testPasswordTypeProgression() {
        PasswordType[] values = PasswordType.values();

        assertSame(PasswordType.ANY, values[0], "ANY should be the simplest");
        assertSame(PasswordType.ALPHANUMERIC, values[1], "ALPHANUMERIC should be second");
        assertSame(PasswordType.LETTER_DIGIT, values[2], "LETTER_DIGIT should be third");
        assertSame(PasswordType.LETTER_MIXED_CASE, values[3], "LETTER_MIXED_CASE should be fourth");
        assertSame(PasswordType.FULL, values[4], "FULL should be fifth");
        assertSame(PasswordType.STRONG_3_OF_4, values[5], "STRONG_3_OF_4 should be the most complex");
    }

    @Test
    @DisplayName("Test enum values contain expected password policy types")
    void testEnumValuesContainExpectedTypes() {
        PasswordType[] values = PasswordType.values();

        boolean hasAny = false;
        boolean hasAlphanumeric = false;
        boolean hasLetterDigit = false;
        boolean hasLetterMixedCase = false;
        boolean hasFull = false;
        boolean hasStrong3Of4 = false;
        
        for (PasswordType type : values) {
            switch (type) {
                case ANY:
                    hasAny = true;
                    break;

                case ALPHANUMERIC:
                    hasAlphanumeric = true;
                    break;

                case LETTER_DIGIT:
                    hasLetterDigit = true;
                    break;

                case LETTER_MIXED_CASE:
                    hasLetterMixedCase = true;
                    break;

                case FULL:
                    hasFull = true;
                    break;

                case STRONG_3_OF_4:
                    hasStrong3Of4 = true;
                    break;
            }
        }
        
        assertTrue(hasAny, "Should contain ANY type");
        assertTrue(hasAlphanumeric, "Should contain ALPHANUMERIC type");
        assertTrue(hasLetterDigit, "Should contain LETTER_DIGIT type");
        assertTrue(hasLetterMixedCase, "Should contain LETTER_MIXED_CASE type");
        assertTrue(hasFull, "Should contain FULL type");
        assertTrue(hasStrong3Of4, "Should contain STRONG_3_OF_4 type");
    }

    private boolean containsEnumValue(PasswordType[] values, PasswordType target) {
        for (PasswordType value : values) {
            if (value == target) {
                return true;
            }
        }
        return false;
    }
}
