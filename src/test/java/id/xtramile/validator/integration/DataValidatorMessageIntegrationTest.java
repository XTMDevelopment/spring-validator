package id.xtramile.validator.integration;

import id.xtramile.validator.annotation.data.*;
import id.xtramile.validator.enums.ISOType;
import id.xtramile.validator.enums.PasswordType;
import id.xtramile.validator.util.MessageUtils;
import id.xtramile.validator.web.FriendlyMessageResolver;
import id.xtramile.validator.web.MessageResourceResolver;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static id.xtramile.validator.integration.ValidationMessageAssertions.assertNoRawValidationKey;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class DataValidatorMessageIntegrationTest {

    private static final Validator VALIDATOR;
    private static final FriendlyMessageResolver RESOLVER;
    private static final MessageResourceResolver MESSAGES;

    static {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        VALIDATOR = factory.getValidator();
        MESSAGES = new MessageResourceResolver("en");
        RESOLVER = new FriendlyMessageResolver(MESSAGES);
    }

    public static class AccountNumberNumbersDto {
        @ValidAccountNumber()
        private final String value;

        public AccountNumberNumbersDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class AccountNumberMinDto {
        @ValidAccountNumber(min = 10)
        private final String value;

        public AccountNumberMinDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class AccountNumberMaxDto {
        @ValidAccountNumber(max = 10)
        private final String value;

        public AccountNumberMaxDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class Base64Dto {
        @ValidBase64
        private final String value;

        public Base64Dto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class HexColorShortDto {
        @ValidHexColor
        private final String value;

        public HexColorShortDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class HexColorLongDto {
        @ValidHexColor
        private final String value;

        public HexColorLongDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class HexColorBaseDto {
        @ValidHexColor
        private final String value;

        public HexColorBaseDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class ISOCodeCurrencyDto {
        @ValidISOCode(ISOType.CURRENCY)
        private final String value;

        public ISOCodeCurrencyDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class ISOCodeCountryDto {
        @ValidISOCode(ISOType.COUNTRY_ALPHA2)
        private final String value;

        public ISOCodeCountryDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class ISOCodeLanguageDto {
        @ValidISOCode(ISOType.LANGUAGE)
        private final String value;

        public ISOCodeLanguageDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class JsonDto {
        @ValidJSON
        private final String value;

        public JsonDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class NameMinDto {
        @ValidName(min = 5)
        private final String value;

        public NameMinDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class NameMaxDto {
        @ValidName(max = 5)
        private final String value;

        public NameMaxDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class NameDigitsDto {
        @ValidName
        private final String value;

        public NameDigitsDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class NameSymbolDto {
        @ValidName
        private final String value;

        public NameSymbolDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class NationalIdBaseDto {
        @ValidNationalID(country = "US")
        private final String value;

        public NationalIdBaseDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class NationalIdLengthDto {
        @ValidNationalID
        private final String value;

        public NationalIdLengthDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class NationalIdNumbersDto {
        @ValidNationalID
        private final String value;

        public NationalIdNumbersDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class PasswordWhitespaceDto {
        @ValidPassword(type = PasswordType.FULL)
        private final String value;

        public PasswordWhitespaceDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class PasswordMinLengthDto {
        @ValidPassword(min = 5, type = PasswordType.FULL)
        private final String value;

        public PasswordMinLengthDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class PasswordAlphanumericDto {
        @ValidPassword(min = 5, type = PasswordType.ALPHANUMERIC)
        private final String value;

        public PasswordAlphanumericDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class PasswordLetterDigitDto {
        @ValidPassword(min = 5, type = PasswordType.LETTER_DIGIT)
        private final String value;

        public PasswordLetterDigitDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class PasswordLetterMixedDto {
        @ValidPassword(min = 5, type = PasswordType.LETTER_MIXED_CASE)
        private final String value;

        public PasswordLetterMixedDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class PasswordFullDto {
        @ValidPassword(min = 5, type = PasswordType.FULL)
        private final String value;

        public PasswordFullDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class PinLengthDto {
        @ValidPIN(length = 5)
        private final String value;

        public PinLengthDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class PinRepetitiveDto {
        @ValidPIN(maxAllowedRepetitive = 2)
        private final String value;

        public PinRepetitiveDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class PinSequentialDto {
        @ValidPIN(maxAllowedSequential = 3)
        private final String value;

        public PinSequentialDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class SlugBaseDto {
        @ValidSlug
        private final String value;

        public SlugBaseDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class SlugMinDto {
        @ValidSlug(min = 3)
        private final String value;

        public SlugMinDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class SlugMaxDto {
        @ValidSlug(max = 5)
        private final String value;

        public SlugMaxDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class TaxIdBaseDto {
        @ValidTaxID(country = "US")
        private final String value;

        public TaxIdBaseDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class TaxIdLengthDto {
        @ValidTaxID
        private final String value;

        public TaxIdLengthDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class TaxIdNumbersDto {
        @ValidTaxID
        private final String value;

        public TaxIdNumbersDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class UsernameBaseDto {
        @ValidUsername
        private final String value;

        public UsernameBaseDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class UsernameMinDto {
        @ValidUsername(min = 8)
        private final String value;

        public UsernameMinDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class UsernameMaxDto {
        @ValidUsername(max = 6)
        private final String value;

        public UsernameMaxDto(String value) {
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
    void accountNumber_numbers() {
        AccountNumberNumbersDto dto = new AccountNumberNumbersDto("abc12345");
        ConstraintViolation<AccountNumberNumbersDto> v = firstViolation(dto);

        assertEquals("validation.data.account-number.numbers", v.getMessageTemplate());
        assertEquals("value must be numbers", RESOLVER.resolve(v, "value", AccountNumberNumbersDto.class));
    }

    @Test
    void accountNumber_min() {
        AccountNumberMinDto dto = new AccountNumberMinDto("12345");
        ConstraintViolation<AccountNumberMinDto> v = firstViolation(dto);

        assertEquals("validation.data.account-number.min", v.getMessageTemplate());
        assertEquals("value must have minimal 10 characters", RESOLVER.resolve(v, "value", AccountNumberMinDto.class));
    }

    @Test
    void accountNumber_max() {
        AccountNumberMaxDto dto = new AccountNumberMaxDto("123456789012");
        ConstraintViolation<AccountNumberMaxDto> v = firstViolation(dto);

        assertEquals("validation.data.account-number.max", v.getMessageTemplate());
        assertEquals("value must have maximal 10 characters", RESOLVER.resolve(v, "value", AccountNumberMaxDto.class));
    }

    @Test
    void base64_pathB() {
        Base64Dto dto = new Base64Dto("not valid base64!!!");
        ConstraintViolation<Base64Dto> v = firstViolation(dto);

        assertEquals("{friendly.default}", v.getMessageTemplate());
        assertEquals("value must be a valid Base64 encoded string", RESOLVER.resolve(v, "value", Base64Dto.class));
    }

    @Test
    void hexColor_short() {
        HexColorShortDto dto = new HexColorShortDto("#XYZ");
        ConstraintViolation<HexColorShortDto> v = firstViolation(dto);

        assertEquals("validation.data.hex-color.short", v.getMessageTemplate());
        assertEquals("value has invalid short hex color", RESOLVER.resolve(v, "value", HexColorShortDto.class));
    }

    @Test
    void hexColor_long() {
        HexColorLongDto dto = new HexColorLongDto("#XYZABC");
        ConstraintViolation<HexColorLongDto> v = firstViolation(dto);

        assertEquals("validation.data.hex-color.long", v.getMessageTemplate());
        assertEquals("value has invalid long hex color", RESOLVER.resolve(v, "value", HexColorLongDto.class));
    }

    @Test
    void hexColor_base() {
        HexColorBaseDto dto = new HexColorBaseDto("ZZZZZ");
        ConstraintViolation<HexColorBaseDto> v = firstViolation(dto);

        assertEquals("validation.data.hex-color", v.getMessageTemplate());
        assertEquals("value must be a valid hex color code", RESOLVER.resolve(v, "value", HexColorBaseDto.class));
    }

    @Test
    void isoCode_currency() {
        ISOCodeCurrencyDto dto = new ISOCodeCurrencyDto("ZZZ");
        ConstraintViolation<ISOCodeCurrencyDto> v = firstViolation(dto);

        assertEquals("validation.data.iso-code.currency", v.getMessageTemplate());
        assertEquals("value must be a valid currency ISO code", RESOLVER.resolve(v, "value", ISOCodeCurrencyDto.class));
    }

    @Test
    void isoCode_country() {
        ISOCodeCountryDto dto = new ISOCodeCountryDto("QQ");
        ConstraintViolation<ISOCodeCountryDto> v = firstViolation(dto);

        assertEquals("validation.data.iso-code.country", v.getMessageTemplate());
        assertEquals("value must be a valid country ISO code", RESOLVER.resolve(v, "value", ISOCodeCountryDto.class));
    }

    @Test
    void isoCode_language() {
        ISOCodeLanguageDto dto = new ISOCodeLanguageDto("qq");
        ConstraintViolation<ISOCodeLanguageDto> v = firstViolation(dto);

        assertEquals("validation.data.iso-code.language", v.getMessageTemplate());
        assertEquals("value must be a valid language ISO code", RESOLVER.resolve(v, "value", ISOCodeLanguageDto.class));
    }

    @Test
    void json_pathB() {
        JsonDto dto = new JsonDto("not json");
        ConstraintViolation<JsonDto> v = firstViolation(dto);

        assertEquals("{friendly.default}", v.getMessageTemplate());
        assertEquals("value must be a valid JSON object", RESOLVER.resolve(v, "value", JsonDto.class));
    }

    @Test
    void name_min() {
        NameMinDto dto = new NameMinDto("Jo");
        ConstraintViolation<NameMinDto> v = firstViolation(dto);

        assertEquals("validation.data.name.min", v.getMessageTemplate());
        assertEquals("value must have minimal 5 characters", RESOLVER.resolve(v, "value", NameMinDto.class));
    }

    @Test
    void name_max() {
        NameMaxDto dto = new NameMaxDto("Alexander");
        ConstraintViolation<NameMaxDto> v = firstViolation(dto);

        assertEquals("validation.data.name.max", v.getMessageTemplate());
        assertEquals("value must have maximal 5 characters", RESOLVER.resolve(v, "value", NameMaxDto.class));
    }

    @Test
    void name_digits() {
        NameDigitsDto dto = new NameDigitsDto("John123");
        ConstraintViolation<NameDigitsDto> v = firstViolation(dto);

        assertEquals("validation.data.name.digits", v.getMessageTemplate());

        String resolved = RESOLVER.resolve(v, "value", NameDigitsDto.class);

        assertEquals(MESSAGES.getMessage("validation.data.name.digits", "value"), resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void name_symbol() {
        NameSymbolDto dto = new NameSymbolDto("John#Doe");
        ConstraintViolation<NameSymbolDto> v = firstViolation(dto);

        assertEquals("validation.data.name.symbol", v.getMessageTemplate());

        String symbolsJoined = MessageUtils.join(Set.of("'", " ", "."));
        String expected = MESSAGES.getMessage("validation.data.name.symbol", "value", symbolsJoined);
        String resolved = RESOLVER.resolve(v, "value", NameSymbolDto.class);

        assertEquals(expected, resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void nationalId_base() {
        NationalIdBaseDto dto = new NationalIdBaseDto("1234567890123456");
        ConstraintViolation<NationalIdBaseDto> v = firstViolation(dto);

        assertEquals("validation.data.national-id", v.getMessageTemplate());
        assertEquals("value must be a valid National ID", RESOLVER.resolve(v, "value", NationalIdBaseDto.class));
    }

    @Test
    void nationalId_numbers() {
        NationalIdNumbersDto dto = new NationalIdNumbersDto("123456789012345X");
        ConstraintViolation<NationalIdNumbersDto> v = firstViolation(dto);

        assertEquals("validation.data.national-id.numbers", v.getMessageTemplate());
        assertEquals("value must be numbers", RESOLVER.resolve(v, "value", NationalIdNumbersDto.class));
    }

    @Test
    void nationalId_length() {
        NationalIdLengthDto dto = new NationalIdLengthDto("12345");
        ConstraintViolation<NationalIdLengthDto> v = firstViolation(dto);

        assertEquals("validation.data.national-id.length", v.getMessageTemplate());
        assertEquals("value must have 16 digits", RESOLVER.resolve(v, "value", NationalIdLengthDto.class));
    }

    @Test
    void password_whitespace() {
        PasswordWhitespaceDto dto = new PasswordWhitespaceDto("hello world");
        ConstraintViolation<PasswordWhitespaceDto> v = firstViolation(dto);

        assertEquals("validation.data.password", v.getMessageTemplate());
        assertEquals("value must be a valid password", RESOLVER.resolve(v, "value", PasswordWhitespaceDto.class));
    }

    @Test
    void password_minLength() {
        PasswordMinLengthDto dto = new PasswordMinLengthDto("Ab1!");
        ConstraintViolation<PasswordMinLengthDto> v = firstViolation(dto);

        assertEquals("validation.data.password", v.getMessageTemplate());
        assertEquals("value must be a valid password", RESOLVER.resolve(v, "value", PasswordMinLengthDto.class));
    }

    @Test
    void password_alphanumeric() {
        PasswordAlphanumericDto dto = new PasswordAlphanumericDto("Hello@World");
        ConstraintViolation<PasswordAlphanumericDto> v = firstViolation(dto);

        assertEquals("validation.data.password.alphanumeric", v.getMessageTemplate());
        assertEquals("value must contain only letters and digits", RESOLVER.resolve(v, "value", PasswordAlphanumericDto.class));
    }

    @Test
    void password_letterDigit() {
        PasswordLetterDigitDto dto = new PasswordLetterDigitDto("HelloWorld");
        ConstraintViolation<PasswordLetterDigitDto> v = firstViolation(dto);

        assertEquals("validation.data.password.letter-digit", v.getMessageTemplate());
        assertEquals("value must contain at least one letter and one digit", RESOLVER.resolve(v, "value", PasswordLetterDigitDto.class));
    }

    @Test
    void password_letterMixed() {
        PasswordLetterMixedDto dto = new PasswordLetterMixedDto("HELLOWORLD1!");
        ConstraintViolation<PasswordLetterMixedDto> v = firstViolation(dto);

        assertEquals("validation.data.password.letter-mixed", v.getMessageTemplate());
        assertEquals("value must contain at least one lowercase and one uppercase letter", RESOLVER.resolve(v, "value", PasswordLetterMixedDto.class));
    }

    @Test
    void password_full() {
        PasswordFullDto dto = new PasswordFullDto("helloworld123");
        ConstraintViolation<PasswordFullDto> v = firstViolation(dto);

        assertEquals("validation.data.password.full", v.getMessageTemplate());
        assertEquals("value must contain at least one lowercase letter, one uppercase letter, one digit, and one symbol", RESOLVER.resolve(v, "value", PasswordFullDto.class));
    }

    @Test
    void pin_length() {
        PinLengthDto dto = new PinLengthDto("1234");
        ConstraintViolation<PinLengthDto> v = firstViolation(dto);

        assertEquals("validation.data.pin.length", v.getMessageTemplate());
        assertEquals("value must be numeric and of length 5", RESOLVER.resolve(v, "value", PinLengthDto.class));
    }

    @Test
    void pin_repetitive() {
        PinRepetitiveDto dto = new PinRepetitiveDto("111234");
        ConstraintViolation<PinRepetitiveDto> v = firstViolation(dto);

        assertEquals("validation.data.pin.repetitive", v.getMessageTemplate());
        assertEquals("value must not have too many repetitive digits", RESOLVER.resolve(v, "value", PinRepetitiveDto.class));
    }

    @Test
    void pin_sequential() {
        PinSequentialDto dto = new PinSequentialDto("123456");
        ConstraintViolation<PinSequentialDto> v = firstViolation(dto);

        assertEquals("validation.data.pin.sequential", v.getMessageTemplate());
        assertEquals("value must not have sequential digits", RESOLVER.resolve(v, "value", PinSequentialDto.class));
    }

    @Test
    void slug_base() {
        SlugBaseDto dto = new SlugBaseDto("Invalid Slug!");
        ConstraintViolation<SlugBaseDto> v = firstViolation(dto);

        assertEquals("validation.data.slug", v.getMessageTemplate());
        assertEquals("value must be a valid slug", RESOLVER.resolve(v, "value", SlugBaseDto.class));
    }

    @Test
    void slug_min() {
        SlugMinDto dto = new SlugMinDto("ab");
        ConstraintViolation<SlugMinDto> v = firstViolation(dto);

        assertEquals("validation.data.slug.min", v.getMessageTemplate());
        assertEquals("value must have minimal 3 characters", RESOLVER.resolve(v, "value", SlugMinDto.class));
    }

    @Test
    void slug_max() {
        SlugMaxDto dto = new SlugMaxDto("abcdef");
        ConstraintViolation<SlugMaxDto> v = firstViolation(dto);

        assertEquals("validation.data.slug.max", v.getMessageTemplate());
        assertEquals("value must have maximal 5 characters", RESOLVER.resolve(v, "value", SlugMaxDto.class));
    }

    @Test
    void taxId_base() {
        TaxIdBaseDto dto = new TaxIdBaseDto("123456789012345");
        ConstraintViolation<TaxIdBaseDto> v = firstViolation(dto);

        assertEquals("validation.data.tax-id", v.getMessageTemplate());
        assertEquals("value must be a valid tax ID", RESOLVER.resolve(v, "value", TaxIdBaseDto.class));
    }

    @Test
    void taxId_numbers() {
        TaxIdNumbersDto dto = new TaxIdNumbersDto("abcdefghijklmno");
        ConstraintViolation<TaxIdNumbersDto> v = firstViolation(dto);

        assertEquals("validation.data.tax-id.numbers", v.getMessageTemplate());
        assertEquals("value must be numbers", RESOLVER.resolve(v, "value", TaxIdNumbersDto.class));
    }

    @Test
    void taxId_length() {
        TaxIdLengthDto dto = new TaxIdLengthDto("12345");
        ConstraintViolation<TaxIdLengthDto> v = firstViolation(dto);

        assertEquals("validation.data.tax-id.length", v.getMessageTemplate());
        assertEquals("value must have 15-16 digits", RESOLVER.resolve(v, "value", TaxIdLengthDto.class));
    }

    @Test
    void username_base() {
        UsernameBaseDto dto = new UsernameBaseDto("invalid user!");
        ConstraintViolation<UsernameBaseDto> v = firstViolation(dto);

        assertEquals("validation.data.username", v.getMessageTemplate());
        assertEquals("value must be a valid username", RESOLVER.resolve(v, "value", UsernameBaseDto.class));
    }

    @Test
    void username_min() {
        UsernameMinDto dto = new UsernameMinDto("abc");
        ConstraintViolation<UsernameMinDto> v = firstViolation(dto);

        assertEquals("validation.data.username.min", v.getMessageTemplate());
        assertEquals("value must have minimal 8 characters", RESOLVER.resolve(v, "value", UsernameMinDto.class));
    }

    @Test
    void username_max() {
        UsernameMaxDto dto = new UsernameMaxDto("abcdefg");
        ConstraintViolation<UsernameMaxDto> v = firstViolation(dto);

        assertEquals("validation.data.username.max", v.getMessageTemplate());
        assertEquals("value must have maximal 6 characters", RESOLVER.resolve(v, "value", UsernameMaxDto.class));
    }
}
