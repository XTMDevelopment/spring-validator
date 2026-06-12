package id.xtramile.validator.autoconfigure;

import id.xtramile.validator.web.ApiExceptionHandler;
import id.xtramile.validator.web.DefaultErrorEnvelopeBuilder;
import id.xtramile.validator.web.ErrorEnvelopeBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ValidationAutoConfiguration.class)
class ValidationAutoConfigurationIntegrationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ErrorEnvelopeBuilder errorEnvelopeBuilder;

    @Autowired
    private ApiExceptionHandler apiExceptionHandler;

    @Test
    void shouldLoadAutoConfigurationInSpringContext() {
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
    void shouldHaveCorrectBeanTypes() {
        assertThat(applicationContext.getBean("errorEnvelopeBuilder"))
                .isInstanceOf(DefaultErrorEnvelopeBuilder.class);
        assertThat(applicationContext.getBean("apiExceptionHandler"))
                .isInstanceOf(ApiExceptionHandler.class);
    }

    @Test
    void shouldHaveSingletonBeans() {
        assertThat(applicationContext.isSingleton("errorEnvelopeBuilder")).isTrue();
        assertThat(applicationContext.isSingleton("apiExceptionHandler")).isTrue();
    }

    @Test
    void shouldHaveCorrectBeanDependencies() {
        // ApiExceptionHandler should be able to use ErrorEnvelopeBuilder
        ErrorEnvelopeBuilder builder = applicationContext.getBean(ErrorEnvelopeBuilder.class);
        ApiExceptionHandler handler = applicationContext.getBean(ApiExceptionHandler.class);
        
        assertThat(builder).isNotNull();
        assertThat(handler).isNotNull();
        // Both beans should be available and functional
        assertThat(applicationContext.getBeanNamesForType(ErrorEnvelopeBuilder.class))
                .contains("errorEnvelopeBuilder");
        assertThat(applicationContext.getBeanNamesForType(ApiExceptionHandler.class))
                .contains("apiExceptionHandler");
    }

    @Test
    void shouldBeAbleToCallErrorEnvelopeBuilderMethods() {
        // Test that the bean is functional
        var errors = List.of(
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

@SpringBootTest(classes = ValidationAutoConfiguration.class)
@TestPropertySource(properties = "id.xtramile.validator.enabled=false")
class ValidationAutoConfigurationDisabledIntegrationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void shouldNotLoadAutoConfigurationWhenDisabled() {
        assertThat(applicationContext.getBeanNamesForType(ValidationAutoConfiguration.class))
                .isEmpty();
        assertThat(applicationContext.getBeanNamesForType(ErrorEnvelopeBuilder.class))
                .isEmpty();
        assertThat(applicationContext.getBeanNamesForType(ApiExceptionHandler.class))
                .isEmpty();
    }
}

@SpringBootTest(classes = ValidationAutoConfiguration.class)
@TestPropertySource(properties = "id.xtramile.validator.enabled=true")
class ValidationAutoConfigurationEnabledIntegrationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void shouldLoadAutoConfigurationWhenExplicitlyEnabled() {
        assertThat(applicationContext.getBeanNamesForType(ValidationAutoConfiguration.class))
                .isNotEmpty();
        assertThat(applicationContext.getBeanNamesForType(ErrorEnvelopeBuilder.class))
                .isNotEmpty();
        assertThat(applicationContext.getBeanNamesForType(ApiExceptionHandler.class))
                .isNotEmpty();
    }
}
