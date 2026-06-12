package id.xtramile.validator.validator.common;

import id.xtramile.validator.annotation.common.NotInBlacklist;
import id.xtramile.validator.enums.Group;
import id.xtramile.validator.util.MessageUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static id.xtramile.validator.util.ValidatorUtils.isBlank;

/**
 * Validator implementation for {@link NotInBlacklist} annotation.
 * <p>
 * Validates that a string value is not one of the provided blacklist entries.
 * Supports case-sensitive and case-insensitive comparison modes.
 *
 * <p>The validator performs the following operations:
 * <ul>
 * <li>Converts the blacklist values to a set for efficient lookup</li>
 * <li>Applies case conversion if case-insensitive mode is enabled</li>
 * <li>Checks if the input value is not in the blacklist</li>
 * </ul>
 *
 * <p>Null/blank values are considered valid.
 *
 * @see NotInBlacklist
 */
public class NotInBlacklistValidator implements ConstraintValidator<NotInBlacklist, String> {
    private Set<String> set;
    private boolean ignoreCase;

    /**
     * Initializes the validator with the annotation parameters.
     *
     * @param annotation the NotInBlacklist annotation instance
     */
    @Override
    public void initialize(NotInBlacklist annotation) {
        this.ignoreCase = annotation.ignoreCase();
        this.set = Stream.of(annotation.values())
                .map(s -> ignoreCase ? s.toLowerCase() : s)
                .collect(Collectors.toSet());
    }

    /**
     * Validates that the value is not in the blacklist.
     *
     * @param value   the string value to validate
     * @param context the constraint validator context
     * @return true if the value is not in the blacklist or is null/blank
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (isBlank(value)) return true;

        String key = ignoreCase ? value.toLowerCase() : value;
        if (!set.contains(key)) {
            return true;
        }

        String messageKey = ignoreCase ? "not-in-blacklist" : "not-in-blacklist.sensitive";
        MessageUtils.buildViolation(context, Group.COMMON, messageKey, MessageUtils.join(set));

        return false;
    }
}
