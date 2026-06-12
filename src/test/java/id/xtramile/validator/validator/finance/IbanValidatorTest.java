package id.xtramile.validator.validator.finance;

import id.xtramile.validator.annotation.finance.ValidIBAN;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IbanValidatorTest {

    private IbanValidator validator;

    private static ValidIBAN getAnnotation(String fieldName) {
        try {
            Field f = IbanDummy.class.getDeclaredField(fieldName);
            return f.getAnnotation(ValidIBAN.class);
        } catch (NoSuchFieldException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
        validator = new IbanValidator();
    }

    @Test
    void testValidIbanCodes() {
        validator.initialize(getAnnotation("defaultIban"));

        // Valid IBAN examples (these are real IBANs with correct check digits)
        assertTrue(validator.isValid("GB82WEST12345698765432", null)); // UK
        assertTrue(validator.isValid("DE89370400440532013000", null)); // Germany
        assertTrue(validator.isValid("FR1420041010050500013M02606", null)); // France
        assertTrue(validator.isValid("IT60X0542811101000000123456", null)); // Italy
        assertTrue(validator.isValid("ES9121000418450200051332", null)); // Spain
    }

    @Test
    void testValidIbanWithSpaces() {
        validator.initialize(getAnnotation("defaultIban"));

        assertTrue(validator.isValid("GB82 WEST 1234 5698 7654 32", null));
        assertTrue(validator.isValid("DE89 3704 0044 0532 0130 00", null));
        assertTrue(validator.isValid("FR14 2004 1010 0505 0001 3M02 606", null));
    }

    @Test
    void testInvalidIbanLength() {
        validator.initialize(getAnnotation("defaultIban"));

        assertFalse(validator.isValid("GB82WEST1234569876543", null)); // Too short (19 chars)
        assertFalse(validator.isValid("GB82WEST123456987654321", null)); // Too long (21 chars)
        assertFalse(validator.isValid("GB82", null)); // Way too short
        assertFalse(validator.isValid("GB82WEST1234569876543212345678901234567890", null)); // Way too long
    }

    @Test
    void testInvalidIbanCharacters() {
        validator.initialize(getAnnotation("defaultIban"));

        assertFalse(validator.isValid("GB82WEST1234569876543a", null)); // Contains lowercase
        assertFalse(validator.isValid("GB82WEST1234569876543-", null)); // Contains special char
        assertFalse(validator.isValid("GB82WEST1234569876543 ", null)); // Contains space at end
        assertFalse(validator.isValid("GB82WEST1234569876543.", null)); // Contains dot
    }

    @Test
    void testInvalidIbanFormat() {
        validator.initialize(getAnnotation("defaultIban"));

        assertFalse(validator.isValid("1234567890123456789012345", null)); // All numbers
        assertFalse(validator.isValid("ABCDEFGHIJKLMNOPQRSTUVWXYZ", null)); // All letters
        assertFalse(validator.isValid("GB82WEST1234569876543@", null)); // Special character
    }

    @Test
    void testIbanWithDifferentCountries() {
        validator.initialize(getAnnotation("defaultIban"));

        // These are valid format IBANs for different countries
        assertTrue(validator.isValid("AD1200012030200359100100", null)); // Andorra (24 chars)
        assertTrue(validator.isValid("AT611904300234573201", null)); // Austria (20 chars)
        assertTrue(validator.isValid("BE68539007547034", null)); // Belgium (16 chars)
        assertTrue(validator.isValid("CH9300762011623852957", null)); // Switzerland (21 chars)
        assertTrue(validator.isValid("DK5000400440116243", null)); // Denmark (18 chars)
    }

    @Test
    void testIbanCheckDigitValidation() {
        validator.initialize(getAnnotation("defaultIban"));

        // These should fail check digit validation
        assertFalse(validator.isValid("GB83WEST12345698765432", null)); // Wrong check digit
        assertFalse(validator.isValid("DE89370400440532013001", null)); // Wrong check digit
        assertFalse(validator.isValid("FR1420041010050500013M02607", null)); // Wrong check digit
    }

    @Test
    void testIbanCountryCodeValidation() {
        validator.initialize(getAnnotation("defaultIban"));

        // Valid country codes
        assertTrue(validator.isValid("GB82WEST12345698765432", null)); // Great Britain
        assertTrue(validator.isValid("DE89370400440532013000", null)); // Germany
        assertTrue(validator.isValid("FR1420041010050500013M02606", null)); // France
        assertTrue(validator.isValid("IT60X0542811101000000123456", null)); // Italy
        assertTrue(validator.isValid("ES9121000418450200051332", null)); // Spain
    }

    @Test
    void testIbanMinimumLength() {
        validator.initialize(getAnnotation("defaultIban"));

        // Test minimum length (15 characters)
        assertTrue(validator.isValid("AD1200012030200359100100", null)); // 24 chars - valid
        assertFalse(validator.isValid("AD120001203020035910010", null)); // 23 chars - too short
    }

    @Test
    void testIbanMaximumLength() {
        validator.initialize(getAnnotation("defaultIban"));

        // Test maximum length (34 characters)
        assertTrue(validator.isValid("IT60X0542811101000000123456", null)); // 27 chars - valid
        assertFalse(validator.isValid("IT60X05428111010000001234567", null)); // 28 chars - too long
    }

    @Test
    void testBlankString() {
        validator.initialize(getAnnotation("defaultIban"));

        assertTrue(validator.isValid("", null));
        assertTrue(validator.isValid("   ", null));
    }

    @Test
    void testIbanWithMixedCase() {
        validator.initialize(getAnnotation("defaultIban"));

        // IBAN should be case-insensitive (converted to uppercase)
        assertTrue(validator.isValid("gb82west12345698765432", null)); // All lowercase
        assertTrue(validator.isValid("Gb82West12345698765432", null)); // Mixed case
        assertTrue(validator.isValid("GB82WEST12345698765432", null)); // All uppercase
    }

    @Test
    void testIbanWithMultipleSpaces() {
        validator.initialize(getAnnotation("defaultIban"));

        assertTrue(validator.isValid("GB82  WEST  1234  5698  7654  32", null)); // Multiple spaces
        assertTrue(validator.isValid("GB82WEST12345698765432", null)); // No spaces
        assertTrue(validator.isValid("GB 82 WE ST 12 34 56 98 76 54 32", null)); // Spaces between every 2 chars
    }

    @Test
    void testIbanSpecialCharacters() {
        validator.initialize(getAnnotation("defaultIban"));

        assertFalse(validator.isValid("GB82-WEST-1234-5698-7654-32", null)); // Hyphens
        assertFalse(validator.isValid("GB82.WEST.1234.5698.7654.32", null)); // Dots
        assertFalse(validator.isValid("GB82_WEST_1234_5698_7654_32", null)); // Underscores
    }

    private static class IbanDummy {
        @ValidIBAN
        String defaultIban;
    }
}
