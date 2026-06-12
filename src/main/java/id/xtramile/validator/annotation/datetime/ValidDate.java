package id.xtramile.validator.annotation.datetime;

import id.xtramile.validator.validator.datetime.DateValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates that a String can be parsed into a LocalDate using the provided pattern.
 * <p>
 * Null/blank values are considered valid. This validator ensures the string
 * represents a valid date according to the specified format.
 *
 * <p>Example usage:
 * <pre>{@code
 * @ValidDate // default pattern yyyy-MM-dd
 * private String birthDate; // e.g., 1990-05-21
 *
 * @ValidDate(pattern = "dd/MM/yyyy")
 * private String altDate; // e.g., 21/05/1990
 * }</pre>
 *
 * @see DateValidator
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateValidator.class)
public @interface ValidDate {
    String message() default "{friendly.default}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * The date pattern to use for parsing.
     */
    String pattern() default "yyyy-MM-dd";
}
