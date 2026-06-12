package id.xtramile.validator.annotation.finance;

import id.xtramile.validator.validator.finance.CardExpiryValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates a card expiry date string in MM/YY format.
 * <p>
 * Null/blank values are considered valid. This validator ensures the expiry date
 * is in the correct format and optionally checks if the card is not expired.
 *
 * <p>Example usage:
 * <pre>{@code
 * @ValidCardExpiry
 * private String expiryDate; // e.g., 12/25
 *
 * @ValidCardExpiry(mustBeFuture = false)
 * private String historicalExpiry; // allows expired cards
 * }</pre>
 *
 * @see CardExpiryValidator
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CardExpiryValidator.class)
public @interface ValidCardExpiry {
    String message() default "{friendly.default}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * If true, card must be in the future (not expired).
     */
    boolean mustBeFuture() default true;
}
