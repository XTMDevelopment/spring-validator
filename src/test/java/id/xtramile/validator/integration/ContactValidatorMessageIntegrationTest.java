package id.xtramile.validator.integration;

import id.xtramile.validator.annotation.contact.*;
import id.xtramile.validator.web.FriendlyMessageResolver;
import id.xtramile.validator.web.MessageResourceResolver;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ContactValidatorMessageIntegrationTest {

    private static final Validator VALIDATOR;
    private static final FriendlyMessageResolver RESOLVER;

    static {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        VALIDATOR = factory.getValidator();

        MessageResourceResolver messageResourceResolver = new MessageResourceResolver("en");
        RESOLVER = new FriendlyMessageResolver(messageResourceResolver);
    }

    public static class PhoneNumberDto {
        @ValidPhoneNumber
        private final String value;

        public PhoneNumberDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class ContactNumberDto {
        @ValidContactNumber
        private final String value;

        public ContactNumberDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class EmailDto {
        @ValidEmail
        private final String value;

        public EmailDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class OtpDto {
        @ValidOtp
        private final String value;

        public OtpDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class EmailDomainDto {
        @ValidEmailDomain(allowed = {"gmail.com", "yahoo.com"})
        private final String value;

        public EmailDomainDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class EmailDomainSensitiveDto {
        @ValidEmailDomain(allowed = {"Gmail.com"}, ignoreCase = false)
        private final String value;

        public EmailDomainSensitiveDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    private <T> ConstraintViolation<T> firstViolation(T dto) {
        Set<ConstraintViolation<T>> violations = VALIDATOR.validate(dto);

        assertFalse(violations.isEmpty(), "Expected at least one violation but got none");

        return violations.iterator().next();
    }

    @Test
    void phoneNumber_pathB() {
        PhoneNumberDto dto = new PhoneNumberDto("12345");
        ConstraintViolation<PhoneNumberDto> v = firstViolation(dto);

        assertEquals("{friendly.default}", v.getMessageTemplate());
        assertEquals("value must be a valid phone number", RESOLVER.resolve(v, "value", PhoneNumberDto.class));
    }

    @Test
    void contactNumber_pathB() {
        ContactNumberDto dto = new ContactNumberDto("!!!invalid!!!");
        ConstraintViolation<ContactNumberDto> v = firstViolation(dto);

        assertEquals("{friendly.default}", v.getMessageTemplate());
        assertEquals("value must be a valid contact number", RESOLVER.resolve(v, "value", ContactNumberDto.class));
    }

    @Test
    void email_pathB() {
        EmailDto dto = new EmailDto("not-an-email");
        ConstraintViolation<EmailDto> v = firstViolation(dto);

        assertEquals("{friendly.default}", v.getMessageTemplate());
        assertEquals("value must be a valid email address", RESOLVER.resolve(v, "value", EmailDto.class));
    }

    @Test
    void otp_pathB() {
        OtpDto dto = new OtpDto("ABCDEF");
        ConstraintViolation<OtpDto> v = firstViolation(dto);

        assertEquals("{friendly.default}", v.getMessageTemplate());
        assertEquals("value must be a valid OTP code", RESOLVER.resolve(v, "value", OtpDto.class));
    }

    @Test
    void emailDomain_domainNotAllowed() {
        EmailDomainDto dto = new EmailDomainDto("user@hotmail.com");
        ConstraintViolation<EmailDomainDto> v = firstViolation(dto);

        assertEquals("validation.contact.email-domain", v.getMessageTemplate());

        String resolved = RESOLVER.resolve(v, "value", EmailDomainDto.class);

        assertTrue(resolved.startsWith("value must be a valid email address with domain(s):"),
                "Unexpected message: " + resolved);
        assertTrue(resolved.contains("gmail.com") || resolved.contains("yahoo.com"),
                "Domains not listed in message: " + resolved);
    }

    @Test
    void emailDomain_sensitive_domainCaseMismatch() {
        EmailDomainSensitiveDto dto = new EmailDomainSensitiveDto("user@gmail.com");
        ConstraintViolation<EmailDomainSensitiveDto> v = firstViolation(dto);

        assertEquals("validation.contact.email-domain.sensitive", v.getMessageTemplate());

        String resolved = RESOLVER.resolve(v, "value", EmailDomainSensitiveDto.class);

        assertTrue(resolved.contains("case-sensitive"), "Expected case-sensitive in message: " + resolved);
    }
}
