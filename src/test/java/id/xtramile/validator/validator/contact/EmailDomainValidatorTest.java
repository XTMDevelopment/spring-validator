package id.xtramile.validator.validator.contact;

import id.xtramile.validator.annotation.contact.ValidEmailDomain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EmailDomainValidatorTest {

    private EmailDomainValidator validator;

    private static ValidEmailDomain getAnnotation(String fieldName) {
        try {
            Field f = EmailDomainDummy.class.getDeclaredField(fieldName);
            return f.getAnnotation(ValidEmailDomain.class);
        } catch (NoSuchFieldException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
        validator = new EmailDomainValidator();
    }

    @Test
    void testValidEmailDomains() {
        validator.initialize(getAnnotation("defaultEmailDomain"));

        assertTrue(validator.isValid("user@gmail.com", null));
        assertTrue(validator.isValid("test@yahoo.com", null));
        assertTrue(validator.isValid("admin@outlook.com", null));
        assertTrue(validator.isValid("john.doe@gmail.com", null));
        assertTrue(validator.isValid("jane+tag@yahoo.com", null));
    }

    @Test
    void testInvalidEmailDomains() {
        validator.initialize(getAnnotation("defaultEmailDomain"));

        assertFalse(validator.isValid("user@hotmail.com", null)); // not in allowed list
        assertFalse(validator.isValid("test@company.com", null)); // not in allowed list
        assertFalse(validator.isValid("admin@invalid.com", null)); // not in allowed list
        assertTrue(validator.isValid("user@GMAIL.COM", null)); // case insensitive by default
    }

    @Test
    void testIgnoreCaseTrue() {
        validator.initialize(getAnnotation("ignoreCaseEmailDomain"));

        // Should be valid (case insensitive)
        assertTrue(validator.isValid("user@company.com", null));
        assertTrue(validator.isValid("user@COMPANY.COM", null));
        assertTrue(validator.isValid("user@Company.Com", null));
        assertTrue(validator.isValid("user@CoMpAnY.cOm", null));

        assertTrue(validator.isValid("user@corp.com", null));
        assertTrue(validator.isValid("user@CORP.COM", null));
        assertTrue(validator.isValid("user@Corp.Com", null));

        // Should be invalid
        assertFalse(validator.isValid("user@gmail.com", null)); // not in allowed list
        assertFalse(validator.isValid("user@yahoo.com", null)); // not in allowed list
    }

    @Test
    void testIgnoreCaseFalse() {
        validator.initialize(getAnnotation("caseSensitiveEmailDomain"));

        // Should be valid (exact case)
        assertTrue(validator.isValid("user@company.com", null));
        assertTrue(validator.isValid("user@corp.com", null));

        // Should be invalid (different case)
        assertFalse(validator.isValid("user@COMPANY.COM", null));
        assertFalse(validator.isValid("user@Company.Com", null));
        assertFalse(validator.isValid("user@CoMpAnY.cOm", null));

        assertFalse(validator.isValid("user@CORP.COM", null));
        assertFalse(validator.isValid("user@Corp.Com", null));
    }

    @Test
    void testBlankValues() {
        validator.initialize(getAnnotation("defaultEmailDomain"));

        assertTrue(validator.isValid("", null));
        assertTrue(validator.isValid("   ", null));
        assertTrue(validator.isValid(null, null));
    }

    @Test
    void testInvalidEmailFormats() {
        validator.initialize(getAnnotation("defaultEmailDomain"));

        assertFalse(validator.isValid("invalid-email", null)); // no @ symbol
        assertFalse(validator.isValid("@gmail.com", null)); // no local part
        assertFalse(validator.isValid("user@", null)); // no domain
        assertFalse(validator.isValid("user@.com", null)); // invalid domain format
        assertFalse(validator.isValid("user@gmail", null)); // incomplete domain
    }

    @Test
    void testCustomEmailDomains() {
        validator.initialize(getAnnotation("customEmailDomain"));

        // Should be valid
        assertTrue(validator.isValid("user@test.com", null));
        assertTrue(validator.isValid("admin@example.org", null));
        assertTrue(validator.isValid("demo@demo.net", null));

        // Should be invalid
        assertFalse(validator.isValid("user@gmail.com", null)); // not in custom list
        assertFalse(validator.isValid("user@yahoo.com", null)); // not in custom list
        assertFalse(validator.isValid("user@outlook.com", null)); // not in custom list
    }

    @Test
    void testEdgeCases() {
        validator.initialize(getAnnotation("defaultEmailDomain"));

        // Test with spaces
        assertFalse(validator.isValid(" user@gmail.com", null)); // leading space
        assertFalse(validator.isValid("user@gmail.com ", null)); // trailing space
        assertFalse(validator.isValid("user @gmail.com", null)); // space in local part

        // Test with special characters in domain
        assertFalse(validator.isValid("user@gmail-.com", null)); // invalid domain
        assertFalse(validator.isValid("user@-gmail.com", null)); // invalid domain
    }

    @Test
    void testCaseVariations() {
        validator.initialize(getAnnotation("ignoreCaseEmailDomain"));

        String[] companyVariations = {"company.com", "COMPANY.COM", "Company.Com", "CoMpAnY.cOm"};
        for (String variation : companyVariations) {
            assertTrue(validator.isValid("user@" + variation, null));
        }

        String[] corpVariations = {"corp.com", "CORP.COM", "Corp.Com", "CoRp.CoM"};
        for (String variation : corpVariations) {
            assertTrue(validator.isValid("user@" + variation, null));
        }
    }

    @Test
    void testCaseSensitiveVariations() {
        validator.initialize(getAnnotation("caseSensitiveEmailDomain"));

        // Only exact case should be valid
        assertTrue(validator.isValid("user@company.com", null));
        assertTrue(validator.isValid("user@corp.com", null));

        // Different cases should be invalid
        String[] invalidVariations = {"COMPANY.COM", "Company.Com", "CoMpAnY.cOm"};
        for (String variation : invalidVariations) {
            assertFalse(validator.isValid("user@" + variation, null));
        }
    }

    @Test
    void testRealWorldExamples() {
        validator.initialize(getAnnotation("defaultEmailDomain"));

        // Common email formats with allowed domains
        assertTrue(validator.isValid("john.doe@gmail.com", null));
        assertTrue(validator.isValid("jane.smith@yahoo.com", null));
        assertTrue(validator.isValid("admin@outlook.com", null));
        assertTrue(validator.isValid("user+tag@gmail.com", null));

        // Common email formats with disallowed domains
        assertFalse(validator.isValid("user@hotmail.com", null));
        assertFalse(validator.isValid("user@aol.com", null));
        assertFalse(validator.isValid("user@icloud.com", null));
    }

    @Test
    void testDomainExtraction() {
        validator.initialize(getAnnotation("defaultEmailDomain"));

        // Test that domain extraction works correctly
        assertTrue(validator.isValid("very.long.local.part@gmail.com", null)); // long local part
        assertTrue(validator.isValid("a@yahoo.com", null)); // short local part
        assertTrue(validator.isValid("user+tag+another@outlook.com", null)); // multiple plus signs

        // Test with subdomains (should be invalid as they don't match exactly)
        assertFalse(validator.isValid("user@mail.gmail.com", null)); // subdomain
        assertFalse(validator.isValid("user@sub.yahoo.com", null)); // subdomain
    }

    @Test
    void testMultipleAtSymbols() {
        validator.initialize(getAnnotation("defaultEmailDomain"));

        // Emails with multiple @ symbols should be invalid
        assertFalse(validator.isValid("user@domain@gmail.com", null)); // multiple @
        assertFalse(validator.isValid("user@@gmail.com", null)); // double @
        assertFalse(validator.isValid("user@domain@", null)); // @ at end
    }

    private static class EmailDomainDummy {
        @ValidEmailDomain(allowed = {"gmail.com", "yahoo.com", "outlook.com"})
        String defaultEmailDomain;

        @ValidEmailDomain(allowed = {"company.com", "corp.com"})
        String ignoreCaseEmailDomain;

        @ValidEmailDomain(allowed = {"company.com", "corp.com"}, ignoreCase = false)
        String caseSensitiveEmailDomain;

        @ValidEmailDomain(allowed = {"test.com", "example.org", "demo.net"})
        String customEmailDomain;
    }
}
