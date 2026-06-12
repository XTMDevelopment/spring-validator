package id.xtramile.validator.validator.contact;

import id.xtramile.validator.annotation.contact.ValidEmail;
import io.github.rigsto.emailvalidator.exception.EmptyValidationList;
import io.github.rigsto.emailvalidator.validation.DNSCheckValidation;
import io.github.rigsto.emailvalidator.validation.MultipleValidationWithAnd;
import io.github.rigsto.emailvalidator.validation.RFCValidation;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

import static id.xtramile.validator.util.ValidatorUtils.isBlank;
import static id.xtramile.validator.util.ValidatorUtils.validateEmail;

/**
 * Validator implementation for {@link ValidEmail} annotation.
 * <p>
 * Validates that a string is a well-formed email address using comprehensive validation.
 * This validator performs both RFC compliance checking and DNS validation to ensure
 * the email address is not only syntactically correct but also has a valid domain.
 * 
 * <p>The validation process includes:
 * <ul>
 * <li>RFC 5322 compliance checking</li>
 * <li>DNS validation to verify the domain exists</li>
 * <li>Basic format validation</li>
 * </ul>
 * 
 * <p>Null/blank values are considered valid.
 * 
 * @see ValidEmail
 */
public class EmailValidator implements ConstraintValidator<ValidEmail, String> {
    /**
     * Validates the email address using comprehensive validation rules.
     * @param value the email string to validate
     * @param constraintValidatorContext the constraint validator context
     * @return true if the email is valid, false otherwise
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (isBlank(value)) return true;

        if (!validateEmail(value)) return false;

        io.github.rigsto.emailvalidator.EmailValidator emailValidator = new io.github.rigsto.emailvalidator.EmailValidator();
        MultipleValidationWithAnd validations;

        try {
            validations = new MultipleValidationWithAnd(
                    List.of(
                            new RFCValidation(),
                            new DNSCheckValidation()
                    )
            );
        } catch (EmptyValidationList e) {
            throw new RuntimeException(e);
        }

        return emailValidator.isValid(value, validations);
    }
}
