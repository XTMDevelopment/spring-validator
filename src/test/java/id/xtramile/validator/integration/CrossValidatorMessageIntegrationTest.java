package id.xtramile.validator.integration;

import id.xtramile.validator.annotation.cross.*;
import id.xtramile.validator.support.ValidationMessageTestSupport;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CrossValidatorMessageIntegrationTest {

    private static final ValidationMessageTestSupport SUPPORT = ValidationMessageTestSupport.EN;

    @FieldMatch(first = "password", second = "confirmPassword")
    public static class FieldMatchDto {
        private final String password;
        private final String confirmPassword;

        public FieldMatchDto(String password, String confirmPassword) {
            this.password = password;
            this.confirmPassword = confirmPassword;
        }

        public String password() {
            return password;
        }

        public String confirmPassword() {
            return confirmPassword;
        }
    }

    @AtLeastOneOf(fields = {"email", "phone"})
    public static class AtLeastOneDto {
        private final String email;
        private final String phone;

        public AtLeastOneDto(String email, String phone) {
            this.email = email;
            this.phone = phone;
        }

        public String email() {
            return email;
        }

        public String phone() {
            return phone;
        }
    }

    @DifferentFrom(field = "newEmail", other = "currentEmail")
    public static class DifferentFromDto {
        private final String currentEmail;
        private final String newEmail;

        public DifferentFromDto(String currentEmail, String newEmail) {
            this.currentEmail = currentEmail;
            this.newEmail = newEmail;
        }

        public String currentEmail() {
            return currentEmail;
        }

        public String newEmail() {
            return newEmail;
        }
    }

    @OnlyOneOf(fields = {"email", "phone"})
    public static class OnlyOneDto {
        private final String email;
        private final String phone;

        public OnlyOneDto(String email, String phone) {
            this.email = email;
            this.phone = phone;
        }

        public String email() {
            return email;
        }

        public String phone() {
            return phone;
        }
    }

    @RequiredWith(when = "token", require = {"email"})
    public static class RequiredWithDto {
        private final String token;
        private final String email;

        public RequiredWithDto(String token, String email) {
            this.token = token;
            this.email = email;
        }

        public String token() {
            return token;
        }

        public String email() {
            return email;
        }
    }

    @Test
    void fieldMatch_mismatch() {
        FieldMatchDto dto = new FieldMatchDto("secret123", "different456");
        ConstraintViolation<FieldMatchDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.cross.field-match", v.getMessageTemplate());

        String resolved = SUPPORT.resolver().resolve(v, "password", FieldMatchDto.class);

        assertEquals("password and confirmPassword must match", resolved);
    }

    @Test
    void atLeastOne_nonePresent() {
        AtLeastOneDto dto = new AtLeastOneDto(null, null);
        ConstraintViolation<AtLeastOneDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.cross.at-least-one", v.getMessageTemplate());

        String resolved = SUPPORT.resolver().resolve(v, "email", AtLeastOneDto.class);

        assertNotNull(resolved);
        assertTrue(resolved.contains("At least one"), "Unexpected message: " + resolved);
    }

    @Test
    void differentFrom_sameValue() {
        DifferentFromDto dto = new DifferentFromDto("user@example.com", "user@example.com");
        ConstraintViolation<DifferentFromDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.cross.different-from", v.getMessageTemplate());

        String resolved = SUPPORT.resolver().resolve(v, "newEmail", DifferentFromDto.class);

        assertEquals("newEmail must be different from currentEmail", resolved);
    }

    @Test
    void onlyOne_bothPresent() {
        OnlyOneDto dto = new OnlyOneDto("user@example.com", "6281234567890");
        ConstraintViolation<OnlyOneDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.cross.only-one", v.getMessageTemplate());

        String resolved = SUPPORT.resolver().resolve(v, "email", OnlyOneDto.class);

        assertNotNull(resolved);
        assertTrue(resolved.contains("Only one"), "Unexpected message: " + resolved);
    }

    @Test
    void onlyOne_nonePresent() {
        OnlyOneDto dto = new OnlyOneDto(null, null);
        ConstraintViolation<OnlyOneDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.cross.only-one", v.getMessageTemplate());

        String resolved = SUPPORT.resolver().resolve(v, "email", OnlyOneDto.class);

        assertNotNull(resolved);
        assertTrue(resolved.contains("Only one"), "Unexpected message: " + resolved);
    }

    @Test
    void requiredWith_missingRequiredField() {
        RequiredWithDto dto = new RequiredWithDto("my-token", null);
        ConstraintViolation<RequiredWithDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.cross.required-with", v.getMessageTemplate());

        String resolved = SUPPORT.resolver().resolve(v, "token", RequiredWithDto.class);

        assertEquals("If token is provided, the following fields must also be provided: email", resolved);
    }
}
