package id.xtramile.validator.validator.finance;

import id.xtramile.validator.annotation.finance.ValidSwiftCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SwiftCodeValidatorTest {

    private static class SwiftCodeDummy {
        @ValidSwiftCode
        String defaultSwift;
    }

    private SwiftCodeValidator validator;

    private static ValidSwiftCode getAnnotation(String fieldName) {
        try {
            Field f = SwiftCodeDummy.class.getDeclaredField(fieldName);
            return f.getAnnotation(ValidSwiftCode.class);
        } catch (NoSuchFieldException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
        validator = new SwiftCodeValidator();
    }

    @Test
    void testValidEightCharacterSwiftCodes() {
        validator.initialize(getAnnotation("defaultSwift"));

        assertTrue(validator.isValid("DEUTDEFF", null)); // Deutsche Bank
        assertTrue(validator.isValid("CHASUS33", null)); // Chase Bank
        assertTrue(validator.isValid("HSBCGB2L", null)); // HSBC UK
        assertTrue(validator.isValid("BNPAFRPP", null)); // BNP Paribas
        assertTrue(validator.isValid("UBSWCHZH", null)); // UBS Switzerland
    }

    @Test
    void testValidElevenCharacterSwiftCodes() {
        validator.initialize(getAnnotation("defaultSwift"));

        assertTrue(validator.isValid("DEUTDEFF500", null)); // Deutsche Bank with branch
        assertTrue(validator.isValid("CHASUS33XXX", null)); // Chase Bank with branch
        assertTrue(validator.isValid("HSBCGB2LXXX", null)); // HSBC UK with branch
        assertTrue(validator.isValid("BNPAFRPPXXX", null)); // BNP Paribas with branch
        assertTrue(validator.isValid("UBSWCHZHXXX", null)); // UBS Switzerland with branch
    }

    @Test
    void testInvalidSwiftCodeLengths() {
        validator.initialize(getAnnotation("defaultSwift"));

        assertFalse(validator.isValid("DEUTDE", null)); // Too short (6 chars)
        assertFalse(validator.isValid("DEUTDEFF5", null)); // 9 chars
        assertFalse(validator.isValid("DEUTDEFF50", null)); // 10 chars
        assertFalse(validator.isValid("DEUTDEFF5000", null)); // 12 chars
        assertFalse(validator.isValid("DEUTDEFF50000", null)); // 13 chars
    }

    @Test
    void testInvalidBankCode() {
        validator.initialize(getAnnotation("defaultSwift"));

        assertFalse(validator.isValid("DEU1DEFF", null)); // Contains number in bank code
        assertFalse(validator.isValid("123TDEFF", null)); // All numbers in bank code
        assertFalse(validator.isValid("DEUTD", null)); // Too short
        assertFalse(validator.isValid("DEUTDEFFF", null)); // Too long
    }

    @Test
    void testInvalidCountryCode() {
        validator.initialize(getAnnotation("defaultSwift"));

        assertFalse(validator.isValid("DEUTD1FF", null)); // Contains number in country
        assertFalse(validator.isValid("DEUT12FF", null)); // All numbers in country
        assertFalse(validator.isValid("DEUTD", null)); // Too short
        assertFalse(validator.isValid("DEUTDEFFF", null)); // Too long
    }

    @Test
    void testInvalidLocationCode() {
        validator.initialize(getAnnotation("defaultSwift"));

        assertFalse(validator.isValid("DEUTDEF", null)); // Too short (7 chars)
        assertFalse(validator.isValid("DEUTDEFFF", null)); // Invalid length (9 chars)
        assertFalse(validator.isValid("DEUTDEFF1", null)); // Invalid length (9 chars)
        assertFalse(validator.isValid("DEUTDEFF12", null)); // Invalid length (10 chars)
    }

    @Test
    void testInvalidBranchCode() {
        validator.initialize(getAnnotation("defaultSwift"));

        assertFalse(validator.isValid("DEUTDEFF50", null)); // Too short branch (2 chars)
        assertFalse(validator.isValid("DEUTDEFF5000", null)); // Too long branch (4 chars)
        assertFalse(validator.isValid("DEUTDEFF50@", null)); // Contains invalid character in branch
    }

    @Test
    void testCaseInsensitiveSwiftCodes() {
        validator.initialize(getAnnotation("defaultSwift"));

        assertTrue(validator.isValid("deutdeff", null)); // All lowercase
        assertTrue(validator.isValid("DeutDeff", null)); // Mixed case
        assertTrue(validator.isValid("DEUTDEFF", null)); // All uppercase
        assertTrue(validator.isValid("deutdeff500", null)); // Lowercase with branch
        assertTrue(validator.isValid("DeutDeff500", null)); // Mixed case with branch
    }

    @Test
    void testWhitespaceHandling() {
        validator.initialize(getAnnotation("defaultSwift"));

        assertTrue(validator.isValid(" DEUTDEFF ", null)); // Leading/trailing spaces
        assertTrue(validator.isValid("DEUTDEFF", null)); // No spaces
        assertFalse(validator.isValid("DEUT DEFF", null)); // Space in middle
        assertFalse(validator.isValid("DEUTDEFF 500", null)); // Space before branch
    }

    @Test
    void testSpecialCharacters() {
        validator.initialize(getAnnotation("defaultSwift"));

        assertFalse(validator.isValid("DEUT-DEFF", null)); // Hyphen
        assertFalse(validator.isValid("DEUT.DEFF", null)); // Dot
        assertFalse(validator.isValid("DEUT_DEFF", null)); // Underscore
        assertFalse(validator.isValid("DEUT@DEFF", null)); // At symbol
        assertFalse(validator.isValid("DEUT#DEFF", null)); // Hash
    }

    @Test
    void testNumericCharactersInBankCode() {
        validator.initialize(getAnnotation("defaultSwift"));

        assertFalse(validator.isValid("DEU1DEFF", null)); // Number in bank code
        assertFalse(validator.isValid("DEU2DEFF", null)); // Number in bank code
        assertFalse(validator.isValid("DEU3DEFF", null)); // Number in bank code
        assertFalse(validator.isValid("1234DEFF", null)); // All numbers in bank code
    }

    @Test
    void testNumericCharactersInCountryCode() {
        validator.initialize(getAnnotation("defaultSwift"));

        assertFalse(validator.isValid("DEUTD1FF", null)); // Number in country code
        assertFalse(validator.isValid("DEUTD2FF", null)); // Number in country code
        assertFalse(validator.isValid("DEUTD3FF", null)); // Number in country code
        assertFalse(validator.isValid("DEUT12FF", null)); // All numbers in country code
    }

    @Test
    void testValidLocationCodes() {
        validator.initialize(getAnnotation("defaultSwift"));

        assertTrue(validator.isValid("DEUTDEFF", null)); // Valid 8-char
        assertTrue(validator.isValid("DEUTDEFF500", null)); // Valid 11-char
        assertTrue(validator.isValid("DEUTDEFFXXX", null)); // Valid with XXX branch
        assertTrue(validator.isValid("DEUTDEFF123", null)); // Valid with numeric branch
    }

    @Test
    void testBlankString() {
        validator.initialize(getAnnotation("defaultSwift"));

        assertTrue(validator.isValid("", null));
        assertTrue(validator.isValid("   ", null));
    }

    @Test
    void testRealWorldSwiftCodes() {
        validator.initialize(getAnnotation("defaultSwift"));

        // Real world examples
        assertTrue(validator.isValid("DEUTDEFF", null)); // Deutsche Bank
        assertTrue(validator.isValid("CHASUS33", null)); // JPMorgan Chase
        assertTrue(validator.isValid("HSBCGB2L", null)); // HSBC Bank
        assertTrue(validator.isValid("BNPAFRPP", null)); // BNP Paribas
        assertTrue(validator.isValid("UBSWCHZH", null)); // UBS
        assertTrue(validator.isValid("CITIUS33", null)); // Citibank
        assertTrue(validator.isValid("WFBIUS6S", null)); // Wells Fargo
    }

    @Test
    void testEdgeCaseLengths() {
        validator.initialize(getAnnotation("defaultSwift"));

        assertTrue(validator.isValid("ABCDEFGH", null)); // Exactly 8 chars
        assertTrue(validator.isValid("ABCDEFGH123", null)); // Exactly 11 chars
        assertFalse(validator.isValid("ABCDEFG", null)); // 7 chars
        assertFalse(validator.isValid("ABCDEFGH1", null)); // 9 chars
        assertFalse(validator.isValid("ABCDEFGH12", null)); // 10 chars
        assertFalse(validator.isValid("ABCDEFGH1234", null)); // 12 chars
    }
}
