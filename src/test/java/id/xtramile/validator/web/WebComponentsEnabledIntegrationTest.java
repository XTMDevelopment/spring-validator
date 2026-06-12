package id.xtramile.validator.web;

import id.xtramile.validator.autoconfigure.ValidationAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ValidationAutoConfiguration.class)
class WebComponentsEnabledIntegrationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ErrorEnvelopeBuilder errorEnvelopeBuilder;

    @Autowired
    private ApiExceptionHandler apiExceptionHandler;

    @Test
    void shouldLoadWebComponentsInSpringContext() {
        assertThat(applicationContext).isNotNull();
        assertThat(applicationContext.getBean(ValidationAutoConfiguration.class)).isNotNull();
    }

    @Test
    void shouldCreateErrorEnvelopeBuilderBean() {
        assertThat(errorEnvelopeBuilder).isNotNull();
        assertThat(errorEnvelopeBuilder).isInstanceOf(DefaultErrorEnvelopeBuilder.class);
    }

    @Test
    void shouldCreateApiExceptionHandlerBean() {
        assertThat(apiExceptionHandler).isNotNull();
        assertThat(apiExceptionHandler).isInstanceOf(ApiExceptionHandler.class);
    }

    @Test
    void shouldHaveCorrectBeanNames() {
        assertThat(applicationContext.getBeanNamesForType(ErrorEnvelopeBuilder.class))
                .contains("errorEnvelopeBuilder");
        assertThat(applicationContext.getBeanNamesForType(ApiExceptionHandler.class))
                .contains("apiExceptionHandler");
    }

    @Test
    void shouldHaveSingletonBeans() {
        assertThat(applicationContext.isSingleton("errorEnvelopeBuilder")).isTrue();
        assertThat(applicationContext.isSingleton("apiExceptionHandler")).isTrue();
    }

    @Test
    void shouldBeAbleToCallErrorEnvelopeBuilderMethods() {
        var errors = java.util.List.of(
                "Error message 1",
                "Error message 2"
        );
        
        var result = errorEnvelopeBuilder.validation(errors);
        assertThat(result).isNotNull();
        assertThat(result).containsKey("code");
        assertThat(result).containsKey("status");
        assertThat(result).containsKey("message");
        assertThat(result).containsKey("data");
        assertThat(result).containsKey("errors");
        assertThat(result.get("data")).isNull();
    }

    @Test
    void shouldBeAbleToCallUnknownErrorMethod() {
        var result = errorEnvelopeBuilder.unknown("Test cause", "TestError");
        assertThat(result).isNotNull();
        assertThat(result).containsKey("code");
        assertThat(result).containsKey("status");
        assertThat(result).containsKey("message");
        assertThat(result).containsKey("data");
    }
}
