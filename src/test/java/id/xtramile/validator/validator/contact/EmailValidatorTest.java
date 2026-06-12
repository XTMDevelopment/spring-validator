package id.xtramile.validator.validator.contact;

import id.xtramile.validator.annotation.contact.ValidEmail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EmailValidatorTest {

    private static class EmailDummy {
        @ValidEmail
        String emailField;
    }

    private EmailValidator validator;

    private static ValidEmail getAnnotation(String fieldName) {
        try {
            Field f = EmailDummy.class.getDeclaredField(fieldName);
            return f.getAnnotation(ValidEmail.class);
        } catch (NoSuchFieldException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
        validator = new EmailValidator();
    }

    @Test
    void testValidEmails() {
        validator.initialize(getAnnotation("emailField"));

        assertTrue(validator.isValid("user@gmail.com", null));
        assertTrue(validator.isValid("test.email@yahoo.co.uk", null));
        assertTrue(validator.isValid("user+tag@outlook.org", null));
        assertTrue(validator.isValid("user123@microsoft.com", null));
        assertTrue(validator.isValid("firstname.lastname@company.com", null));
    }

    @Test
    void testInvalidEmails() {
        validator.initialize(getAnnotation("emailField"));

        assertFalse(validator.isValid("invalid-email", null));
        assertFalse(validator.isValid("@gmail.com", null));
        assertFalse(validator.isValid("user@", null));
        assertFalse(validator.isValid("user@.com", null));
        assertFalse(validator.isValid("user..double@gmail.com", null));
        assertFalse(validator.isValid("user@gmail..com", null));
        assertFalse(validator.isValid("user@gmail", null));
        assertFalse(validator.isValid("user name@gmail.com", null));
    }

    @Test
    void testBlankValues() {
        validator.initialize(getAnnotation("emailField"));

        assertTrue(validator.isValid("", null));
        assertTrue(validator.isValid("   ", null));
        assertTrue(validator.isValid(null, null));
    }

    @Test
    void testEdgeCases() {
        validator.initialize(getAnnotation("emailField"));

        // Test with spaces
        assertFalse(validator.isValid(" user@gmail.com", null)); // leading space
        assertFalse(validator.isValid("user@gmail.com ", null)); // trailing space
        assertFalse(validator.isValid("user @gmail.com", null)); // space in local part

        // Test with special characters
        assertTrue(validator.isValid("user+tag@gmail.com", null)); // plus sign
        assertTrue(validator.isValid("user-tag@gmail.com", null)); // hyphen
        assertTrue(validator.isValid("user_tag@gmail.com", null)); // underscore
        assertTrue(validator.isValid("user.tag@gmail.com", null)); // dot
    }

    @Test
    void testRealWorldExamples() {
        validator.initialize(getAnnotation("emailField"));

        assertTrue(validator.isValid("john.doe@company.com", null));
        assertTrue(validator.isValid("jane.smith@university.edu", null));
        assertTrue(validator.isValid("support@mycompany.co.uk", null));
        assertTrue(validator.isValid("info@outlook.org", null));
        assertTrue(validator.isValid("contact@microsoft.net", null));
    }

    @Test
    void testDomainValidation() {
        validator.initialize(getAnnotation("emailField"));

        // These should be valid if the domain exists (DNS check)
        assertTrue(validator.isValid("test@gmail.com", null));
        assertTrue(validator.isValid("user@yahoo.com", null));
        assertTrue(validator.isValid("admin@microsoft.com", null));
    }

    @Test
    void testInvalidDomainFormats() {
        validator.initialize(getAnnotation("emailField"));

        assertFalse(validator.isValid("user@nonexistentdomain12345.com", null));
        assertFalse(validator.isValid("user@invalid..domain.com", null));
        assertFalse(validator.isValid("user@.invalid.com", null));
        assertFalse(validator.isValid("user@invalid.", null));
    }

    @Test
    void testLocalPartValidation() {
        validator.initialize(getAnnotation("emailField"));

        // Valid local parts
        assertTrue(validator.isValid("user123@gmail.com", null)); // with numbers
        assertTrue(validator.isValid("user-name@gmail.com", null)); // with hyphen
        assertTrue(validator.isValid("user_name@gmail.com", null)); // with underscore

        // Invalid local parts
        assertFalse(validator.isValid("-user@gmail.com", null)); // leading hyphen
        assertFalse(validator.isValid("user-@gmail.com", null)); // trailing hyphen
        assertFalse(validator.isValid(".user@gmail.com", null)); // leading dot
        assertFalse(validator.isValid("user.@gmail.com", null)); // trailing dot
    }

    @Test
    void testDomainPartValidation() {
        validator.initialize(getAnnotation("emailField"));

        // Valid domains
        assertTrue(validator.isValid("user@myboost.co", null));
        assertTrue(validator.isValid("user@payflex.myboost.co.id", null)); // subdomain
        assertTrue(validator.isValid("user@myboost.co.id", null)); // country code
        assertTrue(validator.isValid("user@microsoft.com", null)); // valid domain

        // Invalid domains
        assertFalse(validator.isValid("user@-gmail.com", null)); // leading hyphen
        assertFalse(validator.isValid("user@gmail-.com", null)); // trailing hyphen
        assertFalse(validator.isValid("user@.gmail.com", null)); // leading dot
        assertFalse(validator.isValid("user@gmail.com.", null)); // trailing dot
    }

    @Test
    void testSpecialCharacters() {
        validator.initialize(getAnnotation("emailField"));

        // Valid special characters
        assertTrue(validator.isValid("user+tag@gmail.com", null)); // plus
        assertTrue(validator.isValid("user-tag@gmail.com", null)); // hyphen
        assertTrue(validator.isValid("user_tag@gmail.com", null)); // underscore
        assertTrue(validator.isValid("user.tag@gmail.com", null)); // dot

        // Invalid special characters
        assertFalse(validator.isValid("user name@gmail.com", null)); // space
    }
}
