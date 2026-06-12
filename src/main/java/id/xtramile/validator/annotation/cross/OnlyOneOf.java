package id.xtramile.validator.annotation.cross;

import id.xtramile.validator.validator.cross.OnlyOneOfValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Class-level constraint. Validates that exactly one of the specified fields is present.
 * <p>
 * If none or more than one is present, a violation is added (to the first field when none present,
 * or to the last present field when more than one). This ensures mutual exclusivity.
 * 
 * <p>Example usage:
 * <pre>{@code
 * @OnlyOneOf(fields = {"username", "email"})
 * public class LoginIdentifier {
 *     private String username;
 *     private String email;
 * }
 * }</pre>
 * 
 * @see OnlyOneOfValidator
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = OnlyOneOfValidator.class)
public @interface OnlyOneOf {
    String message() default "{friendly.default}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    /**
     * The list of fields to check - exactly one must be present.
     */
    String[] fields();
}
