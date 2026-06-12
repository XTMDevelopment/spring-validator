package id.xtramile.validator.integration;

import id.xtramile.validator.annotation.finance.*;
import id.xtramile.validator.web.FriendlyMessageResolver;
import id.xtramile.validator.web.MessageResourceResolver;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static id.xtramile.validator.integration.ValidationMessageAssertions.assertNoRawValidationKey;
import static org.junit.jupiter.api.Assertions.*;

class FinanceValidatorMessageIntegrationTest {

    private static final Validator VALIDATOR;
    private static final FriendlyMessageResolver RESOLVER;
    private static final MessageResourceResolver MESSAGES;

    static {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        VALIDATOR = factory.getValidator();
        MESSAGES = new MessageResourceResolver("en");
        RESOLVER = new FriendlyMessageResolver(MESSAGES);
    }

    public static class CardExpiryDto {
        @ValidCardExpiry
        private final String value;

        public CardExpiryDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class CardExpiryFutureDto {
        @ValidCardExpiry
        private final String value;

        public CardExpiryFutureDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class CardNumberDto {
        @ValidCardNumber
        private final String value;

        public CardNumberDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class CardNumberStripDto {
        @ValidCardNumber(stripSeparators = false)
        private final String value;

        public CardNumberStripDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class CurrencyCodeDto {
        @ValidCurrencyCode
        private final String value;

        public CurrencyCodeDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class CvvDto {
        @ValidCVV
        private final String value;

        public CvvDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class CvvFourDto {
        @ValidCVV(allowFourDigits = false)
        private final String value;

        public CvvFourDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class IBANDto {
        @ValidIBAN
        private final String value;

        public IBANDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class PaymentReferenceDto {
        @ValidPaymentReference(pattern = "^[A-Z]{2}\\d{8}$")
        private final String value;

        public PaymentReferenceDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class SwiftCodeDto {
        @ValidSwiftCode
        private final String value;

        public SwiftCodeDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class TransactionAmountMinDto {
        @ValidTransactionAmount(min = 1000L)
        private final Long value;

        public TransactionAmountMinDto(Long value) {
            this.value = value;
        }

        public Long value() {
            return value;
        }
    }

    public static class TransactionAmountMaxDto {
        @ValidTransactionAmount(max = 1000000L)
        private final Long value;

        public TransactionAmountMaxDto(Long value) {
            this.value = value;
        }

        public Long value() {
            return value;
        }
    }

    public static class TransactionAmountZeroDto {
        @ValidTransactionAmount(allowZero = false)
        private final Long value;

        public TransactionAmountZeroDto(Long value) {
            this.value = value;
        }

        public Long value() {
            return value;
        }
    }

    private <T> ConstraintViolation<T> firstViolation(T dto) {
        Set<ConstraintViolation<T>> violations = VALIDATOR.validate(dto);

        assertFalse(violations.isEmpty(), "Expected at least one violation but got none");

        return violations.iterator().next();
    }

    @Test
    void cardExpiry_invalidFormat() {
        CardExpiryDto dto = new CardExpiryDto("not-expiry");
        ConstraintViolation<CardExpiryDto> v = firstViolation(dto);

        assertEquals("validation.finance.card-expiry", v.getMessageTemplate());

        String resolved = RESOLVER.resolve(v, "value", CardExpiryDto.class);

        assertEquals(MESSAGES.getMessage("validation.finance.card-expiry", "value"), resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void cardExpiry_expired() {
        CardExpiryFutureDto dto = new CardExpiryFutureDto("01/20");
        ConstraintViolation<CardExpiryFutureDto> v = firstViolation(dto);

        assertEquals("validation.finance.card-expiry.future", v.getMessageTemplate());

        String resolved = RESOLVER.resolve(v, "value", CardExpiryFutureDto.class);

        assertEquals(MESSAGES.getMessage("validation.finance.card-expiry.future", "value"), resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void cardNumber_invalid() {
        CardNumberDto dto = new CardNumberDto("123456789012");
        ConstraintViolation<CardNumberDto> v = firstViolation(dto);

        assertEquals("validation.finance.card-number", v.getMessageTemplate());

        String resolved = RESOLVER.resolve(v, "value", CardNumberDto.class);

        assertEquals(MESSAGES.getMessage("validation.finance.card-number", "value"), resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void cardNumber_strip() {
        CardNumberStripDto dto = new CardNumberStripDto("4111 1111 1111 1111");
        ConstraintViolation<CardNumberStripDto> v = firstViolation(dto);

        assertEquals("validation.finance.card-number.strip", v.getMessageTemplate());

        String resolved = RESOLVER.resolve(v, "value", CardNumberStripDto.class);

        assertEquals(MESSAGES.getMessage("validation.finance.card-number.strip", "value"), resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void currencyCode_pathB() {
        CurrencyCodeDto dto = new CurrencyCodeDto("INVALID");
        ConstraintViolation<CurrencyCodeDto> v = firstViolation(dto);

        assertEquals("{friendly.default}", v.getMessageTemplate());

        String resolved = RESOLVER.resolve(v, "value", CurrencyCodeDto.class);

        assertEquals(MESSAGES.getMessage("validation.finance.currency-code", "value"), resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void cvv_invalid() {
        CvvDto dto = new CvvDto("12");
        ConstraintViolation<CvvDto> v = firstViolation(dto);

        assertEquals("validation.finance.cvv", v.getMessageTemplate());

        String resolved = RESOLVER.resolve(v, "value", CvvDto.class);

        assertEquals(MESSAGES.getMessage("validation.finance.cvv", "value"), resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void cvv_fourDigitsNotAllowed() {
        CvvFourDto dto = new CvvFourDto("1234");
        ConstraintViolation<CvvFourDto> v = firstViolation(dto);

        assertEquals("validation.finance.cvv.four", v.getMessageTemplate());

        String resolved = RESOLVER.resolve(v, "value", CvvFourDto.class);

        assertEquals(MESSAGES.getMessage("validation.finance.cvv.four", "value"), resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void iban_pathB() {
        IBANDto dto = new IBANDto("NOTANIBAN");
        ConstraintViolation<IBANDto> v = firstViolation(dto);

        assertEquals("{friendly.default}", v.getMessageTemplate());

        String resolved = RESOLVER.resolve(v, "value", IBANDto.class);

        assertEquals(MESSAGES.getMessage("validation.finance.iban", "value"), resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void paymentReference_patternMismatch() {
        PaymentReferenceDto dto = new PaymentReferenceDto("invalid");
        ConstraintViolation<PaymentReferenceDto> v = firstViolation(dto);

        assertEquals("validation.finance.payment-reference.pattern", v.getMessageTemplate());

        String resolved = RESOLVER.resolve(v, "value", PaymentReferenceDto.class);

        assertTrue(resolved.contains("pattern"), "Expected 'pattern' in message: " + resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void swiftCode_pathB() {
        SwiftCodeDto dto = new SwiftCodeDto("12345678");
        ConstraintViolation<SwiftCodeDto> v = firstViolation(dto);

        assertEquals("{friendly.default}", v.getMessageTemplate());

        String resolved = RESOLVER.resolve(v, "value", SwiftCodeDto.class);

        assertEquals(MESSAGES.getMessage("validation.finance.swift-code", "value"), resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void transactionAmount_belowMin() {
        TransactionAmountMinDto dto = new TransactionAmountMinDto(500L);
        ConstraintViolation<TransactionAmountMinDto> v = firstViolation(dto);

        assertEquals("validation.finance.transaction-amount.min", v.getMessageTemplate());

        String resolved = RESOLVER.resolve(v, "value", TransactionAmountMinDto.class);

        assertEquals(MESSAGES.getMessage("validation.finance.transaction-amount.min", "value", "1000"), resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void transactionAmount_aboveMax() {
        TransactionAmountMaxDto dto = new TransactionAmountMaxDto(2000000L);
        ConstraintViolation<TransactionAmountMaxDto> v = firstViolation(dto);

        assertEquals("validation.finance.transaction-amount.max", v.getMessageTemplate());

        String resolved = RESOLVER.resolve(v, "value", TransactionAmountMaxDto.class);

        assertEquals(MESSAGES.getMessage("validation.finance.transaction-amount.max", "value", "1000000"), resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void transactionAmount_zero() {
        TransactionAmountZeroDto dto = new TransactionAmountZeroDto(0L);
        ConstraintViolation<TransactionAmountZeroDto> v = firstViolation(dto);

        assertEquals("validation.finance.transaction-amount.zero", v.getMessageTemplate());

        String resolved = RESOLVER.resolve(v, "value", TransactionAmountZeroDto.class);

        assertEquals(MESSAGES.getMessage("validation.finance.transaction-amount.zero", "value"), resolved);
        assertNoRawValidationKey(resolved);
    }
}
