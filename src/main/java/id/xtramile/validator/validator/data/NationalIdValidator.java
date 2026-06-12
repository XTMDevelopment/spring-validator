package id.xtramile.validator.validator.data;

import id.xtramile.validator.annotation.data.ValidNationalID;
import id.xtramile.validator.enums.Group;
import id.xtramile.validator.util.MessageUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import static id.xtramile.validator.util.ValidatorUtils.isBlank;

/**
 * Validator implementation for {@link ValidNationalID} annotation.
 * <p>
 * Validates a national identification number for the configured country.
 * Currently supports Indonesian NIK (Nomor Induk Kependudukan) format.
 * This validator ensures the national ID follows the correct format for the specified country.
 * 
 * <p>The validator performs the following checks:
 * <ul>
 * <li>Accepts null/blank values as valid</li>
 * <li>For Indonesia (ID): validates 16-digit NIK format</li>
 * <li>Returns false for unsupported countries</li>
 * </ul>
 * 
 * @see ValidNationalID
 */
public class NationalIdValidator implements ConstraintValidator<ValidNationalID, String> {
    private String country;

    /**
     * Initializes the validator with the annotation parameters.
     * @param annotation the ValidNationalID annotation instance
     */
    @Override
    public void initialize(ValidNationalID annotation) {
        this.country = annotation.country();
    }

    /**
     * Validates the national ID against the configured country format.
     * @param value the national ID string to validate
     * @param context the constraint validator context
     * @return true if the national ID is valid or is null/blank
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (isBlank(value)) return true;
        
        if (!"ID".equalsIgnoreCase(country)) {
            MessageUtils.buildViolation(context, Group.DATA, "national-id");
            return false;
        }

        if (!value.matches("\\d+")) {
            MessageUtils.buildViolation(context, Group.DATA, "national-id.numbers");
            return false;
        }

        if (value.length() != 16) {
            MessageUtils.buildViolation(context, Group.DATA, "national-id.length");
            return false;
        }

        return true;
    }
}
