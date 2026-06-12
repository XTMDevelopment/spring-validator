package id.xtramile.validator.integration;

import id.xtramile.validator.annotation.file.ValidFileExtension;
import id.xtramile.validator.annotation.file.ValidFileMimeType;
import id.xtramile.validator.annotation.file.ValidFileSize;
import id.xtramile.validator.annotation.file.ValidImageDimensions;
import id.xtramile.validator.support.ValidationMessageTestSupport;
import id.xtramile.validator.validator.file.MockMultipartFile;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

import static id.xtramile.validator.integration.ValidationMessageAssertions.assertNoRawValidationKey;
import static org.junit.jupiter.api.Assertions.*;

class FileValidatorMessageIntegrationTest {

    private static final ValidationMessageTestSupport SUPPORT = ValidationMessageTestSupport.EN;

    public record FileExtensionDto(@ValidFileExtension(allowed = {"jpg", "png"}) Object value) {
            public FileExtensionDto(Object value) {
                this.value = value;
            }
        }

    public record FileMimeTypeDto(@ValidFileMimeType(allowed = {"image/jpeg", "image/png"}) Object value) {
            public FileMimeTypeDto(Object value) {
                this.value = value;
            }
        }

    public record ImageDimensionsDto(
            @ValidImageDimensions(minWidth = 100, minHeight = 100, maxWidth = 4096, maxHeight = 4096) Object value) {
            public ImageDimensionsDto(Object value) {
                this.value = value;
            }
        }

    public record FileSizeMinDto(@ValidFileSize(minBytes = 1000) Object value) {
            public FileSizeMinDto(Object value) {
                this.value = value;
            }
        }

    public record FileSizeMaxDto(@ValidFileSize(maxBytes = 100) Object value) {
            public FileSizeMaxDto(Object value) {
                this.value = value;
            }
        }

    private static MultipartFile mockFile(String name, String contentType, byte[] bytes) {
        return new MockMultipartFile(name, name, contentType, bytes);
    }

    @Test
    void fileExtension_pathB() {
        FileExtensionDto dto = new FileExtensionDto(mockFile("document.pdf", "application/pdf", new byte[]{1, 2, 3}));
        ConstraintViolation<FileExtensionDto> v = SUPPORT.firstViolation(dto);

        assertEquals("{friendly.default}", v.getMessageTemplate());

        String resolved = SUPPORT.resolver().resolve(v, "value", FileExtensionDto.class);

        assertTrue(resolved.contains("jpg") || resolved.contains("png"),
                "Extensions not in message: " + resolved);
    }

    @Test
    void mimeType_pathB() {
        FileMimeTypeDto dto = new FileMimeTypeDto(mockFile("file.pdf", "application/pdf", new byte[]{1, 2, 3}));
        ConstraintViolation<FileMimeTypeDto> v = SUPPORT.firstViolation(dto);

        assertEquals("{friendly.default}", v.getMessageTemplate());

        String resolved = SUPPORT.resolver().resolve(v, "value", FileMimeTypeDto.class);

        assertTrue(resolved.contains("image/jpeg") || resolved.contains("image/png"),
                "MIME types not in message: " + resolved);
    }

    @Test
    void imageDimensions_pathB() {
        ImageDimensionsDto dto = new ImageDimensionsDto(mockFile("img.jpg", "image/jpeg", new byte[]{0, 0, 0, 0}));
        ConstraintViolation<ImageDimensionsDto> v = SUPPORT.firstViolation(dto);

        assertEquals("{friendly.default}", v.getMessageTemplate());

        String resolved = SUPPORT.resolver().resolve(v, "value", ImageDimensionsDto.class);

        assertTrue(resolved.contains("100"), "expected min dimension in message: " + resolved);
        assertTrue(resolved.contains("4096") || resolved.contains("4,096"),
                "expected max dimension in message: " + resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void fileSize_tooSmall() {
        byte[] smallBytes = new byte[10];
        FileSizeMinDto dto = new FileSizeMinDto(smallBytes);
        ConstraintViolation<FileSizeMinDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.file.file-size.min", v.getMessageTemplate());

        String resolved = SUPPORT.resolver().resolve(v, "value", FileSizeMinDto.class);

        assertTrue(resolved.startsWith("value must have minimal"),
                "Unexpected message: " + resolved);
    }

    @Test
    void fileSize_tooLarge() {
        byte[] largeBytes = new byte[200];
        FileSizeMaxDto dto = new FileSizeMaxDto(largeBytes);
        ConstraintViolation<FileSizeMaxDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.file.file-size.max", v.getMessageTemplate());

        String resolved = SUPPORT.resolver().resolve(v, "value", FileSizeMaxDto.class);

        assertTrue(resolved.startsWith("value must have maximal"),
                "Unexpected message: " + resolved);
    }
}
