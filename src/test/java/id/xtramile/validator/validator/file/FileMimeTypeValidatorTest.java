package id.xtramile.validator.validator.file;

import id.xtramile.validator.annotation.file.ValidFileMimeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileMimeTypeValidatorTest {

    private static class FileMimeTypeDummy {
        @ValidFileMimeType(allowed = {"image/jpeg", "image/png", "application/pdf"})
        String defaultMime;

        @ValidFileMimeType(allowed = {"IMAGE/JPEG", "IMAGE/PNG"}, ignoreCase = false)
        String caseSensitiveMime;

        @ValidFileMimeType(allowed = {"image/*", "text/*"})
        String wildcardMime;

        @ValidFileMimeType(allowed = {"application/*", "text/plain"})
        String mixedWildcardMime;
    }

    private FileMimeTypeValidator validator;

    private static ValidFileMimeType getAnnotation(String fieldName) {
        try {
            Field f = FileMimeTypeDummy.class.getDeclaredField(fieldName);
            return f.getAnnotation(ValidFileMimeType.class);
        } catch (NoSuchFieldException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
        validator = new FileMimeTypeValidator();
    }

    @Test
    void testValidMimeTypes() {
        validator.initialize(getAnnotation("defaultMime"));

        assertTrue(validator.isValid("image/jpeg", null));
        assertTrue(validator.isValid("image/png", null));
        assertTrue(validator.isValid("application/pdf", null));
    }

    @Test
    void testInvalidMimeTypes() {
        validator.initialize(getAnnotation("defaultMime"));

        assertFalse(validator.isValid("image/gif", null));
        assertFalse(validator.isValid("text/plain", null));
        assertFalse(validator.isValid("application/json", null));
        assertFalse(validator.isValid("video/mp4", null));
    }

    @Test
    void testCaseSensitiveValidation() {
        validator.initialize(getAnnotation("caseSensitiveMime"));

        assertTrue(validator.isValid("IMAGE/JPEG", null));
        assertTrue(validator.isValid("IMAGE/PNG", null));

        assertFalse(validator.isValid("image/jpeg", null));
        assertFalse(validator.isValid("image/png", null));
        assertFalse(validator.isValid("Image/Jpeg", null));
    }

    @Test
    void testWildcardMimeTypes() {
        validator.initialize(getAnnotation("wildcardMime"));

        assertTrue(validator.isValid("image/jpeg", null));
        assertTrue(validator.isValid("image/png", null));
        assertTrue(validator.isValid("image/gif", null));
        assertTrue(validator.isValid("text/plain", null));
        assertTrue(validator.isValid("text/html", null));
        assertTrue(validator.isValid("text/css", null));

        assertFalse(validator.isValid("application/pdf", null));
        assertFalse(validator.isValid("video/mp4", null));
        assertFalse(validator.isValid("audio/mp3", null));
    }

    @Test
    void testMixedWildcardMimeTypes() {
        validator.initialize(getAnnotation("mixedWildcardMime"));

        assertTrue(validator.isValid("application/pdf", null));
        assertTrue(validator.isValid("application/json", null));
        assertTrue(validator.isValid("application/octet-stream", null));
        assertTrue(validator.isValid("text/plain", null));

        assertFalse(validator.isValid("image/jpeg", null));
        assertFalse(validator.isValid("video/mp4", null));
        assertFalse(validator.isValid("text/html", null));
    }

    @Test
    void testMultipartFileValidation() {
        validator.initialize(getAnnotation("defaultMime"));

        MockMultipartFile validFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test content".getBytes());
        MockMultipartFile invalidFile = new MockMultipartFile("file", "test.txt", "text/plain", "test content".getBytes());

        assertTrue(validator.isValid(validFile, null));
        assertFalse(validator.isValid(invalidFile, null));
    }

    @Test
    void testEmptyMultipartFile() {
        validator.initialize(getAnnotation("defaultMime"));

        MockMultipartFile emptyFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", new byte[0]);
        assertTrue(validator.isValid(emptyFile, null));
    }

    @Test
    void testUnsupportedObjectTypes() {
        validator.initialize(getAnnotation("defaultMime"));

        assertTrue(validator.isValid(123, null));
        assertTrue(validator.isValid(new Object(), null));
        assertTrue(validator.isValid(new byte[]{1, 2, 3}, null));
    }

    @Test
    void testBlankString() {
        validator.initialize(getAnnotation("defaultMime"));

        assertTrue(validator.isValid("", null));
        assertTrue(validator.isValid("   ", null));
    }

    @Test
    void testMultipartFileWithBlankMimeType() {
        validator.initialize(getAnnotation("defaultMime"));

        MockMultipartFile fileWithBlankMime = new MockMultipartFile("file", "test.jpg", "", "test content".getBytes());
        assertFalse(validator.isValid(fileWithBlankMime, null));
    }

    @Test
    void testInvalidMimeTypeFormat() {
        validator.initialize(getAnnotation("defaultMime"));

        assertFalse(validator.isValid("invalid-mime", null));
        assertFalse(validator.isValid("image", null));
        assertFalse(validator.isValid("/jpeg", null));
        assertFalse(validator.isValid("image/", null));
    }

    @Test
    void testWildcardWithInvalidFormat() {
        validator.initialize(getAnnotation("wildcardMime"));

        assertFalse(validator.isValid("invalid", null));
        assertFalse(validator.isValid("image", null));
        assertFalse(validator.isValid("/jpeg", null));
    }
}
