package id.xtramile.validator.annotation.cross;

import id.xtramile.validator.validator.cross.DifferentFromValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Class-level constraint. Validates that the value of "field" is different from the value of "other".
 * <p>
 * As implemented, it is considered valid when both are null or when they are different;
 * invalid when they are equal. This ensures two fields have different values.
 * 
 * <p>Example usage:
 * <pre>{@code
 * @DifferentFrom(field = "newEmail", other = "oldEmail")
 * public class EmailChangeRequest {
 *     private String oldEmail;
 *     private String newEmail;
 * }
 * }</pre>
 * 
 * @see DifferentFromValidator
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DifferentFromValidator.class)
public @interface DifferentFrom {
    String message() default "{friendly.default}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    /**
     * The field to check for difference.
     */
    String field();
    
    /**
     * The other field to compare against.
     */
    String other();
}
