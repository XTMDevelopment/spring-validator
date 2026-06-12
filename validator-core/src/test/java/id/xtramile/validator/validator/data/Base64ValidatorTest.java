package id.xtramile.validator.validator.data;

import id.xtramile.validator.annotation.data.ValidBase64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Base64ValidatorTest {

    private Base64Validator validator;

    private static ValidBase64 getAnnotation(String fieldName) {
        try {
            Field f = Base64Dummy.class.getDeclaredField(fieldName);
            return f.getAnnotation(ValidBase64.class);
        } catch (NoSuchFieldException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
        validator = new Base64Validator();
    }

    @Test
    void testValidStandardBase64() {
        validator.initialize(getAnnotation("defaultBase64"));

        // Test with standard Base64 encoded strings
        String encoded = Base64.getEncoder().encodeToString("Hello World".getBytes());
        assertTrue(validator.isValid(encoded, null));

        encoded = Base64.getEncoder().encodeToString("test".getBytes());
        assertTrue(validator.isValid(encoded, null));

        encoded = Base64.getEncoder().encodeToString("".getBytes());
        assertTrue(validator.isValid(encoded, null));

        // Test with padding
        assertTrue(validator.isValid("SGVsbG8gV29ybGQ=", null));
        assertTrue(validator.isValid("dGVzdA==", null));
        assertTrue(validator.isValid("dGVzdA==", null));
    }

    @Test
    void testValidUrlSafeBase64() {
        validator.initialize(getAnnotation("urlSafeBase64"));

        // Test with URL-safe Base64 encoded strings
        String encoded = Base64.getUrlEncoder().encodeToString("Hello World".getBytes());
        assertTrue(validator.isValid(encoded, null));

        encoded = Base64.getUrlEncoder().encodeToString("test".getBytes());
        assertTrue(validator.isValid(encoded, null));

        // Test with URL-safe characters
        assertTrue(validator.isValid("SGVsbG8gV29ybGQ", null)); // no padding
        assertTrue(validator.isValid("dGVzdA", null)); // no padding
    }

    @Test
    void testInvalidBase64Strings() {
        validator.initialize(getAnnotation("defaultBase64"));

        assertFalse(validator.isValid("Invalid Base64!", null));
        assertFalse(validator.isValid("SGVsbG8gV29ybGQ=!", null)); // invalid character
        assertFalse(validator.isValid("SGVsbG8gV29ybGQ===", null)); // too much padding
        assertFalse(validator.isValid("SGVsbG8gV29ybGQ= ", null)); // trailing space
        assertFalse(validator.isValid(" SGVsbG8gV29ybGQ=", null)); // leading space
    }

    @Test
    void testUrlSafeVsStandard() {
        // Test that URL-safe Base64 works with urlSafe=true
        validator.initialize(getAnnotation("urlSafeBase64"));
        String urlSafeEncoded = Base64.getUrlEncoder().encodeToString("test".getBytes());
        assertTrue(validator.isValid(urlSafeEncoded, null));

        // Test that standard Base64 works with urlSafe=false
        validator.initialize(getAnnotation("standardBase64"));
        String standardEncoded = Base64.getEncoder().encodeToString("test".getBytes());
        assertTrue(validator.isValid(standardEncoded, null));
    }

    @Test
    void testBlankValues() {
        validator.initialize(getAnnotation("defaultBase64"));

        assertTrue(validator.isValid("", null));
        assertTrue(validator.isValid("   ", null));
        assertTrue(validator.isValid(null, null));
    }

    @Test
    void testEdgeCases() {
        validator.initialize(getAnnotation("defaultBase64"));

        // Test empty string encoding
        String emptyEncoded = Base64.getEncoder().encodeToString("".getBytes());
        assertTrue(validator.isValid(emptyEncoded, null));

        // Test single character
        String singleChar = Base64.getEncoder().encodeToString("a".getBytes());
        assertTrue(validator.isValid(singleChar, null));

        // Test with special characters
        String specialChars = Base64.getEncoder().encodeToString("!@#$%^&*()".getBytes());
        assertTrue(validator.isValid(specialChars, null));
    }

    @Test
    void testPaddingVariations() {
        validator.initialize(getAnnotation("defaultBase64"));

        // Test different padding scenarios
        assertTrue(validator.isValid("SGVsbG8gV29ybGQ=", null)); // one padding - "Hello World"
        assertTrue(validator.isValid("dGVzdA==", null)); // two padding - "test"
        assertTrue(validator.isValid("SGVsbG8gV29ybGQ", null)); // no padding (valid for some cases)
    }

    @Test
    void testRealWorldExamples() {
        validator.initialize(getAnnotation("defaultBase64"));

        // Test with real-world Base64 examples
        assertTrue(validator.isValid("SGVsbG8gV29ybGQ=", null)); // "Hello World"
        assertTrue(validator.isValid("dGVzdA==", null)); // "test"
        assertTrue(validator.isValid("aGVsbG8=", null)); // "hello"
        assertTrue(validator.isValid("d29ybGQ=", null)); // "world"
    }

    @Test
    void testUrlSafeExamples() {
        validator.initialize(getAnnotation("urlSafeBase64"));

        // Test URL-safe Base64 examples
        assertTrue(validator.isValid("SGVsbG8gV29ybGQ", null)); // "Hello World" URL-safe
        assertTrue(validator.isValid("dGVzdA", null)); // "test" URL-safe
        assertTrue(validator.isValid("aGVsbG8", null)); // "hello" URL-safe
    }

    private static class Base64Dummy {
        @ValidBase64
        String defaultBase64;

        @ValidBase64(urlSafe = true)
        String urlSafeBase64;

        @ValidBase64()
        String standardBase64;
    }
}
