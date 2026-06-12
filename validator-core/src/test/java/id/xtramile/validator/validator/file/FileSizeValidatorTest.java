package id.xtramile.validator.validator.file;

import id.xtramile.validator.annotation.file.ValidFileSize;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileSizeValidatorTest {

    private FileSizeValidator validator;

    private static ValidFileSize getAnnotation(String fieldName) {
        try {
            Field f = FileSizeDummy.class.getDeclaredField(fieldName);
            return f.getAnnotation(ValidFileSize.class);
        } catch (NoSuchFieldException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
        validator = new FileSizeValidator();
    }

    @Test
    void testValidFileSizes() {
        validator.initialize(getAnnotation("defaultSize"));

        byte[] validSize = new byte[500]; // 500 bytes
        byte[] validSizeMin = new byte[100]; // exactly 100 bytes
        byte[] validSizeMax = new byte[1000]; // exactly 1000 bytes

        assertTrue(validator.isValid(validSize, null));
        assertTrue(validator.isValid(validSizeMin, null));
        assertTrue(validator.isValid(validSizeMax, null));
    }

    @Test
    void testInvalidFileSizes() {
        validator.initialize(getAnnotation("defaultSize"));

        byte[] tooSmall = new byte[50]; // 50 bytes
        byte[] tooLarge = new byte[1500]; // 1500 bytes

        assertFalse(validator.isValid(tooSmall, null));
        assertFalse(validator.isValid(tooLarge, null));
    }

    @Test
    void testSmallFileValidation() {
        validator.initialize(getAnnotation("smallFile"));

        byte[] validSmall = new byte[250]; // 250 bytes
        byte[] tooLarge = new byte[600]; // 600 bytes

        assertTrue(validator.isValid(validSmall, null));
        assertFalse(validator.isValid(tooLarge, null));
    }

    @Test
    void testLargeFileValidation() {
        validator.initialize(getAnnotation("largeFile"));

        byte[] validLarge = new byte[2000]; // 2000 bytes
        byte[] tooSmall = new byte[500]; // 500 bytes

        assertTrue(validator.isValid(validLarge, null));
        assertFalse(validator.isValid(tooSmall, null));
    }

    @Test
    void testOneMBFileValidation() {
        validator.initialize(getAnnotation("oneMBFile"));

        byte[] validSize = new byte[500 * 1024]; // 500KB
        byte[] tooLarge = new byte[2 * 1024 * 1024]; // 2MB

        assertTrue(validator.isValid(validSize, null));
        assertFalse(validator.isValid(tooLarge, null));
    }

    @Test
    void testFiveMBFileValidation() {
        validator.initialize(getAnnotation("fiveMBFile"));

        byte[] validSize = new byte[3 * 1024 * 1024]; // 3MB
        byte[] tooLarge = new byte[6 * 1024 * 1024]; // 6MB

        assertTrue(validator.isValid(validSize, null));
        assertFalse(validator.isValid(tooLarge, null));
    }

    @Test
    void testMultipartFileValidation() {
        validator.initialize(getAnnotation("defaultSize"));

        byte[] content = new byte[500];
        MockMultipartFile validFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", content);
        MockMultipartFile invalidFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", new byte[50]);

        assertTrue(validator.isValid(validFile, null));
        assertFalse(validator.isValid(invalidFile, null));
    }

    @Test
    void testEmptyMultipartFile() {
        validator.initialize(getAnnotation("defaultSize"));

        MockMultipartFile emptyFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", new byte[0]);
        assertTrue(validator.isValid(emptyFile, null));
    }

    @Test
    void testEmptyByteArray() {
        validator.initialize(getAnnotation("defaultSize"));

        assertTrue(validator.isValid(new byte[0], null));
    }

    @Test
    void testUnsupportedObjectTypes() {
        validator.initialize(getAnnotation("defaultSize"));

        assertTrue(validator.isValid("some string", null));
        assertTrue(validator.isValid(123, null));
        assertTrue(validator.isValid(new Object(), null));
    }

    @Test
    void testCharSequenceTypes() {
        validator.initialize(getAnnotation("defaultSize"));

        assertTrue(validator.isValid("some string", null));
        assertTrue(validator.isValid(new StringBuilder("test"), null));
    }

    @Test
    void testEdgeCaseSizes() {
        validator.initialize(getAnnotation("defaultSize"));

        // Test exactly at boundaries
        assertTrue(validator.isValid(new byte[100], null)); // exactly min
        assertTrue(validator.isValid(new byte[1000], null)); // exactly max
        assertFalse(validator.isValid(new byte[99], null)); // just below min
        assertFalse(validator.isValid(new byte[1001], null)); // just above max
    }

    @Test
    void testMaxBytesOverride() {
        // Test that maxMB overrides maxBytes when both are set
        validator.initialize(getAnnotation("oneMBFile"));

        // 1MB = 1024 * 1024 bytes
        byte[] exactlyOneMB = new byte[1024 * 1024];
        byte[] overOneMB = new byte[1024 * 1024 + 1];

        assertTrue(validator.isValid(exactlyOneMB, null));
        assertFalse(validator.isValid(overOneMB, null));
    }

    private static class FileSizeDummy {
        @ValidFileSize(minBytes = 100, maxBytes = 1000)
        String defaultSize;

        @ValidFileSize(maxBytes = 500)
        String smallFile;

        @ValidFileSize(minBytes = 1000)
        String largeFile;

        @ValidFileSize(maxMB = 1)
        String oneMBFile;

        @ValidFileSize(maxMB = 5)
        String fiveMBFile;
    }
}
