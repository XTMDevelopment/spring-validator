package id.xtramile.validator.annotation.data;

import id.xtramile.validator.enums.PasswordType;
import id.xtramile.validator.validator.data.PasswordValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Password policy validator with configurable strength requirements.
 * <p>
 * Rejects any whitespace. Length must be >= min (Unicode-aware).
 * Strength policy is selected by type. Null/blank values are considered valid.
 *
 * <p>Password strength types:
 * <ul>
 * <li>ANY: length >= min; any non-whitespace characters</li>
 * <li>ALPHANUMERIC: only letters and digits; length >= min</li>
 * <li>LETTER_DIGIT: at least one letter and one digit; symbols optional; length >= min</li>
 * <li>LETTER_MIXED_CASE: at least one lowercase and one uppercase letter; digits/symbols optional; length >= min</li>
 * <li>FULL: lowercase + uppercase + digit + symbol (any non-alphanumeric), all required; length >= min</li>
 * <li>STRONG_3_OF_4: at least 3 of these 4 classes: lowercase, uppercase, digit, symbol; length >= min</li>
 * </ul>
 *
 * <p>Example usage:
 * <pre>{@code
 * @ValidPassword(min = 10, type = PasswordType.FULL)
 * private String password; // Strong policy: lower+upper+digit+symbol, min length 10
 *
 * @ValidPassword(min = 8, type = PasswordType.ALPHANUMERIC)
 * private String pinLikePassword; // Alphanumeric only, min 8
 * }</pre>
 *
 * @see PasswordValidator
 * @see PasswordType
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordValidator.class)
public @interface ValidPassword {
    String message() default "{friendly.default}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Minimum password length required.
     */
    int min() default 8;

    /**
     * Password strength policy type.
     */
    PasswordType type() default PasswordType.FULL;
}
