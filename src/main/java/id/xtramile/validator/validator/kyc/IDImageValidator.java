package id.xtramile.validator.validator.kyc;

import id.xtramile.validator.annotation.kyc.ValidIDImage;
import id.xtramile.validator.enums.Group;
import id.xtramile.validator.util.MessageUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static id.xtramile.validator.util.ValidatorUtils.formatFileSize;

/**
 * Validator implementation for {@link ValidIDImage} annotation.
 * <p>
 * Validates ID document images for KYC (Know Your Customer) processes.
 * This validator ensures uploaded ID images meet specific requirements
 * including file size, dimensions, aspect ratio, and MIME type validation.
 * 
 * <p>The validator performs the following operations:
 * <ul>
 * <li>Accepts null/empty files as valid</li>
 * <li>Validates file size against maximum limit</li>
 * <li>Validates MIME type against allowed types</li>
 * <li>Validates image dimensions (width/height)</li>
 * <li>Checks optional aspect ratio requirements</li>
 * <li>Handles image parsing exceptions gracefully</li>
 * </ul>
 * 
 * @see ValidIDImage
 */
@SuppressWarnings("DuplicatedCode")
public class IDImageValidator implements ConstraintValidator<ValidIDImage, MultipartFile> {
    private long maxBytes;
    private int minW;
    private int minH;
    private int maxW;
    private int maxH;
    private double aspect;
    private Set<String> mime;

    /**
     * Initializes the validator with the annotation parameters.
     * @param annotation the ValidIDImage annotation instance
     */
    @Override
    public void initialize(ValidIDImage annotation) {
        this.maxBytes = annotation.maxMB() <= 0 ? Long.MAX_VALUE : annotation.maxMB() * 1024 * 1024;
        this.minW = annotation.minWidth();
        this.minH = annotation.minHeight();
        this.maxW = annotation.maxWidth();
        this.maxH = annotation.maxHeight();
        this.aspect = annotation.aspectRatio();
        this.mime = Stream.of(annotation.mimeAllowed())
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
    }

    /**
     * Validates the ID image against the configured constraints.
     * @param file the MultipartFile image to validate
     * @param context the constraint validator context
     * @return true if the image meets requirements or is null/empty
     */
    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) return true;

        if (file.getSize() > maxBytes) {
            String sizeText = formatFileSize(maxBytes);
            MessageUtils.buildViolation(context, Group.KYC, "image-id.size", sizeText);
            return false;
        }

        String ct = file.getContentType();
        if (ct == null || !mime.contains(ct.toLowerCase())) {
            String mimes = MessageUtils.join(mime);
            MessageUtils.buildViolation(context, Group.KYC, "image-id.mime", mimes);
            return false;
        }

        try (InputStream in = file.getInputStream()) {
            BufferedImage img = ImageIO.read(in);
            if (img == null) {
                MessageUtils.buildViolation(context, Group.KYC, "image-id");
                return false;
            }

            int w = img.getWidth();
            int h = img.getHeight();

            if (w < minW || h < minH || w > maxW || h > maxH) {
                MessageUtils.buildViolation(context, Group.KYC, "image-id.dimension", minW, minH, maxW, maxH);
                return false;
            }

            if (aspect > 0) {
                double eps = 0.01;
                double actualAspect = (double) w / (double) h;
                double diff = Math.abs(actualAspect - aspect);
                if (diff > eps) {
                    MessageUtils.buildViolation(context, Group.KYC, "image-id.dimension", minW, minH, maxW, maxH);
                    return false;
                }
            }

            return true;

        } catch (Exception e) {
            MessageUtils.buildViolation(context, Group.KYC, "image-id");
            return false;
        }
    }
}
