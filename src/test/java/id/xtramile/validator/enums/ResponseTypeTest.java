package id.xtramile.validator.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResponseTypeTest {

    @Test
    void validationFailed_hasExpectedMetadata() {
        ResponseType responseType = ResponseType.VALIDATION_FAILED;

        assertEquals(98, responseType.getMessageCode());
        assertEquals("Validation failed", responseType.getDescriptionEn());
        assertEquals("Validasi gagal", responseType.getDescriptionId());
    }

    @Test
    void unknownError_hasExpectedMetadata() {
        ResponseType responseType = ResponseType.UNKNOWN_ERROR;

        assertEquals(99, responseType.getMessageCode());
        assertEquals("Unknown error", responseType.getDescriptionEn());
        assertEquals("Error tidak diketahui", responseType.getDescriptionId());
    }

    @Test
    void values_returnsAllConstants() {
        assertArrayEquals(
                new ResponseType[]{ResponseType.VALIDATION_FAILED, ResponseType.UNKNOWN_ERROR},
                ResponseType.values()
        );
    }

    @Test
    void valueOf_resolvesKnownNames() {
        assertEquals(ResponseType.VALIDATION_FAILED, ResponseType.valueOf("VALIDATION_FAILED"));
        assertEquals(ResponseType.UNKNOWN_ERROR, ResponseType.valueOf("UNKNOWN_ERROR"));
    }

    @Test
    void messageCodes_areUniqueAndPositive() {
        for (ResponseType responseType : ResponseType.values()) {
            assertTrue(responseType.getMessageCode() > 0);
            assertNotNull(responseType.getDescriptionEn());
            assertNotNull(responseType.getDescriptionId());
            assertFalse(responseType.getDescriptionEn().isBlank());
            assertFalse(responseType.getDescriptionId().isBlank());
        }

        assertNotEquals(
                ResponseType.VALIDATION_FAILED.getMessageCode(),
                ResponseType.UNKNOWN_ERROR.getMessageCode()
        );
    }
}
