package id.xtramile.validator.validator.datetime;

import id.xtramile.validator.annotation.datetime.ValidFutureDate;
import id.xtramile.validator.enums.Group;
import id.xtramile.validator.util.MessageUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;

import static id.xtramile.validator.util.ValidatorUtils.isBlank;
import static id.xtramile.validator.util.ValidatorUtils.validateLeapYear;

/**
 * Validator implementation for {@link ValidFutureDate} annotation.
 * <p>
 * Validates that a date string represents a date in the future.
 * This validator ensures the parsed date is strictly after the current date/time
 * with comprehensive leap year and date component validation.
 * 
 * <p>The validator performs the following operations:
 * <ul>
 * <li>Accepts null/blank values as valid</li>
 * <li>Validates leap year compatibility</li>
 * <li>Attempts parsing with DateTimeFormatter</li>
 * <li>Handles ZonedDateTime, LocalDateTime, and LocalDate types</li>
 * <li>Compares against current date/time for future validation</li>
 * </ul>
 * 
 * @see ValidFutureDate
 */
public class FutureDateValidator implements ConstraintValidator<ValidFutureDate, String> {
    private String pattern;

    /**
     * Initializes the validator with the annotation parameters.
     * @param annotation the ValidFutureDate annotation instance
     */
    @Override
    public void initialize(ValidFutureDate annotation) {
        this.pattern = annotation.pattern();
    }

    /**
     * Validates that the date is in the future.
     * @param value the date string to validate
     * @param context the constraint validator context
     * @return true if the date is in the future or is null/blank
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (isBlank(value)) return true;

        if (!validateLeapYear(value)) {
            MessageUtils.buildViolation(context, Group.DATETIME, "future-date.pattern", pattern);
            return false;
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);

            try {
                TemporalAccessor parsed = formatter.parseBest(
                        value,
                        ZonedDateTime::from,
                        LocalDateTime::from,
                        LocalDate::from
                );

                if (parsed instanceof ZonedDateTime zdt) {

                    if (zdt.getMonthValue() == 2 && zdt.getDayOfMonth() == 29) {
                        if (!Year.isLeap(zdt.getYear())) {
                            MessageUtils.buildViolation(context, Group.DATETIME, "future-date.pattern", pattern);
                            return false;
                        }
                    }

                    if (!zdt.isAfter(ZonedDateTime.now())) {
                        MessageUtils.buildViolation(context, Group.DATETIME, "future-date");
                        return false;
                    }
                    return true;

                } else if (parsed instanceof LocalDateTime ldt) {

                    if (ldt.getMonthValue() == 2 && ldt.getDayOfMonth() == 29) {
                        if (!Year.isLeap(ldt.getYear())) {
                            MessageUtils.buildViolation(context, Group.DATETIME, "future-date.pattern", pattern);
                            return false;
                        }
                    }

                    if (!ldt.isAfter(LocalDateTime.now())) {
                        MessageUtils.buildViolation(context, Group.DATETIME, "future-date");
                        return false;
                    }
                    return true;

                } else if (parsed instanceof LocalDate ld) {

                    if (ld.getMonthValue() == 2 && ld.getDayOfMonth() == 29) {
                        if (!Year.isLeap(ld.getYear())) {
                            MessageUtils.buildViolation(context, Group.DATETIME, "future-date.pattern", pattern);
                            return false;
                        }
                    }

                    if (!ld.isAfter(LocalDate.now())) {
                        MessageUtils.buildViolation(context, Group.DATETIME, "future-date");
                        return false;
                    }
                    return true;
                }

                MessageUtils.buildViolation(context, Group.DATETIME, "future-date.pattern", pattern);
                return false;

            } catch (DateTimeParseException e) {
                MessageUtils.buildViolation(context, Group.DATETIME, "future-date.pattern", pattern);
                return false;
            }
        } catch (DateTimeParseException e) {
            MessageUtils.buildViolation(context, Group.DATETIME, "future-date.pattern", pattern);
            return false;
        }
    }
}
