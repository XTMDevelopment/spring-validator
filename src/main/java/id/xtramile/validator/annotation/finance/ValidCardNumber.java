package id.xtramile.validator.annotation.finance;

import id.xtramile.validator.validator.finance.CardNumberValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates a credit/debit card number using Luhn algorithm.
 * <p>
 * Null/blank values are considered valid. This validator ensures the card number
 * passes the Luhn checksum algorithm for basic validity.
 * 
 * <p>Example usage:
 * <pre>{@code
 * @ValidCardNumber
 * private String cardNumber; // e.g., 4111111111111111
 * 
 * @ValidCardNumber(stripSeparators = false)
 * private String formattedCard; // e.g., 4111-1111-1111-1111
 * }</pre>
 * 
 * @see CardNumberValidator
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CardNumberValidator.class)
public @interface ValidCardNumber {
    String message() default "{friendly.default}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    /**
     * Strip spaces and dashes before validating.
     */
    boolean stripSeparators() default true;
}
