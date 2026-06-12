package id.xtramile.validator.annotation.common;

import id.xtramile.validator.validator.common.UUIDValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates that the String value is a valid UUID (java.util.UUID format).
 * <p>
 * This validator ensures the string follows the standard UUID format (8-4-4-4-12 hexadecimal digits).
 * Null/blank values are considered valid.
 * 
 * <p>Example usage:
 * <pre>{@code
 * @ValidUUID
 * private String requestId; // e.g., 550e8400-e29b-41d4-a716-446655440000
 * 
 * @ValidUUID
 * private String sessionId; // e.g., 6ba7b810-9dad-11d1-80b4-00c04fd430c8
 * }</pre>
 * 
 * @see UUIDValidator
 */
@Documented
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UUIDValidator.class)
public @interface ValidUUID {
    String message() default "{friendly.default}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
