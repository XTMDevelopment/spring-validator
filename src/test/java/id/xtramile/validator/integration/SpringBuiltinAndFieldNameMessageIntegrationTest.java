package id.xtramile.validator.integration;

import id.xtramile.validator.annotation.common.FieldName;
import id.xtramile.validator.support.ValidationMessageTestSupport;
import id.xtramile.validator.web.BeanValidationMessageDescriptors;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.constraints.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static id.xtramile.validator.integration.ValidationMessageAssertions.assertNoRawValidationKey;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Ensures stock Jakarta Bean Validation constraints resolve to {@code messages_*.properties} entries
 * under {@code validation.spring.*} and honor {@link FieldName}, never leaking technical descriptor
 * keys (braced or unbraced) such as {@code jakarta.validation.constraints.NotBlank.message}.
 */
class SpringBuiltinAndFieldNameMessageIntegrationTest {

    private static final ValidationMessageTestSupport SUPPORT = ValidationMessageTestSupport.EN;

    public static class NotBlankDto {
        @FieldName("Borrower ID")
        @NotBlank
        private final String borrowerId;

        public NotBlankDto(String borrowerId) {
            this.borrowerId = borrowerId;
        }

        public String borrowerId() {
            return borrowerId;
        }
    }

    public static class NotNullDto {
        @FieldName("Account ID")
        @NotNull
        private final Integer accountId;

        public NotNullDto(Integer accountId) {
            this.accountId = accountId;
        }

        public Integer accountId() {
            return accountId;
        }
    }

    public static class MinDto {
        @FieldName("Quantity")
        @Min(10)
        private final int qty;

        public MinDto(int qty) {
            this.qty = qty;
        }

        public int qty() {
            return qty;
        }
    }

    public static class MaxDto {
        @FieldName("Age")
        @Max(100)
        private final int age;

        public MaxDto(int age) {
            this.age = age;
        }

        public int age() {
            return age;
        }
    }

    public static class SizeDto {
        @FieldName("Code")
        @Size(min = 2, max = 5)
        private final String code;

        public SizeDto(String code) {
            this.code = code;
        }

        public String code() {
            return code;
        }
    }

    public static class EmailDto {
        @FieldName("Work Email")
        @Email
        private final String email;

        public EmailDto(String email) {
            this.email = email;
        }

        public String email() {
            return email;
        }
    }

    public static class PatternDto {
        @FieldName("PIN")
        @Pattern(regexp = "\\d+")
        private final String pin;

        public PatternDto(String pin) {
            this.pin = pin;
        }

        public String pin() {
            return pin;
        }
    }

    public static class DecimalMinDto {
        @FieldName("Amount")
        @DecimalMin("10.0")
        private final BigDecimal amount;

        public DecimalMinDto(BigDecimal amount) {
            this.amount = amount;
        }

        public BigDecimal amount() {
            return amount;
        }
    }

    public static class PositiveDto {
        @FieldName("Score")
        @Positive
        private final Integer score;

        public PositiveDto(Integer score) {
            this.score = score;
        }

        public Integer score() {
            return score;
        }
    }

    public static class NotEmptyDto {
        @FieldName("Tags")
        @NotEmpty
        private final List<String> tags;

        public NotEmptyDto(List<String> tags) {
            this.tags = tags;
        }

        public List<String> tags() {
            return tags;
        }
    }

    public static class DigitsDto {
        @FieldName("Price")
        @Digits(integer = 2, fraction = 1)
        private final BigDecimal price;

        public DigitsDto(BigDecimal price) {
            this.price = price;
        }

        public BigDecimal price() {
            return price;
        }
    }

    @Test
    void notBlank_withFieldName() {
        NotBlankDto dto = new NotBlankDto("");
        ConstraintViolation<NotBlankDto> v = SUPPORT.firstViolation(dto);

        assertDescriptorIsDefaultStockKey(v.getMessageTemplate());

        String resolved = SUPPORT.resolver().resolve(v, "borrowerId", NotBlankDto.class);

        assertEquals(SUPPORT.messages().getMessage("validation.spring.not-blank", "Borrower ID"), resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void notNull_withFieldName() {
        NotNullDto dto = new NotNullDto(null);
        ConstraintViolation<NotNullDto> v = SUPPORT.firstViolation(dto);

        assertDescriptorIsDefaultStockKey(v.getMessageTemplate());

        String resolved = SUPPORT.resolver().resolve(v, "accountId", NotNullDto.class);

        assertEquals(SUPPORT.messages().getMessage("validation.spring.not-null", "Account ID"), resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void min_withFieldName() {
        MinDto dto = new MinDto(5);
        ConstraintViolation<MinDto> v = SUPPORT.firstViolation(dto);

        assertDescriptorIsDefaultStockKey(v.getMessageTemplate());

        String resolved = SUPPORT.resolver().resolve(v, "qty", MinDto.class);

        assertEquals(SUPPORT.messages().getMessage("validation.spring.min", "Quantity", 10L), resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void max_withFieldName() {
        MaxDto dto = new MaxDto(150);
        ConstraintViolation<MaxDto> v = SUPPORT.firstViolation(dto);

        assertDescriptorIsDefaultStockKey(v.getMessageTemplate());

        String resolved = SUPPORT.resolver().resolve(v, "age", MaxDto.class);

        assertEquals(SUPPORT.messages().getMessage("validation.spring.max", "Age", 100L), resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void size_withFieldName() {
        SizeDto dto = new SizeDto("X");
        ConstraintViolation<SizeDto> v = SUPPORT.firstViolation(dto);

        assertDescriptorIsDefaultStockKey(v.getMessageTemplate());

        String resolved = SUPPORT.resolver().resolve(v, "code", SizeDto.class);

        assertEquals(SUPPORT.messages().getMessage("validation.spring.size", "Code", 2, 5), resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void email_withFieldName() {
        EmailDto dto = new EmailDto("not-an-email");
        ConstraintViolation<EmailDto> v = SUPPORT.firstViolation(dto);

        assertDescriptorIsDefaultStockKey(v.getMessageTemplate());

        String resolved = SUPPORT.resolver().resolve(v, "email", EmailDto.class);

        assertEquals(SUPPORT.messages().getMessage("validation.spring.email", "Work Email"), resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void pattern_withFieldName() {
        PatternDto dto = new PatternDto("abc");
        ConstraintViolation<PatternDto> v = SUPPORT.firstViolation(dto);

        assertDescriptorIsDefaultStockKey(v.getMessageTemplate());

        String resolved = SUPPORT.resolver().resolve(v, "pin", PatternDto.class);

        assertEquals(SUPPORT.messages().getMessage("validation.spring.pattern", "PIN"), resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void decimalMin_withFieldName() {
        DecimalMinDto dto = new DecimalMinDto(new BigDecimal("1.0"));
        ConstraintViolation<DecimalMinDto> v = SUPPORT.firstViolation(dto);

        assertDescriptorIsDefaultStockKey(v.getMessageTemplate());

        String resolved = SUPPORT.resolver().resolve(v, "amount", DecimalMinDto.class);

        assertEquals(SUPPORT.messages().getMessage("validation.spring.decimal-min", "Amount", "10.0"), resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void positive_withFieldName() {
        PositiveDto dto = new PositiveDto(-1);
        ConstraintViolation<PositiveDto> v = SUPPORT.firstViolation(dto);

        assertDescriptorIsDefaultStockKey(v.getMessageTemplate());

        String resolved = SUPPORT.resolver().resolve(v, "score", PositiveDto.class);

        assertEquals(SUPPORT.messages().getMessage("validation.spring.positive", "Score"), resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void notEmpty_withFieldName() {
        NotEmptyDto dto = new NotEmptyDto(Collections.emptyList());
        ConstraintViolation<NotEmptyDto> v = SUPPORT.firstViolation(dto);

        assertDescriptorIsDefaultStockKey(v.getMessageTemplate());

        String resolved = SUPPORT.resolver().resolve(v, "tags", NotEmptyDto.class);

        assertEquals(SUPPORT.messages().getMessage("validation.spring.not-empty", "Tags"), resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void digits_withFieldName() {
        DigitsDto dto = new DigitsDto(new BigDecimal("123.45"));
        ConstraintViolation<DigitsDto> v = SUPPORT.firstViolation(dto);

        assertDescriptorIsDefaultStockKey(v.getMessageTemplate());

        String resolved = SUPPORT.resolver().resolve(v, "price", DigitsDto.class);

        assertEquals(SUPPORT.messages().getMessage("validation.spring.digits", "Price", 2, 1), resolved);
        assertNoRawValidationKey(resolved);
    }

    private static void assertDescriptorIsDefaultStockKey(String template) {
        assertTrue(
                BeanValidationMessageDescriptors.isDefaultBeanValidationMessageDescriptor(template),
                "Expected a default BV/HV message descriptor, was: " + template);
    }
}
