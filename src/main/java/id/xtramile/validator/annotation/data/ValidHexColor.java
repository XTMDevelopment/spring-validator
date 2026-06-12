package id.xtramile.validator.annotation.data;

import id.xtramile.validator.validator.data.HexColorValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates a hex color code.
 * <p>
 * Long form #RRGGBB is always allowed; when shortFormAllowed=true, #RGB is also allowed.
 * Case-insensitive. Null/blank values are considered valid.
 *
 * <p>Example usage:
 * <pre>{@code
 * @ValidHexColor
 * private String primaryColor; // e.g., #ff8800 or #f80
 *
 * @ValidHexColor(shortFormAllowed = false)
 * private String strictColor; // e.g., #00FFCC
 * }</pre>
 *
 * @see HexColorValidator
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = HexColorValidator.class)
public @interface ValidHexColor {
    String message() default "{friendly.default}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Whether to allow short form hex colors (#RGB).
     */
    boolean shortFormAllowed() default true;
}
