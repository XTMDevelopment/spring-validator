package id.xtramile.validator.web;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class BeanValidationMessageDescriptorsTest {

    @Test
    void isFriendlyDefault_onlyMatchesExactToken() {
        assertThat(BeanValidationMessageDescriptors.isFriendlyDefault("{friendly.default}")).isTrue();
        assertThat(BeanValidationMessageDescriptors.isFriendlyDefault("{friendly.default} ")).isFalse();
        assertThat(BeanValidationMessageDescriptors.isFriendlyDefault("custom")).isFalse();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void isDefaultTemplate_falseForBlankOrNull(String template) {
        assertThat(BeanValidationMessageDescriptors.isDefaultTemplate(template)).isFalse();
    }

    @Test
    void isDefaultTemplate_trueForFriendlyAndJakartaKeys() {
        assertThat(BeanValidationMessageDescriptors.isDefaultTemplate("{friendly.default}")).isTrue();
        assertThat(BeanValidationMessageDescriptors.isDefaultTemplate("{jakarta.validation.constraints.NotNull.message}")).isTrue();
        assertThat(BeanValidationMessageDescriptors.isDefaultTemplate("jakarta.validation.constraints.NotBlank.message")).isTrue();
    }

    @Test
    void isDefaultTemplate_falseForCustomMessage() {
        assertThat(BeanValidationMessageDescriptors.isDefaultTemplate("Please fill this field")).isFalse();
    }

    @Test
    void isDefaultBeanValidationMessageDescriptor_coversBracedAndUnbraced() {
        assertThat(BeanValidationMessageDescriptors.isDefaultBeanValidationMessageDescriptor(
                "{jakarta.validation.constraints.Email.message}")).isTrue();
        assertThat(BeanValidationMessageDescriptors.isDefaultBeanValidationMessageDescriptor(
                "{org.hibernate.validator.constraints.Length.message}")).isTrue();
        assertThat(BeanValidationMessageDescriptors.isDefaultBeanValidationMessageDescriptor(
                "jakarta.validation.constraints.Size.message")).isTrue();
        assertThat(BeanValidationMessageDescriptors.isDefaultBeanValidationMessageDescriptor(
                "org.hibernate.validator.constraints.SafeHtml.message")).isTrue();
    }

    @Test
    void isDefaultBeanValidationMessageDescriptor_falseForUnknown() {
        assertThat(BeanValidationMessageDescriptors.isDefaultBeanValidationMessageDescriptor(null)).isFalse();
        assertThat(BeanValidationMessageDescriptors.isDefaultBeanValidationMessageDescriptor("")).isFalse();
        assertThat(BeanValidationMessageDescriptors.isDefaultBeanValidationMessageDescriptor("validation.custom.key")).isFalse();
    }

    @Test
    void shouldResolveFieldErrorUsingConstraintDefaults_falseForNullOrEmptyAttrs() {
        assertThat(BeanValidationMessageDescriptors.shouldResolveFieldErrorUsingConstraintDefaults(null)).isFalse();
        assertThat(BeanValidationMessageDescriptors.shouldResolveFieldErrorUsingConstraintDefaults(new HashMap<>())).isFalse();
    }

    @Test
    void shouldResolveFieldErrorUsingConstraintDefaults_trueWhenMessageAttrMissingOrNotString() {
        Map<String, Object> noMessage = new HashMap<>();
        noMessage.put("groups", new Class[0]);
        assertThat(BeanValidationMessageDescriptors.shouldResolveFieldErrorUsingConstraintDefaults(noMessage)).isTrue();

        Map<String, Object> intMessage = new HashMap<>();
        intMessage.put("message", 1);
        assertThat(BeanValidationMessageDescriptors.shouldResolveFieldErrorUsingConstraintDefaults(intMessage)).isTrue();
    }

    @Test
    void usesConstraintDefaultOrFriendlyMessageKey_trueForStockKeys() {
        assertThat(BeanValidationMessageDescriptors.usesConstraintDefaultOrFriendlyMessageKey("")).isTrue();
        assertThat(BeanValidationMessageDescriptors.usesConstraintDefaultOrFriendlyMessageKey("{friendly.default}")).isTrue();
        assertThat(BeanValidationMessageDescriptors.usesConstraintDefaultOrFriendlyMessageKey(
                "{jakarta.validation.constraints.NotBlank.message}")).isTrue();
    }

    @Test
    void usesConstraintDefaultOrFriendlyMessageKey_falseForPlainCustomText() {
        assertThat(BeanValidationMessageDescriptors.usesConstraintDefaultOrFriendlyMessageKey("Pick a value")).isFalse();
    }
}
