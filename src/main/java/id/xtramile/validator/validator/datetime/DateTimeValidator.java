package id.xtramile.validator.validator.datetime;

import id.xtramile.validator.annotation.datetime.ValidDateTime;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

import static id.xtramile.validator.util.ValidatorUtils.*;

/**
 * Validator implementation for {@link ValidDateTime} annotation.
 * <p>
 * Validates a date-time string using the provided pattern with strict and smart parsing.
 * This validator ensures the string represents a valid date-time according to the specified
 * format with comprehensive date-time component validation.
 * 
 * <p>The validator performs the following operations:
 * <ul>
 * <li>Accepts null/blank values as valid</li>
 * <li>Validates leap year compatibility</li>
 * <li>Checks for invalid time values (24:xx, :60:xx, :60)</li>
 * <li>Validates whitespace formatting</li>
 * <li>Attempts strict parsing first, then smart parsing</li>
 * </ul>
 * 
 * @see ValidDateTime
 */
public class DateTimeValidator implements ConstraintValidator<ValidDateTime, String> {
    private String pattern;

    /**
     * Initializes the validator with the annotation parameters.
     * @param annotation the ValidDateTime annotation instance
     */
    @Override
    public void initialize(ValidDateTime annotation) {
        this.pattern = annotation.pattern();
    }

    /**
     * Validates the date-time string against the configured pattern.
     * @param value the date-time string to validate
     * @param context the constraint validator context
     * @return true if the string is a valid date-time or is null/blank
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (isBlank(value)) return true;

        try {
            if (value.contains("24:") || value.contains(":60:") || value.contains(":60")) {
                return false;
            }

            if (value.trim().length() != value.length() || value.contains("  ")) {
                return false;
            }

            if (!validateLeapYear(value)) return false;

            try {
                DateTimeFormatter strictFormatter = new DateTimeFormatterBuilder()
                        .appendPattern(pattern)
                        .toFormatter()
                        .withResolverStyle(ResolverStyle.STRICT);

                LocalDateTime parsed = LocalDateTime.parse(value, strictFormatter);
                return validateDateTimeComponents(parsed);

            } catch (DateTimeParseException e) {
                DateTimeFormatter smartFormatter = new DateTimeFormatterBuilder()
                        .appendPattern(pattern)
                        .toFormatter()
                        .withResolverStyle(ResolverStyle.SMART);

                LocalDateTime parsed = LocalDateTime.parse(value, smartFormatter);
                return validateDateTimeComponents(parsed);
            }

        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
