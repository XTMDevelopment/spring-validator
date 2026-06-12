package id.xtramile.validator.support;

import id.xtramile.validator.web.ApiExceptionHandler;
import id.xtramile.validator.web.ErrorEnvelopeBuilder;
import id.xtramile.validator.web.FriendlyMessageResolver;
import id.xtramile.validator.web.MessageResourceResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

public final class AutoConfigurationTestFixtures {

    private AutoConfigurationTestFixtures() {}

    public static class CustomErrorEnvelopeBuilder implements ErrorEnvelopeBuilder {
        @Override
        public Map<String, Object> validation(List<String> errors) {
            return Map.of("custom", "validation", "source", "CustomErrorEnvelopeBuilder");
        }

        @Override
        public Map<String, Object> unknown(String cause, String error) {
            return Map.of("custom", "unknown", "source", "CustomErrorEnvelopeBuilder");
        }
    }

    @Configuration
    public static class CustomErrorEnvelopeBuilderConfig {
        @Bean
        public ErrorEnvelopeBuilder customErrorEnvelopeBuilder() {
            return new CustomErrorEnvelopeBuilder();
        }
    }

    @Configuration
    public static class CustomErrorEnvelopeBuilderConfiguration {
        @Bean
        public ErrorEnvelopeBuilder errorEnvelopeBuilder() {
            return new CustomErrorEnvelopeBuilder();
        }
    }

    @Configuration
    public static class CustomApiExceptionHandlerConfig {
        @Bean
        public ApiExceptionHandler customApiExceptionHandler() {
            return new CustomApiExceptionHandler();
        }
    }

    @Configuration
    public static class CustomApiExceptionHandlerConfiguration {
        @Bean
        public ApiExceptionHandler apiExceptionHandler() {
            return new CustomApiExceptionHandler();
        }
    }

    @Configuration
    public static class BothCustomBeansConfig {
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
    public static class MultipleErrorEnvelopeBuildersConfig {
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
    public static class MultipleApiExceptionHandlersConfig {
        @Bean
        public ApiExceptionHandler customApiExceptionHandler1() {
            return new CustomApiExceptionHandler();
        }

        @Bean
        public ApiExceptionHandler customApiExceptionHandler2() {
            return new CustomApiExceptionHandler();
        }
    }

    public static class CustomApiExceptionHandler extends ApiExceptionHandler {
        public CustomApiExceptionHandler() {
            super(new CustomErrorEnvelopeBuilder(), new FriendlyMessageResolver(new MessageResourceResolver("id")));
        }
    }
}
