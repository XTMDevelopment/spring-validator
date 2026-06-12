package id.xtramile.validator.sample;

import id.xtramile.validator.annotation.contact.ValidEmail;
import id.xtramile.validator.annotation.data.ValidPassword;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class SampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SampleApplication.class, args);
    }

    @RestController
    static class RegistrationController {

        @PostMapping("/register")
        RegistrationResponse register(@Valid @RequestBody RegistrationRequest request) {
            return new RegistrationResponse(request.email(), "ok");
        }
    }

    record RegistrationRequest(
            @NotBlank String name,
            @ValidEmail String email,
            @ValidPassword String password) {
    }

    record RegistrationResponse(String email, String status) {
    }
}
