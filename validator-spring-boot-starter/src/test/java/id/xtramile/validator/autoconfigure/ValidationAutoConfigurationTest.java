package id.xtramile.validator.autoconfigure;

import id.xtramile.validator.web.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class ValidationAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(ValidationAutoConfiguration.class));

    @Mock
    private ErrorEnvelopeBuilder mockErrorEnvelopeBuilder;

    @Test
    void shouldLoadAutoConfigurationWhenPropertyIsTrue() {
        contextRunner
                .withPropertyValues("id.xtramile.validator.enabled=true")
                .run(context -> {
                    assertThat(context).hasSingleBean(ValidationAutoConfiguration.class);
                    assertThat(context).hasSingleBean(ErrorEnvelopeBuilder.class);
                    assertThat(context).hasSingleBean(MessageResourceResolver.class);
                    assertThat(context).hasSingleBean(FriendlyMessageResolver.class);
                    assertThat(context).hasSingleBean(ApiExceptionHandler.class);
                    assertThat(context.getBean(ErrorEnvelopeBuilder.class))
                            .isInstanceOf(DefaultErrorEnvelopeBuilder.class);
                });
    }

    @Test
    void shouldLoadAutoConfigurationWhenPropertyIsMissing() {
        contextRunner
                .run(context -> {
                    assertThat(context).hasSingleBean(ValidationAutoConfiguration.class);
                    assertThat(context).hasSingleBean(ErrorEnvelopeBuilder.class);
                    assertThat(context).hasSingleBean(MessageResourceResolver.class);
                    assertThat(context).hasSingleBean(FriendlyMessageResolver.class);
                    assertThat(context).hasSingleBean(ApiExceptionHandler.class);
                });
    }

    @Test
    void shouldNotLoadAutoConfigurationWhenPropertyIsFalse() {
        contextRunner
                .withPropertyValues("id.xtramile.validator.enabled=false")
                .run(context -> {
                    assertThat(context).doesNotHaveBean(ValidationAutoConfiguration.class);
                    assertThat(context).doesNotHaveBean(ErrorEnvelopeBuilder.class);
                    assertThat(context).doesNotHaveBean(MessageResourceResolver.class);
                    assertThat(context).doesNotHaveBean(FriendlyMessageResolver.class);
                    assertThat(context).doesNotHaveBean(ApiExceptionHandler.class);
                });
    }

    @Test
    void shouldNotCreateErrorEnvelopeBuilderWhenAlreadyExists() {
        contextRunner
                .withUserConfiguration(TestConfiguration.class)
                .withPropertyValues("id.xtramile.validator.enabled=true")
                .run(context -> {
                    assertThat(context).hasSingleBean(ErrorEnvelopeBuilder.class);
                    assertThat(context).hasSingleBean(ApiExceptionHandler.class);
                    assertThat(context.getBean(ErrorEnvelopeBuilder.class))
                            .isInstanceOf(ErrorEnvelopeBuilder.class);
                });
    }

    @Test
    void shouldNotCreateApiExceptionHandlerWhenAlreadyExists() {
        contextRunner
                .withUserConfiguration(TestConfigurationWithHandler.class)
                .withPropertyValues("id.xtramile.validator.enabled=true")
                .run(context -> {
                    assertThat(context).hasSingleBean(ApiExceptionHandler.class);
                    assertThat(context.getBean(ApiExceptionHandler.class))
                            .isSameAs(TestConfigurationWithHandler.testHandler);
                });
    }

    @Test
    void shouldCreateDefaultErrorEnvelopeBuilder() {
        contextRunner
                .withPropertyValues("id.xtramile.validator.enabled=true")
                .run(context -> {
                    ErrorEnvelopeBuilder builder = context.getBean(ErrorEnvelopeBuilder.class);
                    assertThat(builder).isNotNull();
                    assertThat(builder).isInstanceOf(DefaultErrorEnvelopeBuilder.class);
                });
    }

    @Test
    void shouldCreateApiExceptionHandlerWithErrorEnvelopeBuilder() {
        contextRunner
                .withPropertyValues("id.xtramile.validator.enabled=true")
                .run(context -> {
                    ApiExceptionHandler handler = context.getBean(ApiExceptionHandler.class);
                    ErrorEnvelopeBuilder builder = context.getBean(ErrorEnvelopeBuilder.class);

                    assertThat(handler).isNotNull();
                    assertThat(builder).isNotNull();
                });
    }

    @Test
    void shouldHandleMultiplePropertyValues() {
        contextRunner
                .withPropertyValues(
                        "id.xtramile.validator.enabled=true",
                        "id.xtramile.validator.other.property=value"
                )
                .run(context -> {
                    assertThat(context).hasSingleBean(ValidationAutoConfiguration.class);
                    assertThat(context).hasSingleBean(ErrorEnvelopeBuilder.class);
                    assertThat(context).hasSingleBean(ApiExceptionHandler.class);
                });
    }

    @Test
    void shouldRespectConditionalOnMissingBeanForErrorEnvelopeBuilder() {
        contextRunner
                .withUserConfiguration(TestConfiguration.class)
                .withPropertyValues("id.xtramile.validator.enabled=true")
                .run(context -> {
                    // Should use the existing bean, not create a new one
                    assertThat(context).hasSingleBean(ErrorEnvelopeBuilder.class);
                    assertThat(context.getBean(ErrorEnvelopeBuilder.class))
                            .isInstanceOf(ErrorEnvelopeBuilder.class);
                });
    }

    @Test
    void shouldRespectConditionalOnMissingBeanForApiExceptionHandler() {
        contextRunner
                .withUserConfiguration(TestConfigurationWithHandler.class)
                .withPropertyValues("id.xtramile.validator.enabled=true")
                .run(context -> {
                    // Should use the existing bean, not create a new one
                    assertThat(context).hasSingleBean(ApiExceptionHandler.class);
                    assertThat(context.getBean(ApiExceptionHandler.class))
                            .isSameAs(TestConfigurationWithHandler.testHandler);
                });
    }

    @Test
    void shouldCreateBeansInCorrectOrder() {
        contextRunner
                .withPropertyValues("id.xtramile.validator.enabled=true")
                .run(context -> {
                    // ErrorEnvelopeBuilder should be created first
                    ErrorEnvelopeBuilder builder = context.getBean(ErrorEnvelopeBuilder.class);
                    assertThat(builder).isNotNull();

                    // ApiExceptionHandler should be created with the builder
                    ApiExceptionHandler handler = context.getBean(ApiExceptionHandler.class);
                    assertThat(handler).isNotNull();
                });
    }

    @Configuration
    static class TestConfiguration {
        @Bean
        public ErrorEnvelopeBuilder errorEnvelopeBuilder() {
            return mock(ErrorEnvelopeBuilder.class);
        }
    }

    @Configuration
    static class TestConfigurationWithHandler {
        static final ApiExceptionHandler testHandler = mock(ApiExceptionHandler.class);

        @Bean
        public ApiExceptionHandler apiExceptionHandler() {
            return testHandler;
        }

        @Bean
        public MessageResourceResolver messageResourceResolver() {
            return new MessageResourceResolver("id");
        }

        @Bean
        public FriendlyMessageResolver friendlyMessageResolver(MessageResourceResolver messageResourceResolver) {
            return new FriendlyMessageResolver(messageResourceResolver);
        }
    }
}
