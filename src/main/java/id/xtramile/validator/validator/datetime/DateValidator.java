package id.xtramile.validator.validator.datetime;

import id.xtramile.validator.annotation.datetime.ValidDate;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

import static id.xtramile.validator.util.ValidatorUtils.*;

/**
 * Validator implementation for {@link ValidDate} annotation.
 * <p>
 * Validates a date string using the provided pattern with strict and smart parsing.
 * This validator ensures the string represents a valid date according to the specified
 * format with comprehensive date component validation.
 * 
 * <p>The validator performs the following operations:
 * <ul>
 * <li>Accepts null/blank values as valid</li>
 * <li>Validates leap year compatibility</li>
 * <li>Checks day limits for specific months</li>
 * <li>Attempts strict parsing first, then smart parsing</li>
 * <li>Validates date components for logical consistency</li>
 * </ul>
 * 
 * @see ValidDate
 */
public class DateValidator implements ConstraintValidator<ValidDate, String> {
    private String pattern;

    /**
     * Initializes the validator with the annotation parameters.
     * @param annotation the ValidDate annotation instance
     */
    @Override
    public void initialize(ValidDate annotation) {
        this.pattern = annotation.pattern();
    }

    /**
     * Validates the date string against the configured pattern.
     * @param value the date string to validate
     * @param context the constraint validator context
     * @return true if the string is a valid date or is null/blank
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (isBlank(value)) return true;

        try {
            if (!validateLeapYear(value)) return false;

            if (pattern.equals("yyyy-MM-dd") && value.contains("-")) {
                String[] parts = value.split("-");
                if (parts.length >= 3) {
                    try {
                        int year = Integer.parseInt(parts[0]);
                        int month = Integer.parseInt(parts[1]);
                        int day = Integer.parseInt(parts[2]);

                        if (day > getDaysInMonth(year, month)) {
                            return false;
                        }

                    } catch (NumberFormatException ignore) {}
                }
            }

            try {
                DateTimeFormatter strictFormatter = new DateTimeFormatterBuilder()
                        .appendPattern(pattern)
                        .toFormatter()
                        .withResolverStyle(ResolverStyle.STRICT);

                LocalDate parsed = LocalDate.parse(value, strictFormatter);
                return validateDateComponents(parsed);

            } catch (DateTimeParseException e) {
                DateTimeFormatter smartFormatter = new DateTimeFormatterBuilder()
                        .appendPattern(pattern)
                        .toFormatter()
                        .withResolverStyle(ResolverStyle.SMART);

                LocalDate parsed = LocalDate.parse(value, smartFormatter);
                return validateDateComponents(parsed);
            }

        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
