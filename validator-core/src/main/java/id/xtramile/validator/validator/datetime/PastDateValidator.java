package id.xtramile.validator.validator.datetime;

import id.xtramile.validator.annotation.datetime.ValidPastDate;
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
 * Validator implementation for {@link ValidPastDate} annotation.
 * <p>
 * Validates that a date string represents a date in the past.
 * This validator ensures the parsed date is strictly before the current date/time
 * with comprehensive leap year and date component validation.
 *
 * <p>The validator performs the following operations:
 * <ul>
 * <li>Accepts null/blank values as valid</li>
 * <li>Validates leap year compatibility</li>
 * <li>Attempts strict parsing first, then smart parsing</li>
 * <li>Handles ZonedDateTime, LocalDateTime, and LocalDate types</li>
 * <li>Compares against current date/time for past validation</li>
 * </ul>
 *
 * @see ValidPastDate
 */
public class PastDateValidator implements ConstraintValidator<ValidPastDate, String> {
    private String pattern;

    @Override
    public void initialize(ValidPastDate annotation) {
        this.pattern = annotation.pattern();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (isBlank(value)) {
            return true;
        }

        if (!validateLeapYear(value)) {
            MessageUtils.buildViolation(context, Group.DATETIME, "past-date.pattern", pattern);
            return false;
        }

        ParsedTemporal[] parsed = new ParsedTemporal[1];
        ParseResult result = DateToleranceEvaluator.parseStrictThenSmart(value, pattern, parsed);
        if (result == ParseResult.LEAP_YEAR_ERROR || result == ParseResult.PATTERN_ERROR) {
            MessageUtils.buildViolation(context, Group.DATETIME, "past-date.pattern", pattern);
            return false;
        }

        if (!DateToleranceEvaluator.isStrictlyBeforeNow(parsed[0])) {
            MessageUtils.buildViolation(context, Group.DATETIME, "past-date");
            return false;
        }

        return true;
    }
}
