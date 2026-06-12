package id.xtramile.validator.validator.finance;

import id.xtramile.validator.annotation.finance.ValidCardExpiry;
import id.xtramile.validator.enums.Group;
import id.xtramile.validator.util.MessageUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.YearMonth;
import java.time.ZoneId;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static id.xtramile.validator.util.ValidatorUtils.isBlank;

/**
 * Validator implementation for {@link ValidCardExpiry} annotation.
 * <p>
 * Validates card expiry dates in MM/YY or MM/YYYY format.
 * This validator ensures the expiry date follows the correct format
 * and optionally checks if the card is not expired.
 *
 * <p>The validator performs the following operations:
 * <ul>
 * <li>Accepts null/blank values as valid</li>
 * <li>Validates MM/YY or MM/YYYY format</li>
 * <li>Handles both 2-digit and 4-digit years</li>
 * <li>Optionally checks if card is not expired</li>
 * <li>Uses regex pattern matching for format validation</li>
 * </ul>
 *
 * @see ValidCardExpiry
 */
public class CardExpiryValidator implements ConstraintValidator<ValidCardExpiry, String> {
    private static final Pattern REGEX = Pattern.compile("^(0[1-9]|1[0-2])\\s*/\\s*(\\d{2}|\\d{4})$");
    private boolean mustBeFuture;

    /**
     * Initializes the validator with the annotation parameters.
     *
     * @param annotation the ValidCardExpiry annotation instance
     */
    @Override
    public void initialize(ValidCardExpiry annotation) {
        this.mustBeFuture = annotation.mustBeFuture();
    }

    /**
     * Validates the card expiry date format and optional future check.
     *
     * @param value   the expiry date string to validate
     * @param context the constraint validator context
     * @return true if the expiry date is valid or is null/blank
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (isBlank(value)) return true;

        Matcher matcher = REGEX.matcher(value.trim());
        if (!matcher.matches()) {
            MessageUtils.buildViolation(context, Group.FINANCE, "card-expiry");
            return false;
        }

        int month = Integer.parseInt(matcher.group(1));
        String y = matcher.group(2);
        int year = (y.length() == 2)
                ? 2000 + Integer.parseInt(y)
                : Integer.parseInt(y);

        YearMonth exp = YearMonth.of(year, month);
        if (mustBeFuture) {
            YearMonth now = YearMonth.now(ZoneId.systemDefault());

            if (exp.isBefore(now)) {
                MessageUtils.buildViolation(context, Group.FINANCE, "card-expiry.future");
                return false;
            }
        }

        return true;
    }
}
