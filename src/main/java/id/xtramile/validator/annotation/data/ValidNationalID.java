package id.xtramile.validator.annotation.data;

import id.xtramile.validator.validator.data.NationalIdValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates a national identification number for the configured country.
 * <p>
 * Currently supported: ID (Indonesia) NIK = exactly 16 digits.
 * Null/blank values are considered valid.
 * 
 * <p>Example usage:
 * <pre>{@code
 * @ValidNationalID // defaults to ID (Indonesia)
 * private String nik; // 16 digits
 * 
 * @ValidNationalID(country = "ID")
 * private String nationalId; // Indonesian NIK
 * }</pre>
 * 
 * @see NationalIdValidator
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NationalIdValidator.class)
public @interface ValidNationalID {
    String message() default "{friendly.default}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    /**
     * The country code for national ID validation.
     */
    String country() default "ID";
}
