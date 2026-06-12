package id.xtramile.validator.integration;

import id.xtramile.validator.annotation.finance.*;
import id.xtramile.validator.support.ValidationMessageTestSupport;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

import static id.xtramile.validator.integration.ValidationMessageAssertions.assertNoRawValidationKey;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FinanceValidatorMessageIntegrationTest {

    private static final ValidationMessageTestSupport SUPPORT = ValidationMessageTestSupport.EN;

    @Test
    void cardExpiry_invalidFormat() {
        CardExpiryDto dto = new CardExpiryDto("not-expiry");
        ConstraintViolation<CardExpiryDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.finance.card-expiry", v.getMessageTemplate());

        String resolved = SUPPORT.resolver().resolve(v, "value", CardExpiryDto.class);

        assertEquals(SUPPORT.messages().getMessage("validation.finance.card-expiry", "value"), resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void cardExpiry_expired() {
        CardExpiryFutureDto dto = new CardExpiryFutureDto("01/20");
        ConstraintViolation<CardExpiryFutureDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.finance.card-expiry.future", v.getMessageTemplate());

        String resolved = SUPPORT.resolver().resolve(v, "value", CardExpiryFutureDto.class);

        assertEquals(SUPPORT.messages().getMessage("validation.finance.card-expiry.future", "value"), resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void cardNumber_invalid() {
        CardNumberDto dto = new CardNumberDto("123456789012");
        ConstraintViolation<CardNumberDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.finance.card-number", v.getMessageTemplate());

        String resolved = SUPPORT.resolver().resolve(v, "value", CardNumberDto.class);

        assertEquals(SUPPORT.messages().getMessage("validation.finance.card-number", "value"), resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void cardNumber_strip() {
        CardNumberStripDto dto = new CardNumberStripDto("4111 1111 1111 1111");
        ConstraintViolation<CardNumberStripDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.finance.card-number.strip", v.getMessageTemplate());

        String resolved = SUPPORT.resolver().resolve(v, "value", CardNumberStripDto.class);

        assertEquals(SUPPORT.messages().getMessage("validation.finance.card-number.strip", "value"), resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void currencyCode_pathB() {
        CurrencyCodeDto dto = new CurrencyCodeDto("INVALID");
        ConstraintViolation<CurrencyCodeDto> v = SUPPORT.firstViolation(dto);

        assertEquals("{friendly.default}", v.getMessageTemplate());

        String resolved = SUPPORT.resolver().resolve(v, "value", CurrencyCodeDto.class);

        assertEquals(SUPPORT.messages().getMessage("validation.finance.currency-code", "value"), resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void cvv_invalid() {
        CvvDto dto = new CvvDto("12");
        ConstraintViolation<CvvDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.finance.cvv", v.getMessageTemplate());

        String resolved = SUPPORT.resolver().resolve(v, "value", CvvDto.class);

        assertEquals(SUPPORT.messages().getMessage("validation.finance.cvv", "value"), resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void cvv_fourDigitsNotAllowed() {
        CvvFourDto dto = new CvvFourDto("1234");
        ConstraintViolation<CvvFourDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.finance.cvv.four", v.getMessageTemplate());

        String resolved = SUPPORT.resolver().resolve(v, "value", CvvFourDto.class);

        assertEquals(SUPPORT.messages().getMessage("validation.finance.cvv.four", "value"), resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void iban_pathB() {
        IBANDto dto = new IBANDto("NOTANIBAN");
        ConstraintViolation<IBANDto> v = SUPPORT.firstViolation(dto);

        assertEquals("{friendly.default}", v.getMessageTemplate());

        String resolved = SUPPORT.resolver().resolve(v, "value", IBANDto.class);

        assertEquals(SUPPORT.messages().getMessage("validation.finance.iban", "value"), resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void paymentReference_patternMismatch() {
        PaymentReferenceDto dto = new PaymentReferenceDto("invalid");
        ConstraintViolation<PaymentReferenceDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.finance.payment-reference.pattern", v.getMessageTemplate());

        String resolved = SUPPORT.resolver().resolve(v, "value", PaymentReferenceDto.class);

        assertTrue(resolved.contains("pattern"), "Expected 'pattern' in message: " + resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void swiftCode_pathB() {
        SwiftCodeDto dto = new SwiftCodeDto("12345678");
        ConstraintViolation<SwiftCodeDto> v = SUPPORT.firstViolation(dto);

        assertEquals("{friendly.default}", v.getMessageTemplate());

        String resolved = SUPPORT.resolver().resolve(v, "value", SwiftCodeDto.class);

        assertEquals(SUPPORT.messages().getMessage("validation.finance.swift-code", "value"), resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void transactionAmount_belowMin() {
        TransactionAmountMinDto dto = new TransactionAmountMinDto(500L);
        ConstraintViolation<TransactionAmountMinDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.finance.transaction-amount.min", v.getMessageTemplate());

        String resolved = SUPPORT.resolver().resolve(v, "value", TransactionAmountMinDto.class);

        assertEquals(SUPPORT.messages().getMessage("validation.finance.transaction-amount.min", "value", "1000"), resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void transactionAmount_aboveMax() {
        TransactionAmountMaxDto dto = new TransactionAmountMaxDto(2000000L);
        ConstraintViolation<TransactionAmountMaxDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.finance.transaction-amount.max", v.getMessageTemplate());

        String resolved = SUPPORT.resolver().resolve(v, "value", TransactionAmountMaxDto.class);

        assertEquals(SUPPORT.messages().getMessage("validation.finance.transaction-amount.max", "value", "1000000"), resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void transactionAmount_zero() {
        TransactionAmountZeroDto dto = new TransactionAmountZeroDto(0L);
        ConstraintViolation<TransactionAmountZeroDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.finance.transaction-amount.zero", v.getMessageTemplate());

        String resolved = SUPPORT.resolver().resolve(v, "value", TransactionAmountZeroDto.class);

        assertEquals(SUPPORT.messages().getMessage("validation.finance.transaction-amount.zero", "value"), resolved);
        assertNoRawValidationKey(resolved);
    }

    public record CardExpiryDto(@ValidCardExpiry String value) {
        public CardExpiryDto(String value) {
            this.value = value;
        }
    }

    public record CardExpiryFutureDto(@ValidCardExpiry String value) {
        public CardExpiryFutureDto(String value) {
            this.value = value;
        }
    }

    public record CardNumberDto(@ValidCardNumber String value) {
        public CardNumberDto(String value) {
            this.value = value;
        }
    }

    public record CardNumberStripDto(@ValidCardNumber(stripSeparators = false) String value) {
        public CardNumberStripDto(String value) {
            this.value = value;
        }
    }

    public record CurrencyCodeDto(@ValidCurrencyCode String value) {
        public CurrencyCodeDto(String value) {
            this.value = value;
        }
    }

    public record CvvDto(@ValidCVV String value) {
        public CvvDto(String value) {
            this.value = value;
        }
    }

    public record CvvFourDto(@ValidCVV(allowFourDigits = false) String value) {
        public CvvFourDto(String value) {
            this.value = value;
        }
    }

    public record IBANDto(@ValidIBAN String value) {
        public IBANDto(String value) {
            this.value = value;
        }
    }

    public record PaymentReferenceDto(@ValidPaymentReference(pattern = "^[A-Z]{2}\\d{8}$") String value) {
        public PaymentReferenceDto(String value) {
            this.value = value;
        }
    }

    public record SwiftCodeDto(@ValidSwiftCode String value) {
        public SwiftCodeDto(String value) {
            this.value = value;
        }
    }

    public record TransactionAmountMinDto(@ValidTransactionAmount(min = 1000L) Long value) {
        public TransactionAmountMinDto(Long value) {
            this.value = value;
        }
    }

    public record TransactionAmountMaxDto(@ValidTransactionAmount(max = 1000000L) Long value) {
        public TransactionAmountMaxDto(Long value) {
            this.value = value;
        }
    }

    public record TransactionAmountZeroDto(@ValidTransactionAmount(allowZero = false) Long value) {
        public TransactionAmountZeroDto(Long value) {
            this.value = value;
        }
    }
}
