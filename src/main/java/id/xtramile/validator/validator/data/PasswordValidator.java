package id.xtramile.validator.validator.data;

import id.xtramile.validator.annotation.data.ValidPassword;
import id.xtramile.validator.enums.Group;
import id.xtramile.validator.enums.PasswordType;
import id.xtramile.validator.util.MessageUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

/**
 * Validator implementation for {@link ValidPassword} annotation.
 * <p>
 * Validates password strength according to configurable policies.
 * Rejects any whitespace and enforces minimum length requirements.
 * Supports various password strength types from basic to enterprise-level security.
 * 
 * <p>The validator performs the following checks:
 * <ul>
 * <li>Rejects passwords containing whitespace</li>
 * <li>Enforces minimum length using Unicode-aware character counting</li>
 * <li>Applies strength policy based on the configured type</li>
 * </ul>
 * 
 * <p>Null/empty values are considered valid.
 * 
 * @see ValidPassword
 * @see PasswordType
 */
public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {
    private int min;
    private PasswordType type;

    private static final Pattern WHITESPACE = Pattern.compile(" ");
    private static final Pattern ONLY_ALNUM = Pattern.compile("^[a-zA-Z0-9]+$");
    private static final Pattern HAS_LOWER = Pattern.compile(".*[a-z].*");
    private static final Pattern HAS_UPPER = Pattern.compile(".*[A-Z].*");
    private static final Pattern HAS_LETTER = Pattern.compile(".*[A-Za-z].*");
    private static final Pattern HAS_DIGIT = Pattern.compile(".*\\d.*");
    private static final Pattern HAS_SYMBOL = Pattern.compile(".*[^A-Za-z\\d].*");

    /**
     * Initializes the validator with the annotation parameters.
     * @param ann the ValidPassword annotation instance
     */
    @Override
    public void initialize(ValidPassword ann) {
        this.min = ann.min();
        this.type = ann.type();
    }

    /**
     * Validates the password against the configured strength policy.
     * @param value the password string to validate
     * @param context the constraint validator context
     * @return true if the password meets all requirements
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) return true;

        if (WHITESPACE.matcher(value).find()) {
            MessageUtils.buildViolation(context, Group.DATA, "password");
            return false;
        }
        if (value.codePointCount(0, value.length()) < min) {
            MessageUtils.buildViolation(context, Group.DATA, "password");
            return false;
        }

        boolean isValid;
        String messageKey = switch (type) {
            case ANY -> {
                isValid = true;
                yield "password";
            }
            case ALPHANUMERIC -> {
                isValid = ONLY_ALNUM.matcher(value).matches();
                yield "password.alphanumeric";
            }
            case LETTER_DIGIT -> {
                isValid = HAS_LETTER.matcher(value).matches()
                        && HAS_DIGIT.matcher(value).matches();
                yield "password.letter-digit";
            }
            case LETTER_MIXED_CASE -> {
                isValid = HAS_LOWER.matcher(value).matches()
                        && HAS_UPPER.matcher(value).matches();
                yield "password.letter-mixed";
            }
            case FULL -> {
                isValid = HAS_LOWER.matcher(value).matches()
                        && HAS_UPPER.matcher(value).matches()
                        && HAS_DIGIT.matcher(value).matches()
                        && HAS_SYMBOL.matcher(value).matches();
                yield "password.full";
            }
            case STRONG_3_OF_4 -> {
                isValid = countSatisfied(
                        HAS_LOWER.matcher(value).matches(),
                        HAS_UPPER.matcher(value).matches(),
                        HAS_DIGIT.matcher(value).matches(),
                        HAS_SYMBOL.matcher(value).matches()
                ) >= 3;
                yield "password.full";
            }
            default -> {
                isValid = false;
                yield "password";
            }
        };


        if (!isValid) {
            MessageUtils.buildViolation(context, Group.DATA, messageKey);
        }

        return isValid;
    }

    /**
     * Counts how many of the provided boolean flags are true.
     * @param flags the boolean flags to count
     * @return the number of true flags
     */
    private static int countSatisfied(boolean... flags) {
        int count = 0;
        for (boolean f : flags) if (f) count++;
        return count;
    }
}
