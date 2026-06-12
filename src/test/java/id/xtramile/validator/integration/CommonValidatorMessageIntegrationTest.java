package id.xtramile.validator.integration;

import id.xtramile.validator.annotation.common.*;
import id.xtramile.validator.enums.ISOType;
import id.xtramile.validator.util.MessageUtils;
import id.xtramile.validator.support.ValidationMessageTestSupport;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static id.xtramile.validator.integration.ValidationMessageAssertions.assertNoRawValidationKey;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("SameParameterValue")
class CommonValidatorMessageIntegrationTest {

    private static final ValidationMessageTestSupport SUPPORT = ValidationMessageTestSupport.EN;

    public record InWhitelistDto(@InWhitelist(values = {"ACTIVE", "INACTIVE"}) String value) {
        public InWhitelistDto(String value) {
            this.value = value;
        }
    }

    public record InWhitelistSensitiveDto(@InWhitelist(values = {"ACTIVE", "INACTIVE"}, ignoreCase = false) String value) {
        public InWhitelistSensitiveDto(String value) {
            this.value = value;
        }
    }

    public record NotInBlacklistDto(@NotInBlacklist(values = {"BANNED", "BLOCKED"}) String value) {
        public NotInBlacklistDto(String value) {
            this.value = value;
        }
    }

    public record NotInBlacklistSensitiveDto(@NotInBlacklist(values = {"BANNED", "BLOCKED"}, ignoreCase = false) String value) {
        public NotInBlacklistSensitiveDto(String value) {
            this.value = value;
        }
    }

    public record ValidEnumDto(@ValidEnum(enumClass = ISOType.class) String value) {
        public ValidEnumDto(String value) {
            this.value = value;
        }
    }

    public record ValidEnumSensitiveDto(@ValidEnum(enumClass = ISOType.class, ignoreCase = false) String value) {
        public ValidEnumSensitiveDto(String value) {
            this.value = value;
        }
    }

    public record UniqueElementsDto(@UniqueElements List<String> value) {
        public UniqueElementsDto(List<String> value) {
            this.value = value;
        }
    }

    public record NotEmptyCollectionDto(@NotEmptyCollection List<String> value) {
        public NotEmptyCollectionDto(List<String> value) {
            this.value = value;
        }
    }

    public record ValidUUIDDto(@ValidUUID String value) {
        public ValidUUIDDto(String value) {
            this.value = value;
        }
    }

    @Test
    void inWhitelist_caseInsensitive() {
        InWhitelistDto dto = new InWhitelistDto("UNKNOWN");
        ConstraintViolation<InWhitelistDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.common.in-whitelist", v.getMessageTemplate());

        String expected = SUPPORT.messages().getMessage("validation.common.in-whitelist", "value", "active, inactive");
        String resolved = SUPPORT.resolver().resolve(v, "value", InWhitelistDto.class);

        assertEquals(expected, resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void inWhitelist_caseSensitive() {
        InWhitelistSensitiveDto dto = new InWhitelistSensitiveDto("active");
        ConstraintViolation<InWhitelistSensitiveDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.common.in-whitelist.sensitive", v.getMessageTemplate());

        String expected = SUPPORT.messages().getMessage("validation.common.in-whitelist.sensitive", "value", "ACTIVE, INACTIVE");
        String resolved = SUPPORT.resolver().resolve(v, "value", InWhitelistSensitiveDto.class);

        assertEquals(expected, resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void notInBlacklist_caseInsensitive() {
        NotInBlacklistDto dto = new NotInBlacklistDto("BANNED");
        ConstraintViolation<NotInBlacklistDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.common.not-in-blacklist", v.getMessageTemplate());

        String expected = SUPPORT.messages().getMessage("validation.common.not-in-blacklist", "value", "banned, blocked");
        String resolved = SUPPORT.resolver().resolve(v, "value", NotInBlacklistDto.class);

        assertEquals(expected, resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void notInBlacklist_caseSensitive() {
        NotInBlacklistSensitiveDto dto = new NotInBlacklistSensitiveDto("BANNED");
        ConstraintViolation<NotInBlacklistSensitiveDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.common.not-in-blacklist.sensitive", v.getMessageTemplate());

        String expected = SUPPORT.messages().getMessage("validation.common.not-in-blacklist.sensitive", "value", "BANNED, BLOCKED");
        String resolved = SUPPORT.resolver().resolve(v, "value", NotInBlacklistSensitiveDto.class);

        assertEquals(expected, resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void validEnum_caseInsensitive() {
        ValidEnumDto dto = new ValidEnumDto("INVALID");
        ConstraintViolation<ValidEnumDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.common.enum", v.getMessageTemplate());

        String allowed = MessageUtils.join(Stream.of(ISOType.class.getEnumConstants())
                .map(e -> e.name().toLowerCase())
                .collect(Collectors.toSet()));
        String expected = SUPPORT.messages().getMessage("validation.common.enum", "value", allowed);
        String resolved = SUPPORT.resolver().resolve(v, "value", ValidEnumDto.class);

        assertEquals(expected, resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void validEnum_caseSensitive() {
        ValidEnumSensitiveDto dto = new ValidEnumSensitiveDto("currency");
        ConstraintViolation<ValidEnumSensitiveDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.common.enum.sensitive", v.getMessageTemplate());

        String allowed = MessageUtils.join(Stream.of(ISOType.class.getEnumConstants())
                .map(Enum::name)
                .collect(Collectors.toSet()));
        String expected = SUPPORT.messages().getMessage("validation.common.enum.sensitive", "value", allowed);
        String resolved = SUPPORT.resolver().resolve(v, "value", ValidEnumSensitiveDto.class);

        assertEquals(expected, resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void uniqueElements() {
        UniqueElementsDto dto = new UniqueElementsDto(List.of("apple", "banana", "apple"));
        ConstraintViolation<UniqueElementsDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.common.unique-elements", v.getMessageTemplate());

        String expected = SUPPORT.messages().getMessage("validation.common.unique-elements", "value");
        String resolved = SUPPORT.resolver().resolve(v, "value", UniqueElementsDto.class);

        assertEquals(expected, resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void notEmptyCollection() {
        NotEmptyCollectionDto dto = new NotEmptyCollectionDto(List.of());
        ConstraintViolation<NotEmptyCollectionDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.common.not-empty-collection", v.getMessageTemplate());

        String expected = SUPPORT.messages().getMessage("validation.common.not-empty-collection", "value");
        String resolved = SUPPORT.resolver().resolve(v, "value", NotEmptyCollectionDto.class);

        assertEquals(expected, resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void validUUID() {
        ValidUUIDDto dto = new ValidUUIDDto("not-a-uuid");
        ConstraintViolation<ValidUUIDDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.common.uuid", v.getMessageTemplate());

        String expected = SUPPORT.messages().getMessage("validation.common.uuid", "value");
        String resolved = SUPPORT.resolver().resolve(v, "value", ValidUUIDDto.class);

        assertEquals(expected, resolved);
        assertNoRawValidationKey(resolved);
    }
}
