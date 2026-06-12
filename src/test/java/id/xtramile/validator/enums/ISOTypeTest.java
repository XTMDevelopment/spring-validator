package id.xtramile.validator.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ISOTypeTest {

    @Test
    void testAllEnumValues() {
        ISOType[] values = ISOType.values();
        
        assertEquals(4, values.length);
        assertTrue(containsEnumValue(values, ISOType.CURRENCY));
        assertTrue(containsEnumValue(values, ISOType.COUNTRY_ALPHA2));
        assertTrue(containsEnumValue(values, ISOType.COUNTRY_ALPHA3));
        assertTrue(containsEnumValue(values, ISOType.LANGUAGE));
    }

    @Test
    void testValueOf() {
        assertEquals(ISOType.CURRENCY, ISOType.valueOf("CURRENCY"));
        assertEquals(ISOType.COUNTRY_ALPHA2, ISOType.valueOf("COUNTRY_ALPHA2"));
        assertEquals(ISOType.COUNTRY_ALPHA3, ISOType.valueOf("COUNTRY_ALPHA3"));
        assertEquals(ISOType.LANGUAGE, ISOType.valueOf("LANGUAGE"));
    }

    @Test
    void testValueOfInvalidName() {
        assertThrows(IllegalArgumentException.class, () -> ISOType.valueOf("INVALID_ISO_TYPE"));
    }

    @Test
    void testValueOfNull() {
        assertThrows(NullPointerException.class, () -> ISOType.valueOf(null));
    }

    @Test
    void testOrdinalValues() {
        assertEquals(0, ISOType.CURRENCY.ordinal());
        assertEquals(1, ISOType.COUNTRY_ALPHA2.ordinal());
        assertEquals(2, ISOType.COUNTRY_ALPHA3.ordinal());
        assertEquals(3, ISOType.LANGUAGE.ordinal());
    }

    @Test
    void testNameMethod() {
        assertEquals("CURRENCY", ISOType.CURRENCY.name());
        assertEquals("COUNTRY_ALPHA2", ISOType.COUNTRY_ALPHA2.name());
        assertEquals("COUNTRY_ALPHA3", ISOType.COUNTRY_ALPHA3.name());
        assertEquals("LANGUAGE", ISOType.LANGUAGE.name());
    }

    @Test
    void testToStringMethod() {
        assertEquals("CURRENCY", ISOType.CURRENCY.toString());
        assertEquals("COUNTRY_ALPHA2", ISOType.COUNTRY_ALPHA2.toString());
        assertEquals("COUNTRY_ALPHA3", ISOType.COUNTRY_ALPHA3.toString());
        assertEquals("LANGUAGE", ISOType.LANGUAGE.toString());
    }

    @Test
    void testEnumComparison() {
        assertTrue(ISOType.CURRENCY.compareTo(ISOType.COUNTRY_ALPHA2) < 0);
        assertTrue(ISOType.COUNTRY_ALPHA2.compareTo(ISOType.COUNTRY_ALPHA3) < 0);
        assertTrue(ISOType.COUNTRY_ALPHA3.compareTo(ISOType.LANGUAGE) < 0);
    }

    @Test
    void testEnumEquality() {
        assertNotEquals(ISOType.CURRENCY, ISOType.COUNTRY_ALPHA2);
        assertNotEquals(ISOType.CURRENCY, null);
    }

    @Test
    void testEnumHashCode() {
        assertNotEquals(ISOType.CURRENCY.hashCode(), ISOType.COUNTRY_ALPHA2.hashCode());
    }

    @Test
    void testEnumValuesAreUnique() {
        ISOType[] values = ISOType.values();
        for (int i = 0; i < values.length; i++) {
            for (int j = i + 1; j < values.length; j++) {
                assertNotEquals(values[i], values[j], 
                    "Enum values should be unique: " + values[i] + " and " + values[j]);
            }
        }
    }

    @Test
    void testEnumNamesAreNotNullOrEmpty() {
        for (ISOType isoType : ISOType.values()) {
            assertNotNull(isoType.name(), "Enum name should not be null");
            assertFalse(isoType.name().trim().isEmpty(), "Enum name should not be empty");
        }
    }

    @Test
    void testEnumValuesIteration() {
        ISOType[] expectedValues = {ISOType.CURRENCY, ISOType.COUNTRY_ALPHA2, ISOType.COUNTRY_ALPHA3, ISOType.LANGUAGE};
        ISOType[] actualValues = ISOType.values();
        
        assertEquals(expectedValues.length, actualValues.length);
        for (int i = 0; i < expectedValues.length; i++) {
            assertEquals(expectedValues[i], actualValues[i]);
        }
    }

    @Test
    void testEnumSwitchStatementCompatibility() {
        for (ISOType isoType : ISOType.values()) {
            String result;

            switch (isoType) {
                case CURRENCY:
                    result = "currency";
                    break;

                case COUNTRY_ALPHA2:
                    result = "country_alpha2";
                    break;

                case COUNTRY_ALPHA3:
                    result = "country_alpha3";
                    break;

                case LANGUAGE:
                    result = "language";
                    break;

                default:
                    result = "";
            }

            assertNotNull(result);
            assertFalse(result.trim().isEmpty());
        }
    }

    private boolean containsEnumValue(ISOType[] values, ISOType target) {
        for (ISOType value : values) {
            if (value == target) {
                return true;
            }
        }

        return false;
    }
}
