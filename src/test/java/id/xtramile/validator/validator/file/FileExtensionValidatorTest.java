package id.xtramile.validator.validator.file;

import id.xtramile.validator.annotation.file.ValidFileExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileExtensionValidatorTest {

    private static class FileExtensionDummy {
        @ValidFileExtension(allowed = {"jpg", "png", "pdf"})
        String defaultFile;

        @ValidFileExtension(allowed = {"JPG", "PNG", "PDF"}, ignoreCase = false)
        String caseSensitiveFile;

        @ValidFileExtension(allowed = {"txt", "doc", "docx"})
        String caseInsensitiveFile;
    }

    private FileExtensionValidator validator;

    private static ValidFileExtension getAnnotation(String fieldName) {
        try {
            Field f = FileExtensionDummy.class.getDeclaredField(fieldName);
            return f.getAnnotation(ValidFileExtension.class);
        } catch (NoSuchFieldException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
        validator = new FileExtensionValidator();
    }

    @Test
    void testValidExtensions() {
        validator.initialize(getAnnotation("defaultFile"));

        assertTrue(validator.isValid("document.pdf", null));
        assertTrue(validator.isValid("image.jpg", null));
        assertTrue(validator.isValid("photo.png", null));
        assertTrue(validator.isValid("file.PDF", null)); // case insensitive by default
        assertTrue(validator.isValid("image.JPG", null));
    }

    @Test
    void testInvalidExtensions() {
        validator.initialize(getAnnotation("defaultFile"));

        assertFalse(validator.isValid("document.txt", null));
        assertFalse(validator.isValid("image.gif", null));
        assertFalse(validator.isValid("file.docx", null));
        assertFalse(validator.isValid("data.xml", null));
    }

    @Test
    void testCaseSensitiveValidation() {
        validator.initialize(getAnnotation("caseSensitiveFile"));

        assertTrue(validator.isValid("document.PDF", null));
        assertTrue(validator.isValid("image.JPG", null));
        assertTrue(validator.isValid("photo.PNG", null));

        assertFalse(validator.isValid("document.pdf", null));
        assertFalse(validator.isValid("image.jpg", null));
        assertFalse(validator.isValid("photo.png", null));
    }

    @Test
    void testCaseInsensitiveValidation() {
        validator.initialize(getAnnotation("caseInsensitiveFile"));

        assertTrue(validator.isValid("document.txt", null));
        assertTrue(validator.isValid("file.TXT", null));
        assertTrue(validator.isValid("document.doc", null));
        assertTrue(validator.isValid("file.DOC", null));
        assertTrue(validator.isValid("document.docx", null));
        assertTrue(validator.isValid("file.DOCX", null));
    }

    @Test
    void testFilesWithoutExtensions() {
        validator.initialize(getAnnotation("defaultFile"));

        assertFalse(validator.isValid("document", null));
        assertFalse(validator.isValid("file.", null));
        assertFalse(validator.isValid("image.", null));
    }

    @Test
    void testMultipartFileValidation() {
        validator.initialize(getAnnotation("defaultFile"));

        MockMultipartFile validFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test content".getBytes());
        MockMultipartFile invalidFile = new MockMultipartFile("file", "test.txt", "text/plain", "test content".getBytes());

        assertTrue(validator.isValid(validFile, null));
        assertFalse(validator.isValid(invalidFile, null));
    }

    @Test
    void testEmptyMultipartFile() {
        validator.initialize(getAnnotation("defaultFile"));

        MockMultipartFile emptyFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", new byte[0]);
        assertTrue(validator.isValid(emptyFile, null));
    }

    @Test
    void testUnsupportedObjectTypes() {
        validator.initialize(getAnnotation("defaultFile"));

        assertTrue(validator.isValid(123, null));
        assertTrue(validator.isValid(new Object(), null));
        assertTrue(validator.isValid(new byte[]{1, 2, 3}, null));
    }

    @Test
    void testBlankString() {
        validator.initialize(getAnnotation("defaultFile"));

        assertTrue(validator.isValid("", null));
        assertTrue(validator.isValid("   ", null));
    }

    @Test
    void testMultipartFileWithBlankFilename() {
        validator.initialize(getAnnotation("defaultFile"));

        MockMultipartFile fileWithBlankName = new MockMultipartFile("file", "", "image/jpeg", "test content".getBytes());
        assertFalse(validator.isValid(fileWithBlankName, null));
    }
}
