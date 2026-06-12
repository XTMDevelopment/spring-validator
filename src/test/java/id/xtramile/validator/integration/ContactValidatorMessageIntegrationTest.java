package id.xtramile.validator.integration;

import id.xtramile.validator.annotation.contact.*;
import id.xtramile.validator.support.ValidationMessageTestSupport;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ContactValidatorMessageIntegrationTest {

    private static final ValidationMessageTestSupport SUPPORT = ValidationMessageTestSupport.EN;

    public record PhoneNumberDto(@ValidPhoneNumber String value) {
            public PhoneNumberDto(String value) {
                this.value = value;
            }
        }

    public record ContactNumberDto(@ValidContactNumber String value) {
            public ContactNumberDto(String value) {
                this.value = value;
            }
        }

    public record EmailDto(@ValidEmail String value) {
            public EmailDto(String value) {
                this.value = value;
            }
        }

    public record OtpDto(@ValidOtp String value) {
            public OtpDto(String value) {
                this.value = value;
            }
        }

    public record EmailDomainDto(@ValidEmailDomain(allowed = {"gmail.com", "yahoo.com"}) String value) {
            public EmailDomainDto(String value) {
                this.value = value;
            }
        }

    public record EmailDomainSensitiveDto(@ValidEmailDomain(allowed = {"Gmail.com"}, ignoreCase = false) String value) {
            public EmailDomainSensitiveDto(String value) {
                this.value = value;
            }
        }

    @Test
    void phoneNumber_pathB() {
        PhoneNumberDto dto = new PhoneNumberDto("12345");
        ConstraintViolation<PhoneNumberDto> v = SUPPORT.firstViolation(dto);

        assertEquals("{friendly.default}", v.getMessageTemplate());
        assertEquals("value must be a valid phone number", SUPPORT.resolver().resolve(v, "value", PhoneNumberDto.class));
    }

    @Test
    void contactNumber_pathB() {
        ContactNumberDto dto = new ContactNumberDto("!!!invalid!!!");
        ConstraintViolation<ContactNumberDto> v = SUPPORT.firstViolation(dto);

        assertEquals("{friendly.default}", v.getMessageTemplate());
        assertEquals("value must be a valid contact number", SUPPORT.resolver().resolve(v, "value", ContactNumberDto.class));
    }

    @Test
    void email_pathB() {
        EmailDto dto = new EmailDto("not-an-email");
        ConstraintViolation<EmailDto> v = SUPPORT.firstViolation(dto);

        assertEquals("{friendly.default}", v.getMessageTemplate());
        assertEquals("value must be a valid email address", SUPPORT.resolver().resolve(v, "value", EmailDto.class));
    }

    @Test
    void otp_pathB() {
        OtpDto dto = new OtpDto("ABCDEF");
        ConstraintViolation<OtpDto> v = SUPPORT.firstViolation(dto);

        assertEquals("{friendly.default}", v.getMessageTemplate());
        assertEquals("value must be a valid OTP code", SUPPORT.resolver().resolve(v, "value", OtpDto.class));
    }

    @Test
    void emailDomain_domainNotAllowed() {
        EmailDomainDto dto = new EmailDomainDto("user@hotmail.com");
        ConstraintViolation<EmailDomainDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.contact.email-domain", v.getMessageTemplate());

        String resolved = SUPPORT.resolver().resolve(v, "value", EmailDomainDto.class);

        assertTrue(resolved.startsWith("value must be a valid email address with domain(s):"),
                "Unexpected message: " + resolved);
        assertTrue(resolved.contains("gmail.com") || resolved.contains("yahoo.com"),
                "Domains not listed in message: " + resolved);
    }

    @Test
    void emailDomain_sensitive_domainCaseMismatch() {
        EmailDomainSensitiveDto dto = new EmailDomainSensitiveDto("user@gmail.com");
        ConstraintViolation<EmailDomainSensitiveDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.contact.email-domain.sensitive", v.getMessageTemplate());

        String resolved = SUPPORT.resolver().resolve(v, "value", EmailDomainSensitiveDto.class);

        assertTrue(resolved.contains("case-sensitive"), "Expected case-sensitive in message: " + resolved);
    }
}
