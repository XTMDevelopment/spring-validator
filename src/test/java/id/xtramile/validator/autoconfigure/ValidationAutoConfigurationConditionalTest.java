package id.xtramile.validator.autoconfigure;

import id.xtramile.validator.web.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ValidationAutoConfigurationConditionalTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(ValidationAutoConfiguration.class));

    @Test
    void shouldCreateBeansWhenPropertyIsTrue() {
        contextRunner
                .withPropertyValues("id.xtramile.validator.enabled=true")
                .run(context -> {
                    assertThat(context).hasBean("errorEnvelopeBuilder");
                    assertThat(context).hasBean("apiExceptionHandler");
                });
    }

    @Test
    void shouldCreateBeansWhenPropertyIsMissing() {
        contextRunner
                .run(context -> {
                    assertThat(context).hasBean("errorEnvelopeBuilder");
                    assertThat(context).hasBean("apiExceptionHandler");
                });
    }

    @Test
    void shouldNotCreateBeansWhenPropertyIsFalse() {
        contextRunner
                .withPropertyValues("id.xtramile.validator.enabled=false")
                .run(context -> {
                    assertThat(context).doesNotHaveBean("errorEnvelopeBuilder");
                    assertThat(context).doesNotHaveBean("apiExceptionHandler");
                });
    }

    @Test
    void shouldNotCreateErrorEnvelopeBuilderWhenCustomBeanExists() {
        contextRunner
                .withUserConfiguration(CustomErrorEnvelopeBuilderConfiguration.class)
                .withPropertyValues("id.xtramile.validator.enabled=true")
                .run(context -> {
                    assertThat(context).hasBean("errorEnvelopeBuilder");
                    assertThat(context).hasBean("apiExceptionHandler");
                    assertThat(context.getBean("errorEnvelopeBuilder"))
                            .isInstanceOf(CustomErrorEnvelopeBuilder.class);
                });
    }

    @Test
    void shouldNotCreateApiExceptionHandlerWhenCustomBeanExists() {
        contextRunner
                .withUserConfiguration(CustomApiExceptionHandlerConfiguration.class)
                .withPropertyValues("id.xtramile.validator.enabled=true")
                .run(context -> {
                    assertThat(context).hasBean("errorEnvelopeBuilder");
                    assertThat(context).hasBean("apiExceptionHandler");
                    assertThat(context.getBean("apiExceptionHandler"))
                            .isInstanceOf(CustomApiExceptionHandler.class);
                });
    }

    @Test
    void shouldCreateDefaultBeansWhenNoCustomBeansExist() {
        contextRunner
                .withPropertyValues("id.xtramile.validator.enabled=true")
                .run(context -> {
                    assertThat(context).hasBean("errorEnvelopeBuilder");
                    assertThat(context).hasBean("apiExceptionHandler");
                    assertThat(context.getBean("errorEnvelopeBuilder"))
                            .isInstanceOf(DefaultErrorEnvelopeBuilder.class);
                    assertThat(context.getBean("apiExceptionHandler"))
                            .isInstanceOf(ApiExceptionHandler.class);
                });
    }

    @Test
    void shouldHandleMultiplePropertyValues() {
        contextRunner
                .withPropertyValues(
                        "id.xtramile.validator.enabled=true",
                        "id.xtramile.validator.debug=false",
                        "id.xtramile.validator.timeout=5000"
                )
                .run(context -> {
                    assertThat(context).hasBean("errorEnvelopeBuilder");
                    assertThat(context).hasBean("apiExceptionHandler");
                });
    }

    @Test
    void shouldHandlePropertyWithDifferentCase() {
        contextRunner
                .withPropertyValues("id.xtramile.validator.enabled=TRUE")
                .run(context -> {
                    assertThat(context).hasBean("errorEnvelopeBuilder");
                    assertThat(context).hasBean("apiExceptionHandler");
                });
    }

    @Test
    void shouldHandlePropertyWithDifferentCaseFalse() {
        contextRunner
                .withPropertyValues("id.xtramile.validator.enabled=FALSE")
                .run(context -> {
                    assertThat(context).doesNotHaveBean("errorEnvelopeBuilder");
                    assertThat(context).doesNotHaveBean("apiExceptionHandler");
                });
    }

    @Test
    void shouldHandleInvalidPropertyValue() {
        contextRunner
                .withPropertyValues("id.xtramile.validator.enabled=invalid")
                .run(context -> {
                    // Invalid values are treated as false by Spring Boot
                    assertThat(context).doesNotHaveBean("errorEnvelopeBuilder");
                    assertThat(context).doesNotHaveBean("apiExceptionHandler");
                });
    }

    @Test
    void shouldHandleEmptyPropertyValue() {
        contextRunner
                .withPropertyValues("id.xtramile.validator.enabled=")
                .run(context -> {
                    // Empty values are treated as false by Spring Boot
                    assertThat(context).doesNotHaveBean("errorEnvelopeBuilder");
                    assertThat(context).doesNotHaveBean("apiExceptionHandler");
                });
    }

    @Configuration
    static class CustomErrorEnvelopeBuilderConfiguration {
        @Bean
        public ErrorEnvelopeBuilder errorEnvelopeBuilder() {
            return new CustomErrorEnvelopeBuilder();
        }
    }

    @Configuration
    static class CustomApiExceptionHandlerConfiguration {
        @Bean
        public ApiExceptionHandler apiExceptionHandler() {
            return new CustomApiExceptionHandler();
        }
    }

    static class CustomErrorEnvelopeBuilder implements ErrorEnvelopeBuilder {
        @Override
        public Map<String, Object> validation(List<String> errors) {
            return Map.of("custom", "validation");
        }

        @Override
        public Map<String, Object> unknown(String cause, String error) {
            return Map.of("custom", "unknown");
        }
    }

    static class CustomApiExceptionHandler extends ApiExceptionHandler {
        public CustomApiExceptionHandler() {
            super(new CustomErrorEnvelopeBuilder(), new FriendlyMessageResolver(
                    new MessageResourceResolver("id")));
        }
    }
}
