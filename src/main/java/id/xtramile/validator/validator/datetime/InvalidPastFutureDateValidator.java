package id.xtramile.validator.validator.datetime;

import id.xtramile.validator.annotation.datetime.InvalidPastFutureDate;
import id.xtramile.validator.enums.Group;
import id.xtramile.validator.util.DateUtils;
import id.xtramile.validator.util.MessageUtils;
import id.xtramile.validator.util.ValidatorUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.temporal.TemporalAccessor;

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
@SuppressWarnings("DuplicatedCode")
public class InvalidPastFutureDateValidator implements ConstraintValidator<InvalidPastFutureDate, String> {
    private String pattern;
    private int toleranceHours;

    /**
     * Initializes the validator with the annotation parameters.
     * @param annotation the InvalidPastFutureDate annotation instance
     */
    @Override
    public void initialize(InvalidPastFutureDate annotation) {
        this.pattern = annotation.pattern();
        this.toleranceHours = annotation.toleranceHours();
    }

    /**
     * Validates that the date is within acceptable past range and not in the future.
     * @param value the date string to validate
     * @param context the constraint validator context
     * @return true if the date is within acceptable range or is null/blank
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (isBlank(value)) return true;

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
    
    /**
     * Validates date-time without timezone (no conversion, just compare with current time).
     */
    private boolean validateDateTimeWithoutTimezone(String value, ConstraintValidatorContext context) {
        try {
            DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                    .appendPattern(pattern)
                    .toFormatter()
                    .withResolverStyle(ResolverStyle.STRICT);
            
            LocalDateTime inputLdt = LocalDateTime.parse(value, formatter);
            LocalDateTime now = LocalDateTime.now();

            LocalDateTime minAllowed = now.minusHours(toleranceHours);
            if (inputLdt.isBefore(minAllowed) || inputLdt.isAfter(now)) {
                MessageUtils.buildViolation(context, Group.DATETIME, "invalid-past-future-date.tolerance", toleranceHours);
                return false;
            }

            return true;
            
        } catch (DateTimeParseException e) {
            try {
                DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                        .appendPattern(pattern)
                        .toFormatter()
                        .withResolverStyle(ResolverStyle.SMART);
                
                LocalDateTime inputLdt = LocalDateTime.parse(value, formatter);

                if (inputLdt.getMonthValue() == 2 && inputLdt.getDayOfMonth() == 29) {
                    if (!Year.isLeap(inputLdt.getYear())) {
                        MessageUtils.buildViolation(context, Group.DATETIME, "invalid-past-future-date.pattern", pattern);
                        return false;
                    }
                }
                
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime minAllowed = now.minusHours(toleranceHours);
                if (inputLdt.isBefore(minAllowed) || inputLdt.isAfter(now)) {
                    MessageUtils.buildViolation(context, Group.DATETIME, "invalid-past-future-date.tolerance", toleranceHours);
                    return false;
                }

                return true;
                
            } catch (DateTimeParseException e2) {
                MessageUtils.buildViolation(context, Group.DATETIME, "invalid-past-future-date.pattern", pattern);
                return false;
            }
        }
    }
    
    /**
     * Validates date only (no time, just compare dates).
     */
    private boolean validateDateOnly(String value, ConstraintValidatorContext context) {
        try {
            DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                    .appendPattern(pattern)
                    .toFormatter()
                    .withResolverStyle(ResolverStyle.STRICT);
            
            LocalDate inputDate = LocalDate.parse(value, formatter);
            LocalDate today = LocalDate.now();

            LocalDate minAllowed = today.minusDays(toleranceHours / 24);
            if (inputDate.isBefore(minAllowed) || inputDate.isAfter(today)) {
                MessageUtils.buildViolation(context, Group.DATETIME, "invalid-past-future-date.tolerance", toleranceHours);
                return false;
            }

            return true;
            
        } catch (DateTimeParseException e) {
            try {
                DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                        .appendPattern(pattern)
                        .toFormatter()
                        .withResolverStyle(ResolverStyle.SMART);
                
                LocalDate inputDate = LocalDate.parse(value, formatter);

                if (inputDate.getMonthValue() == 2 && inputDate.getDayOfMonth() == 29) {
                    if (!Year.isLeap(inputDate.getYear())) {
                        MessageUtils.buildViolation(context, Group.DATETIME, "invalid-past-future-date.pattern", pattern);
                        return false;
                    }
                }
                
                LocalDate today = LocalDate.now();
                LocalDate minAllowed = today.minusDays(toleranceHours / 24);
                if (inputDate.isBefore(minAllowed) || inputDate.isAfter(today)) {
                    MessageUtils.buildViolation(context, Group.DATETIME, "invalid-past-future-date.tolerance", toleranceHours);
                    return false;
                }

                return true;
                
            } catch (DateTimeParseException e2) {
                MessageUtils.buildViolation(context, Group.DATETIME, "invalid-past-future-date.pattern", pattern);
                return false;
            }
        }
    }
    
    /**
     * Default validation behavior for other patterns.
     */
    private boolean validateDefault(String value, ConstraintValidatorContext context) {
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
                ZonedDateTime now = ZonedDateTime.now();
                ZonedDateTime minAllowed = now.minusHours(toleranceHours);

                if (zdt.isBefore(minAllowed) || zdt.isAfter(now)) {
                    MessageUtils.buildViolation(context, Group.DATETIME, "invalid-past-future-date.tolerance", toleranceHours);
                    return false;
                }

                return true;

            } else if (parsed instanceof LocalDateTime) {
                LocalDateTime ldt = (LocalDateTime) parsed;
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime minAllowed = now.minusHours(toleranceHours);

                if (ldt.isBefore(minAllowed) || ldt.isAfter(now)) {
                    MessageUtils.buildViolation(context, Group.DATETIME, "invalid-past-future-date.tolerance", toleranceHours);
                    return false;
                }

                return true;

            } else if (parsed instanceof LocalDate) {
                LocalDate ld = (LocalDate) parsed;
                LocalDate today = LocalDate.now();
                LocalDate minAllowed = today.minusDays(toleranceHours / 24);

                if (ld.isBefore(minAllowed) || ld.isAfter(today)) {
                    MessageUtils.buildViolation(context, Group.DATETIME, "invalid-past-future-date.tolerance", toleranceHours);
                    return false;
                }

                return true;
            }

            MessageUtils.buildViolation(context, Group.DATETIME, "invalid-past-future-date.pattern", pattern);
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
                        if (!Year.isLeap(zdt.getYear())) {
                            MessageUtils.buildViolation(context, Group.DATETIME, "invalid-past-future-date.pattern", pattern);
                            return false;
                        }
                    }

                    ZonedDateTime now = ZonedDateTime.now();
                    ZonedDateTime minAllowed = now.minusHours(toleranceHours);
                    if (zdt.isBefore(minAllowed) || zdt.isAfter(now)) {
                        MessageUtils.buildViolation(context, Group.DATETIME, "invalid-past-future-date.tolerance", toleranceHours);
                        return false;
                    }

                    return true;

                } else if (parsed instanceof LocalDateTime) {
                    LocalDateTime ldt = (LocalDateTime) parsed;
                    if (ldt.getMonthValue() == 2 && ldt.getDayOfMonth() == 29) {
                        if (!Year.isLeap(ldt.getYear())) {
                            MessageUtils.buildViolation(context, Group.DATETIME, "invalid-past-future-date.pattern", pattern);
                            return false;
                        }
                    }

                    LocalDateTime now = LocalDateTime.now();
                    LocalDateTime minAllowed = now.minusHours(toleranceHours);
                    if (ldt.isBefore(minAllowed) || ldt.isAfter(now)) {
                        MessageUtils.buildViolation(context, Group.DATETIME, "invalid-past-future-date.tolerance", toleranceHours);
                        return false;
                    }

                    return true;

                } else if (parsed instanceof LocalDate) {
                    LocalDate ld = (LocalDate) parsed;
                    if (ld.getMonthValue() == 2 && ld.getDayOfMonth() == 29) {
                        if (!Year.isLeap(ld.getYear())) {
                            MessageUtils.buildViolation(context, Group.DATETIME, "invalid-past-future-date.pattern", pattern);
                            return false;
                        }
                    }

                    LocalDate today = LocalDate.now();
                    LocalDate minAllowed = today.minusDays(toleranceHours / 24);
                    if (ld.isBefore(minAllowed) || ld.isAfter(today)) {
                        MessageUtils.buildViolation(context, Group.DATETIME, "invalid-past-future-date.tolerance", toleranceHours);
                        return false;
                    }

                    return true;
                }

                MessageUtils.buildViolation(context, Group.DATETIME, "invalid-past-future-date.pattern", pattern);
                return false;

            } catch (DateTimeParseException e2) {
                MessageUtils.buildViolation(context, Group.DATETIME, "invalid-past-future-date.pattern", pattern);
                return false;
            }
        }
    }
}
