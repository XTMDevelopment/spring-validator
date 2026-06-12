package id.xtramile.validator.validator.common;

import id.xtramile.validator.annotation.common.InWhitelist;
import id.xtramile.validator.enums.Group;
import id.xtramile.validator.util.MessageUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static id.xtramile.validator.util.ValidatorUtils.isBlank;

/**
 * Validator implementation for {@link InWhitelist} annotation.
 * <p>
 * Validates that a string value is one of the provided whitelist entries.
 * Supports case-sensitive and case-insensitive comparison modes.
 * 
 * <p>The validator performs the following operations:
 * <ul>
 * <li>Converts the whitelist values to a set for efficient lookup</li>
 * <li>Applies case conversion if case-insensitive mode is enabled</li>
 * <li>Checks if the input value exists in the whitelist</li>
 * </ul>
 * 
 * <p>Null/blank values are considered valid.
 * 
 * @see InWhitelist
 */
public class InWhitelistValidator implements ConstraintValidator<InWhitelist, String> {
    private Set<String> set;
    private boolean ignoreCase;

    /**
     * Initializes the validator with the annotation parameters.
     * @param annotation the InWhitelist annotation instance
     */
    @Override
    public void initialize(InWhitelist annotation) {
        this.ignoreCase = annotation.ignoreCase();
        this.set = Stream.of(annotation.values())
                .map(s -> ignoreCase ? s.toLowerCase() : s)
                .collect(Collectors.toSet());
    }

    /**
     * Validates that the value is in the whitelist.
     * @param value the string value to validate
     * @param context the constraint validator context
     * @return true if the value is in the whitelist or is null/blank
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
         if (isBlank(value)) return true;

         String key = ignoreCase ? value.toLowerCase() : value;
         if (set.contains(key)) {
             return true;
         }

         String messageKey = ignoreCase ? "in-whitelist" : "in-whitelist.sensitive";
         MessageUtils.buildViolation(context, Group.COMMON, messageKey, MessageUtils.join(set));

         return false;
    }
}
