package id.xtramile.validator.autoconfigure;

import id.xtramile.validator.web.ApiExceptionHandler;
import id.xtramile.validator.web.DefaultErrorEnvelopeBuilder;
import id.xtramile.validator.web.ErrorEnvelopeBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies auto-configuration loads on Spring Boot 3.5.x and 4.0.x.
 * CI runs this class with {@code -Dspring-boot.version=3.5.14} and {@code 4.0.5}.
 */
@SpringBootTest(classes = ValidationAutoConfiguration.class)
class SpringBoot4AutoConfigurationIntegrationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ErrorEnvelopeBuilder errorEnvelopeBuilder;

    @Autowired
    private ApiExceptionHandler apiExceptionHandler;

    @Test
    void shouldLoadAutoConfigurationBeans() {
        assertThat(applicationContext.getBean(ValidationAutoConfiguration.class)).isNotNull();
        assertThat(errorEnvelopeBuilder).isInstanceOf(DefaultErrorEnvelopeBuilder.class);
        assertThat(apiExceptionHandler).isInstanceOf(ApiExceptionHandler.class);
    }

    @Test
    void shouldExposeSingletonBeansWithExpectedNames() {
        assertThat(applicationContext.isSingleton("errorEnvelopeBuilder")).isTrue();
        assertThat(applicationContext.isSingleton("apiExceptionHandler")).isTrue();
        assertThat(applicationContext.getBeanNamesForType(ErrorEnvelopeBuilder.class))
                .contains("errorEnvelopeBuilder");
        assertThat(applicationContext.getBeanNamesForType(ApiExceptionHandler.class))
                .contains("apiExceptionHandler");
    }
}
