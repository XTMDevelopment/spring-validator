package id.xtramile.validator.annotation.data;

import id.xtramile.validator.validator.data.PinValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates a numeric PIN of exact length (digits only).
 * <p>
 * This validator ensures the PIN contains only digits and meets the specified length requirement.
 * It also checks for repetitive and sequential patterns to enhance security.
 * Null/blank values are considered valid.
 * 
 * <p>Example usage:
 * <pre>{@code
 * @ValidPIN(length = 6)
 * private String transactionPin; // e.g., 123456
 * 
 * @ValidPIN(length = 4, maxAllowedRepetitive = 2, maxAllowedSequential = 3)
 * private String atmPin; // e.g., 1234
 * }</pre>
 * 
 * @see PinValidator
 */
@Documented
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PinValidator.class)
public @interface ValidPIN {
    String message() default "{friendly.default}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    /**
     * The exact length the PIN must have.
     */
    int length() default 6;
    
    /**
     * Maximum number of consecutive identical digits allowed.
     */
    int maxAllowedRepetitive() default 5;
    
    /**
     * Maximum number of consecutive sequential digits allowed.
     */
    int maxAllowedSequential() default 5;
}
