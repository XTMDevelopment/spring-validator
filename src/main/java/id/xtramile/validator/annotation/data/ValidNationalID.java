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
    /**
     * Default violation message template.
     *
     * @return the message template
     */
    String message() default "{friendly.default}";

    /**
     * Validation groups for conditional validation.
     *
     * @return the validation groups
     */
    Class<?>[] groups() default {};

    /**
     * Payload types for extensibility metadata.
     *
     * @return the payload types
     */
    Class<? extends Payload>[] payload() default {};

    /**
     * The country code for national ID validation.
     *
     * @return the country code for national ID validation
     */
    String country() default "ID";
}
