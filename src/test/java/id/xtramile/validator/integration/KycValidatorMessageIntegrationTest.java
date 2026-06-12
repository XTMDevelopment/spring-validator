package id.xtramile.validator.integration;

import id.xtramile.validator.annotation.kyc.ValidIDImage;
import id.xtramile.validator.annotation.kyc.ValidSelfieImage;
import id.xtramile.validator.validator.file.MockMultipartFile;
import id.xtramile.validator.web.FriendlyMessageResolver;
import id.xtramile.validator.web.MessageResourceResolver;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class KycValidatorMessageIntegrationTest {

    private static final Validator VALIDATOR;
    private static final FriendlyMessageResolver RESOLVER;

    static {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        VALIDATOR = factory.getValidator();

        MessageResourceResolver messageResourceResolver = new MessageResourceResolver("en");
        RESOLVER = new FriendlyMessageResolver(messageResourceResolver);
    }

    public static class IDImageDto {
        @ValidIDImage(maxMB = 5, minWidth = 200, minHeight = 200, maxWidth = 4096, maxHeight = 4096,
                mimeAllowed = {"image/png", "image/jpeg"})
        private final MultipartFile value;

        public IDImageDto(MultipartFile value) {
            this.value = value;
        }

        public MultipartFile value() {
            return value;
        }
    }

    public static class IDImageSmallSizeDto {
        @ValidIDImage(maxMB = 1, minWidth = 1, minHeight = 1, maxWidth = 4096, maxHeight = 4096,
                mimeAllowed = {"image/png", "image/jpeg"})
        private final MultipartFile value;

        public IDImageSmallSizeDto(MultipartFile value) {
            this.value = value;
        }

        public MultipartFile value() {
            return value;
        }
    }

    public static class IDImageWrongMimeDto {
        @ValidIDImage(maxMB = 5, minWidth = 1, minHeight = 1, maxWidth = 4096, maxHeight = 4096,
                mimeAllowed = {"image/png"})
        private final MultipartFile value;

        public IDImageWrongMimeDto(MultipartFile value) {
            this.value = value;
        }

        public MultipartFile value() {
            return value;
        }
    }

    public static class IDImageWrongDimensionDto {
        @ValidIDImage(maxMB = 5, minWidth = 300, minHeight = 300, maxWidth = 4096, maxHeight = 4096,
                mimeAllowed = {"image/png", "image/jpeg"})
        private final MultipartFile value;

        public IDImageWrongDimensionDto(MultipartFile value) {
            this.value = value;
        }

        public MultipartFile value() {
            return value;
        }
    }

    public static class SelfieImageDto {
        @ValidSelfieImage(minWidth = 200, minHeight = 200, mimeAllowed = {"image/png", "image/jpeg"})
        private final MultipartFile value;

        public SelfieImageDto(MultipartFile value) {
            this.value = value;
        }

        public MultipartFile value() {
            return value;
        }
    }

    public static class SelfieImageSmallSizeDto {
        @ValidSelfieImage(maxMB = 1, minWidth = 1, minHeight = 1, mimeAllowed = {"image/png", "image/jpeg"})
        private final MultipartFile value;

        public SelfieImageSmallSizeDto(MultipartFile value) {
            this.value = value;
        }

        public MultipartFile value() {
            return value;
        }
    }

    public static class SelfieImageWrongMimeDto {
        @ValidSelfieImage(minWidth = 1, minHeight = 1, mimeAllowed = {"image/png"})
        private final MultipartFile value;

        public SelfieImageWrongMimeDto(MultipartFile value) {
            this.value = value;
        }

        public MultipartFile value() {
            return value;
        }
    }

    public static class SelfieImageWrongDimensionDto {
        @ValidSelfieImage(minWidth = 300, minHeight = 300, mimeAllowed = {"image/png", "image/jpeg"})
        private final MultipartFile value;

        public SelfieImageWrongDimensionDto(MultipartFile value) {
            this.value = value;
        }

        public MultipartFile value() {
            return value;
        }
    }

    private <T> ConstraintViolation<T> firstViolation(T dto) {
        Set<ConstraintViolation<T>> violations = VALIDATOR.validate(dto);

        assertFalse(violations.isEmpty(), "Expected at least one violation but got none");

        return violations.iterator().next();
    }

    /** Creates a minimal valid PNG image with the given dimensions. */
    private static byte[] createPng(int width, int height) throws IOException {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, "png", baos);

        return baos.toByteArray();
    }

    private static MultipartFile mockFile(String name, String contentType, byte[] bytes) {
        return new MockMultipartFile(name, name, contentType, bytes);
    }

    @Test
    void idImage_invalidBytes() {
        MultipartFile file = mockFile("id.png", "image/png", new byte[]{0, 1, 2, 3, 4, 5});
        IDImageDto dto = new IDImageDto(file);
        ConstraintViolation<IDImageDto> v = firstViolation(dto);

        assertEquals("validation.kyc.image-id", v.getMessageTemplate());
        assertEquals("value must be a valid ID image", RESOLVER.resolve(v, "value", IDImageDto.class));
    }

    @Test
    void idImage_tooLarge() {
        byte[] twoMB = new byte[2 * 1024 * 1024 + 1];
        MultipartFile file = mockFile("id.png", "image/png", twoMB);
        IDImageSmallSizeDto dto = new IDImageSmallSizeDto(file);
        ConstraintViolation<IDImageSmallSizeDto> v = firstViolation(dto);

        assertEquals("validation.kyc.image-id.size", v.getMessageTemplate());

        String resolved = RESOLVER.resolve(v, "value", IDImageSmallSizeDto.class);

        assertTrue(resolved.startsWith("value must have maximal"), "Unexpected message: " + resolved);
    }

    @Test
    void idImage_wrongMime() {
        MultipartFile file = mockFile("id.jpg", "image/jpeg", new byte[]{1, 2, 3});
        IDImageWrongMimeDto dto = new IDImageWrongMimeDto(file);
        ConstraintViolation<IDImageWrongMimeDto> v = firstViolation(dto);

        assertEquals("validation.kyc.image-id.mime", v.getMessageTemplate());

        String resolved = RESOLVER.resolve(v, "value", IDImageWrongMimeDto.class);

        assertTrue(resolved.contains("image/png"), "MIME type missing in message: " + resolved);
    }

    @Test
    void idImage_wrongDimension() throws IOException {
        byte[] smallPng = createPng(100, 100);
        MultipartFile file = mockFile("id.png", "image/png", smallPng);
        IDImageWrongDimensionDto dto = new IDImageWrongDimensionDto(file);
        ConstraintViolation<IDImageWrongDimensionDto> v = firstViolation(dto);

        assertEquals("validation.kyc.image-id.dimension", v.getMessageTemplate());

        String resolved = RESOLVER.resolve(v, "value", IDImageWrongDimensionDto.class);

        assertTrue(resolved.contains("300"), "Dimension not in message: " + resolved);
    }

    @Test
    void selfieImage_invalidBytes() {
        MultipartFile file = mockFile("selfie.png", "image/png", new byte[]{0, 1, 2, 3, 4, 5});
        SelfieImageDto dto = new SelfieImageDto(file);
        ConstraintViolation<SelfieImageDto> v = firstViolation(dto);

        assertEquals("validation.kyc.image-selfie", v.getMessageTemplate());
        assertEquals("value must be a valid selfie image", RESOLVER.resolve(v, "value", SelfieImageDto.class));
    }

    @Test
    void selfieImage_tooLarge() {
        byte[] twoMB = new byte[2 * 1024 * 1024 + 1];
        MultipartFile file = mockFile("selfie.png", "image/png", twoMB);
        SelfieImageSmallSizeDto dto = new SelfieImageSmallSizeDto(file);
        ConstraintViolation<SelfieImageSmallSizeDto> v = firstViolation(dto);

        assertEquals("validation.kyc.image-selfie.size", v.getMessageTemplate());

        String resolved = RESOLVER.resolve(v, "value", SelfieImageSmallSizeDto.class);

        assertTrue(resolved.startsWith("value must have maximal"), "Unexpected message: " + resolved);
    }

    @Test
    void selfieImage_wrongMime() {
        MultipartFile file = mockFile("selfie.jpg", "image/jpeg", new byte[]{1, 2, 3});
        SelfieImageWrongMimeDto dto = new SelfieImageWrongMimeDto(file);
        ConstraintViolation<SelfieImageWrongMimeDto> v = firstViolation(dto);

        assertEquals("validation.kyc.image-selfie.mime", v.getMessageTemplate());

        String resolved = RESOLVER.resolve(v, "value", SelfieImageWrongMimeDto.class);

        assertTrue(resolved.contains("image/png"), "MIME type missing in message: " + resolved);
    }

    @Test
    void selfieImage_wrongDimension() throws IOException {
        byte[] smallPng = createPng(100, 100);
        MultipartFile file = mockFile("selfie.png", "image/png", smallPng);
        SelfieImageWrongDimensionDto dto = new SelfieImageWrongDimensionDto(file);
        ConstraintViolation<SelfieImageWrongDimensionDto> v = firstViolation(dto);

        assertEquals("validation.kyc.image-selfie.dimension", v.getMessageTemplate());

        String resolved = RESOLVER.resolve(v, "value", SelfieImageWrongDimensionDto.class);

        assertTrue(resolved.contains("300"), "Dimension not in message: " + resolved);
    }
}
