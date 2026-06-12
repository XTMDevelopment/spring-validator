package id.xtramile.validator.integration;

import id.xtramile.validator.annotation.common.FieldName;
import id.xtramile.validator.autoconfigure.ValidationAutoConfiguration;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.annotation.Validated;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = MethodValidationIntegrationTest.TestApplication.class)
@TestPropertySource(properties = "id.xtramile.validator.locale=id")
class MethodValidationIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setUpMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void missingNotNullRequestParam_returns400WithFriendlyMessage() throws Exception {
        mockMvc.perform(get("/api/method-validation/check"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(98))
                .andExpect(jsonPath("$.status").value("ERROR"))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[0]").value(containsString("wajib diisi")))
                .andExpect(jsonPath("$.errors[0]").value(not(containsString("validation."))));
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @Import({
            ValidationAutoConfiguration.class,
            MethodValidationTestController.class,
            MethodValidationIntegrationTest.MethodValidationConfig.class
    })
    static class TestApplication {
    }

    static class MethodValidationConfig {
        @Bean
        static MethodValidationPostProcessor methodValidationPostProcessor() {
            return new MethodValidationPostProcessor();
        }
    }

    @Validated
    @RestController
    static class MethodValidationTestController {

        @GetMapping("/api/method-validation/check")
        String check(@RequestParam(value = "code", required = false) @NotNull @FieldName("Kode") String code) {
            return code;
        }
    }
}
