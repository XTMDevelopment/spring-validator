package id.xtramile.validator.validator.finance;

import id.xtramile.validator.annotation.finance.ValidPaymentReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PaymentReferenceValidatorTest {

    private PaymentReferenceValidator validator;

    private static ValidPaymentReference getAnnotation(String fieldName) {
        try {
            Field f = PaymentReferenceDummy.class.getDeclaredField(fieldName);
            return f.getAnnotation(ValidPaymentReference.class);
        } catch (NoSuchFieldException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
        validator = new PaymentReferenceValidator();
    }

    @Test
    void testValidDefaultPaymentReferences() {
        validator.initialize(getAnnotation("defaultReference"));

        assertTrue(validator.isValid("REF123456", null));
        assertTrue(validator.isValid("PAY-REF-001", null));
        assertTrue(validator.isValid("INV.2023.001", null));
        assertTrue(validator.isValid("TXN_12345", null));
        assertTrue(validator.isValid("ORDER:ABC123", null));
        assertTrue(validator.isValid("A", null)); // Minimum length
        assertTrue(validator.isValid("A".repeat(64), null)); // Maximum length
    }

    @Test
    void testInvalidDefaultPaymentReferences() {
        validator.initialize(getAnnotation("defaultReference"));

        assertFalse(validator.isValid("REF@123", null)); // Invalid character @
        assertFalse(validator.isValid("REF#123", null)); // Invalid character #
        assertFalse(validator.isValid("REF$123", null)); // Invalid character $
        assertFalse(validator.isValid("REF%123", null)); // Invalid character %
        assertFalse(validator.isValid("REF&123", null)); // Invalid character &
        assertFalse(validator.isValid("REF*123", null)); // Invalid character *
        assertFalse(validator.isValid("REF+123", null)); // Invalid character +
        assertFalse(validator.isValid("REF=123", null)); // Invalid character =
        assertFalse(validator.isValid("REF?123", null)); // Invalid character ?
        assertFalse(validator.isValid("REF<123", null)); // Invalid character <
        assertFalse(validator.isValid("REF>123", null)); // Invalid character >
        assertFalse(validator.isValid("REF|123", null)); // Invalid character |
        assertFalse(validator.isValid("REF\\123", null)); // Invalid character \
        assertFalse(validator.isValid("REF\"123", null)); // Invalid character "
        assertFalse(validator.isValid("REF'123", null)); // Invalid character '
        assertFalse(validator.isValid("REF`123", null)); // Invalid character `
        assertFalse(validator.isValid("REF~123", null)); // Invalid character ~
        assertFalse(validator.isValid("REF!123", null)); // Invalid character !
        assertFalse(validator.isValid("REF@123", null)); // Invalid character @
        assertFalse(validator.isValid("REF#123", null)); // Invalid character #
        assertFalse(validator.isValid("REF$123", null)); // Invalid character $
        assertFalse(validator.isValid("REF%123", null)); // Invalid character %
        assertFalse(validator.isValid("REF^123", null)); // Invalid character ^
        assertFalse(validator.isValid("REF&123", null)); // Invalid character &
        assertFalse(validator.isValid("REF*123", null)); // Invalid character *
        assertFalse(validator.isValid("REF(123", null)); // Invalid character (
        assertFalse(validator.isValid("REF)123", null)); // Invalid character )
        assertFalse(validator.isValid("REF[123", null)); // Invalid character [
        assertFalse(validator.isValid("REF]123", null)); // Invalid character ]
        assertFalse(validator.isValid("REF{123", null)); // Invalid character {
        assertFalse(validator.isValid("REF}123", null)); // Invalid character }
        assertFalse(validator.isValid("REF;123", null)); // Invalid character ;
        assertFalse(validator.isValid("REF,123", null)); // Invalid character ,
        assertFalse(validator.isValid("REF 123", null)); // Space
    }

    @Test
    void testValidAlphanumericReferences() {
        validator.initialize(getAnnotation("alphanumericReference"));

        assertTrue(validator.isValid("ABC123", null)); // 6 chars
        assertTrue(validator.isValid("XYZ789", null)); // 6 chars
        assertTrue(validator.isValid("ABCD1234", null)); // 8 chars
        assertTrue(validator.isValid("ABCDEF123456", null)); // 12 chars
        assertTrue(validator.isValid("123456", null)); // All numbers
        assertTrue(validator.isValid("ABCDEF", null)); // All letters
    }

    @Test
    void testInvalidAlphanumericReferences() {
        validator.initialize(getAnnotation("alphanumericReference"));

        assertFalse(validator.isValid("ABC12", null)); // Too short (5 chars)
        assertFalse(validator.isValid("ABCDEF1234567", null)); // Too long (13 chars)
        assertFalse(validator.isValid("ABC-123", null)); // Contains hyphen
        assertFalse(validator.isValid("ABC_123", null)); // Contains underscore
        assertFalse(validator.isValid("ABC.123", null)); // Contains dot
        assertFalse(validator.isValid("ABC:123", null)); // Contains colon
        assertFalse(validator.isValid("abc123", null)); // Lowercase
    }

    @Test
    void testValidNumericReferences() {
        validator.initialize(getAnnotation("numericReference"));

        assertTrue(validator.isValid("1234567890", null)); // 10 digits
        assertTrue(validator.isValid("123456789012345", null)); // 15 digits
        assertTrue(validator.isValid("0000000000", null)); // All zeros
        assertTrue(validator.isValid("9999999999", null)); // All nines
    }

    @Test
    void testInvalidNumericReferences() {
        validator.initialize(getAnnotation("numericReference"));

        assertFalse(validator.isValid("123456789", null)); // Too short (9 digits)
        assertFalse(validator.isValid("1234567890123456", null)); // Too long (16 digits)
        assertFalse(validator.isValid("123456789a", null)); // Contains letter
        assertFalse(validator.isValid("123456789-", null)); // Contains hyphen
        assertFalse(validator.isValid("123456789.", null)); // Contains dot
    }

    @Test
    void testValidPrefixedReferences() {
        validator.initialize(getAnnotation("prefixedReference"));

        assertTrue(validator.isValid("REF-12345678", null)); // 8 chars after prefix
        assertTrue(validator.isValid("REF-ABCD1234", null)); // Mixed alphanumeric
        assertTrue(validator.isValid("REF-00000000", null)); // All zeros
        assertTrue(validator.isValid("REF-99999999", null)); // All nines
    }

    @Test
    void testInvalidPrefixedReferences() {
        validator.initialize(getAnnotation("prefixedReference"));

        assertFalse(validator.isValid("REF-1234567", null)); // Too short (7 chars)
        assertFalse(validator.isValid("REF-123456789", null)); // Too long (9 chars)
        assertFalse(validator.isValid("REF_12345678", null)); // Wrong separator
        assertFalse(validator.isValid("REF.12345678", null)); // Wrong separator
        assertFalse(validator.isValid("ref-12345678", null)); // Lowercase prefix
        assertFalse(validator.isValid("REF-1234567a", null)); // Contains lowercase
        assertFalse(validator.isValid("REF-1234567-", null)); // Contains hyphen
    }

    @Test
    void testEdgeCaseLengths() {
        validator.initialize(getAnnotation("defaultReference"));

        assertTrue(validator.isValid("A", null)); // Minimum length (1 char)
        assertTrue(validator.isValid("A".repeat(64), null)); // Maximum length (64 chars)
        assertFalse(validator.isValid("A".repeat(65), null)); // Too long (65 chars)
    }

    @Test
    void testSpecialCharactersInDefaultPattern() {
        validator.initialize(getAnnotation("defaultReference"));

        assertTrue(validator.isValid("REF-123", null)); // Hyphen
        assertTrue(validator.isValid("REF_123", null)); // Underscore
        assertTrue(validator.isValid("REF.123", null)); // Dot
        assertTrue(validator.isValid("REF:123", null)); // Colon
        assertTrue(validator.isValid("REF/123", null)); // Forward slash
    }

    @Test
    void testBlankString() {
        validator.initialize(getAnnotation("defaultReference"));

        assertTrue(validator.isValid("", null));
        assertTrue(validator.isValid("   ", null));
    }

    @Test
    void testWhitespaceHandling() {
        validator.initialize(getAnnotation("defaultReference"));

        assertFalse(validator.isValid("REF 123", null)); // Space in middle
        assertFalse(validator.isValid(" REF123", null)); // Leading space
        assertFalse(validator.isValid("REF123 ", null)); // Trailing space
    }

    @Test
    void testCaseSensitivity() {
        validator.initialize(getAnnotation("defaultReference"));

        assertTrue(validator.isValid("REF123", null)); // Uppercase
        assertTrue(validator.isValid("ref123", null)); // Lowercase
        assertTrue(validator.isValid("Ref123", null)); // Mixed case
    }

    private static class PaymentReferenceDummy {
        @ValidPaymentReference
        String defaultReference;

        @ValidPaymentReference(pattern = "^[A-Z0-9]{6,12}$")
        String alphanumericReference;

        @ValidPaymentReference(pattern = "^[0-9]{10,15}$")
        String numericReference;

        @ValidPaymentReference(pattern = "^REF-[A-Z0-9]{8}$")
        String prefixedReference;
    }
}
