package id.xtramile.validator.validator.location;

import id.xtramile.validator.annotation.location.ValidPostalCode;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Map;
import java.util.regex.Pattern;

import static id.xtramile.validator.util.ValidatorUtils.isBlank;

/**
 * Validator implementation for {@link ValidPostalCode} annotation.
 * <p>
 * Validates postal codes for specific countries using regex patterns.
 * This validator ensures the postal code follows the correct format
 * for the specified country with predefined validation rules.
 * 
 * <p>The validator performs the following operations:
 * <ul>
 * <li>Accepts null/blank values as valid</li>
 * <li>Validates against country-specific patterns</li>
 * <li>Trims whitespace before validation</li>
 * <li>Returns false for unsupported countries</li>
 * </ul>
 * 
 * @see ValidPostalCode
 */
public class PostalCodeValidator implements ConstraintValidator<ValidPostalCode, Object> {
    private String country;

    private static final Map<String, Pattern> RULES = Map.of(
            "ID", Pattern.compile("^[0-9]{5}$"),
            "MY", Pattern.compile("^[0-9]{5}$")
    );

    /**
     * Initializes the validator with the annotation parameters.
     * @param annotation the ValidPostalCode annotation instance
     */
    @Override
    public void initialize(ValidPostalCode annotation) {
        this.country = annotation.country();
    }

    /**
     * Validates the postal code against the country-specific pattern.
     * @param value the postal code string to validate
     * @param context the constraint validator context
     * @return true if the postal code is valid or is null/blank
     */
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        Pattern pattern = RULES.get(this.country);
        if (pattern == null) {
            return false;
        }

        if (value instanceof String) {
            String s = (String) value;
            if (isBlank(s)) {
                return true;
            }

            return pattern.matcher(s.trim()).matches();

        } else if (value instanceof Integer) {
            Integer num = (Integer) value;
            if (num == 0) {
                return true;
            }

            return pattern.matcher(num.toString()).matches();
        }

        return false;
    }
}
