package id.xtramile.validator.validator.network;

import id.xtramile.validator.annotation.network.ValidDomainName;
import id.xtramile.validator.support.ValidatorTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("HttpUrlsUsage")
public class DomainNameValidatorTest {

    private DomainNameValidator validator;

    private static ValidDomainName getAnnotation(String fieldName) {
        try {
            Field f = DomainNameDummy.class.getDeclaredField(fieldName);
            return f.getAnnotation(ValidDomainName.class);
        } catch (NoSuchFieldException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
        validator = new DomainNameValidator();
        ValidatorTestSupport.initializeValidator(validator, DomainNameDummy.class, "defaultDomainName", ValidDomainName.class);
    }

    @Test
    void testValidDomainNames() {
        validator.initialize(getAnnotation("defaultDomainName"));

        assertTrue(validator.isValid("example.com", null)); // Basic domain
        assertTrue(validator.isValid("www.example.com", null)); // Subdomain
        assertTrue(validator.isValid("api.v1.example.com", null)); // Multi-level subdomain
        assertTrue(validator.isValid("test-server.example.com", null)); // Hyphen in subdomain
        assertTrue(validator.isValid("example.co.uk", null)); // Country code TLD
        assertTrue(validator.isValid("example.org.uk", null)); // Multi-level TLD
        assertTrue(validator.isValid("a.com", null)); // Single character domain
        assertTrue(validator.isValid("example123.com", null)); // Numbers in domain
        assertTrue(validator.isValid("test-123.example.com", null)); // Numbers and hyphens
    }

    @Test
    void testValidDomainNamesWithPunycode() {
        validator.initialize(getAnnotation("punycodeAllowedDomainName"));

        assertTrue(validator.isValid("example.com", null)); // Basic domain
        assertTrue(validator.isValid("www.example.com", null)); // Subdomain
        assertTrue(validator.isValid("api.v1.example.com", null)); // Multi-level subdomain
        assertTrue(validator.isValid("test-server.example.com", null)); // Hyphen in subdomain
        assertTrue(validator.isValid("example.co.uk", null)); // Country code TLD
        assertTrue(validator.isValid("example.org.uk", null)); // Multi-level TLD
        assertTrue(validator.isValid("a.com", null)); // Single character domain
        assertTrue(validator.isValid("example123.com", null)); // Numbers in domain
        assertTrue(validator.isValid("test-123.example.com", null)); // Numbers and hyphens
    }

    @Test
    void testValidDomainNamesWithoutPunycode() {
        validator.initialize(getAnnotation("punycodeNotAllowedDomainName"));

        assertTrue(validator.isValid("example.com", null)); // Basic domain
        assertTrue(validator.isValid("www.example.com", null)); // Subdomain
        assertTrue(validator.isValid("api.v1.example.com", null)); // Multi-level subdomain
        assertTrue(validator.isValid("test-server.example.com", null)); // Hyphen in subdomain
        assertTrue(validator.isValid("example.co.uk", null)); // Country code TLD
        assertTrue(validator.isValid("example.org.uk", null)); // Multi-level TLD
        assertTrue(validator.isValid("a.com", null)); // Single character domain
        assertTrue(validator.isValid("example123.com", null)); // Numbers in domain
        assertTrue(validator.isValid("test-123.example.com", null)); // Numbers and hyphens
    }

    @Test
    void testInvalidDomainNames() {
        validator.initialize(getAnnotation("defaultDomainName"));

        assertFalse(validator.isValid("example", null)); // No TLD
        assertFalse(validator.isValid(".com", null)); // Leading dot
        assertFalse(validator.isValid("example.", null)); // Trailing dot
        assertFalse(validator.isValid("example..com", null)); // Double dot
        assertFalse(validator.isValid("example.com.", null)); // Trailing dot
        assertFalse(validator.isValid(".example.com", null)); // Leading dot
        assertFalse(validator.isValid("example..com", null)); // Double dot
        assertFalse(validator.isValid("example...com", null)); // Triple dot
    }

    @Test
    void testInvalidDomainCharacters() {
        validator.initialize(getAnnotation("defaultDomainName"));

        assertFalse(validator.isValid("example@.com", null)); // At symbol
        assertFalse(validator.isValid("example#.com", null)); // Hash symbol
        assertFalse(validator.isValid("example%.com", null)); // Percentage
        assertFalse(validator.isValid("example&.com", null)); // Ampersand
        assertFalse(validator.isValid("example*.com", null)); // Asterisk
        assertFalse(validator.isValid("example+.com", null)); // Plus sign
        assertFalse(validator.isValid("example=.com", null)); // Equals sign
        assertFalse(validator.isValid("example!.com", null)); // Exclamation mark
        assertFalse(validator.isValid("example@.com", null)); // At symbol
    }

    @Test
    void testInvalidDomainLengths() {
        validator.initialize(getAnnotation("defaultDomainName"));

        assertFalse(validator.isValid("a".repeat(64) + ".com", null)); // Label too long
        assertFalse(validator.isValid("a".repeat(255) + ".com", null)); // Domain too long
        assertFalse(validator.isValid("a".repeat(300) + ".com", null)); // Way too long
        assertFalse(validator.isValid("a".repeat(1000) + ".com", null)); // Way too long
    }

    @Test
    void testInvalidDomainHyphens() {
        validator.initialize(getAnnotation("defaultDomainName"));

        assertFalse(validator.isValid("-example.com", null)); // Leading hyphen
        assertFalse(validator.isValid("example-.com", null)); // Trailing hyphen
        assertFalse(validator.isValid("-example-.com", null)); // Both leading and trailing hyphens
        assertFalse(validator.isValid("example--test.com", null)); // Double hyphen
        assertFalse(validator.isValid("example---test.com", null)); // Triple hyphen
    }

    @Test
    void testInvalidDomainSpaces() {
        validator.initialize(getAnnotation("defaultDomainName"));

        assertFalse(validator.isValid("example .com", null)); // Space in domain
        assertFalse(validator.isValid("example. com", null)); // Space before TLD
        assertFalse(validator.isValid("example . com", null)); // Spaces around dot
        assertFalse(validator.isValid("example .com", null)); // Space before dot
        assertFalse(validator.isValid("example. com", null)); // Space after dot
        assertFalse(validator.isValid("example . com", null)); // Spaces around dot
    }

    @Test
    void testSpecialCharacters() {
        validator.initialize(getAnnotation("defaultDomainName"));

        assertFalse(validator.isValid("example.com+", null)); // Plus sign
        assertFalse(validator.isValid("example.com-", null)); // Minus sign
        assertFalse(validator.isValid("example.com%", null)); // Percentage
        assertFalse(validator.isValid("example.com#", null)); // Hash symbol
        assertFalse(validator.isValid("example.com@", null)); // At symbol
        assertFalse(validator.isValid("example.com&", null)); // Ampersand
        assertFalse(validator.isValid("example.com*", null)); // Asterisk
    }

    @Test
    void testWhitespaceHandling() {
        validator.initialize(getAnnotation("defaultDomainName"));

        assertTrue(validator.isValid(" example.com ", null)); // Leading and trailing spaces
        assertTrue(validator.isValid("\texample.com\t", null)); // Tabs
        assertTrue(validator.isValid("\nexample.com\n", null)); // Newlines
        assertTrue(validator.isValid("  example.com  ", null)); // Multiple spaces
    }

    @Test
    void testRealWorldExamples() {
        validator.initialize(getAnnotation("defaultDomainName"));

        assertTrue(validator.isValid("google.com", null)); // Google
        assertTrue(validator.isValid("www.google.com", null)); // Google with www
        assertTrue(validator.isValid("github.com", null)); // GitHub
        assertTrue(validator.isValid("stackoverflow.com", null)); // Stack Overflow
        assertTrue(validator.isValid("wikipedia.org", null)); // Wikipedia
        assertTrue(validator.isValid("amazon.com", null)); // Amazon
        assertTrue(validator.isValid("microsoft.com", null)); // Microsoft
        assertTrue(validator.isValid("apple.com", null)); // Apple
        assertTrue(validator.isValid("facebook.com", null)); // Facebook
        assertTrue(validator.isValid("twitter.com", null)); // Twitter
        assertTrue(validator.isValid("linkedin.com", null)); // LinkedIn
    }

    @Test
    void testCountryCodeTLDs() {
        validator.initialize(getAnnotation("defaultDomainName"));

        assertTrue(validator.isValid("example.co.uk", null)); // UK
        assertTrue(validator.isValid("example.com.au", null)); // Australia
        assertTrue(validator.isValid("example.ca", null)); // Canada
        assertTrue(validator.isValid("example.de", null)); // Germany
        assertTrue(validator.isValid("example.fr", null)); // France
        assertTrue(validator.isValid("example.jp", null)); // Japan
        assertTrue(validator.isValid("example.cn", null)); // China
        assertTrue(validator.isValid("example.in", null)); // India
        assertTrue(validator.isValid("example.br", null)); // Brazil
        assertTrue(validator.isValid("example.ru", null)); // Russia
    }

    @Test
    void testMultiLevelTLDs() {
        validator.initialize(getAnnotation("defaultDomainName"));

        assertTrue(validator.isValid("example.org.uk", null)); // Multi-level TLD
        assertTrue(validator.isValid("example.co.uk", null)); // Multi-level TLD
        assertTrue(validator.isValid("example.com.au", null)); // Multi-level TLD
        assertTrue(validator.isValid("example.net.au", null)); // Multi-level TLD
        assertTrue(validator.isValid("example.org.au", null)); // Multi-level TLD
        assertTrue(validator.isValid("example.gov.uk", null)); // Multi-level TLD
        assertTrue(validator.isValid("example.ac.uk", null)); // Multi-level TLD
        assertTrue(validator.isValid("example.mil.uk", null)); // Multi-level TLD
    }

    @Test
    void testSubdomains() {
        validator.initialize(getAnnotation("defaultDomainName"));

        assertTrue(validator.isValid("www.example.com", null)); // Single subdomain
        assertTrue(validator.isValid("api.example.com", null)); // API subdomain
        assertTrue(validator.isValid("blog.example.com", null)); // Blog subdomain
        assertTrue(validator.isValid("shop.example.com", null)); // Shop subdomain
        assertTrue(validator.isValid("mail.example.com", null)); // Mail subdomain
        assertTrue(validator.isValid("ftp.example.com", null)); // FTP subdomain
        assertTrue(validator.isValid("admin.example.com", null)); // Admin subdomain
        assertTrue(validator.isValid("test.example.com", null)); // Test subdomain
    }

    @Test
    void testMultiLevelSubdomains() {
        validator.initialize(getAnnotation("defaultDomainName"));

        assertTrue(validator.isValid("api.v1.example.com", null)); // Multi-level subdomain
        assertTrue(validator.isValid("api.v2.example.com", null)); // Multi-level subdomain
        assertTrue(validator.isValid("blog.v1.example.com", null)); // Multi-level subdomain
        assertTrue(validator.isValid("shop.v1.example.com", null)); // Multi-level subdomain
        assertTrue(validator.isValid("mail.v1.example.com", null)); // Multi-level subdomain
        assertTrue(validator.isValid("ftp.v1.example.com", null)); // Multi-level subdomain
        assertTrue(validator.isValid("admin.v1.example.com", null)); // Multi-level subdomain
        assertTrue(validator.isValid("test.v1.example.com", null)); // Multi-level subdomain
    }

    @Test
    void testNumbersInDomains() {
        validator.initialize(getAnnotation("defaultDomainName"));

        assertTrue(validator.isValid("example123.com", null)); // Numbers in domain
        assertTrue(validator.isValid("123example.com", null)); // Numbers at start
        assertTrue(validator.isValid("example123test.com", null)); // Numbers in middle
        assertTrue(validator.isValid("test123example.com", null)); // Numbers in middle
        assertTrue(validator.isValid("example123test456.com", null)); // Multiple numbers
        assertTrue(validator.isValid("123example456.com", null)); // Numbers at start and middle
        assertTrue(validator.isValid("example123test456.com", null)); // Numbers in middle and end
    }

    @Test
    void testHyphensInDomains() {
        validator.initialize(getAnnotation("defaultDomainName"));

        assertTrue(validator.isValid("example-test.com", null)); // Hyphen in domain
        assertTrue(validator.isValid("test-example.com", null)); // Hyphen in domain
        assertTrue(validator.isValid("example-test-example.com", null)); // Multiple hyphens
        assertTrue(validator.isValid("test-example-test.com", null)); // Multiple hyphens
        assertTrue(validator.isValid("example-test-example-test.com", null)); // Many hyphens
        assertTrue(validator.isValid("test-example-test-example.com", null)); // Many hyphens
    }

    @Test
    void testEdgeCaseLengths() {
        validator.initialize(getAnnotation("defaultDomainName"));

        assertTrue(validator.isValid("a.com", null)); // Single character domain
        assertTrue(validator.isValid("a.b.com", null)); // Single character subdomain
        assertTrue(validator.isValid("a.b.c.com", null)); // Single character multi-level
        assertTrue(validator.isValid("a".repeat(63) + ".com", null)); // Maximum label length
        assertTrue(validator.isValid(("a".repeat(61) + ".").repeat(4) + "co.id", null));
    }

    @Test
    void testInvalidProtocols() {
        validator.initialize(getAnnotation("defaultDomainName"));

        assertFalse(validator.isValid("http://example.com", null)); // HTTP protocol
        assertFalse(validator.isValid("https://example.com", null)); // HTTPS protocol
        assertFalse(validator.isValid("ftp://example.com", null)); // FTP protocol
        assertFalse(validator.isValid("file://example.com", null)); // File protocol
        assertFalse(validator.isValid("mailto:example.com", null)); // Mailto protocol
        assertFalse(validator.isValid("tel:example.com", null)); // Tel protocol
        assertFalse(validator.isValid("ssh://example.com", null)); // SSH protocol
        assertFalse(validator.isValid("git://example.com", null)); // Git protocol
    }

    private static class DomainNameDummy {
        @ValidDomainName
        String defaultDomainName;

        @ValidDomainName()
        String punycodeAllowedDomainName;

        @ValidDomainName(allowPunycode = false)
        String punycodeNotAllowedDomainName;
    }
}
