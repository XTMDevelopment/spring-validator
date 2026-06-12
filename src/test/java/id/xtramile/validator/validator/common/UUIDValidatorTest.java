package id.xtramile.validator.validator.common;

import id.xtramile.validator.annotation.common.ValidUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UUIDValidatorTest {

    private static class UUIDDummy {
        @ValidUUID
        String uuidField;
    }

    private UUIDValidator validator;

    private static ValidUUID getAnnotation(String fieldName) {
        try {
            Field f = UUIDDummy.class.getDeclaredField(fieldName);
            return f.getAnnotation(ValidUUID.class);
        } catch (NoSuchFieldException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
        validator = new UUIDValidator();
    }

    @Test
    void testValidUUIDs() {
        validator.initialize(getAnnotation("uuidField"));

        // Test with generated UUIDs
        String validUUID1 = UUID.randomUUID().toString();
        assertTrue(validator.isValid(validUUID1, null));

        String validUUID2 = UUID.randomUUID().toString();
        assertTrue(validator.isValid(validUUID2, null));

        // Test with known valid UUIDs
        assertTrue(validator.isValid("550e8400-e29b-41d4-a716-446655440000", null));
        assertTrue(validator.isValid("6ba7b810-9dad-11d1-80b4-00c04fd430c8", null));
        assertTrue(validator.isValid("6ba7b811-9dad-11d1-80b4-00c04fd430c8", null));
    }

    @Test
    void testInvalidUUIDs() {
        validator.initialize(getAnnotation("uuidField"));

        assertFalse(validator.isValid("not-a-uuid", null));
        assertFalse(validator.isValid("550e8400-e29b-41d4-a716", null)); // too short
        assertFalse(validator.isValid("550e8400-e29b-41d4-a716-446655440000-extra", null)); // too long
        assertFalse(validator.isValid("550e8400e29b41d4a716446655440000", null)); // no hyphens
        assertFalse(validator.isValid("550e8400-e29b-41d4-a716-44665544000g", null)); // invalid character
        assertFalse(validator.isValid("550e8400-e29b-41d4-a716-44665544000", null)); // too short last segment
        assertFalse(validator.isValid("550e8400-e29b-41d4-a716-4466554400000", null)); // too long last segment
    }

    @Test
    void testBlankValues() {
        validator.initialize(getAnnotation("uuidField"));

        assertTrue(validator.isValid("", null));
        assertTrue(validator.isValid("   ", null));
        assertTrue(validator.isValid(null, null));
    }

    @Test
    void testUUIDVariations() {
        validator.initialize(getAnnotation("uuidField"));

        // Test different UUID versions
        assertTrue(validator.isValid("550e8400-e29b-11d4-a716-446655440000", null)); // version 1
        assertTrue(validator.isValid("6ba7b810-9dad-11d1-80b4-00c04fd430c8", null)); // version 1
        assertTrue(validator.isValid("6ba7b811-9dad-11d1-80b4-00c04fd430c8", null)); // version 1
        assertTrue(validator.isValid("550e8400-e29b-21d4-a716-446655440000", null)); // version 2
        assertTrue(validator.isValid("550e8400-e29b-31d4-a716-446655440000", null)); // version 3
        assertTrue(validator.isValid("550e8400-e29b-41d4-a716-446655440000", null)); // version 4
        assertTrue(validator.isValid("550e8400-e29b-51d4-a716-446655440000", null)); // version 5
    }

    @Test
    void testEdgeCases() {
        validator.initialize(getAnnotation("uuidField"));

        // Test with minimum valid UUID
        assertTrue(validator.isValid("00000000-0000-0000-0000-000000000000", null));
        
        // Test with maximum valid UUID
        assertTrue(validator.isValid("ffffffff-ffff-ffff-ffff-ffffffffffff", null));

        // Test with mixed case (should be valid)
        assertTrue(validator.isValid("550E8400-E29B-41D4-A716-446655440000", null));
    }

    @Test
    void testMalformedUUIDs() {
        validator.initialize(getAnnotation("uuidField"));

        assertFalse(validator.isValid("550e8400-e29b-41d4-a716-44665544000", null)); // missing last character
        assertFalse(validator.isValid("550e8400-e29b-41d4-a716-4466554400000", null)); // extra character
        assertFalse(validator.isValid("550e8400-e29b-41d4-a716-44665544000g", null)); // invalid character
        assertFalse(validator.isValid("550e8400-e29b-41d4-a716-44665544000-", null)); // trailing hyphen
        assertFalse(validator.isValid("-550e8400-e29b-41d4-a716-446655440000", null)); // leading hyphen
        assertFalse(validator.isValid("550e8400--e29b-41d4-a716-446655440000", null)); // double hyphen
    }

    @Test
    void testRealWorldExamples() {
        validator.initialize(getAnnotation("uuidField"));

        // Test with some real-world UUID examples
        assertTrue(validator.isValid("123e4567-e89b-12d3-a456-426614174000", null));
        assertTrue(validator.isValid("f47ac10b-58cc-4372-a567-0e02b2c3d479", null));
        assertTrue(validator.isValid("6ba7b810-9dad-11d1-80b4-00c04fd430c8", null));
    }
}
