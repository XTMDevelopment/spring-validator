package id.xtramile.validator.validator.contact;

import id.xtramile.validator.annotation.contact.ValidEmailDomain;
import id.xtramile.validator.enums.Group;
import id.xtramile.validator.util.MessageUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static id.xtramile.validator.util.ValidatorUtils.isBlank;
import static id.xtramile.validator.util.ValidatorUtils.validateEmail;

/**
 * Validator implementation for {@link ValidEmailDomain} annotation.
 * <p>
 * Validates that an email address has a domain from the allowed list.
 * This validator ensures the email domain is in the whitelist of allowed domains
 * with optional case-insensitive comparison.
 *
 * <p>The validator performs the following operations:
 * <ul>
 * <li>Accepts null/blank values as valid</li>
 * <li>Validates the email format first</li>
 * <li>Extracts the domain part from the email</li>
 * <li>Checks if the domain is in the allowed list</li>
 * </ul>
 *
 * @see ValidEmailDomain
 */
public class EmailDomainValidator implements ConstraintValidator<ValidEmailDomain, String> {
    private Set<String> allowed;
    private boolean ignoreCase;

    /**
     * Initializes the validator with the annotation parameters.
     *
     * @param annotation the ValidEmailDomain annotation instance
     */
    @Override
    public void initialize(ValidEmailDomain annotation) {
        this.ignoreCase = annotation.ignoreCase();
        this.allowed = Stream.of(annotation.allowed())
                .map(domain -> ignoreCase ? domain.toLowerCase() : domain)
                .collect(Collectors.toSet());
    }

    /**
     * Validates that the email domain is in the allowed list.
     *
     * @param value   the email string to validate
     * @param context the constraint validator context
     * @return true if the email domain is allowed or is null/blank
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (isBlank(value)) return true;

        if (!validateEmail(value)) {
            MessageUtils.buildViolation(context, Group.CONTACT, "email");
            return false;
        }

        String[] parts = value.split("@", 2);
        if (parts.length != 2) {
            MessageUtils.buildViolation(context, Group.CONTACT, "email");
            return false;
        }

        String domain = parts[1];

        String key = ignoreCase ? domain.toLowerCase() : domain;
        if (allowed.contains(key)) {
            return true;
        }

        String messageKey = ignoreCase ? "email-domain" : "email-domain.sensitive";
        MessageUtils.buildViolation(context, Group.CONTACT, messageKey, MessageUtils.join(allowed));

        return false;
    }
}
