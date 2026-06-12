package id.xtramile.validator.validator.datetime;

import id.xtramile.validator.annotation.datetime.InvalidPastDate;
import id.xtramile.validator.enums.Group;
import id.xtramile.validator.util.MessageUtils;
import id.xtramile.validator.validator.datetime.support.DateToleranceEvaluator;
import id.xtramile.validator.validator.datetime.support.DateToleranceEvaluator.ParseResult;
import id.xtramile.validator.validator.datetime.support.DateToleranceEvaluator.ParsedTemporal;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import static id.xtramile.validator.util.ValidatorUtils.isBlank;
import static id.xtramile.validator.util.ValidatorUtils.validateLeapYear;

/**
 * Validator implementation for {@link InvalidPastDate} annotation.
 * <p>
 * Validates that a date string is not too far in the past based on tolerance.
 * This validator ensures the date is within the acceptable past range,
 * allowing for a configurable tolerance period.
 *
 * <p>The validator performs the following operations:
 * <ul>
 * <li>Accepts null/blank values as valid</li>
 * <li>Validates leap year compatibility</li>
 * <li>Attempts strict parsing first, then smart parsing</li>
 * <li>Handles ZonedDateTime, LocalDateTime, and LocalDate types</li>
 * <li>Applies tolerance-based past date validation</li>
 * </ul>
 *
 * @see InvalidPastDate
 */
public class InvalidPastDateValidator implements ConstraintValidator<InvalidPastDate, String> {
    private String pattern;
    private int tolerance;

    @Override
    public void initialize(InvalidPastDate annotation) {
        this.pattern = annotation.pattern();
        this.tolerance = annotation.tolerance();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (isBlank(value)) {
            return true;
        }

        if (!validateLeapYear(value)) {
            MessageUtils.buildViolation(context, Group.DATETIME, "invalid-past-date.pattern", pattern);
            return false;
        }

        ParsedTemporal[] parsed = new ParsedTemporal[1];
        ParseResult result = DateToleranceEvaluator.parseStrictThenSmart(value, pattern, parsed);
        if (result == ParseResult.LEAP_YEAR_ERROR || result == ParseResult.PATTERN_ERROR) {
            MessageUtils.buildViolation(context, Group.DATETIME, "invalid-past-date.pattern", pattern);
            return false;
        }

        if (DateToleranceEvaluator.isTooFarInPast(parsed[0], tolerance, true)) {
            MessageUtils.buildViolation(context, Group.DATETIME, "invalid-past-date.tolerance", tolerance);
            return false;
        }

        return true;
    }
}
