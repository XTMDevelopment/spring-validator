package id.xtramile.validator.integration;

import id.xtramile.validator.annotation.data.*;
import id.xtramile.validator.enums.ISOType;
import id.xtramile.validator.enums.PasswordType;
import id.xtramile.validator.util.MessageUtils;
import id.xtramile.validator.support.ValidationMessageTestSupport;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static id.xtramile.validator.integration.ValidationMessageAssertions.assertNoRawValidationKey;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DataValidatorMessageIntegrationTest {

    private static final ValidationMessageTestSupport SUPPORT = ValidationMessageTestSupport.EN;

    public record AccountNumberNumbersDto(@ValidAccountNumber() String value) {
            public AccountNumberNumbersDto(String value) {
                this.value = value;
            }
        }

    public record AccountNumberMinDto(@ValidAccountNumber(min = 10) String value) {
            public AccountNumberMinDto(String value) {
                this.value = value;
            }
        }

    public record AccountNumberMaxDto(@ValidAccountNumber(max = 10) String value) {
            public AccountNumberMaxDto(String value) {
                this.value = value;
            }
        }

    public record Base64Dto(@ValidBase64 String value) {
            public Base64Dto(String value) {
                this.value = value;
            }
        }

    public record HexColorShortDto(@ValidHexColor String value) {
            public HexColorShortDto(String value) {
                this.value = value;
            }
        }

    public record HexColorLongDto(@ValidHexColor String value) {
            public HexColorLongDto(String value) {
                this.value = value;
            }
        }

    public record HexColorBaseDto(@ValidHexColor String value) {
            public HexColorBaseDto(String value) {
                this.value = value;
            }
        }

    public record ISOCodeCurrencyDto(@ValidISOCode(ISOType.CURRENCY) String value) {
            public ISOCodeCurrencyDto(String value) {
                this.value = value;
            }
        }

    public record ISOCodeCountryDto(@ValidISOCode(ISOType.COUNTRY_ALPHA2) String value) {
            public ISOCodeCountryDto(String value) {
                this.value = value;
            }
        }

    public record ISOCodeLanguageDto(@ValidISOCode(ISOType.LANGUAGE) String value) {
            public ISOCodeLanguageDto(String value) {
                this.value = value;
            }
        }

    public record JsonDto(@ValidJSON String value) {
            public JsonDto(String value) {
                this.value = value;
            }
        }

    public record NameMinDto(@ValidName(min = 5) String value) {
            public NameMinDto(String value) {
                this.value = value;
            }
        }

    public record NameMaxDto(@ValidName(max = 5) String value) {
            public NameMaxDto(String value) {
                this.value = value;
            }
        }

    public record NameDigitsDto(@ValidName String value) {
            public NameDigitsDto(String value) {
                this.value = value;
            }
        }

    public record NameSymbolDto(@ValidName String value) {
            public NameSymbolDto(String value) {
                this.value = value;
            }
        }

    public record NationalIdBaseDto(@ValidNationalID(country = "US") String value) {
            public NationalIdBaseDto(String value) {
                this.value = value;
            }
        }

    public record NationalIdLengthDto(@ValidNationalID String value) {
            public NationalIdLengthDto(String value) {
                this.value = value;
            }
        }

    public record NationalIdNumbersDto(@ValidNationalID String value) {
            public NationalIdNumbersDto(String value) {
                this.value = value;
            }
        }

    public record PasswordWhitespaceDto(@ValidPassword(type = PasswordType.FULL) String value) {
            public PasswordWhitespaceDto(String value) {
                this.value = value;
            }
        }

    public record PasswordMinLengthDto(@ValidPassword(min = 5, type = PasswordType.FULL) String value) {
            public PasswordMinLengthDto(String value) {
                this.value = value;
            }
        }

    public record PasswordAlphanumericDto(@ValidPassword(min = 5, type = PasswordType.ALPHANUMERIC) String value) {
            public PasswordAlphanumericDto(String value) {
                this.value = value;
            }
        }

    public record PasswordLetterDigitDto(@ValidPassword(min = 5, type = PasswordType.LETTER_DIGIT) String value) {
            public PasswordLetterDigitDto(String value) {
                this.value = value;
            }
        }

    public record PasswordLetterMixedDto(@ValidPassword(min = 5, type = PasswordType.LETTER_MIXED_CASE) String value) {
            public PasswordLetterMixedDto(String value) {
                this.value = value;
            }
        }

    public record PasswordFullDto(@ValidPassword(min = 5, type = PasswordType.FULL) String value) {
            public PasswordFullDto(String value) {
                this.value = value;
            }
        }

    public record PinLengthDto(@ValidPIN(length = 5) String value) {
            public PinLengthDto(String value) {
                this.value = value;
            }
        }

    public record PinRepetitiveDto(@ValidPIN(maxAllowedRepetitive = 2) String value) {
            public PinRepetitiveDto(String value) {
                this.value = value;
            }
        }

    public record PinSequentialDto(@ValidPIN(maxAllowedSequential = 3) String value) {
            public PinSequentialDto(String value) {
                this.value = value;
            }
        }

    public record SlugBaseDto(@ValidSlug String value) {
            public SlugBaseDto(String value) {
                this.value = value;
            }
        }

    public record SlugMinDto(@ValidSlug(min = 3) String value) {
            public SlugMinDto(String value) {
                this.value = value;
            }
        }

    public record SlugMaxDto(@ValidSlug(max = 5) String value) {
            public SlugMaxDto(String value) {
                this.value = value;
            }
        }

    public record TaxIdBaseDto(@ValidTaxID(country = "US") String value) {
            public TaxIdBaseDto(String value) {
                this.value = value;
            }
        }

    public record TaxIdLengthDto(@ValidTaxID String value) {
            public TaxIdLengthDto(String value) {
                this.value = value;
            }
        }

    public record TaxIdNumbersDto(@ValidTaxID String value) {
            public TaxIdNumbersDto(String value) {
                this.value = value;
            }
        }

    public record UsernameBaseDto(@ValidUsername String value) {
            public UsernameBaseDto(String value) {
                this.value = value;
            }
        }

    public record UsernameMinDto(@ValidUsername(min = 8) String value) {
            public UsernameMinDto(String value) {
                this.value = value;
            }
        }

    public record UsernameMaxDto(@ValidUsername(max = 6) String value) {
            public UsernameMaxDto(String value) {
                this.value = value;
            }
        }

    @Test
    void accountNumber_numbers() {
        AccountNumberNumbersDto dto = new AccountNumberNumbersDto("abc12345");
        ConstraintViolation<AccountNumberNumbersDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.data.account-number.numbers", v.getMessageTemplate());
        assertEquals("value must be numbers", SUPPORT.resolver().resolve(v, "value", AccountNumberNumbersDto.class));
    }

    @Test
    void accountNumber_min() {
        AccountNumberMinDto dto = new AccountNumberMinDto("12345");
        ConstraintViolation<AccountNumberMinDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.data.account-number.min", v.getMessageTemplate());
        assertEquals("value must have minimal 10 characters", SUPPORT.resolver().resolve(v, "value", AccountNumberMinDto.class));
    }

    @Test
    void accountNumber_max() {
        AccountNumberMaxDto dto = new AccountNumberMaxDto("123456789012");
        ConstraintViolation<AccountNumberMaxDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.data.account-number.max", v.getMessageTemplate());
        assertEquals("value must have maximal 10 characters", SUPPORT.resolver().resolve(v, "value", AccountNumberMaxDto.class));
    }

    @Test
    void base64_pathB() {
        Base64Dto dto = new Base64Dto("not valid base64!!!");
        ConstraintViolation<Base64Dto> v = SUPPORT.firstViolation(dto);

        assertEquals("{friendly.default}", v.getMessageTemplate());
        assertEquals("value must be a valid Base64 encoded string", SUPPORT.resolver().resolve(v, "value", Base64Dto.class));
    }

    @Test
    void hexColor_short() {
        HexColorShortDto dto = new HexColorShortDto("#XYZ");
        ConstraintViolation<HexColorShortDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.data.hex-color.short", v.getMessageTemplate());
        assertEquals("value has invalid short hex color", SUPPORT.resolver().resolve(v, "value", HexColorShortDto.class));
    }

    @Test
    void hexColor_long() {
        HexColorLongDto dto = new HexColorLongDto("#XYZABC");
        ConstraintViolation<HexColorLongDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.data.hex-color.long", v.getMessageTemplate());
        assertEquals("value has invalid long hex color", SUPPORT.resolver().resolve(v, "value", HexColorLongDto.class));
    }

    @Test
    void hexColor_base() {
        HexColorBaseDto dto = new HexColorBaseDto("ZZZZZ");
        ConstraintViolation<HexColorBaseDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.data.hex-color", v.getMessageTemplate());
        assertEquals("value must be a valid hex color code", SUPPORT.resolver().resolve(v, "value", HexColorBaseDto.class));
    }

    @Test
    void isoCode_currency() {
        ISOCodeCurrencyDto dto = new ISOCodeCurrencyDto("ZZZ");
        ConstraintViolation<ISOCodeCurrencyDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.data.iso-code.currency", v.getMessageTemplate());
        assertEquals("value must be a valid currency ISO code", SUPPORT.resolver().resolve(v, "value", ISOCodeCurrencyDto.class));
    }

    @Test
    void isoCode_country() {
        ISOCodeCountryDto dto = new ISOCodeCountryDto("QQ");
        ConstraintViolation<ISOCodeCountryDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.data.iso-code.country", v.getMessageTemplate());
        assertEquals("value must be a valid country ISO code", SUPPORT.resolver().resolve(v, "value", ISOCodeCountryDto.class));
    }

    @Test
    void isoCode_language() {
        ISOCodeLanguageDto dto = new ISOCodeLanguageDto("qq");
        ConstraintViolation<ISOCodeLanguageDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.data.iso-code.language", v.getMessageTemplate());
        assertEquals("value must be a valid language ISO code", SUPPORT.resolver().resolve(v, "value", ISOCodeLanguageDto.class));
    }

    @Test
    void json_pathB() {
        JsonDto dto = new JsonDto("not json");
        ConstraintViolation<JsonDto> v = SUPPORT.firstViolation(dto);

        assertEquals("{friendly.default}", v.getMessageTemplate());
        assertEquals("value must be a valid JSON object", SUPPORT.resolver().resolve(v, "value", JsonDto.class));
    }

    @Test
    void name_min() {
        NameMinDto dto = new NameMinDto("Jo");
        ConstraintViolation<NameMinDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.data.name.min", v.getMessageTemplate());
        assertEquals("value must have minimal 5 characters", SUPPORT.resolver().resolve(v, "value", NameMinDto.class));
    }

    @Test
    void name_max() {
        NameMaxDto dto = new NameMaxDto("Alexander");
        ConstraintViolation<NameMaxDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.data.name.max", v.getMessageTemplate());
        assertEquals("value must have maximal 5 characters", SUPPORT.resolver().resolve(v, "value", NameMaxDto.class));
    }

    @Test
    void name_digits() {
        NameDigitsDto dto = new NameDigitsDto("John123");
        ConstraintViolation<NameDigitsDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.data.name.digits", v.getMessageTemplate());

        String resolved = SUPPORT.resolver().resolve(v, "value", NameDigitsDto.class);

        assertEquals(SUPPORT.messages().getMessage("validation.data.name.digits", "value"), resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void name_symbol() {
        NameSymbolDto dto = new NameSymbolDto("John#Doe");
        ConstraintViolation<NameSymbolDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.data.name.symbol", v.getMessageTemplate());

        String symbolsJoined = MessageUtils.join(Set.of("'", " ", "."));
        String expected = SUPPORT.messages().getMessage("validation.data.name.symbol", "value", symbolsJoined);
        String resolved = SUPPORT.resolver().resolve(v, "value", NameSymbolDto.class);

        assertEquals(expected, resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void nationalId_base() {
        NationalIdBaseDto dto = new NationalIdBaseDto("1234567890123456");
        ConstraintViolation<NationalIdBaseDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.data.national-id", v.getMessageTemplate());
        assertEquals("value must be a valid National ID", SUPPORT.resolver().resolve(v, "value", NationalIdBaseDto.class));
    }

    @Test
    void nationalId_numbers() {
        NationalIdNumbersDto dto = new NationalIdNumbersDto("123456789012345X");
        ConstraintViolation<NationalIdNumbersDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.data.national-id.numbers", v.getMessageTemplate());
        assertEquals("value must be numbers", SUPPORT.resolver().resolve(v, "value", NationalIdNumbersDto.class));
    }

    @Test
    void nationalId_length() {
        NationalIdLengthDto dto = new NationalIdLengthDto("12345");
        ConstraintViolation<NationalIdLengthDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.data.national-id.length", v.getMessageTemplate());
        assertEquals("value must have 16 digits", SUPPORT.resolver().resolve(v, "value", NationalIdLengthDto.class));
    }

    @Test
    void password_whitespace() {
        PasswordWhitespaceDto dto = new PasswordWhitespaceDto("hello world");
        ConstraintViolation<PasswordWhitespaceDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.data.password", v.getMessageTemplate());
        assertEquals("value must be a valid password", SUPPORT.resolver().resolve(v, "value", PasswordWhitespaceDto.class));
    }

    @Test
    void password_minLength() {
        PasswordMinLengthDto dto = new PasswordMinLengthDto("Ab1!");
        ConstraintViolation<PasswordMinLengthDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.data.password", v.getMessageTemplate());
        assertEquals("value must be a valid password", SUPPORT.resolver().resolve(v, "value", PasswordMinLengthDto.class));
    }

    @Test
    void password_alphanumeric() {
        PasswordAlphanumericDto dto = new PasswordAlphanumericDto("Hello@World");
        ConstraintViolation<PasswordAlphanumericDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.data.password.alphanumeric", v.getMessageTemplate());
        assertEquals("value must contain only letters and digits", SUPPORT.resolver().resolve(v, "value", PasswordAlphanumericDto.class));
    }

    @Test
    void password_letterDigit() {
        PasswordLetterDigitDto dto = new PasswordLetterDigitDto("HelloWorld");
        ConstraintViolation<PasswordLetterDigitDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.data.password.letter-digit", v.getMessageTemplate());
        assertEquals("value must contain at least one letter and one digit", SUPPORT.resolver().resolve(v, "value", PasswordLetterDigitDto.class));
    }

    @Test
    void password_letterMixed() {
        PasswordLetterMixedDto dto = new PasswordLetterMixedDto("HELLOWORLD1!");
        ConstraintViolation<PasswordLetterMixedDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.data.password.letter-mixed", v.getMessageTemplate());
        assertEquals("value must contain at least one lowercase and one uppercase letter", SUPPORT.resolver().resolve(v, "value", PasswordLetterMixedDto.class));
    }

    @Test
    void password_full() {
        PasswordFullDto dto = new PasswordFullDto("helloworld123");
        ConstraintViolation<PasswordFullDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.data.password.full", v.getMessageTemplate());
        assertEquals("value must contain at least one lowercase letter, one uppercase letter, one digit, and one symbol", SUPPORT.resolver().resolve(v, "value", PasswordFullDto.class));
    }

    @Test
    void pin_length() {
        PinLengthDto dto = new PinLengthDto("1234");
        ConstraintViolation<PinLengthDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.data.pin.length", v.getMessageTemplate());
        assertEquals("value must be numeric and of length 5", SUPPORT.resolver().resolve(v, "value", PinLengthDto.class));
    }

    @Test
    void pin_repetitive() {
        PinRepetitiveDto dto = new PinRepetitiveDto("111234");
        ConstraintViolation<PinRepetitiveDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.data.pin.repetitive", v.getMessageTemplate());
        assertEquals("value must not have too many repetitive digits", SUPPORT.resolver().resolve(v, "value", PinRepetitiveDto.class));
    }

    @Test
    void pin_sequential() {
        PinSequentialDto dto = new PinSequentialDto("123456");
        ConstraintViolation<PinSequentialDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.data.pin.sequential", v.getMessageTemplate());
        assertEquals("value must not have sequential digits", SUPPORT.resolver().resolve(v, "value", PinSequentialDto.class));
    }

    @Test
    void slug_base() {
        SlugBaseDto dto = new SlugBaseDto("Invalid Slug!");
        ConstraintViolation<SlugBaseDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.data.slug", v.getMessageTemplate());
        assertEquals("value must be a valid slug", SUPPORT.resolver().resolve(v, "value", SlugBaseDto.class));
    }

    @Test
    void slug_min() {
        SlugMinDto dto = new SlugMinDto("ab");
        ConstraintViolation<SlugMinDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.data.slug.min", v.getMessageTemplate());
        assertEquals("value must have minimal 3 characters", SUPPORT.resolver().resolve(v, "value", SlugMinDto.class));
    }

    @Test
    void slug_max() {
        SlugMaxDto dto = new SlugMaxDto("abcdef");
        ConstraintViolation<SlugMaxDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.data.slug.max", v.getMessageTemplate());
        assertEquals("value must have maximal 5 characters", SUPPORT.resolver().resolve(v, "value", SlugMaxDto.class));
    }

    @Test
    void taxId_base() {
        TaxIdBaseDto dto = new TaxIdBaseDto("123456789012345");
        ConstraintViolation<TaxIdBaseDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.data.tax-id", v.getMessageTemplate());
        assertEquals("value must be a valid tax ID", SUPPORT.resolver().resolve(v, "value", TaxIdBaseDto.class));
    }

    @Test
    void taxId_numbers() {
        TaxIdNumbersDto dto = new TaxIdNumbersDto("abcdefghijklmno");
        ConstraintViolation<TaxIdNumbersDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.data.tax-id.numbers", v.getMessageTemplate());
        assertEquals("value must be numbers", SUPPORT.resolver().resolve(v, "value", TaxIdNumbersDto.class));
    }

    @Test
    void taxId_length() {
        TaxIdLengthDto dto = new TaxIdLengthDto("12345");
        ConstraintViolation<TaxIdLengthDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.data.tax-id.length", v.getMessageTemplate());
        assertEquals("value must have 15-16 digits", SUPPORT.resolver().resolve(v, "value", TaxIdLengthDto.class));
    }

    @Test
    void username_base() {
        UsernameBaseDto dto = new UsernameBaseDto("invalid user!");
        ConstraintViolation<UsernameBaseDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.data.username", v.getMessageTemplate());
        assertEquals("value must be a valid username", SUPPORT.resolver().resolve(v, "value", UsernameBaseDto.class));
    }

    @Test
    void username_min() {
        UsernameMinDto dto = new UsernameMinDto("abc");
        ConstraintViolation<UsernameMinDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.data.username.min", v.getMessageTemplate());
        assertEquals("value must have minimal 8 characters", SUPPORT.resolver().resolve(v, "value", UsernameMinDto.class));
    }

    @Test
    void username_max() {
        UsernameMaxDto dto = new UsernameMaxDto("abcdefg");
        ConstraintViolation<UsernameMaxDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.data.username.max", v.getMessageTemplate());
        assertEquals("value must have maximal 6 characters", SUPPORT.resolver().resolve(v, "value", UsernameMaxDto.class));
    }
}
