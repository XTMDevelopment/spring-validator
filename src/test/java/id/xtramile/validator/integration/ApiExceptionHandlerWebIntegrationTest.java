package id.xtramile.validator.integration;

import id.xtramile.validator.annotation.common.FieldName;
import id.xtramile.validator.autoconfigure.ValidationAutoConfiguration;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ApiExceptionHandlerWebIntegrationTest.TestApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(properties = "id.xtramile.validator.locale=id")
class ApiExceptionHandlerWebIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void postInvalidDto_returns400WithFriendlyErrorEnvelope() throws Exception {
        mockMvc.perform(post("/test/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(98))
                .andExpect(jsonPath("$.status").value("ERROR"))
                .andExpect(jsonPath("$.message.id").value("Validasi gagal"))
                .andExpect(jsonPath("$.data").value(nullValue()))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[0]").value(containsString("Email")))
                .andExpect(jsonPath("$.errors[0]").value(containsString("wajib diisi")));
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @Import({ValidationAutoConfiguration.class, ValidationTestController.class})
    static class TestApplication {
    }

    @RestController
    static class ValidationTestController {

        @PostMapping("/test/validate")
        void validate(@Valid @RequestBody SampleRequest request) {
        }
    }

    static class SampleRequest {

        @FieldName("Email")
        @NotBlank
        private String email;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
}
