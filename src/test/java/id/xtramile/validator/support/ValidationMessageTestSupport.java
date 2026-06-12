package id.xtramile.validator.support;

import id.xtramile.validator.web.FriendlyMessageResolver;
import id.xtramile.validator.web.MessageResourceResolver;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public final class ValidationMessageTestSupport {

    public static final ValidationMessageTestSupport EN = forLocale("en");
    public static final ValidationMessageTestSupport ID = forLocale("id");

    private final Validator validator;
    private final MessageResourceResolver messages;
    private final FriendlyMessageResolver resolver;

    private ValidationMessageTestSupport(String locale) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
        this.messages = new MessageResourceResolver(locale);
        this.resolver = new FriendlyMessageResolver(messages);
    }

    public static ValidationMessageTestSupport forLocale(String locale) {
        return new ValidationMessageTestSupport(locale);
    }

    public Validator validator() {
        return validator;
    }

    public MessageResourceResolver messages() {
        return messages;
    }

    public FriendlyMessageResolver resolver() {
        return resolver;
    }

    public <T> ConstraintViolation<T> firstViolation(T dto) {
        Set<ConstraintViolation<T>> violations = validator.validate(dto);
        assertThat(violations).as("expected at least one violation").isNotEmpty();
        return violations.iterator().next();
    }
}
