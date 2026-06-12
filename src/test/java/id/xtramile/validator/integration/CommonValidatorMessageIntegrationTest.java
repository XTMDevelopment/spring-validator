package id.xtramile.validator.integration;

import id.xtramile.validator.annotation.common.*;
import id.xtramile.validator.enums.ISOType;
import id.xtramile.validator.util.MessageUtils;
import id.xtramile.validator.web.FriendlyMessageResolver;
import id.xtramile.validator.web.MessageResourceResolver;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static id.xtramile.validator.integration.ValidationMessageAssertions.assertNoRawValidationKey;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SuppressWarnings("SameParameterValue")
class CommonValidatorMessageIntegrationTest {

    private static final Validator VALIDATOR;
    private static final FriendlyMessageResolver RESOLVER;
    private static final MessageResourceResolver MESSAGES;

    static {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();

        VALIDATOR = factory.getValidator();
        MESSAGES = new MessageResourceResolver("en");
        RESOLVER = new FriendlyMessageResolver(MESSAGES);
    }

    public static class InWhitelistDto {
        @InWhitelist(values = {"ACTIVE", "INACTIVE"})
        private final String value;

        public InWhitelistDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class InWhitelistSensitiveDto {
        @InWhitelist(values = {"ACTIVE", "INACTIVE"}, ignoreCase = false)
        private final String value;

        public InWhitelistSensitiveDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class NotInBlacklistDto {
        @NotInBlacklist(values = {"BANNED", "BLOCKED"})
        private final String value;

        public NotInBlacklistDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class NotInBlacklistSensitiveDto {
        @NotInBlacklist(values = {"BANNED", "BLOCKED"}, ignoreCase = false)
        private final String value;

        public NotInBlacklistSensitiveDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class ValidEnumDto {
        @ValidEnum(enumClass = ISOType.class)
        private final String value;

        public ValidEnumDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class ValidEnumSensitiveDto {
        @ValidEnum(enumClass = ISOType.class, ignoreCase = false)
        private final String value;

        public ValidEnumSensitiveDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class UniqueElementsDto {
        @UniqueElements
        private final List<String> value;

        public UniqueElementsDto(List<String> value) {
            this.value = value;
        }

        public List<String> value() {
            return value;
        }
    }

    public static class NotEmptyCollectionDto {
        @NotEmptyCollection
        private final List<String> value;

        public NotEmptyCollectionDto(List<String> value) {
            this.value = value;
        }

        public List<String> value() {
            return value;
        }
    }

    public static class ValidUUIDDto {
        @ValidUUID
        private final String value;

        public ValidUUIDDto(String value) {
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
    void inWhitelist_caseInsensitive() {
        InWhitelistDto dto = new InWhitelistDto("UNKNOWN");
        ConstraintViolation<InWhitelistDto> v = firstViolation(dto);

        assertEquals("validation.common.in-whitelist", v.getMessageTemplate());

        String expected = MESSAGES.getMessage("validation.common.in-whitelist", "value", "active, inactive");
        String resolved = RESOLVER.resolve(v, "value", InWhitelistDto.class);

        assertEquals(expected, resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void inWhitelist_caseSensitive() {
        InWhitelistSensitiveDto dto = new InWhitelistSensitiveDto("active");
        ConstraintViolation<InWhitelistSensitiveDto> v = firstViolation(dto);

        assertEquals("validation.common.in-whitelist.sensitive", v.getMessageTemplate());

        String expected = MESSAGES.getMessage("validation.common.in-whitelist.sensitive", "value", "ACTIVE, INACTIVE");
        String resolved = RESOLVER.resolve(v, "value", InWhitelistSensitiveDto.class);

        assertEquals(expected, resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void notInBlacklist_caseInsensitive() {
        NotInBlacklistDto dto = new NotInBlacklistDto("BANNED");
        ConstraintViolation<NotInBlacklistDto> v = firstViolation(dto);

        assertEquals("validation.common.not-in-blacklist", v.getMessageTemplate());

        String expected = MESSAGES.getMessage("validation.common.not-in-blacklist", "value", "banned, blocked");
        String resolved = RESOLVER.resolve(v, "value", NotInBlacklistDto.class);

        assertEquals(expected, resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void notInBlacklist_caseSensitive() {
        NotInBlacklistSensitiveDto dto = new NotInBlacklistSensitiveDto("BANNED");
        ConstraintViolation<NotInBlacklistSensitiveDto> v = firstViolation(dto);

        assertEquals("validation.common.not-in-blacklist.sensitive", v.getMessageTemplate());

        String expected = MESSAGES.getMessage("validation.common.not-in-blacklist.sensitive", "value", "BANNED, BLOCKED");
        String resolved = RESOLVER.resolve(v, "value", NotInBlacklistSensitiveDto.class);

        assertEquals(expected, resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void validEnum_caseInsensitive() {
        ValidEnumDto dto = new ValidEnumDto("INVALID");
        ConstraintViolation<ValidEnumDto> v = firstViolation(dto);

        assertEquals("validation.common.enum", v.getMessageTemplate());

        String allowed = MessageUtils.join(Stream.of(ISOType.class.getEnumConstants())
                .map(e -> e.name().toLowerCase())
                .collect(Collectors.toSet()));
        String expected = MESSAGES.getMessage("validation.common.enum", "value", allowed);
        String resolved = RESOLVER.resolve(v, "value", ValidEnumDto.class);

        assertEquals(expected, resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void validEnum_caseSensitive() {
        ValidEnumSensitiveDto dto = new ValidEnumSensitiveDto("currency");
        ConstraintViolation<ValidEnumSensitiveDto> v = firstViolation(dto);

        assertEquals("validation.common.enum.sensitive", v.getMessageTemplate());

        String allowed = MessageUtils.join(Stream.of(ISOType.class.getEnumConstants())
                .map(Enum::name)
                .collect(Collectors.toSet()));
        String expected = MESSAGES.getMessage("validation.common.enum.sensitive", "value", allowed);
        String resolved = RESOLVER.resolve(v, "value", ValidEnumSensitiveDto.class);

        assertEquals(expected, resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void uniqueElements() {
        UniqueElementsDto dto = new UniqueElementsDto(List.of("apple", "banana", "apple"));
        ConstraintViolation<UniqueElementsDto> v = firstViolation(dto);

        assertEquals("validation.common.unique-elements", v.getMessageTemplate());

        String expected = MESSAGES.getMessage("validation.common.unique-elements", "value");
        String resolved = RESOLVER.resolve(v, "value", UniqueElementsDto.class);

        assertEquals(expected, resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void notEmptyCollection() {
        NotEmptyCollectionDto dto = new NotEmptyCollectionDto(List.of());
        ConstraintViolation<NotEmptyCollectionDto> v = firstViolation(dto);

        assertEquals("validation.common.not-empty-collection", v.getMessageTemplate());

        String expected = MESSAGES.getMessage("validation.common.not-empty-collection", "value");
        String resolved = RESOLVER.resolve(v, "value", NotEmptyCollectionDto.class);

        assertEquals(expected, resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void validUUID() {
        ValidUUIDDto dto = new ValidUUIDDto("not-a-uuid");
        ConstraintViolation<ValidUUIDDto> v = firstViolation(dto);

        assertEquals("validation.common.uuid", v.getMessageTemplate());

        String expected = MESSAGES.getMessage("validation.common.uuid", "value");
        String resolved = RESOLVER.resolve(v, "value", ValidUUIDDto.class);

        assertEquals(expected, resolved);
        assertNoRawValidationKey(resolved);
    }
}
