package id.xtramile.validator.annotation.data;

import id.xtramile.validator.validator.data.Base64Validator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates a Base64-encoded string.
 * <p>
 * When urlSafe=true, uses URL-safe Base64 alphabet. Null/blank values are considered valid.
 * This validator ensures the string is properly Base64 encoded.
 *
 * <p>Example usage:
 * <pre>{@code
 * @ValidBase64
 * private String imagePayload; // Standard Base64
 *
 * @ValidBase64(urlSafe = true)
 * private String jwtSegment; // URL-safe Base64
 * }</pre>
 *
 * @see Base64Validator
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = Base64Validator.class)
public @interface ValidBase64 {
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
     * Whether to use URL-safe Base64 alphabet.
     *
     * @return the whether to use URL-safe Base64 alphabet
     */
    boolean urlSafe() default false;
}
