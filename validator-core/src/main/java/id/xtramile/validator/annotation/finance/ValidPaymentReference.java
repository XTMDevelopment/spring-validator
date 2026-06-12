package id.xtramile.validator.annotation.finance;

import id.xtramile.validator.validator.finance.PaymentReferenceValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates a payment reference string against a configurable pattern.
 * <p>
 * Null/blank values are considered valid. This validator ensures payment references
 * follow the specified format for financial transactions.
 *
 * <p>Example usage:
 * <pre>{@code
 * @ValidPaymentReference
 * private String reference; // e.g., TXN-2025-001
 *
 * @ValidPaymentReference(pattern = "^PAY-[A-Z0-9]{8}$")
 * private String customRef; // e.g., PAY-ABC12345
 * }</pre>
 *
 * @see PaymentReferenceValidator
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PaymentReferenceValidator.class)
public @interface ValidPaymentReference {
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
     * Default: 1–64 of A-Z a-z 0-9 - _ / . :
     *
     * @return the default: 1–64 of A-Z a-z 0-9 - _ / . :
     */
    String pattern() default "^[A-Za-z0-9\\-_/.:]{1,64}$";
}
