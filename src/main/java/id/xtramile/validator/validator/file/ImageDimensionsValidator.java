package id.xtramile.validator.validator.file;

import id.xtramile.validator.annotation.file.ValidImageDimensions;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;

/**
 * Validator implementation for {@link ValidImageDimensions} annotation.
 * <p>
 * Validates image dimensions and optional aspect ratio for MultipartFile objects.
 * This validator ensures uploaded images meet specific dimension requirements
 * with support for file extension filtering and aspect ratio validation.
 * 
 * <p>The validator performs the following operations:
 * <ul>
 * <li>Accepts null values as valid</li>
 * <li>Validates image dimensions (width/height)</li>
 * <li>Checks optional aspect ratio requirements</li>
 * <li>Handles image parsing exceptions gracefully</li>
 * </ul>
 * 
 * @see ValidImageDimensions
 */
public class ImageDimensionsValidator implements ConstraintValidator<ValidImageDimensions, Object> {
    private int minW;
    private int minH;
    private int maxW;
    private int maxH;
    private double aspect;

    /**
     * Initializes the validator with the annotation parameters.
     * @param annotation the ValidImageDimensions annotation instance
     */
    @Override
    public void initialize(ValidImageDimensions annotation) {
        this.minW = Math.max(0, annotation.minWidth());
        this.minH = Math.max(0, annotation.minHeight());
        this.maxW = Math.max(annotation.maxWidth(), minW);
        this.maxH = Math.max(annotation.maxHeight(), minH);
        this.aspect = annotation.aspectRatio();
    }

    /**
     * Validates the image dimensions against the configured constraints.
     * @param value the MultipartFile object to validate
     * @param context the constraint validator context
     * @return true if the image meets dimension requirements or value is null/empty
     */
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) return true;

        BufferedImage img;

        try {
            if (value instanceof MultipartFile mf) {
                if (mf.isEmpty()) return true;

                try (InputStream in = mf.getInputStream()) {
                    img = ImageIO.read(in);
                }
            } else {
                // unsupported type
                return true;
            }
        } catch (Exception e) {
            return false;
        }

        if (img == null) return false;

        int w = img.getWidth();
        int h = img.getHeight();

        if (w < minW || h < minH) return false;
        if (w > maxW || h > maxH) return false;

        if (aspect > 0.0) {
            double actual = (double) w / (double) h;
            double eps = 0.01; // Tolerance for aspect ratio (1%)

            return !(Math.abs(actual - aspect) > eps);
        }

        return true;
    }
}
