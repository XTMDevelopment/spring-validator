package id.xtramile.validator.validator.file;

import id.xtramile.validator.annotation.file.ValidImageDimensions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ImageDimensionsValidatorTest {

    private static class ImageDimensionsDummy {
        @ValidImageDimensions(minWidth = 100, maxWidth = 1000, minHeight = 100, maxHeight = 1000)
        String defaultDimensions;

        @ValidImageDimensions(minWidth = 200, maxWidth = 800, minHeight = 200, maxHeight = 600, aspectRatio = 1.33)
        String aspectRatioImage;

        @ValidImageDimensions(maxWidth = 2000, maxHeight = 2000)
        String extensionFilteredImage;

        @ValidImageDimensions(minWidth = 50, maxWidth = 500, minHeight = 50, maxHeight = 500, aspectRatio = 1.0)
        String squareImage;
    }

    private ImageDimensionsValidator validator;

    private static ValidImageDimensions getAnnotation(String fieldName) {
        try {
            Field f = ImageDimensionsDummy.class.getDeclaredField(fieldName);
            return f.getAnnotation(ValidImageDimensions.class);
        } catch (NoSuchFieldException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
        validator = new ImageDimensionsValidator();
    }

    @Test
    void testValidImageDimensions() {
        validator.initialize(getAnnotation("defaultDimensions"));

        MockMultipartFile validImage = createMockImageFile("test.jpg", 500, 500);
        assertTrue(validator.isValid(validImage, null));
    }

    @Test
    void testInvalidImageDimensions() {
        validator.initialize(getAnnotation("defaultDimensions"));

        MockMultipartFile tooSmallImage = createMockImageFile("test.jpg", 50, 50);
        MockMultipartFile tooLargeImage = createMockImageFile("test.jpg", 1500, 1500);

        assertFalse(validator.isValid(tooSmallImage, null));
        assertFalse(validator.isValid(tooLargeImage, null));
    }

    @Test
    void testAspectRatioValidation() {
        validator.initialize(getAnnotation("aspectRatioImage"));

        // Valid aspect ratio (4:3 = 1.33)
        MockMultipartFile validAspectImage = createMockImageFile("test.jpg", 400, 300);
        assertTrue(validator.isValid(validAspectImage, null));

        // Invalid aspect ratio (1:1 = 1.0)
        MockMultipartFile invalidAspectImage = createMockImageFile("test.jpg", 300, 300);
        assertFalse(validator.isValid(invalidAspectImage, null));
    }

    @Test
    void testSquareImageValidation() {
        validator.initialize(getAnnotation("squareImage"));

        MockMultipartFile validSquareImage = createMockImageFile("test.jpg", 200, 200);
        MockMultipartFile invalidRectangularImage = createMockImageFile("test.jpg", 200, 300);

        assertTrue(validator.isValid(validSquareImage, null));
        assertFalse(validator.isValid(invalidRectangularImage, null));
    }

    @Test
    void testEmptyMultipartFile() {
        validator.initialize(getAnnotation("defaultDimensions"));

        MockMultipartFile emptyFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", new byte[0]);
        assertTrue(validator.isValid(emptyFile, null));
    }

    @Test
    void testUnsupportedObjectTypes() {
        validator.initialize(getAnnotation("defaultDimensions"));

        assertTrue(validator.isValid("some string", null));
        assertTrue(validator.isValid(123, null));
        assertTrue(validator.isValid(new Object(), null));
        assertTrue(validator.isValid(new byte[]{1, 2, 3}, null));
    }

    @Test
    void testInvalidImageFile() {
        validator.initialize(getAnnotation("defaultDimensions"));

        // Create a file that's not a valid image
        MockMultipartFile invalidImageFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", "not an image".getBytes());
        assertFalse(validator.isValid(invalidImageFile, null));
    }

    @Test
    void testEdgeCaseDimensions() {
        validator.initialize(getAnnotation("defaultDimensions"));

        // Test exactly at boundaries
        MockMultipartFile minSizeImage = createMockImageFile("test.jpg", 100, 100);
        MockMultipartFile maxSizeImage = createMockImageFile("test.jpg", 1000, 1000);

        assertTrue(validator.isValid(minSizeImage, null));
        assertTrue(validator.isValid(maxSizeImage, null));
    }

    @Test
    void testAspectRatioWithTolerance() {
        validator.initialize(getAnnotation("aspectRatioImage"));

        // Test aspect ratio with small tolerance (should still be valid)
        MockMultipartFile slightlyOffAspectImage = createMockImageFile("test.jpg", 401, 300);
        assertTrue(validator.isValid(slightlyOffAspectImage, null));
    }

    @Test
    void testCaseInsensitiveExtensions() {
        validator.initialize(getAnnotation("extensionFilteredImage"));

        MockMultipartFile upperCaseExtImage = createMockImageFile("test.JPG", 500, 500);
        MockMultipartFile mixedCaseExtImage = createMockImageFile("test.PnG", 500, 500);

        assertTrue(validator.isValid(upperCaseExtImage, null));
        assertTrue(validator.isValid(mixedCaseExtImage, null));
    }

    // Helper method to create a mock image file
    private MockMultipartFile createMockImageFile(String filename, int width, int height) {
        try {
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", baos);
            byte[] imageBytes = baos.toByteArray();
            return new MockMultipartFile("file", filename, "image/jpeg", imageBytes);

        } catch (IOException e) {
            throw new RuntimeException("Failed to create mock image file", e);
        }
    }
}
