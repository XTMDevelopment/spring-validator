package id.xtramile.validator.validator.common;

import id.xtramile.validator.annotation.common.ValidEnum;
import id.xtramile.validator.enums.Group;
import id.xtramile.validator.util.MessageUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static id.xtramile.validator.util.ValidatorUtils.isBlank;

/**
 * Validator implementation for {@link ValidEnum} annotation.
 * <p>
 * Validates that a string value matches one of the names of the provided enum class.
 * Supports case-sensitive and case-insensitive comparison modes.
 *
 * <p>The validator performs the following operations:
 * <ul>
 * <li>Extracts all enum constant names from the specified enum class</li>
 * <li>Applies case conversion if case-insensitive mode is enabled</li>
 * <li>Checks if the input value matches any of the enum names</li>
 * </ul>
 *
 * <p>Null/blank values are considered valid.
 *
 * @see ValidEnum
 */
public class EnumValueValidator implements ConstraintValidator<ValidEnum, String> {
    private Set<String> allowed;
    private boolean ignoreCase;

    /**
     * Initializes the validator with the annotation parameters.
     * @param annotation the ValidEnum annotation instance
     */
    @Override
    public void initialize(ValidEnum annotation) {
        this.ignoreCase = annotation.ignoreCase();
        this.allowed = Arrays.stream(annotation.enumClass().getEnumConstants())
                .map(e -> ignoreCase ? e.name().toLowerCase() : e.name())
                .collect(Collectors.toSet());
    }

    /**
     * Validates that the value matches one of the enum names.
     * @param value the string value to validate
     * @param context the constraint validator context
     * @return true if the value matches an enum name or is null/blank
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (isBlank(value)) return true;

        for (String allow : this.allowed) {
            if (ignoreCase && allow.equalsIgnoreCase(value)) {
                return true;
            } else if (allow.equals(value)) {
                return true;
            }
        }


        String key = ignoreCase ? "enum" : "enum.sensitive";
        MessageUtils.buildViolation(context, Group.COMMON, key, MessageUtils.join(allowed));

        return false;
    }
}
