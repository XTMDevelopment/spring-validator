package id.xtramile.validator.annotation.location;

import id.xtramile.validator.validator.location.RtRwValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates an Indonesian RT/RW (Rukun Tetangga/Rukun Warga) address format.
 * <p>
 * Null/blank values are considered valid. This validator ensures the RT/RW format
 * follows Indonesian administrative structure requirements.
 * 
 * <p>Example usage:
 * <pre>{@code
 * @ValidRTRW
 * private String rtRw; // e.g., "001/002"
 * 
 * @ValidRTRW
 * private String address; // e.g., "RT 001 RW 002"
 * }</pre>
 * 
 * @see RtRwValidator
 */
@Documented
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RtRwValidator.class)
public @interface ValidRTRW {
    String message() default "{friendly.default}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
