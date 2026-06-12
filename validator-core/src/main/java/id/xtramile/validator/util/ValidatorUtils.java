package id.xtramile.validator.util;

import java.lang.reflect.Array;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.Map;

/**
 * Shared validation helpers for string, collection, date, and file checks.
 */
public class ValidatorUtils {
    private ValidatorUtils() {
    }

    /**
     * Returns whether the string is null or blank.
     *
     * @param str the string to check
     * @return {@code true} when {@code str} is null or blank
     */
    public static boolean isBlank(String str) {
        return str == null || str.isBlank();
    }

    /**
     * Returns whether the value is non-null and non-empty (for strings, collections, maps, arrays).
     *
     * @param v the value to check
     * @return {@code true} when present and non-empty
     */
    public static boolean isPresent(Object v) {
        if (v == null) return false;

        if (v instanceof String) {
            return !((String) v).isBlank();
        }

        if (v instanceof Collection<?>) {
            return !((Collection<?>) v).isEmpty();
        }

        if (v instanceof Map<?, ?>) {
            return !((Map<?, ?>) v).isEmpty();
        }

        if (v.getClass().isArray()) {
            return Array.getLength(v) > 0;
        }

        return true;
    }

    /**
     * Converts megabytes to bytes.
     *
     * @param mb megabytes to convert
     * @return bytes, or {@code -1} for negative input
     */
    public static long mbToBytes(long mb) {
        return mb < 0 ? -1 : mb * 1024L * 1024L;
    }

    /**
     * Formats a byte count as a human-readable size string.
     *
     * @param bytes byte count to format
     * @return formatted size string
     */
    public static String formatFileSize(long bytes) {
        return formatFileSizeRecursive(bytes, 0);
    }

    /**
     * Returns whether a date string with Feb 29 falls in a leap year.
     *
     * @param date date string to inspect
     * @return {@code false} when Feb 29 is present in a non-leap year
     */
    public static boolean validateLeapYear(String date) {
        if (date == null) {
            return true;
        }

        if (date.contains("-02-29")) {
            String[] parts = date.split("-");

            if (parts.length >= 1) {
                try {
                    int year = Integer.parseInt(parts[0]);
                    if (!Year.isLeap(year)) {
                        return false;
                    }
                } catch (NumberFormatException ignore) {
                }
            }
        }

        return true;
    }

    /**
     * Validates day-of-month against month length and leap-year rules.
     *
     * @param date date to validate
     * @return {@code true} when day-of-month is valid for the month/year
     */
    public static boolean validateDateComponents(LocalDate date) {
        if (date.getMonthValue() == 2 && date.getDayOfMonth() == 29) {
            int year = date.getYear();
            if (!Year.isLeap(year)) {
                return false;
            }
        }

        return date.getDayOfMonth() <= getDaysInMonth(date.getYear(), date.getMonthValue());
    }

    /**
     * Validates date components of a {@link LocalDateTime}.
     *
     * @param dateTime date-time to validate
     * @return {@code true} when date components are valid
     */
    public static boolean validateDateTimeComponents(LocalDateTime dateTime) {
        LocalDate date = dateTime.toLocalDate();
        return validateDateComponents(date);
    }

    /**
     * Returns the number of days in the given month of the given year.
     *
     * @param year  calendar year
     * @param month month number (1–12)
     * @return days in the month, or {@code 0} for invalid month
     */
    public static int getDaysInMonth(int year, int month) {
        return switch (month) {
            case 1, 3, 5, 7, 8, 10, 12 -> 31;
            case 4, 6, 9, 11 -> 30;
            case 2 -> Year.isLeap(year) ? 29 : 28;
            default -> 0;
        };
    }

    /**
     * Performs basic structural email validation.
     *
     * @param email email address to validate
     * @return {@code true} when structurally valid
     */
    public static boolean validateEmail(String email) {
        if (!email.contains("@")) return false;

        String[] parts = email.split("@", 2);
        if (parts.length != 2) return false;

        String localPart = parts[0];
        String domain = parts[1];

        if (localPart.isEmpty() || domain.isEmpty()) return false;
        if (localPart.contains("..") || domain.contains("..")) return false;
        if (localPart.contains(" ") || domain.contains(" ")) return false;

        if (!domain.contains(".")) return false;
        if (domain.startsWith(".") || domain.endsWith(".")) return false;

        return !localPart.startsWith("-") && !localPart.endsWith("-") &&
                !localPart.startsWith(".") && !localPart.endsWith(".");
    }

    /**
     * Validates that an ISO-8601 date-time is not beyond now plus the tolerance.
     *
     * @param value          the date-time string
     * @param pattern        optional parse pattern
     * @param toleranceHours allowed hours into the future
     * @return {@code true} if within the allowed window
     */
    @SuppressWarnings("DuplicatedCode")
    public static boolean validateISO8601ForFutureDate(String value, String pattern, int toleranceHours) {
        try {
            ZonedDateTime zdt;

            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
                zdt = ZonedDateTime.parse(value, formatter);

            } catch (DateTimeParseException e) {
                zdt = ZonedDateTime.parse(value);
            }

            ZonedDateTime inputInSystemZone = zdt.withZoneSameInstant(ZoneId.systemDefault());
            ZonedDateTime now = ZonedDateTime.now();

            ZonedDateTime maxAllowed = now.plusHours(toleranceHours);
            return !inputInSystemZone.isAfter(maxAllowed);

        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Validates that an ISO-8601 date-time falls within the past tolerance window up to now.
     *
     * @param value          the date-time string
     * @param pattern        optional parse pattern
     * @param toleranceHours allowed hours into the past
     * @return {@code true} if within the allowed window
     */
    @SuppressWarnings("DuplicatedCode")
    public static boolean validateISO8601ForPastFutureDate(String value, String pattern, int toleranceHours) {
        try {
            ZonedDateTime zdt;

            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
                zdt = ZonedDateTime.parse(value, formatter);

            } catch (DateTimeParseException e) {
                zdt = ZonedDateTime.parse(value);
            }

            ZonedDateTime inputInSystemZone = zdt.withZoneSameInstant(ZoneId.systemDefault());
            ZonedDateTime now = ZonedDateTime.now();

            ZonedDateTime minAllowed = now.minusHours(toleranceHours);
            return !inputInSystemZone.isBefore(minAllowed) && !inputInSystemZone.isAfter(now);

        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private static String formatFileSizeRecursive(long bytes, int unitIndex) {
        if (unitIndex >= 4) {
            return bytes + (bytes == 1 ? " byte" : " bytes");
        }

        long divisor = (long) Math.pow(1024, 3 - unitIndex);
        String[] unitNames = {"gigabyte", "megabyte", "kilobyte", "byte"};

        if (bytes >= divisor) {
            long value = bytes / divisor;
            long remainder = bytes % divisor;

            if (remainder == 0) {
                String unit = unitNames[unitIndex];
                return value + " " + unit;
            }

            return formatFileSizeRecursive(bytes, unitIndex + 1);
        }
        return formatFileSizeRecursive(bytes, unitIndex + 1);
    }
}
