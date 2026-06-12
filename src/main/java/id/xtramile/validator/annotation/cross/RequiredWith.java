package id.xtramile.validator.annotation.cross;

import id.xtramile.validator.validator.cross.RequiredWithValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Class-level constraint. If the "when" field is present, validation passes (no requirements).
 * <p>
 * If the "when" field is absent, all fields listed in "require" must be present;
 * otherwise violations are added to each missing field. This behaves like a
 * "required when [when] is missing" rule.
 *
 * <p>Example usage:
 * <pre>{@code
 * @RequiredWith(when = "primaryId", require = {"secondaryId", "backupCode"})
 * public class VerificationPayload {
 *     private String primaryId;     // if present -> rule passes; if absent -> both below must be present
 *     private String secondaryId;
 *     private String backupCode;
 * }
 * }</pre>
 *
 * @see RequiredWithValidator
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RequiredWithValidator.class)
public @interface RequiredWith {
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
     * The field that, when present, makes the rule pass.
     *
     * @return the field that, when present, makes the rule pass
     */
    String when();

    /**
     * The fields that must be present when "when" field is absent.
     *
     * @return the fields that must be present when "when" field is absent
     */
    String[] require();
}
