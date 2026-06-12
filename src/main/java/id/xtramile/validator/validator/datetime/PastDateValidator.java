package id.xtramile.validator.validator.datetime;

import id.xtramile.validator.annotation.datetime.ValidPastDate;
import id.xtramile.validator.enums.Group;
import id.xtramile.validator.util.MessageUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.temporal.TemporalAccessor;

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

    /**
     * Initializes the validator with the annotation parameters.
     * @param annotation the ValidPastDate annotation instance
     */
    @Override
    public void initialize(ValidPastDate annotation) {
        this.pattern = annotation.pattern();
    }

    /**
     * Validates that the date is in the past.
     * @param value the date string to validate
     * @param context the constraint validator context
     * @return true if the date is in the past or is null/blank
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (isBlank(value)) return true;

        if (!validateLeapYear(value)) {
            MessageUtils.buildViolation(context, Group.DATETIME, "past-date.pattern", pattern);
            return false;
        }

        try {
            DateTimeFormatter strictFormatter = new DateTimeFormatterBuilder()
                    .appendPattern(pattern)
                    .toFormatter()
                    .withResolverStyle(ResolverStyle.STRICT);

            TemporalAccessor parsed = strictFormatter.parseBest(
                    value,
                    ZonedDateTime::from,
                    LocalDateTime::from,
                    LocalDate::from
            );

            if (parsed instanceof ZonedDateTime) {
                ZonedDateTime zdt = (ZonedDateTime) parsed;
                if (!zdt.isBefore(ZonedDateTime.now())) {
                    MessageUtils.buildViolation(context, Group.DATETIME, "past-date");
                    return false;
                }
                return true;

            } else if (parsed instanceof LocalDateTime) {
                LocalDateTime ldt = (LocalDateTime) parsed;
                if (!ldt.isBefore(LocalDateTime.now())) {
                    MessageUtils.buildViolation(context, Group.DATETIME, "past-date");
                    return false;
                }
                return true;

            } else if (parsed instanceof LocalDate) {
                LocalDate ld = (LocalDate) parsed;
                if (!ld.isBefore(LocalDate.now())) {
                    MessageUtils.buildViolation(context, Group.DATETIME, "past-date");
                    return false;
                }
                return true;
            }

            MessageUtils.buildViolation(context, Group.DATETIME, "past-date.pattern", pattern);
            return false;
        } catch (DateTimeParseException e) {
            try {
                DateTimeFormatter smartFormatter = new DateTimeFormatterBuilder()
                        .appendPattern(pattern)
                        .toFormatter()
                        .withResolverStyle(ResolverStyle.SMART);

                TemporalAccessor parsed = smartFormatter.parseBest(
                        value,
                        ZonedDateTime::from,
                        LocalDateTime::from,
                        LocalDate::from
                );

                if (parsed instanceof ZonedDateTime) {
                    ZonedDateTime zdt = (ZonedDateTime) parsed;
                    if (zdt.getMonthValue() == 2 && zdt.getDayOfMonth() == 29) {
                        if (!java.time.Year.isLeap(zdt.getYear())) {
                            MessageUtils.buildViolation(context, Group.DATETIME, "past-date.pattern", pattern);
                            return false;
                        }
                    }

                    if (!zdt.isBefore(ZonedDateTime.now())) {
                        MessageUtils.buildViolation(context, Group.DATETIME, "past-date");
                        return false;
                    }
                    return true;

                } else if (parsed instanceof LocalDateTime) {
                    LocalDateTime ldt = (LocalDateTime) parsed;
                    if (ldt.getMonthValue() == 2 && ldt.getDayOfMonth() == 29) {
                        if (!java.time.Year.isLeap(ldt.getYear())) {
                            MessageUtils.buildViolation(context, Group.DATETIME, "past-date.pattern", pattern);
                            return false;
                        }
                    }

                    if (!ldt.isBefore(LocalDateTime.now())) {
                        MessageUtils.buildViolation(context, Group.DATETIME, "past-date");
                        return false;
                    }
                    return true;

                } else if (parsed instanceof LocalDate) {
                    LocalDate ld = (LocalDate) parsed;
                    if (ld.getMonthValue() == 2 && ld.getDayOfMonth() == 29) {
                        if (!java.time.Year.isLeap(ld.getYear())) {
                            MessageUtils.buildViolation(context, Group.DATETIME, "past-date.pattern", pattern);
                            return false;
                        }
                    }

                    if (!ld.isBefore(LocalDate.now())) {
                        MessageUtils.buildViolation(context, Group.DATETIME, "past-date");
                        return false;
                    }
                    return true;
                }

                MessageUtils.buildViolation(context, Group.DATETIME, "past-date.pattern", pattern);
                return false;
            } catch (DateTimeParseException e2) {
                MessageUtils.buildViolation(context, Group.DATETIME, "past-date.pattern", pattern);
                return false;
            }
        }
    }
}
