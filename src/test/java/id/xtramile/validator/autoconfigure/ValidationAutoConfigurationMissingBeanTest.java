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

class ValidationAutoConfigurationMissingBeanTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(ValidationAutoConfiguration.class))
            .withPropertyValues("id.xtramile.validator.enabled=true");

    @Test
    void shouldCreateDefaultErrorEnvelopeBuilderWhenNoneExists() {
        contextRunner
                .run(context -> {
                    assertThat(context).hasSingleBean(ErrorEnvelopeBuilder.class);
                    assertThat(context.getBean(ErrorEnvelopeBuilder.class))
                            .isInstanceOf(DefaultErrorEnvelopeBuilder.class);
                });
    }

    @Test
    void shouldNotCreateErrorEnvelopeBuilderWhenCustomOneExists() {
        contextRunner
                .withUserConfiguration(CustomErrorEnvelopeBuilderConfig.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(ErrorEnvelopeBuilder.class);
                    assertThat(context.getBean(ErrorEnvelopeBuilder.class))
                            .isInstanceOf(CustomErrorEnvelopeBuilder.class);
                });
    }

    @Test
    void shouldCreateDefaultApiExceptionHandlerWhenNoneExists() {
        contextRunner
                .run(context -> {
                    assertThat(context).hasSingleBean(ApiExceptionHandler.class);
                    assertThat(context.getBean(ApiExceptionHandler.class))
                            .isInstanceOf(ApiExceptionHandler.class);
                });
    }

    @Test
    void shouldNotCreateApiExceptionHandlerWhenCustomOneExists() {
        contextRunner
                .withUserConfiguration(CustomApiExceptionHandlerConfig.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(ApiExceptionHandler.class);
                    assertThat(context.getBean(ApiExceptionHandler.class))
                            .isInstanceOf(CustomApiExceptionHandler.class);
                });
    }

    @Test
    void shouldCreateDefaultBeansWhenBothMissing() {
        contextRunner
                .run(context -> {
                    assertThat(context).hasSingleBean(ErrorEnvelopeBuilder.class);
                    assertThat(context).hasSingleBean(ApiExceptionHandler.class);
                    assertThat(context.getBean(ErrorEnvelopeBuilder.class))
                            .isInstanceOf(DefaultErrorEnvelopeBuilder.class);
                    assertThat(context.getBean(ApiExceptionHandler.class))
                            .isInstanceOf(ApiExceptionHandler.class);
                });
    }

    @Test
    void shouldNotCreateErrorEnvelopeBuilderWhenCustomExistsButCreateHandler() {
        contextRunner
                .withUserConfiguration(CustomErrorEnvelopeBuilderConfig.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(ErrorEnvelopeBuilder.class);
                    assertThat(context).hasSingleBean(ApiExceptionHandler.class);
                    assertThat(context.getBean(ErrorEnvelopeBuilder.class))
                            .isInstanceOf(CustomErrorEnvelopeBuilder.class);
                    assertThat(context.getBean(ApiExceptionHandler.class))
                            .isInstanceOf(ApiExceptionHandler.class);
                });
    }

    @Test
    void shouldNotCreateApiExceptionHandlerWhenCustomExistsButCreateErrorEnvelopeBuilder() {
        contextRunner
                .withUserConfiguration(CustomApiExceptionHandlerConfig.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(ErrorEnvelopeBuilder.class);
                    assertThat(context).hasSingleBean(ApiExceptionHandler.class);
                    assertThat(context.getBean(ErrorEnvelopeBuilder.class))
                            .isInstanceOf(DefaultErrorEnvelopeBuilder.class);
                    assertThat(context.getBean(ApiExceptionHandler.class))
                            .isInstanceOf(CustomApiExceptionHandler.class);
                });
    }

    @Test
    void shouldNotCreateAnyBeansWhenBothCustomBeansExist() {
        contextRunner
                .withUserConfiguration(BothCustomBeansConfig.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(ErrorEnvelopeBuilder.class);
                    assertThat(context).hasSingleBean(ApiExceptionHandler.class);
                    assertThat(context.getBean(ErrorEnvelopeBuilder.class))
                            .isInstanceOf(CustomErrorEnvelopeBuilder.class);
                    assertThat(context.getBean(ApiExceptionHandler.class))
                            .isInstanceOf(CustomApiExceptionHandler.class);
                });
    }

    @Test
    void shouldHandleMultipleCustomErrorEnvelopeBuilders() {
        contextRunner
                .withUserConfiguration(MultipleErrorEnvelopeBuildersConfig.class)
                .run(context -> {
                    // This should fail to start due to ambiguous bean definition
                    assertThat(context).hasFailed();
                });
    }

    @Test
    void shouldHandleMultipleCustomApiExceptionHandlers() {
        contextRunner
                .withUserConfiguration(MultipleApiExceptionHandlersConfig.class)
                .run(context -> {
                    // Multiple handlers are allowed - Spring can handle multiple beans of the same type
                    assertThat(context).hasBean("customApiExceptionHandler1");
                    assertThat(context).hasBean("customApiExceptionHandler2");
                    assertThat(context).getBeans(ApiExceptionHandler.class).hasSize(2);
                });
    }

    @Test
    void shouldCreateBeansWithCorrectDependencies() {
        contextRunner
                .run(context -> {
                    ErrorEnvelopeBuilder builder = context.getBean(ErrorEnvelopeBuilder.class);
                    ApiExceptionHandler handler = context.getBean(ApiExceptionHandler.class);
                    
                    assertThat(builder).isNotNull();
                    assertThat(handler).isNotNull();
                    
                    // The handler should be able to use the builder
                    assertThat(handler).isInstanceOf(ApiExceptionHandler.class);
                });
    }

    @Configuration
    static class CustomErrorEnvelopeBuilderConfig {
        @Bean
        public ErrorEnvelopeBuilder customErrorEnvelopeBuilder() {
            return new CustomErrorEnvelopeBuilder();
        }
    }

    @Configuration
    static class CustomApiExceptionHandlerConfig {
        @Bean
        public ApiExceptionHandler customApiExceptionHandler() {
            return new CustomApiExceptionHandler();
        }
    }

    @Configuration
    static class BothCustomBeansConfig {
        @Bean
        public ErrorEnvelopeBuilder customErrorEnvelopeBuilder() {
            return new CustomErrorEnvelopeBuilder();
        }
        
        @Bean
        public ApiExceptionHandler customApiExceptionHandler() {
            return new CustomApiExceptionHandler();
        }
    }

    @Configuration
    static class MultipleErrorEnvelopeBuildersConfig {
        @Bean
        public ErrorEnvelopeBuilder customErrorEnvelopeBuilder1() {
            return new CustomErrorEnvelopeBuilder();
        }
        
        @Bean
        public ErrorEnvelopeBuilder customErrorEnvelopeBuilder2() {
            return new CustomErrorEnvelopeBuilder();
        }
    }

    @Configuration
    static class MultipleApiExceptionHandlersConfig {
        @Bean
        public ApiExceptionHandler customApiExceptionHandler1() {
            return new CustomApiExceptionHandler();
        }
        
        @Bean
        public ApiExceptionHandler customApiExceptionHandler2() {
            return new CustomApiExceptionHandler();
        }
    }

    static class CustomErrorEnvelopeBuilder implements ErrorEnvelopeBuilder {
        @Override
        public Map<String, Object> validation(List<String> errors) {
            return Map.of("custom", "validation", "source", "CustomErrorEnvelopeBuilder");
        }

        @Override
        public Map<String, Object> unknown(String cause, String error) {
            return Map.of("custom", "unknown", "source", "CustomErrorEnvelopeBuilder");
        }
    }

    static class CustomApiExceptionHandler extends ApiExceptionHandler {
        public CustomApiExceptionHandler() {
            super(new CustomErrorEnvelopeBuilder(), new FriendlyMessageResolver(
                    new MessageResourceResolver("id")));
        }
    }
}
