package id.xtramile.validator.validator.data;

import id.xtramile.validator.annotation.data.ValidPIN;
import id.xtramile.validator.enums.Group;
import id.xtramile.validator.util.MessageUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import static id.xtramile.validator.util.ValidatorUtils.isBlank;

/**
 * Validator implementation for {@link ValidPIN} annotation.
 * <p>
 * Validates that a PIN contains only digits, meets the specified length requirement,
 * and doesn't have excessive repetitive or sequential patterns for security purposes.
 *
 * <p>The validator performs the following checks:
 * <ul>
 * <li>Ensures the PIN contains only numeric digits</li>
 * <li>Verifies the PIN matches the exact required length</li>
 * <li>Checks for consecutive identical digits (repetitive pattern)</li>
 * <li>Checks for consecutive sequential digits (ascending or descending)</li>
 * </ul>
 *
 * <p>Null/blank values are considered valid.
 *
 * @see ValidPIN
 */
public class PinValidator implements ConstraintValidator<ValidPIN, String> {
    private int length;
    private int maxAllowedRepetitive;
    private int maxAllowedSequential;

    /**
     * Initializes the validator with the annotation parameters.
     *
     * @param annotation the ValidPIN annotation instance
     */
    @Override
    public void initialize(ValidPIN annotation) {
        this.length = annotation.length();
        this.maxAllowedSequential = annotation.maxAllowedSequential();
        this.maxAllowedRepetitive = annotation.maxAllowedRepetitive();
    }

    /**
     * Validates the PIN value against all configured constraints.
     *
     * @param value   the PIN string to validate
     * @param context the constraint validator context
     * @return true if valid, false otherwise
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (isBlank(value)) return true;

        String regex = "^[0-9]{" + length + "}$";
        if (!value.matches(regex)) {
            MessageUtils.buildViolation(context, Group.DATA, "pin.length", length);
            return false;
        }

        if (hasTooManyRepetitions(value)) {
            MessageUtils.buildViolation(context, Group.DATA, "pin.repetitive");
            return false;
        }

        if (hasSequentialPattern(value)) {
            MessageUtils.buildViolation(context, Group.DATA, "pin.sequential");
            return false;
        }

        return true;
    }

    /**
     * Checks if the PIN has too many consecutive identical digits.
     *
     * @param value the PIN string to check
     * @return true if repetitive pattern exceeds allowed limit
     */
    private boolean hasTooManyRepetitions(String value) {
        int repeatCount = 1;
        char[] chars = value.toCharArray();

        for (int i = 1; i < chars.length; i++) {
            if (chars[i] == chars[i - 1]) {
                repeatCount++;
                if (repeatCount > maxAllowedRepetitive) return true;
            } else {
                repeatCount = 1;
            }
        }
        return false;
    }

    /**
     * Checks if the PIN has too many consecutive sequential digits.
     * Supports both ascending (1234) and descending (4321) sequences.
     *
     * @param value the PIN string to check
     * @return true if sequential pattern exceeds allowed limit
     */
    private boolean hasSequentialPattern(String value) {
        int count = 1;
        int dir = 0;
        char[] chars = value.toCharArray();

        for (int i = 1; i < chars.length; i++) {
            char prev = chars[i - 1];
            char curr = chars[i];

            if (!Character.isDigit(prev) || !Character.isDigit(curr)) {
                count = 1;
                dir = 0;
                continue;
            }

            boolean stepAsc = ((curr - prev + 10) % 10) == 1;
            boolean stepDesc = ((prev - curr + 10) % 10) == 1;

            if (stepAsc || stepDesc) {
                int stepDir = stepAsc ? 1 : -1;

                if (dir == 0 || dir == stepDir) {
                    count++;
                } else {
                    count = 2;
                }
                dir = stepDir;

                if (count > maxAllowedSequential) {
                    return true;
                }
            } else {
                count = 1;
                dir = 0;
            }
        }

        return false;
    }
}
