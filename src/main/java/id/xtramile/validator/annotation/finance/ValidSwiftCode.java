package id.xtramile.validator.annotation.finance;

import id.xtramile.validator.validator.finance.SwiftCodeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates a SWIFT/BIC (Bank Identifier Code) string.
 * <p>
 * Null/blank values are considered valid. This validator ensures the SWIFT code
 * follows the international standard format for bank identification.
 *
 * <p>Example usage:
 * <pre>{@code
 * @ValidSwiftCode
 * private String swiftCode; // e.g., DEUTDEFF
 *
 * @ValidSwiftCode
 * private String bankCode; // e.g., CHASUS33
 * }</pre>
 *
 * @see SwiftCodeValidator
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SwiftCodeValidator.class)
public @interface ValidSwiftCode {
    String message() default "{friendly.default}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
