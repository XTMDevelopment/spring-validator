package id.xtramile.validator.annotation.data;

import id.xtramile.validator.validator.data.NameValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates a human name string containing only letters and configured allowed symbols.
 * <p>
 * Length must be between min and max. Null/blank values are considered valid.
 * This validator is designed for human names with configurable symbol support.
 * 
 * <p>Example usage:
 * <pre>{@code
 * // Allow apostrophes and dots (default), length 3..100
 * @ValidName
 * private String fullName; // e.g., O'Connor, A. B. Clark
 * 
 * // Custom symbols and length
 * @ValidName(allowedSymbols = {"'", " ", ".", "-"}, min = 2, max = 60)
 * private String shortName;
 * 
 * // Allow digits in names
 * @ValidName(allowDigits = true)
 * private String nameWithDigits; // e.g., John2, Mary123
 * }</pre>
 */
@Documented
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NameValidator.class)
public @interface ValidName {
    String message() default "{friendly.default}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    /**
     * Symbols allowed in the name (default: apostrophe, space, dot).
     */
    String[] allowedSymbols() default { "'", " ", "." };

    /**
     * Minimum name length.
     */
    int min() default 3;
    
    /**
     * Maximum name length.
     */
    int max() default 100;

    /**
     * Whether digits (0-9) are allowed in the name.
     * Default is false, meaning only letters and allowed symbols are permitted.
     */
    boolean allowDigits() default false;
}
