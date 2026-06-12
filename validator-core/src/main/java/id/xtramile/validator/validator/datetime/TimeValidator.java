package id.xtramile.validator.validator.datetime;

import id.xtramile.validator.annotation.datetime.ValidTime;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

import static id.xtramile.validator.util.ValidatorUtils.isBlank;

/**
 * Validator implementation for {@link ValidTime} annotation.
 * <p>
 * Validates a time string using the provided pattern with strict and smart parsing.
 * This validator ensures the string represents a valid time according to the specified
 * format with support for both 12-hour and 24-hour formats.
 *
 * <p>The validator performs the following operations:
 * <ul>
 * <li>Accepts null/blank values as valid</li>
 * <li>Validates 12-hour format with AM/PM indicators</li>
 * <li>Checks for invalid time values (24:xx, :60:xx, :60)</li>
 * <li>Attempts strict parsing first, then smart parsing</li>
 * <li>Validates hour and minute ranges appropriately</li>
 * </ul>
 *
 * @see ValidTime
 */
public class TimeValidator implements ConstraintValidator<ValidTime, String> {
    private String pattern;

    /**
     * Initializes the validator with the annotation parameters.
     *
     * @param annotation the ValidTime annotation instance
     */
    @Override
    public void initialize(ValidTime annotation) {
        this.pattern = annotation.pattern();
    }

    /**
     * Validates the time string against the configured pattern.
     *
     * @param value   the time string to validate
     * @param context the constraint validator context
     * @return true if the string is a valid time or is null/blank
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (isBlank(value)) return true;

        try {
            if (pattern.contains("h") && pattern.contains("a")) {
                return validateTwelveHourPattern(value);
            }

            if (value.contains("24:") || value.contains(":60:") || value.contains(":60")) {
                return false;
            }

            try {
                DateTimeFormatter strictFormatter = new DateTimeFormatterBuilder()
                        .appendPattern(pattern)
                        .toFormatter()
                        .withResolverStyle(ResolverStyle.STRICT);

                LocalTime ignore = LocalTime.parse(value, strictFormatter);
                return true;

            } catch (DateTimeParseException e) {
                DateTimeFormatter smartFormatter = new DateTimeFormatterBuilder()
                        .appendPattern(pattern)
                        .toFormatter()
                        .withResolverStyle(ResolverStyle.SMART);

                LocalTime ignore = LocalTime.parse(value, smartFormatter);
                return true;
            }

        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Validates 12-hour format time with AM/PM indicators.
     *
     * @param value the time string to validate
     * @return true if the time is valid in 12-hour format
     */
    private boolean validateTwelveHourPattern(String value) {
        try {
            String[] parts = value.split(" ");
            if (parts.length != 2) {
                return false;
            }

            String timePart = parts[0];
            String ampmPart = parts[1];

            if (!"AM".equals(ampmPart) && !"PM".equals(ampmPart)) {
                return false;
            }

            String[] timeComponents = timePart.split(":");
            if (timeComponents.length != 2) {
                return false;
            }

            int hour = Integer.parseInt(timeComponents[0]);
            int minute = Integer.parseInt(timeComponents[1]);

            if (hour < 1 || hour > 12) {
                return false;
            }

            return minute >= 0 && minute <= 59;

        } catch (NumberFormatException e) {
            return false;
        }
    }

}
