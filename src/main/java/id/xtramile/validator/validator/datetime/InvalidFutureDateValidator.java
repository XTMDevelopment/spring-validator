package id.xtramile.validator.validator.datetime;

import id.xtramile.validator.annotation.datetime.InvalidFutureDate;
import id.xtramile.validator.enums.Group;
import id.xtramile.validator.util.DateUtils;
import id.xtramile.validator.util.MessageUtils;
import id.xtramile.validator.util.ValidatorUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;

import static id.xtramile.validator.util.ValidatorUtils.isBlank;

/**
 * Validator implementation for {@link InvalidFutureDate} annotation.
 * <p>
 * Validates that a date string is not in the future, except when allowed by the specified tolerance.
 * This validator ensures the parsed date is not after the current date/time plus tolerance,
 * commonly used for birth dates and historical data validation.
 * <p>
 * For ISO 8601 format (yyyy-MM-dd'T'HH:mm:ssX), input data is expected to be in UTC.
 * The validation converts the UTC input to the system timezone (which should be UTC+7)
 * and compares with the current time (which is already in the system timezone), applying tolerance in hours.
 * 
 * <p>The validator performs the following operations:
 * <ul>
 * <li>Accepts null/blank values as valid</li>
 * <li>Handles different patterns: ISO 8601 with timezone, date-time without timezone, and date only</li>
 * <li>For ISO 8601: parses UTC input, converts to system timezone, and compares with current time</li>
 * <li>For date-time without timezone: compares with current time (no conversion)</li>
 * <li>For date only: compares dates only</li>
 * <li>Applies tolerance in hours when specified</li>
 * </ul>
 * 
 * @see InvalidFutureDate
 */
public class InvalidFutureDateValidator implements ConstraintValidator<InvalidFutureDate, String> {
    private String pattern;
    private int toleranceHours;

    /**
     * Initializes the validator with the annotation parameters.
     * @param annotation the InvalidFutureDate annotation instance
     */
    @Override
    public void initialize(InvalidFutureDate annotation) {
        this.pattern = annotation.pattern();
        this.toleranceHours = annotation.toleranceHours();
    }

    /**
     * Validates that the date is not in the future (within tolerance).
     * @param value the date string to validate
     * @param context the constraint validator context
     * @return true if the date is not in the future (within tolerance) or is null/blank
     */
    @SuppressWarnings("DuplicatedCode")
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (isBlank(value)) return true;

        try {
            if (DateUtils.isISO8601Pattern(pattern)) {
                if (!ValidatorUtils.validateISO8601ForFutureDate(value, pattern, toleranceHours)) {
                    MessageUtils.buildViolation(context, Group.DATETIME, "invalid-future-date");
                    return false;
                }
                return true;
            }

            if (pattern.contains("HH:mm:ss") && !pattern.contains("X") && !pattern.contains("Z")) {
                return validateDateTimeWithoutTimezone(value, context);
            }

            if (pattern.matches(".*[yMd].*") && !pattern.contains("H") && !pattern.contains("h")) {
                return validateDateOnly(value, context);
            }

            return validateDefault(value, context);
            
        } catch (Exception e) {
            MessageUtils.buildViolation(context, Group.DATETIME, "invalid-future-date.pattern", pattern);
            return false;
        }
    }
    
    /**
     * Validates date-time without timezone (no conversion, just compare with current time).
     */
    private boolean validateDateTimeWithoutTimezone(String value, ConstraintValidatorContext context) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            LocalDateTime inputLdt = LocalDateTime.parse(value, formatter);
            LocalDateTime now = LocalDateTime.now();

            LocalDateTime maxAllowed = now.plusHours(toleranceHours);
            if (inputLdt.isAfter(maxAllowed)) {
                MessageUtils.buildViolation(context, Group.DATETIME, "invalid-future-date.tolerance", toleranceHours);
                return false;
            }

            return true;
            
        } catch (DateTimeParseException e) {
            MessageUtils.buildViolation(context, Group.DATETIME, "invalid-future-date.pattern", pattern);
            return false;
        }
    }
    
    /**
     * Validates date only (no time, just compare dates).
     */
    private boolean validateDateOnly(String value, ConstraintValidatorContext context) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            LocalDate inputDate = LocalDate.parse(value, formatter);
            LocalDate today = LocalDate.now();

            if (inputDate.isAfter(today)) {
                MessageUtils.buildViolation(context, Group.DATETIME, "invalid-future-date");
                return false;
            }

            return true;
            
        } catch (DateTimeParseException e) {
            MessageUtils.buildViolation(context, Group.DATETIME, "invalid-future-date.pattern", pattern);
            return false;
        }
    }
    
    /**
     * Default validation behavior for other patterns.
     */
    private boolean validateDefault(String value, ConstraintValidatorContext context) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            TemporalAccessor parsed = formatter.parseBest(
                    value,
                    ZonedDateTime::from,
                    LocalDateTime::from,
                    LocalDate::from
            );

            if (parsed instanceof ZonedDateTime) {
                ZonedDateTime zdt = (ZonedDateTime) parsed;
                ZonedDateTime now = ZonedDateTime.now();
                ZonedDateTime maxAllowed = now.plusHours(toleranceHours);

                if (zdt.isAfter(maxAllowed)) {
                    MessageUtils.buildViolation(context, Group.DATETIME, "invalid-future-date.tolerance", toleranceHours);
                    return false;
                }

                return true;

            } else if (parsed instanceof LocalDateTime) {
                LocalDateTime ldt = (LocalDateTime) parsed;
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime maxAllowed = now.plusHours(toleranceHours);

                if (ldt.isAfter(maxAllowed)) {
                    MessageUtils.buildViolation(context, Group.DATETIME, "invalid-future-date.tolerance", toleranceHours);
                    return false;
                }

                return true;

            } else if (parsed instanceof LocalDate) {
                LocalDate ld = (LocalDate) parsed;

                if (ld.isAfter(LocalDate.now())) {
                    MessageUtils.buildViolation(context, Group.DATETIME, "invalid-future-date");
                    return false;
                }

                return true;
            }

            MessageUtils.buildViolation(context, Group.DATETIME, "invalid-future-date.pattern", pattern);
            return false;

        } catch (DateTimeParseException e) {
            MessageUtils.buildViolation(context, Group.DATETIME, "invalid-future-date.pattern", pattern);
            return false;
        }
    }
}
