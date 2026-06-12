package id.xtramile.validator.annotation.finance;

import id.xtramile.validator.validator.finance.IbanValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates an IBAN (International Bank Account Number) string.
 * <p>
 * Null/blank values are considered valid. This validator ensures the IBAN
 * follows the international standard format and passes the checksum validation.
 *
 * <p>Example usage:
 * <pre>{@code
 * @ValidIBAN
 * private String iban; // e.g., GB82WEST12345698765432
 *
 * @ValidIBAN
 * private String bankAccount; // e.g., DE89370400440532013000
 * }</pre>
 *
 * @see IbanValidator
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = IbanValidator.class)
public @interface ValidIBAN {
    String message() default "{friendly.default}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
