package id.xtramile.validator.validator.finance;

import id.xtramile.validator.annotation.finance.ValidIBAN;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import static id.xtramile.validator.util.ValidatorUtils.isBlank;

/**
 * Validator implementation for {@link ValidIBAN} annotation.
 * <p>
 * Validates IBAN (International Bank Account Number) using the mod-97 algorithm.
 * This validator ensures the IBAN follows the international standard format
 * and passes the checksum validation.
 *
 * <p>The validator performs the following operations:
 * <ul>
 * <li>Accepts null/blank values as valid</li>
 * <li>Removes whitespace and converts to uppercase</li>
 * <li>Validates length (15-34 characters)</li>
 * <li>Validates alphanumeric format</li>
 * <li>Applies mod-97 checksum algorithm</li>
 * </ul>
 *
 * @see ValidIBAN
 */
public class IbanValidator implements ConstraintValidator<ValidIBAN, String> {

    /**
     * Validates the IBAN using mod-97 algorithm.
     *
     * @param value   the IBAN string to validate
     * @param context the constraint validator context
     * @return true if the IBAN is valid or is null/blank
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (isBlank(value)) return true;

        String iban = value.replaceAll("\\s+", "").toUpperCase();
        if (iban.length() < 15 || iban.length() > 34) return false;
        if (!iban.matches("^[A-Z0-9]+$")) return false;

        String rearranged = iban.substring(4) + iban.substring(0, 4);

        StringBuilder sb = new StringBuilder(rearranged.length() * 2);
        for (char ch : rearranged.toCharArray()) {
            if (Character.isDigit(ch)) {
                sb.append(ch);
            } else {
                sb.append(ch - 'A' + 10);
            }
        }

        int mod = 0;
        for (int i = 0; i < sb.length(); i++) {
            mod = (mod * 10 + (sb.charAt(i) - '0')) % 97;
        }

        return mod == 1;
    }
}
