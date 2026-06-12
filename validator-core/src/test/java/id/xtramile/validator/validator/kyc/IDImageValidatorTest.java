package id.xtramile.validator.validator.kyc;

import id.xtramile.validator.annotation.kyc.ValidIDImage;
import id.xtramile.validator.validator.file.MockMultipartFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IDImageValidatorTest {

    private IDImageValidator validator;

    private static ValidIDImage getAnnotation(String fieldName) {
        try {
            Field f = IDImageDummy.class.getDeclaredField(fieldName);
            return f.getAnnotation(ValidIDImage.class);
        } catch (NoSuchFieldException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
        validator = new IDImageValidator();
    }

    @Test
    void testValidDefaultImages() {
        validator.initialize(getAnnotation("defaultImage"));

        MockMultipartFile validImage = createMockImageFile("id.jpg", 800, 600, "image/jpeg", 2 * 1024 * 1024);
        assertTrue(validator.isValid(validImage, null));
    }

    @Test
    void testValidCustomImages() {
        validator.initialize(getAnnotation("customImage"));

        MockMultipartFile validImage = createMockImageFile("id.jpg", 1000, 750, "image/jpeg", 3 * 1024 * 1024);
        assertTrue(validator.isValid(validImage, null));
    }

    @Test
    void testInvalidImageSize() {
        validator.initialize(getAnnotation("defaultImage"));

        // Create a file with actual large size for size testing
        byte[] largeContent = new byte[8 * 1024 * 1024]; // 8MB
        MockMultipartFile tooLargeImage = new MockMultipartFile("file", "id.jpg", "image/jpeg", largeContent);
        assertFalse(validator.isValid(tooLargeImage, null));
    }

    @Test
    void testInvalidImageDimensions() {
        validator.initialize(getAnnotation("defaultImage"));

        MockMultipartFile tooSmallImage = createMockImageFile("id.jpg", 500, 300, "image/jpeg", 2 * 1024 * 1024);
        MockMultipartFile tooLargeImage = createMockImageFile("id.jpg", 9000, 9000, "image/jpeg", 2 * 1024 * 1024);

        assertFalse(validator.isValid(tooSmallImage, null));
        assertFalse(validator.isValid(tooLargeImage, null));
    }

    @Test
    void testInvalidMimeType() {
        validator.initialize(getAnnotation("defaultImage"));

        MockMultipartFile invalidMimeImage = createMockImageFile("id.gif", 800, 600, "image/gif", 2 * 1024 * 1024);
        assertFalse(validator.isValid(invalidMimeImage, null));
    }

    @Test
    void testValidMultipleMimeTypes() {
        validator.initialize(getAnnotation("multipleMimeImage"));

        MockMultipartFile jpegImage = createMockImageFile("id.jpg", 800, 600, "image/jpeg", 5 * 1024 * 1024);
        MockMultipartFile pngImage = createMockImageFile("id.png", 800, 600, "image/png", 5 * 1024 * 1024);
        MockMultipartFile webpImage = createMockImageFile("id.webp", 800, 600, "image/webp", 5 * 1024 * 1024);

        assertTrue(validator.isValid(jpegImage, null));
        assertTrue(validator.isValid(pngImage, null));
        assertTrue(validator.isValid(webpImage, null));
    }

    @Test
    void testInvalidMimeTypeForMultipleMime() {
        validator.initialize(getAnnotation("multipleMimeImage"));

        MockMultipartFile gifImage = createMockImageFile("id.gif", 800, 600, "image/gif", 5 * 1024 * 1024);
        assertFalse(validator.isValid(gifImage, null));
    }

    @Test
    void testAspectRatioValidation() {
        validator.initialize(getAnnotation("customImage"));

        // Valid aspect ratio (4:3 = 1.33)
        MockMultipartFile validAspectImage = createMockImageFile("id.jpg", 800, 600, "image/jpeg", 3 * 1024 * 1024);
        assertTrue(validator.isValid(validAspectImage, null));

        // Invalid aspect ratio (1:1 = 1.0)
        MockMultipartFile invalidAspectImage = createMockImageFile("id.jpg", 800, 800, "image/jpeg", 3 * 1024 * 1024);
        assertFalse(validator.isValid(invalidAspectImage, null));
    }

    @Test
    void testSquareImageValidation() {
        validator.initialize(getAnnotation("squareImage"));

        MockMultipartFile validSquareImage = createMockImageFile("id.jpg", 600, 600, "image/jpeg", 1024 * 1024);
        MockMultipartFile invalidRectangularImage = createMockImageFile("id.jpg", 600, 400, "image/jpeg", 1024 * 1024);

        assertTrue(validator.isValid(validSquareImage, null));
        assertFalse(validator.isValid(invalidRectangularImage, null));
    }

    @Test
    void testEmptyFile() {
        validator.initialize(getAnnotation("defaultImage"));

        MockMultipartFile emptyFile = new MockMultipartFile("file", "id.jpg", "image/jpeg", new byte[0]);
        assertTrue(validator.isValid(emptyFile, null));
    }

    @Test
    void testNullFile() {
        validator.initialize(getAnnotation("defaultImage"));

        assertTrue(validator.isValid(null, null));
    }

    @Test
    void testInvalidImageFile() {
        validator.initialize(getAnnotation("defaultImage"));

        MockMultipartFile invalidImageFile = new MockMultipartFile("file", "id.jpg", "image/jpeg", "not an image".getBytes());
        assertFalse(validator.isValid(invalidImageFile, null));
    }

    @Test
    void testCaseInsensitiveMimeTypes() {
        validator.initialize(getAnnotation("defaultImage"));

        MockMultipartFile upperCaseMimeImage = createMockImageFile("id.jpg", 800, 600, "IMAGE/JPEG", 2 * 1024 * 1024);
        MockMultipartFile mixedCaseMimeImage = createMockImageFile("id.jpg", 800, 600, "Image/Jpeg", 2 * 1024 * 1024);

        assertTrue(validator.isValid(upperCaseMimeImage, null));
        assertTrue(validator.isValid(mixedCaseMimeImage, null));
    }

    @Test
    void testEdgeCaseDimensions() {
        validator.initialize(getAnnotation("defaultImage"));

        // Test exactly at boundaries
        MockMultipartFile minSizeImage = createMockImageFile("id.jpg", 600, 400, "image/jpeg", 2 * 1024 * 1024);
        MockMultipartFile maxSizeImage = createMockImageFile("id.jpg", 8000, 8000, "image/jpeg", 2 * 1024 * 1024);

        assertTrue(validator.isValid(minSizeImage, null));
        assertTrue(validator.isValid(maxSizeImage, null));
    }

    @Test
    void testEdgeCaseFileSize() {
        validator.initialize(getAnnotation("defaultImage"));

        // Create valid images with different sizes
        MockMultipartFile maxSizeImage = createMockImageFile("id.jpg", 800, 600, "image/jpeg", 7 * 1024 * 1024);
        MockMultipartFile overSizeImage = createMockImageFile("id.jpg", 800, 600, "image/jpeg", 8 * 1024 * 1024);

        assertTrue(validator.isValid(maxSizeImage, null));
        assertFalse(validator.isValid(overSizeImage, null));
    }

    @Test
    void testAspectRatioWithTolerance() {
        validator.initialize(getAnnotation("customImage"));

        // Test aspect ratio with small tolerance (should still be valid)
        MockMultipartFile slightlyOffAspectImage = createMockImageFile("id.jpg", 801, 600, "image/jpeg", 3 * 1024 * 1024);
        assertTrue(validator.isValid(slightlyOffAspectImage, null));
    }

    @Test
    void testBlankMimeType() {
        validator.initialize(getAnnotation("defaultImage"));

        MockMultipartFile blankMimeImage = new MockMultipartFile("file", "id.jpg", "", new byte[1024]);
        assertFalse(validator.isValid(blankMimeImage, null));
    }

    @Test
    void testNullMimeType() {
        validator.initialize(getAnnotation("defaultImage"));

        MockMultipartFile nullMimeImage = new MockMultipartFile("file", "id.jpg", null, new byte[1024]);
        assertFalse(validator.isValid(nullMimeImage, null));
    }

    // Helper method to create a mock image file
    private MockMultipartFile createMockImageFile(String filename, int width, int height, String mimeType, long size) {
        try {
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", baos);
            byte[] imageBytes = baos.toByteArray();

            // If the requested size is larger than the actual image, pad it
            if (size > imageBytes.length) {
                byte[] sizedBytes = new byte[(int) size];
                System.arraycopy(imageBytes, 0, sizedBytes, 0, imageBytes.length);
                // Fill the rest with zeros
                for (int i = imageBytes.length; i < sizedBytes.length; i++) {
                    sizedBytes[i] = 0;
                }
                return new MockMultipartFile("file", filename, mimeType, sizedBytes);
            } else {
                // Use the actual image bytes
                return new MockMultipartFile("file", filename, mimeType, imageBytes);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to create mock image file", e);
        }
    }

    private static class IDImageDummy {
        @ValidIDImage
        MultipartFile defaultImage;

        @ValidIDImage(maxMB = 5, minWidth = 800, minHeight = 600, maxWidth = 4000, maxHeight = 4000, aspectRatio = 1.33)
        MultipartFile customImage;

        @ValidIDImage(maxMB = 10, mimeAllowed = {"image/jpeg", "image/png", "image/webp"})
        MultipartFile multipleMimeImage;

        @ValidIDImage(maxMB = 2, aspectRatio = 1.0)
        MultipartFile squareImage;
    }
}
