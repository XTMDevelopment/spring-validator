package id.xtramile.validator.integration;

import id.xtramile.validator.annotation.common.FieldName;
import id.xtramile.validator.annotation.common.ValidUUID;
import id.xtramile.validator.annotation.contact.ValidEmail;
import id.xtramile.validator.support.ValidationMessageTestSupport;
import id.xtramile.validator.web.BeanValidationMessageDescriptors;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.Test;

import static id.xtramile.validator.integration.ValidationMessageAssertions.assertNoRawValidationKey;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * E2E validation message resolution with Indonesian locale via {@link ValidationMessageTestSupport#ID}.
 */
class IndonesianMessageIntegrationTest {

    private static final ValidationMessageTestSupport SUPPORT = ValidationMessageTestSupport.ID;

    public static class NotBlankDto {
        @FieldName("ID Peminjam")
        @NotBlank
        private final String borrowerId;

        public NotBlankDto(String borrowerId) {
            this.borrowerId = borrowerId;
        }

        public String borrowerId() {
            return borrowerId;
        }
    }

    public static class ValidUUIDDto {
        @FieldName("Referensi")
        @ValidUUID
        private final String value;

        public ValidUUIDDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class EmailDto {
        @FieldName("Email Kerja")
        @ValidEmail
        private final String value;

        public EmailDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    @Test
    void notBlank_withFieldName_resolvesIndonesianMessage() {
        NotBlankDto dto = new NotBlankDto("");
        ConstraintViolation<NotBlankDto> violation = SUPPORT.firstViolation(dto);

        assertTrue(BeanValidationMessageDescriptors.isDefaultBeanValidationMessageDescriptor(violation.getMessageTemplate()));

        String expected = SUPPORT.messages().getMessage("validation.spring.not-blank", "ID Peminjam");
        String resolved = SUPPORT.resolver().resolve(violation, "borrowerId", NotBlankDto.class);

        assertEquals(expected, resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void validUUID_resolvesIndonesianMessage() {
        ValidUUIDDto dto = new ValidUUIDDto("bukan-uuid");
        ConstraintViolation<ValidUUIDDto> violation = SUPPORT.firstViolation(dto);

        assertEquals("validation.common.uuid", violation.getMessageTemplate());

        String expected = SUPPORT.messages().getMessage("validation.common.uuid", "Referensi");
        String resolved = SUPPORT.resolver().resolve(violation, "value", ValidUUIDDto.class);

        assertEquals(expected, resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void validEmail_resolvesIndonesianMessage() {
        EmailDto dto = new EmailDto("bukan-email");
        ConstraintViolation<EmailDto> violation = SUPPORT.firstViolation(dto);

        assertEquals("{friendly.default}", violation.getMessageTemplate());

        String expected = SUPPORT.messages().getMessage("validation.contact.email", "Email Kerja");
        String resolved = SUPPORT.resolver().resolve(violation, "value", EmailDto.class);

        assertEquals(expected, resolved);
        assertNoRawValidationKey(resolved);
    }
}
