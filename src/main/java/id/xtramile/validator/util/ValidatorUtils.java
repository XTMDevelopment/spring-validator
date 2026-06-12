package id.xtramile.validator.util;

import java.lang.reflect.Array;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.Map;

public class ValidatorUtils {
    private ValidatorUtils() {
    }

    public static boolean isBlank(String str) {
        return str == null || str.isBlank();
    }

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

    public static long mbToBytes(long mb) {
        return mb < 0 ? -1 : mb * 1024L * 1024L;
    }

    public static String formatFileSize(long bytes) {
        return formatFileSizeRecursive(bytes, 0);
    }

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

    public static boolean validateDateComponents(LocalDate date) {
        if (date.getMonthValue() == 2 && date.getDayOfMonth() == 29) {
            int year = date.getYear();
            if (!Year.isLeap(year)) {
                return false;
            }
        }

        return date.getDayOfMonth() <= getDaysInMonth(date.getYear(), date.getMonthValue());
    }

    public static boolean validateDateTimeComponents(LocalDateTime dateTime) {
        LocalDate date = dateTime.toLocalDate();
        return validateDateComponents(date);
    }

    public static int getDaysInMonth(int year, int month) {
        return switch (month) {
            case 1, 3, 5, 7, 8, 10, 12 -> 31;
            case 4, 6, 9, 11 -> 30;
            case 2 -> Year.isLeap(year) ? 29 : 28;
            default -> 0;
        };
    }

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
