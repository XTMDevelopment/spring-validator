package id.xtramile.validator.validator.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.xtramile.validator.annotation.data.ValidJSON;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import static id.xtramile.validator.util.ValidatorUtils.isBlank;

/**
 * Validator implementation for {@link ValidJSON} annotation.
 * <p>
 * Validates that a string is a syntactically valid JSON using Jackson's ObjectMapper.
 * This validator ensures the string can be parsed as valid JSON (object, array, etc.)
 * without throwing parsing exceptions.
 * 
 * <p>The validator performs the following operations:
 * <ul>
 * <li>Accepts null/blank values as valid</li>
 * <li>Attempts to parse the string as JSON using Jackson</li>
 * <li>Returns false if parsing fails with any exception</li>
 * </ul>
 * 
 * @see ValidJSON
 */
public class JsonStringValidator implements ConstraintValidator<ValidJSON, String> {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Validates that the string is valid JSON.
     * @param value the JSON string to validate
     * @param context the constraint validator context
     * @return true if the string is valid JSON or is null/blank
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (isBlank(value)) return true;

        try {
            MAPPER.readTree(value);
            return true;

        } catch (Exception e) {
            return false;
        }
    }
}
