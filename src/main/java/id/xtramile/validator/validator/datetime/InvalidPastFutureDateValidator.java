package id.xtramile.validator.validator.datetime;

import id.xtramile.validator.annotation.datetime.InvalidPastFutureDate;
import id.xtramile.validator.enums.Group;
import id.xtramile.validator.util.DateUtils;
import id.xtramile.validator.util.MessageUtils;
import id.xtramile.validator.util.ValidatorUtils;
import id.xtramile.validator.validator.datetime.support.DateToleranceEvaluator;
import id.xtramile.validator.validator.datetime.support.DateToleranceEvaluator.ParseResult;
import id.xtramile.validator.validator.datetime.support.DateToleranceEvaluator.ParsedTemporal;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static id.xtramile.validator.util.ValidatorUtils.isBlank;

/**
 * Validator implementation for {@link InvalidPastFutureDate} annotation.
 * <p>
 * Validates that a date string is within an acceptable past range and not in the future.
 * This validator ensures the date falls within the specified tolerance period (in hours)
 * while preventing future dates, commonly used for recent activity validation.
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
 * <li>Applies tolerance in hours for past validation and prevents future dates</li>
 * </ul>
 *
 * @see InvalidPastFutureDate
 */
public class InvalidPastFutureDateValidator implements ConstraintValidator<InvalidPastFutureDate, String> {
    private String pattern;
    private int toleranceHours;

    @Override
    public void initialize(InvalidPastFutureDate annotation) {
        this.pattern = annotation.pattern();
        this.toleranceHours = annotation.toleranceHours();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (isBlank(value)) {
            return true;
        }

        try {
            if (DateUtils.isISO8601Pattern(pattern)) {
                if (!ValidatorUtils.validateISO8601ForPastFutureDate(value, pattern, toleranceHours)) {
                    MessageUtils.buildViolation(context, Group.DATETIME, "invalid-past-future-date");
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
            MessageUtils.buildViolation(context, Group.DATETIME, "invalid-past-future-date.pattern", pattern);
            return false;
        }
    }

    private boolean validateDateTimeWithoutTimezone(String value, ConstraintValidatorContext context) {
        LocalDateTime[] parsed = new LocalDateTime[1];
        ParseResult result = DateToleranceEvaluator.parseLocalDateTimeStrictThenSmart(value, pattern, parsed);
        if (result == ParseResult.LEAP_YEAR_ERROR || result == ParseResult.PATTERN_ERROR) {
            MessageUtils.buildViolation(context, Group.DATETIME, "invalid-past-future-date.pattern", pattern);
            return false;
        }
        if (DateToleranceEvaluator.isOutsidePastFutureWindow(parsed[0], toleranceHours)) {
            MessageUtils.buildViolation(context, Group.DATETIME, "invalid-past-future-date.tolerance", toleranceHours);
            return false;
        }
        return true;
    }

    private boolean validateDateOnly(String value, ConstraintValidatorContext context) {
        LocalDate[] parsed = new LocalDate[1];
        ParseResult result = DateToleranceEvaluator.parseLocalDateStrictThenSmart(value, pattern, parsed);
        if (result == ParseResult.LEAP_YEAR_ERROR || result == ParseResult.PATTERN_ERROR) {
            MessageUtils.buildViolation(context, Group.DATETIME, "invalid-past-future-date.pattern", pattern);
            return false;
        }
        if (DateToleranceEvaluator.isOutsidePastFutureWindow(parsed[0], toleranceHours)) {
            MessageUtils.buildViolation(context, Group.DATETIME, "invalid-past-future-date.tolerance", toleranceHours);
            return false;
        }
        return true;
    }

    private boolean validateDefault(String value, ConstraintValidatorContext context) {
        ParsedTemporal[] parsed = new ParsedTemporal[1];
        ParseResult result = DateToleranceEvaluator.parseStrictThenSmart(value, pattern, parsed);
        if (result == ParseResult.LEAP_YEAR_ERROR || result == ParseResult.PATTERN_ERROR) {
            MessageUtils.buildViolation(context, Group.DATETIME, "invalid-past-future-date.pattern", pattern);
            return false;
        }
        if (DateToleranceEvaluator.isOutsidePastFutureWindow(parsed[0], toleranceHours)) {
            MessageUtils.buildViolation(context, Group.DATETIME, "invalid-past-future-date.tolerance", toleranceHours);
            return false;
        }
        return true;
    }
}
