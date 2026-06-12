package id.xtramile.validator.validator.data;

import id.xtramile.validator.annotation.data.ValidTaxID;
import id.xtramile.validator.enums.Group;
import id.xtramile.validator.util.MessageUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import static id.xtramile.validator.util.ValidatorUtils.isBlank;

/**
 * Validator implementation for {@link ValidTaxID} annotation.
 * <p>
 * Validates a tax identification number for the configured country.
 * Currently supports Indonesian NPWP (Nomor Pokok Wajib Pajak) format.
 * This validator ensures the tax ID follows the correct format for the specified country.
 * 
 * <p>The validator performs the following checks:
 * <ul>
 * <li>Accepts null/blank values as valid</li>
 * <li>For Indonesia (ID): validates 15-16 digit NPWP format</li>
 * <li>Removes non-digit characters before validation</li>
 * <li>Returns false for unsupported countries</li>
 * </ul>
 * 
 * @see ValidTaxID
 */
public class TaxIdValidator implements ConstraintValidator<ValidTaxID, String> {
    private String country;

    /**
     * Initializes the validator with the annotation parameters.
     * @param annotation the ValidTaxID annotation instance
     */
    @Override
    public void initialize(ValidTaxID annotation) {
        this.country = annotation.country();
    }

    /**
     * Validates the tax ID against the configured country format.
     * @param value the tax ID string to validate
     * @param context the constraint validator context
     * @return true if the tax ID is valid or is null/blank
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (isBlank(value)) return true;
        
        if (!"ID".equalsIgnoreCase(country)) {
            MessageUtils.buildViolation(context, Group.DATA, "tax-id");
            return false;
        }

        String digits = value.replaceAll("[^0-9]", "");

        if (digits.isEmpty()) {
            MessageUtils.buildViolation(context, Group.DATA, "tax-id.numbers");
            return false;
        }

        if (!digits.matches("\\d+")) {
            MessageUtils.buildViolation(context, Group.DATA, "tax-id.numbers");
            return false;
        }

        if (digits.length() != 15 && digits.length() != 16) {
            MessageUtils.buildViolation(context, Group.DATA, "tax-id.length");
            return false;
        }

        return true;
    }
}
