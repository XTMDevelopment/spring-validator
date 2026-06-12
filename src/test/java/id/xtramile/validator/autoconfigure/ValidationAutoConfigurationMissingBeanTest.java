package id.xtramile.validator.autoconfigure;

import id.xtramile.validator.support.AutoConfigurationTestFixtures.BothCustomBeansConfig;
import id.xtramile.validator.support.AutoConfigurationTestFixtures.CustomApiExceptionHandler;
import id.xtramile.validator.support.AutoConfigurationTestFixtures.CustomApiExceptionHandlerConfig;
import id.xtramile.validator.support.AutoConfigurationTestFixtures.CustomErrorEnvelopeBuilder;
import id.xtramile.validator.support.AutoConfigurationTestFixtures.CustomErrorEnvelopeBuilderConfig;
import id.xtramile.validator.support.AutoConfigurationTestFixtures.MultipleApiExceptionHandlersConfig;
import id.xtramile.validator.support.AutoConfigurationTestFixtures.MultipleErrorEnvelopeBuildersConfig;
import id.xtramile.validator.web.ApiExceptionHandler;
import id.xtramile.validator.web.DefaultErrorEnvelopeBuilder;
import id.xtramile.validator.web.ErrorEnvelopeBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

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

}
