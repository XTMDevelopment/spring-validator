package id.xtramile.validator.annotation.cross;

import id.xtramile.validator.validator.cross.AtLeastOneOfValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Class-level constraint. Validates that at least one of the specified fields is present.
 * <p>
 * If none are present, a violation is attached to the first field.
 * This validator ensures that at least one field from the list has a value.
 *
 * <p>Example usage:
 * <pre>{@code
 * @AtLeastOneOf(fields = {"email", "phone"})
 * public class ContactPreference {
 *     private String email;
 *     private String phone;
 * }
 * }</pre>
 *
 * @see AtLeastOneOfValidator
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AtLeastOneOfValidator.class)
public @interface AtLeastOneOf {
    String message() default "{friendly.default}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * The list of fields to check - at least one must be present.
     */
    String[] fields();
}
