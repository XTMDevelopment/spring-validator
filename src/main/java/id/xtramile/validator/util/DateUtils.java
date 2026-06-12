package id.xtramile.validator.util;

import id.xtramile.validator.enums.DatePrecision;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

public class DateUtils {
    private DateUtils() {}

    public static LocalDateTime parseDateTime(String dateStr, String pattern) {
        try {
            if (dateStr.contains("24:") || dateStr.contains(":60:") || dateStr.contains(":60")) {
                return null;
            }

            if (dateStr.trim().length() != dateStr.length() || dateStr.contains("  ")) {
                return null;
            }

            if (!ValidatorUtils.validateLeapYear(dateStr)) {
                return null;
            }

            try {
                DateTimeFormatter strictFormatter = new DateTimeFormatterBuilder()
                        .appendPattern(pattern)
                        .toFormatter()
                        .withResolverStyle(ResolverStyle.STRICT);

                LocalDateTime parsed = LocalDateTime.parse(dateStr, strictFormatter);
                if (!ValidatorUtils.validateDateTimeComponents(parsed)) {
                    return null;
                }

                return parsed;

            } catch (DateTimeParseException e) {
                DateTimeFormatter smartFormatter = new DateTimeFormatterBuilder()
                        .appendPattern(pattern)
                        .toFormatter()
                        .withResolverStyle(ResolverStyle.SMART);

                LocalDateTime parsed = LocalDateTime.parse(dateStr, smartFormatter);
                if (!ValidatorUtils.validateDateTimeComponents(parsed)) {
                    return null;
                }

                return parsed;
            }

        } catch (DateTimeParseException e) {
            return null;
        }
    }

    public static long calculateDistance(LocalDateTime first, LocalDateTime second, DatePrecision precision) {
        Duration duration = Duration.between(first, second);

        return switch (precision) {
            case DAYS -> duration.toDays();
            case HOURS -> duration.toHours();
            case MINUTES -> duration.toMinutes();
            case SECONDS -> duration.toSeconds();
        };
    }

    public static boolean isISO8601Pattern(String pattern) {
        return pattern.contains("yyyy") && pattern.contains("MM") && pattern.contains("dd") &&
                (pattern.contains("'T'") || pattern.contains("T")) &&
                pattern.contains("HH") && pattern.contains("mm") && pattern.contains("ss") &&
                (pattern.contains("X") || pattern.contains("Z") || pattern.contains("'Z'"));
    }

    public static String pluralLabel(Enum<?> precision) {
        if (precision == null) {
            return "days";
        }

        String name = precision.name();
        String lower = name.toLowerCase();

        return lower.endsWith("s") ? lower : lower + "s";
    }
}
