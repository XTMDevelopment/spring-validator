package id.xtramile.validator.web;

import id.xtramile.validator.autoconfigure.ValidationAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ValidationAutoConfiguration.class)
@TestPropertySource(properties = "id.xtramile.validator.enabled=false")
class WebComponentsDisabledIntegrationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void shouldNotLoadWebComponentsWhenDisabled() {
        assertThat(applicationContext.getBeanNamesForType(ErrorEnvelopeBuilder.class))
                .isEmpty();
        assertThat(applicationContext.getBeanNamesForType(ApiExceptionHandler.class))
                .isEmpty();
    }
}
