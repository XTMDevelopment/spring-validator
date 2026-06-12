package id.xtramile.validator.validator.data;

import id.xtramile.validator.annotation.data.ValidName;
import id.xtramile.validator.enums.Group;
import id.xtramile.validator.util.MessageUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static id.xtramile.validator.util.ValidatorUtils.isBlank;

/**
 * Validator implementation for {@link ValidName} annotation.
 * <p>
 * Validates a human name string containing only letters and configured allowed symbols.
 * This validator ensures the name follows the specified format with proper length constraints
 * and allowed symbol validation.
 *
 * <p>The validator performs the following operations:
 * <ul>
 * <li>Accepts null/blank values as valid</li>
 * <li>Validates length is between min and max</li>
 * <li>Allows only letters and configured symbols</li>
 * <li>Optionally allows digits when {@code allowDigits} is enabled</li>
 * <li>Properly escapes special regex characters in symbols</li>
 * </ul>
 *
 * @see ValidName
 */
public class NameValidator implements ConstraintValidator<ValidName, String> {
    private Set<String> allowedSymbols;
    private int min;
    private int max;
    private boolean allowDigits;

    /**
     * Initializes the validator with the annotation parameters.
     *
     * @param annotation the ValidName annotation instance
     */
    @Override
    public void initialize(ValidName annotation) {
        this.min = annotation.min();
        this.max = annotation.max();
        this.allowedSymbols = Stream.of(annotation.allowedSymbols())
                .map(s -> {
                    String t = s.trim();
                    if (t.isEmpty() && !s.isEmpty()) {
                        return s;
                    }
                    return t;
                })
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
        this.allowDigits = annotation.allowDigits();
    }

    /**
     * Validates the name string against the configured constraints.
     *
     * @param value   the name string to validate
     * @param context the constraint validator context
     * @return true if the name is valid or is null/blank
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (isBlank(value)) return true;

        String trimmed = value.trim();
        int length = trimmed.length();

        if (length < min) {
            MessageUtils.buildViolation(context, Group.DATA, "name.min", min);
            return false;
        }
        if (length > max) {
            MessageUtils.buildViolation(context, Group.DATA, "name.max", max);
            return false;
        }

        if (!allowDigits && containsDigits(trimmed)) {
            MessageUtils.buildViolation(context, Group.DATA, "name.digits");
            return false;
        }

        if (!matchesAllowedPattern(trimmed)) {
            MessageUtils.buildViolation(context, Group.DATA, "name.symbol", MessageUtils.join(allowedSymbols));
            return false;
        }

        return true;
    }

    /**
     * Checks if the string contains any digits.
     *
     * @param value the string to check
     * @return true if the string contains digits
     */
    private boolean containsDigits(String value) {
        return value.chars().anyMatch(Character::isDigit);
    }

    /**
     * Checks if the string matches the allowed pattern (letters, optionally digits, and allowed symbols).
     *
     * @param value the string to check
     * @return true if the string matches the allowed pattern
     */
    private boolean matchesAllowedPattern(String value) {
        StringBuilder sb = new StringBuilder();

        if (allowDigits) {
            sb.append("0-9 ");
        } else {
            sb.append(" ");
        }

        for (String symbol : allowedSymbols) {
            switch (symbol) {
                case "\\":
                    sb.append("\\\\");
                    break;
                case "^":
                    sb.append("\\^");
                    break;
                case "$":
                    sb.append("\\$");
                    break;
                case ".":
                    sb.append("\\.");
                    break;
                case "|":
                    sb.append("\\|");
                    break;
                case "?":
                    sb.append("\\?");
                    break;
                case "*":
                    sb.append("\\*");
                    break;
                case "+":
                    sb.append("\\+");
                    break;
                case "(":
                    sb.append("\\(");
                    break;
                case ")":
                    sb.append("\\)");
                    break;
                case "[":
                    sb.append("\\[");
                    break;
                case "]":
                    sb.append("\\]");
                    break;
                case "{":
                    sb.append("\\{");
                    break;
                case "}":
                    sb.append("\\}");
                    break;
                default:
                    sb.append(symbol);
            }
        }

        Pattern pattern = Pattern.compile("^[a-zA-Z" + sb + "]+$");
        return pattern.matcher(value).matches();
    }
}
