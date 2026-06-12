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
     * Strip spaces and dashes before validating.
     *
     * @return the strip spaces and dashes before validating
     */
    boolean stripSeparators() default true;
}
