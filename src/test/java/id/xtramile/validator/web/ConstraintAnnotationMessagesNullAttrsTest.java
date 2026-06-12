package id.xtramile.validator.web;

import id.xtramile.validator.web.messages.CompositeConstraintMessageResolver;
import jakarta.validation.constraints.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConstraintAnnotationMessagesNullAttrsTest {

    private CompositeConstraintMessageResolver resolver;

    @BeforeEach
    void setUp() {
        MessageResourceResolver messages = new MessageResourceResolver("en");
        ValidationFieldDisplayNames fieldNames = new ValidationFieldDisplayNames();
        resolver = new CompositeConstraintMessageResolver(messages, fieldNames);
    }

    @Test
    void minWithNullAttrs_usesDefaultValue() {
        assertThat(resolver.resolveFromAnnotation("Age", Min.class, null, Object.class))
                .isEqualTo("Age must be at least 0");
    }

    @Test
    void maxWithNullAttrs_usesDefaultValue() {
        assertThat(resolver.resolveFromAnnotation("Age", Max.class, null, Object.class))
                .startsWith("Age must be at most ");
    }

    @Test
    void decimalMinWithNullAttrs_usesDefaultValue() {
        assertThat(resolver.resolveFromAnnotation("Price", DecimalMin.class, null, Object.class))
                .isEqualTo("Price must be at least 0");
    }

    @Test
    void decimalMaxWithNullAttrs_usesDefaultValue() {
        assertThat(resolver.resolveFromAnnotation("Price", DecimalMax.class, null, Object.class))
                .isEqualTo("Price must be at most 0");
    }

    @Test
    void digitsWithNullAttrs_usesDefaultValue() {
        assertThat(resolver.resolveFromAnnotation("Amount", Digits.class, null, Object.class))
                .isEqualTo("Amount must have at most 0 integer digits and at most 0 fractional digits");
    }

    @Test
    void sizeWithNullAttrs_usesDefaultValue() {
        assertThat(resolver.resolveFromAnnotation("Nick", Size.class, null, Object.class))
                .startsWith("Nick must be at most ");
    }
}
