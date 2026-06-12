package id.xtramile.validator.annotation.data;

import id.xtramile.validator.validator.data.JsonStringValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates that the String is a syntactically valid JSON (object/array/etc.), parsed via Jackson.
 * <p>
 * Null/blank values are considered valid. This validator ensures the string can be parsed
 * as valid JSON using Jackson's ObjectMapper.
 *
 * <p>Example usage:
 * <pre>{@code
 * @ValidJSON
 * private String jsonConfig; // e.g., {"a":1,"b":[true,false]}
 *
 * @ValidJSON
 * private String apiResponse; // e.g., [{"id":1,"name":"test"}]
 * }</pre>
 *
 * @see JsonStringValidator
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = JsonStringValidator.class)
public @interface ValidJSON {
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
}
