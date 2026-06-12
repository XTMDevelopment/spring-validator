package id.xtramile.validator.validator.datetime;

import id.xtramile.validator.annotation.datetime.ValidISO8601;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.OffsetDateTime;

import static id.xtramile.validator.util.ValidatorUtils.isBlank;

/**
 * Validator implementation for {@link ValidISO8601} annotation.
 * <p>
 * Validates an ISO 8601 date-time string using OffsetDateTime parsing.
 * This validator ensures the string follows the ISO 8601 standard format
 * with proper timezone handling and format validation.
 *
 * <p>The validator performs the following operations:
 * <ul>
 * <li>Accepts null/blank values as valid</li>
 * <li>Validates timezone format for +/- indicators</li>
 * <li>Attempts to parse using OffsetDateTime</li>
 * <li>Handles parsing exceptions gracefully</li>
 * </ul>
 *
 * @see ValidISO8601
 */
public class ISO8601Validator implements ConstraintValidator<ValidISO8601, String> {

    /**
     * Validates the ISO 8601 date-time string.
     *
     * @param value   the ISO 8601 string to validate
     * @param context the constraint validator context
     * @return true if the string is valid ISO 8601 or is null/blank
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (isBlank(value)) return true;

        try {
            if (value.contains("+") || value.contains("-")) {
                if (isInvalidTimezoneFormat(value)) {
                    return false;
                }
            }

            OffsetDateTime.parse(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Checks if the timezone format is invalid.
     *
     * @param value the string to check
     * @return true if the timezone format is invalid
     */
    private boolean isInvalidTimezoneFormat(String value) {
        if (value.contains("+")) {
            String[] parts = value.split("\\+");
            if (parts.length == 2) {
                String timezonePart = parts[1];

                if (!timezonePart.contains(":") && timezonePart.matches("\\d{1,2}")) {
                    return true;
                }
            }
        }

        if (value.contains("-")) {
            String[] parts = value.split("-");
            if (parts.length >= 2) {
                String lastPart = parts[parts.length - 1];
                return lastPart.matches("\\d{1,2}$");
            }
        }

        return false;
    }
}
