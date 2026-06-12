package id.xtramile.validator.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ResponseTypeTest {

    @Test
    void testValidationFailedProperties() {
        ResponseType responseType = ResponseType.VALIDATION_FAILED;
        
        assertEquals(98, responseType.getMessageCode());
        assertEquals("Validation failed", responseType.getDescriptionEn());
        assertEquals("Validasi gagal", responseType.getDescriptionId());
    }

    @Test
    void testUnknownErrorProperties() {
        ResponseType responseType = ResponseType.UNKNOWN_ERROR;
        
        assertEquals(99, responseType.getMessageCode());
        assertEquals("Unknown error", responseType.getDescriptionEn());
        assertEquals("Error tidak diketahui", responseType.getDescriptionId());
    }

    @Test
    void testAllEnumValues() {
        ResponseType[] values = ResponseType.values();
        
        assertEquals(2, values.length);
        assertTrue(containsEnumValue(values, ResponseType.VALIDATION_FAILED));
        assertTrue(containsEnumValue(values, ResponseType.UNKNOWN_ERROR));
    }

    @Test
    void testValueOf() {
        assertEquals(ResponseType.VALIDATION_FAILED, ResponseType.valueOf("VALIDATION_FAILED"));
        assertEquals(ResponseType.UNKNOWN_ERROR, ResponseType.valueOf("UNKNOWN_ERROR"));
    }

    @Test
    void testValueOfInvalidName() {
        assertThrows(IllegalArgumentException.class, () -> ResponseType.valueOf("INVALID_RESPONSE_TYPE"));
    }

    @Test
    void testValueOfNull() {
        assertThrows(NullPointerException.class, () -> ResponseType.valueOf(null));
    }

    @Test
    void testOrdinalValues() {
        assertEquals(0, ResponseType.VALIDATION_FAILED.ordinal());
        assertEquals(1, ResponseType.UNKNOWN_ERROR.ordinal());
    }

    @Test
    void testNameMethod() {
        assertEquals("VALIDATION_FAILED", ResponseType.VALIDATION_FAILED.name());
        assertEquals("UNKNOWN_ERROR", ResponseType.UNKNOWN_ERROR.name());
    }

    @Test
    void testToStringMethod() {
        assertEquals("VALIDATION_FAILED", ResponseType.VALIDATION_FAILED.toString());
        assertEquals("UNKNOWN_ERROR", ResponseType.UNKNOWN_ERROR.toString());
    }

    @Test
    void testEnumComparison() {
        assertTrue(ResponseType.VALIDATION_FAILED.compareTo(ResponseType.UNKNOWN_ERROR) < 0);
        assertTrue(ResponseType.UNKNOWN_ERROR.compareTo(ResponseType.VALIDATION_FAILED) > 0);
    }

    @Test
    void testEnumEquality() {
        assertNotEquals(ResponseType.VALIDATION_FAILED, ResponseType.UNKNOWN_ERROR);
        assertNotEquals(ResponseType.VALIDATION_FAILED, null);
    }

    @Test
    void testEnumHashCode() {
        assertNotEquals(ResponseType.VALIDATION_FAILED.hashCode(), ResponseType.UNKNOWN_ERROR.hashCode());
    }

    @Test
    void testMessageCodesAreUnique() {
        ResponseType[] values = ResponseType.values();
        for (int i = 0; i < values.length; i++) {
            for (int j = i + 1; j < values.length; j++) {
                assertNotEquals(values[i].getMessageCode(), values[j].getMessageCode(),
                    "Message codes should be unique: " + values[i] + " and " + values[j]);
            }
        }
    }

    @Test
    void testDescriptionsAreNotNullOrEmpty() {
        for (ResponseType responseType : ResponseType.values()) {
            assertNotNull(responseType.getDescriptionEn(), "English description should not be null");
            assertNotNull(responseType.getDescriptionId(), "Indonesian description should not be null");
            assertFalse(responseType.getDescriptionEn().trim().isEmpty(), "English description should not be empty");
            assertFalse(responseType.getDescriptionId().trim().isEmpty(), "Indonesian description should not be empty");
        }
    }

    @Test
    void testMessageCodesArePositive() {
        for (ResponseType responseType : ResponseType.values()) {
            assertTrue(responseType.getMessageCode() > 0, 
                "Message code should be positive for " + responseType);
        }
    }

    private boolean containsEnumValue(ResponseType[] values, ResponseType target) {
        for (ResponseType value : values) {
            if (value == target) {
                return true;
            }
        }

        return false;
    }
}
