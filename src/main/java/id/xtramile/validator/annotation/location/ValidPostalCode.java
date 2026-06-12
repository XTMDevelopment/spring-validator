package id.xtramile.validator.annotation.location;

import id.xtramile.validator.validator.location.PostalCodeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates a postal code string for the specified country.
 * <p>
 * Null/blank values are considered valid. This validator ensures postal codes
 * follow the correct format for the specified country.
 *
 * <p>Example usage:
 * <pre>{@code
 * @ValidPostalCode(country = "ID")
 * private String postalCode; // e.g., 12345 (Indonesia)
 *
 * @ValidPostalCode(country = "US")
 * private String zipCode; // e.g., 12345-6789 (United States)
 * }</pre>
 *
 * @see PostalCodeValidator
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PostalCodeValidator.class)
public @interface ValidPostalCode {
    String message() default "{friendly.default}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * ISO country code, e.g. "ID" (Indonesia), "US", "GB".
     */
    String country() default "ID";
}
