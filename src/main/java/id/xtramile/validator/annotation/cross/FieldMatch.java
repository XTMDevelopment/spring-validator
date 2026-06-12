package id.xtramile.validator.annotation.cross;

import id.xtramile.validator.validator.cross.FieldMatchValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Class-level constraint. Validates that the two specified fields are equal.
 * <p>
 * It is valid when both are null or when both are non-null and equal.
 * Violation is attached to the "second" field when they don't match.
 *
 * <p>Example usage:
 * <pre>{@code
 * @FieldMatch(first = "password", second = "confirmPassword")
 * public class PasswordForm {
 *     private String password;
 *     private String confirmPassword;
 * }
 * }</pre>
 *
 * @see FieldMatchValidator
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FieldMatchValidator.class)
public @interface FieldMatch {
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
     * The first field to compare.
     *
     * @return the first field to compare
     */
    String first();

    /**
     * The second field to compare.
     *
     * @return the second field to compare
     */
    String second();
}
