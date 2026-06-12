package id.xtramile.validator.validator.kyc;

import id.xtramile.validator.annotation.kyc.ValidSelfieImage;
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

public class SelfieImageValidatorTest {

    private static class SelfieImageDummy {
        @ValidSelfieImage
        MultipartFile defaultSelfie;

        @ValidSelfieImage(maxMB = 3, minWidth = 512, minHeight = 512, maxWidth = 2048, maxHeight = 2048)
        MultipartFile customSelfie;

        @ValidSelfieImage(maxMB = 8, mimeAllowed = {"image/jpeg", "image/png", "image/webp"})
        MultipartFile multipleMimeSelfie;

        @ValidSelfieImage(maxMB = 1, aspectRatio = 0.75)
        MultipartFile portraitSelfie;
    }

    private SelfieImageValidator validator;

    private static ValidSelfieImage getAnnotation(String fieldName) {
        try {
            Field f = SelfieImageDummy.class.getDeclaredField(fieldName);
            return f.getAnnotation(ValidSelfieImage.class);
        } catch (NoSuchFieldException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
        validator = new SelfieImageValidator();
    }

    @Test
    void testValidDefaultSelfies() {
        validator.initialize(getAnnotation("defaultSelfie"));

        MockMultipartFile validSelfie = createMockImageFile("selfie.jpg", 512, 512, "image/jpeg", 3 * 1024 * 1024);
        assertTrue(validator.isValid(validSelfie, null));
    }

    @Test
    void testValidCustomSelfies() {
        validator.initialize(getAnnotation("customSelfie"));

        MockMultipartFile validSelfie = createMockImageFile("selfie.jpg", 1024, 1024, "image/jpeg", 2 * 1024 * 1024);
        assertTrue(validator.isValid(validSelfie, null));
    }

    @Test
    void testInvalidSelfieSize() {
        validator.initialize(getAnnotation("defaultSelfie"));

        // Create a file with actual large size for size testing
        byte[] largeContent = new byte[6 * 1024 * 1024]; // 6MB
        MockMultipartFile tooLargeSelfie = new MockMultipartFile("file", "selfie.jpg", "image/jpeg", largeContent);
        assertFalse(validator.isValid(tooLargeSelfie, null));
    }

    @Test
    void testInvalidSelfieDimensions() {
        validator.initialize(getAnnotation("defaultSelfie"));

        MockMultipartFile tooSmallSelfie = createMockImageFile("selfie.jpg", 200, 200, "image/jpeg", 3 * 1024 * 1024);
        MockMultipartFile tooLargeSelfie = createMockImageFile("selfie.jpg", 5000, 5000, "image/jpeg", 3 * 1024 * 1024);

        assertFalse(validator.isValid(tooSmallSelfie, null));
        assertFalse(validator.isValid(tooLargeSelfie, null));
    }

    @Test
    void testInvalidMimeType() {
        validator.initialize(getAnnotation("defaultSelfie"));

        MockMultipartFile invalidMimeSelfie = createMockImageFile("selfie.gif", 512, 512, "image/gif", 3 * 1024 * 1024);
        assertFalse(validator.isValid(invalidMimeSelfie, null));
    }

    @Test
    void testValidMultipleMimeTypes() {
        validator.initialize(getAnnotation("multipleMimeSelfie"));

        MockMultipartFile jpegSelfie = createMockImageFile("selfie.jpg", 512, 512, "image/jpeg", 5 * 1024 * 1024);
        MockMultipartFile pngSelfie = createMockImageFile("selfie.png", 512, 512, "image/png", 5 * 1024 * 1024);
        MockMultipartFile webpSelfie = createMockImageFile("selfie.webp", 512, 512, "image/webp", 5 * 1024 * 1024);

        assertTrue(validator.isValid(jpegSelfie, null));
        assertTrue(validator.isValid(pngSelfie, null));
        assertTrue(validator.isValid(webpSelfie, null));
    }

    @Test
    void testInvalidMimeTypeForMultipleMime() {
        validator.initialize(getAnnotation("multipleMimeSelfie"));

        MockMultipartFile gifSelfie = createMockImageFile("selfie.gif", 512, 512, "image/gif", 5 * 1024 * 1024);
        assertFalse(validator.isValid(gifSelfie, null));
    }

    @Test
    void testAspectRatioValidation() {
        validator.initialize(getAnnotation("customSelfie"));

        // Valid aspect ratio (1:1 = 1.0)
        MockMultipartFile validAspectSelfie = createMockImageFile("selfie.jpg", 1024, 1024, "image/jpeg", 2 * 1024 * 1024);
        assertTrue(validator.isValid(validAspectSelfie, null));

        // Invalid aspect ratio (4:3 = 1.33)
        MockMultipartFile invalidAspectSelfie = createMockImageFile("selfie.jpg", 1024, 768, "image/jpeg", 2 * 1024 * 1024);
        assertFalse(validator.isValid(invalidAspectSelfie, null));
    }

    @Test
    void testPortraitSelfieValidation() {
        validator.initialize(getAnnotation("portraitSelfie"));

        // Valid portrait aspect ratio (3:4 = 0.75)
        MockMultipartFile validPortraitSelfie = createMockImageFile("selfie.jpg", 600, 800, "image/jpeg", 500 * 1024);
        assertTrue(validator.isValid(validPortraitSelfie, null));

        // Invalid aspect ratio (1:1 = 1.0)
        MockMultipartFile invalidAspectSelfie = createMockImageFile("selfie.jpg", 600, 600, "image/jpeg", 500 * 1024);
        assertFalse(validator.isValid(invalidAspectSelfie, null));
    }

    @Test
    void testEmptyFile() {
        validator.initialize(getAnnotation("defaultSelfie"));

        MockMultipartFile emptyFile = new MockMultipartFile("file", "selfie.jpg", "image/jpeg", new byte[0]);
        assertTrue(validator.isValid(emptyFile, null));
    }

    @Test
    void testNullFile() {
        validator.initialize(getAnnotation("defaultSelfie"));

        assertTrue(validator.isValid(null, null));
    }

    @Test
    void testInvalidImageFile() {
        validator.initialize(getAnnotation("defaultSelfie"));

        MockMultipartFile invalidImageFile = new MockMultipartFile("file", "selfie.jpg", "image/jpeg", "not an image".getBytes());
        assertFalse(validator.isValid(invalidImageFile, null));
    }

    @Test
    void testCaseInsensitiveMimeTypes() {
        validator.initialize(getAnnotation("defaultSelfie"));

        MockMultipartFile upperCaseMimeSelfie = createMockImageFile("selfie.jpg", 512, 512, "IMAGE/JPEG", 3 * 1024 * 1024);
        MockMultipartFile mixedCaseMimeSelfie = createMockImageFile("selfie.jpg", 512, 512, "Image/Jpeg", 3 * 1024 * 1024);

        assertTrue(validator.isValid(upperCaseMimeSelfie, null));
        assertTrue(validator.isValid(mixedCaseMimeSelfie, null));
    }

    @Test
    void testEdgeCaseDimensions() {
        validator.initialize(getAnnotation("defaultSelfie"));

        // Test exactly at boundaries
        MockMultipartFile minSizeSelfie = createMockImageFile("selfie.jpg", 256, 256, "image/jpeg", 3 * 1024 * 1024);
        MockMultipartFile maxSizeSelfie = createMockImageFile("selfie.jpg", 4096, 4096, "image/jpeg", 3 * 1024 * 1024);

        assertTrue(validator.isValid(minSizeSelfie, null));
        assertTrue(validator.isValid(maxSizeSelfie, null));
    }

    @Test
    void testEdgeCaseFileSize() {
        validator.initialize(getAnnotation("defaultSelfie"));

        // Create valid images with different sizes
        MockMultipartFile maxSizeSelfie = createMockImageFile("selfie.jpg", 512, 512, "image/jpeg", 5 * 1024 * 1024);
        MockMultipartFile overSizeSelfie = createMockImageFile("selfie.jpg", 512, 512, "image/jpeg", 6 * 1024 * 1024);

        assertTrue(validator.isValid(maxSizeSelfie, null));
        assertFalse(validator.isValid(overSizeSelfie, null));
    }

    @Test
    void testAspectRatioWithTolerance() {
        validator.initialize(getAnnotation("customSelfie"));

        // Test aspect ratio with small tolerance (should still be valid)
        MockMultipartFile slightlyOffAspectSelfie = createMockImageFile("selfie.jpg", 1001, 1000, "image/jpeg", 2 * 1024 * 1024);
        assertTrue(validator.isValid(slightlyOffAspectSelfie, null));
    }

    @Test
    void testBlankMimeType() {
        validator.initialize(getAnnotation("defaultSelfie"));

        MockMultipartFile blankMimeSelfie = new MockMultipartFile("file", "selfie.jpg", "", new byte[1024]);
        assertFalse(validator.isValid(blankMimeSelfie, null));
    }

    @Test
    void testNullMimeType() {
        validator.initialize(getAnnotation("defaultSelfie"));

        MockMultipartFile nullMimeSelfie = new MockMultipartFile("file", "selfie.jpg", null, new byte[1024]);
        assertFalse(validator.isValid(nullMimeSelfie, null));
    }

    @Test
    void testSquareSelfieValidation() {
        validator.initialize(getAnnotation("customSelfie"));

        MockMultipartFile validSquareSelfie = createMockImageFile("selfie.jpg", 1024, 1024, "image/jpeg", 2 * 1024 * 1024);
        MockMultipartFile invalidRectangularSelfie = createMockImageFile("selfie.jpg", 1024, 768, "image/jpeg", 2 * 1024 * 1024);

        assertTrue(validator.isValid(validSquareSelfie, null));
        assertFalse(validator.isValid(invalidRectangularSelfie, null));
    }

    @Test
    void testPortraitAspectRatioEdgeCases() {
        validator.initialize(getAnnotation("portraitSelfie"));

        // Test various portrait aspect ratios
        MockMultipartFile validPortrait1 = createMockImageFile("selfie.jpg", 300, 400, "image/jpeg", 500 * 1024); // 0.75
        MockMultipartFile validPortrait2 = createMockImageFile("selfie.jpg", 600, 800, "image/jpeg", 500 * 1024); // 0.75
        MockMultipartFile invalidPortrait = createMockImageFile("selfie.jpg", 400, 300, "image/jpeg", 500 * 1024); // 1.33

        assertTrue(validator.isValid(validPortrait1, null));
        assertTrue(validator.isValid(validPortrait2, null));
        assertFalse(validator.isValid(invalidPortrait, null));
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
}
