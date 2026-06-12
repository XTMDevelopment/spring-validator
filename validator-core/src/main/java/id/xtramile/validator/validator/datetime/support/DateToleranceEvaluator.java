package id.xtramile.validator.validator.datetime.support;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;

/**
 * Shared date parsing and tolerance checks for datetime validators.
 * <p>
 * Parses values with strict formatting first, then falls back to smart resolution.
 * Leap-year validation is applied on the smart path for February 29 values.
 */
public final class DateToleranceEvaluator {

    private DateToleranceEvaluator() {
    }

    /**
     * Parses a temporal value using strict then smart formatting.
     *
     * @param value   the input string
     * @param pattern the date-time pattern
     * @param out     single-element output holder for the parsed temporal
     * @return parse outcome
     */
    public static ParseResult parseStrictThenSmart(String value, String pattern, ParsedTemporal[] out) {
        try {
            TemporalAccessor parsed = strictFormatter(pattern).parseBest(
                    value,
                    ZonedDateTime::from,
                    LocalDateTime::from,
                    LocalDate::from
            );
            out[0] = ParsedTemporal.from(parsed);
            return out[0].isRecognized() ? ParseResult.SUCCESS : ParseResult.PATTERN_ERROR;
        } catch (DateTimeParseException firstFailure) {
            try {
                TemporalAccessor parsed = smartFormatter(pattern).parseBest(
                        value,
                        ZonedDateTime::from,
                        LocalDateTime::from,
                        LocalDate::from
                );
                ParseResult leapYearResult = validateFebruary29LeapYear(parsed);
                if (leapYearResult != ParseResult.SUCCESS) {
                    return leapYearResult;
                }
                out[0] = ParsedTemporal.from(parsed);
                return out[0].isRecognized() ? ParseResult.SUCCESS : ParseResult.PATTERN_ERROR;
            } catch (DateTimeParseException secondFailure) {
                return ParseResult.PATTERN_ERROR;
            }
        }
    }

    /**
     * Parses a {@link LocalDateTime} using strict then smart formatting.
     *
     * @param value   the input string
     * @param pattern the date-time pattern
     * @param out     single-element output holder
     * @return parse outcome
     */
    public static ParseResult parseLocalDateTimeStrictThenSmart(String value, String pattern, LocalDateTime[] out) {
        try {
            out[0] = LocalDateTime.parse(value, strictFormatter(pattern));
            return ParseResult.SUCCESS;
        } catch (DateTimeParseException firstFailure) {
            try {
                LocalDateTime parsed = LocalDateTime.parse(value, smartFormatter(pattern));
                if (parsed.getMonthValue() == 2 && parsed.getDayOfMonth() == 29 && !Year.isLeap(parsed.getYear())) {
                    return ParseResult.LEAP_YEAR_ERROR;
                }
                out[0] = parsed;
                return ParseResult.SUCCESS;
            } catch (DateTimeParseException secondFailure) {
                return ParseResult.PATTERN_ERROR;
            }
        }
    }

    /**
     * Parses a {@link LocalDate} using strict then smart formatting.
     *
     * @param value   the input string
     * @param pattern the date pattern
     * @param out     single-element output holder
     * @return parse outcome
     */
    public static ParseResult parseLocalDateStrictThenSmart(String value, String pattern, LocalDate[] out) {
        try {
            out[0] = LocalDate.parse(value, strictFormatter(pattern));
            return ParseResult.SUCCESS;
        } catch (DateTimeParseException firstFailure) {
            try {
                LocalDate parsed = LocalDate.parse(value, smartFormatter(pattern));
                if (parsed.getMonthValue() == 2 && parsed.getDayOfMonth() == 29 && !Year.isLeap(parsed.getYear())) {
                    return ParseResult.LEAP_YEAR_ERROR;
                }
                out[0] = parsed;
                return ParseResult.SUCCESS;
            } catch (DateTimeParseException secondFailure) {
                return ParseResult.PATTERN_ERROR;
            }
        }
    }

    /**
     * Returns whether the parsed value is older than the allowed past tolerance.
     *
     * @param parsed            parsed temporal value
     * @param toleranceDays     allowed days in the past
     * @param truncateToSeconds whether to truncate current time to seconds
     * @return {@code true} when the value is too far in the past
     */
    public static boolean isTooFarInPast(ParsedTemporal parsed, int toleranceDays, boolean truncateToSeconds) {
        if (parsed.zonedDateTime != null) {
            ZonedDateTime now = truncateToSeconds
                    ? ZonedDateTime.now().truncatedTo(ChronoUnit.SECONDS)
                    : ZonedDateTime.now();
            return parsed.zonedDateTime.isBefore(now.minusDays(toleranceDays));
        }
        if (parsed.localDateTime != null) {
            LocalDateTime now = truncateToSeconds
                    ? LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
                    : LocalDateTime.now();
            return parsed.localDateTime.isBefore(now.minusDays(toleranceDays));
        }
        if (parsed.localDate != null) {
            return parsed.localDate.isBefore(LocalDate.now().minusDays(toleranceDays));
        }
        return true;
    }

    /**
     * Returns whether the parsed value is strictly before the current instant or date.
     *
     * @param parsed parsed temporal value
     * @return {@code true} when before now
     */
    public static boolean isStrictlyBeforeNow(ParsedTemporal parsed) {
        if (parsed.zonedDateTime != null) {
            return parsed.zonedDateTime.isBefore(ZonedDateTime.now());
        }
        if (parsed.localDateTime != null) {
            return parsed.localDateTime.isBefore(LocalDateTime.now());
        }
        if (parsed.localDate != null) {
            return parsed.localDate.isBefore(LocalDate.now());
        }
        return false;
    }

    /**
     * Returns whether a parsed temporal falls outside the past tolerance window up to now.
     *
     * @param parsed         parsed temporal value
     * @param toleranceHours allowed hours in the past
     * @return {@code true} when outside the allowed window
     */
    public static boolean isOutsidePastFutureWindow(ParsedTemporal parsed, int toleranceHours) {
        if (parsed.zonedDateTime != null) {
            ZonedDateTime now = ZonedDateTime.now();
            ZonedDateTime minAllowed = now.minusHours(toleranceHours);
            return parsed.zonedDateTime.isBefore(minAllowed) || parsed.zonedDateTime.isAfter(now);
        }
        if (parsed.localDateTime != null) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime minAllowed = now.minusHours(toleranceHours);
            return parsed.localDateTime.isBefore(minAllowed) || parsed.localDateTime.isAfter(now);
        }
        if (parsed.localDate != null) {
            LocalDate today = LocalDate.now();
            LocalDate minAllowed = today.minusDays(toleranceHours / 24);
            return parsed.localDate.isBefore(minAllowed) || parsed.localDate.isAfter(today);
        }
        return true;
    }

    /**
     * Returns whether a {@link LocalDateTime} falls outside the past tolerance window up to now.
     *
     * @param dateTime       value to check
     * @param toleranceHours allowed hours in the past
     * @return {@code true} when outside the allowed window
     */
    public static boolean isOutsidePastFutureWindow(LocalDateTime dateTime, int toleranceHours) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime minAllowed = now.minusHours(toleranceHours);
        return dateTime.isBefore(minAllowed) || dateTime.isAfter(now);
    }

    /**
     * Returns whether a {@link LocalDate} falls outside the past tolerance window up to today.
     *
     * @param date           value to check
     * @param toleranceHours allowed hours in the past (converted to whole days)
     * @return {@code true} when outside the allowed window
     */
    public static boolean isOutsidePastFutureWindow(LocalDate date, int toleranceHours) {
        LocalDate today = LocalDate.now();
        LocalDate minAllowed = today.minusDays(toleranceHours / 24);
        return date.isBefore(minAllowed) || date.isAfter(today);
    }

    private static DateTimeFormatter strictFormatter(String pattern) {
        return new DateTimeFormatterBuilder()
                .appendPattern(pattern)
                .toFormatter()
                .withResolverStyle(ResolverStyle.STRICT);
    }

    private static DateTimeFormatter smartFormatter(String pattern) {
        return new DateTimeFormatterBuilder()
                .appendPattern(pattern)
                .toFormatter()
                .withResolverStyle(ResolverStyle.SMART);
    }

    private static ParseResult validateFebruary29LeapYear(TemporalAccessor parsed) {
        if (parsed instanceof ZonedDateTime zdt) {
            if (zdt.getMonthValue() == 2 && zdt.getDayOfMonth() == 29 && !Year.isLeap(zdt.getYear())) {
                return ParseResult.LEAP_YEAR_ERROR;
            }
        } else if (parsed instanceof LocalDateTime ldt) {
            if (ldt.getMonthValue() == 2 && ldt.getDayOfMonth() == 29 && !Year.isLeap(ldt.getYear())) {
                return ParseResult.LEAP_YEAR_ERROR;
            }
        } else if (parsed instanceof LocalDate ld) {
            if (ld.getMonthValue() == 2 && ld.getDayOfMonth() == 29 && !Year.isLeap(ld.getYear())) {
                return ParseResult.LEAP_YEAR_ERROR;
            }
        }
        return ParseResult.SUCCESS;
    }

    /**
     * Outcome of a parse attempt.
     */
    public enum ParseResult {
        /**
         * Parsing succeeded.
         */
        SUCCESS,
        /**
         * The value does not match the pattern.
         */
        PATTERN_ERROR,
        /**
         * February 29 is not valid for the parsed year.
         */
        LEAP_YEAR_ERROR
    }

    /**
     * Holder for one of the supported parsed temporal types.
     */
    public static final class ParsedTemporal {
        private final ZonedDateTime zonedDateTime;
        private final LocalDateTime localDateTime;
        private final LocalDate localDate;

        private ParsedTemporal(ZonedDateTime zonedDateTime, LocalDateTime localDateTime, LocalDate localDate) {
            this.zonedDateTime = zonedDateTime;
            this.localDateTime = localDateTime;
            this.localDate = localDate;
        }

        /**
         * Wraps a parsed {@link TemporalAccessor} into a {@link ParsedTemporal}.
         *
         * @param parsed parsed accessor
         * @return wrapped temporal holder
         */
        public static ParsedTemporal from(TemporalAccessor parsed) {
            if (parsed instanceof ZonedDateTime) {
                return new ParsedTemporal((ZonedDateTime) parsed, null, null);
            }
            if (parsed instanceof LocalDateTime) {
                return new ParsedTemporal(null, (LocalDateTime) parsed, null);
            }
            if (parsed instanceof LocalDate) {
                return new ParsedTemporal(null, null, (LocalDate) parsed);
            }
            return new ParsedTemporal(null, null, null);
        }

        /**
         * Returns whether a supported temporal type was parsed.
         *
         * @return {@code true} when a temporal value is present
         */
        public boolean isRecognized() {
            return zonedDateTime != null || localDateTime != null || localDate != null;
        }
    }
}
