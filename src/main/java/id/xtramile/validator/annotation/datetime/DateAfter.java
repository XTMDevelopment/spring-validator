package id.xtramile.validator.annotation.datetime;

import id.xtramile.validator.enums.DatePrecision;
import id.xtramile.validator.validator.datetime.DateAfterValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates that the first date field must be after the second date field.
 * <p>
 * Both fields must be String values that can be parsed as date-time using the specified pattern.
 * The validator also optionally enforces a maximum distance between the two dates based on the precision type.
 * Null/blank values are considered valid.
 * 
 * <p>Example usage:
 * <pre>{@code
 * @DateAfter(first = "endDate", second = "startDate")
 * public class EventDTO {
 *     private String startDate;  // e.g., "2025-01-01 10:00:00"
 *     private String endDate;    // e.g., "2025-01-02 15:30:00"
 * }
 * 
 * @DateAfter(first = "expiryDate", second = "issueDate",
 *            pattern = "yyyy-MM-dd HH:mm:ss",
 *            maxDistance = 365, precision = DatePrecision.DAYS)
 * public class DocumentDTO {
 *     private String issueDate;
 *     private String expiryDate;
 * }
 * }</pre>
 * 
 * @see DateAfterValidator
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateAfterValidator.class)
public @interface DateAfter {
    String message() default "{friendly.default}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    /**
     * The name of the first date field (must be after the second).
     */
    String first();
    
    /**
     * The name of the second date field (must be before the first).
     */
    String second();
    
    /**
     * The date-time pattern to use for parsing both fields.
     */
    String pattern() default "yyyy-MM-dd HH:mm:ss";
    
    /**
     * The maximum distance allowed between the two dates.
     * If set to -1 (default), no distance check is performed.
     * The unit is determined by the precision parameter.
     */
    long maxDistance() default -1;
    
    /**
     * The precision type for calculating the distance between dates.
     * Only used when maxDistance is set to a positive value.
     */
    DatePrecision precision() default DatePrecision.DAYS;
}
