package id.xtramile.validator.validator.network;

import id.xtramile.validator.annotation.network.ValidURL;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("HttpUrlsUsage")
public class URLValidatorTest {

    private static class URLDummy {
        @ValidURL
        String defaultURL;

        @ValidURL(httpsOnly = true)
        String httpsOnlyURL;

        @ValidURL()
        String httpAndHttpsURL;
    }

    private URLValidator validator;

    private static ValidURL getAnnotation(String fieldName) {
        try {
            Field f = URLDummy.class.getDeclaredField(fieldName);
            return f.getAnnotation(ValidURL.class);
        } catch (NoSuchFieldException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
        validator = new URLValidator();
    }

    @Test
    void testValidHttpURLs() {
        validator.initialize(getAnnotation("defaultURL"));

        assertTrue(validator.isValid("http://example.com", null)); // Basic HTTP
        assertTrue(validator.isValid("http://www.example.com", null)); // HTTP with www
        assertTrue(validator.isValid("http://example.com/path", null)); // HTTP with path
        assertTrue(validator.isValid("http://example.com/path/to/page", null)); // HTTP with deep path
        assertTrue(validator.isValid("http://example.com:8080", null)); // HTTP with port
        assertTrue(validator.isValid("http://example.com:8080/path", null)); // HTTP with port and path
        assertTrue(validator.isValid("http://user:pass@example.com", null)); // HTTP with credentials
        assertTrue(validator.isValid("http://example.com?param=value", null)); // HTTP with query
        assertTrue(validator.isValid("http://example.com#fragment", null)); // HTTP with fragment
    }

    @Test
    void testValidHttpsURLs() {
        validator.initialize(getAnnotation("defaultURL"));

        assertTrue(validator.isValid("https://example.com", null)); // Basic HTTPS
        assertTrue(validator.isValid("https://www.example.com", null)); // HTTPS with www
        assertTrue(validator.isValid("https://example.com/path", null)); // HTTPS with path
        assertTrue(validator.isValid("https://example.com/path/to/page", null)); // HTTPS with deep path
        assertTrue(validator.isValid("https://example.com:8443", null)); // HTTPS with port
        assertTrue(validator.isValid("https://example.com:8443/path", null)); // HTTPS with port and path
        assertTrue(validator.isValid("https://user:pass@example.com", null)); // HTTPS with credentials
        assertTrue(validator.isValid("https://example.com?param=value", null)); // HTTPS with query
        assertTrue(validator.isValid("https://example.com#fragment", null)); // HTTPS with fragment
    }

    @Test
    void testHttpsOnlyURLs() {
        validator.initialize(getAnnotation("httpsOnlyURL"));

        assertTrue(validator.isValid("https://example.com", null)); // Valid HTTPS
        assertTrue(validator.isValid("https://www.example.com", null)); // Valid HTTPS with www
        assertTrue(validator.isValid("https://example.com/path", null)); // Valid HTTPS with path
        assertTrue(validator.isValid("https://example.com:8443", null)); // Valid HTTPS with port
        assertTrue(validator.isValid("https://user:pass@example.com", null)); // Valid HTTPS with credentials
        assertTrue(validator.isValid("https://example.com?param=value", null)); // Valid HTTPS with query
        assertTrue(validator.isValid("https://example.com#fragment", null)); // Valid HTTPS with fragment
    }

    @Test
    void testHttpsOnlyRejectsHttp() {
        validator.initialize(getAnnotation("httpsOnlyURL"));

        assertFalse(validator.isValid("http://example.com", null)); // HTTP rejected
        assertFalse(validator.isValid("http://www.example.com", null)); // HTTP with www rejected
        assertFalse(validator.isValid("http://example.com/path", null)); // HTTP with path rejected
        assertFalse(validator.isValid("http://example.com:8080", null)); // HTTP with port rejected
        assertFalse(validator.isValid("http://user:pass@example.com", null)); // HTTP with credentials rejected
        assertFalse(validator.isValid("http://example.com?param=value", null)); // HTTP with query rejected
        assertFalse(validator.isValid("http://example.com#fragment", null)); // HTTP with fragment rejected
    }

    @Test
    void testHttpAndHttpsURLs() {
        validator.initialize(getAnnotation("httpAndHttpsURL"));

        assertTrue(validator.isValid("http://example.com", null)); // Valid HTTP
        assertTrue(validator.isValid("https://example.com", null)); // Valid HTTPS
        assertTrue(validator.isValid("http://www.example.com", null)); // Valid HTTP with www
        assertTrue(validator.isValid("https://www.example.com", null)); // Valid HTTPS with www
        assertTrue(validator.isValid("http://example.com/path", null)); // Valid HTTP with path
        assertTrue(validator.isValid("https://example.com/path", null)); // Valid HTTPS with path
    }

    @Test
    void testInvalidURLs() {
        validator.initialize(getAnnotation("defaultURL"));

        assertFalse(validator.isValid("ftp://example.com", null)); // FTP protocol
        assertFalse(validator.isValid("file:///path/to/file", null)); // File protocol
        assertFalse(validator.isValid("mailto:user@example.com", null)); // Mailto protocol
        assertFalse(validator.isValid("tel:+1234567890", null)); // Tel protocol
        assertFalse(validator.isValid("ssh://user@server", null)); // SSH protocol
        assertFalse(validator.isValid("git://github.com/user/repo", null)); // Git protocol
        assertFalse(validator.isValid("example.com", null)); // No protocol
        assertFalse(validator.isValid("www.example.com", null)); // No protocol
        assertFalse(validator.isValid("//example.com", null)); // No protocol
    }

    @Test
    void testInvalidURLFormats() {
        validator.initialize(getAnnotation("defaultURL"));

        assertFalse(validator.isValid("not-a-url", null)); // Not a URL
    }

    @Test
    void testSpecialCharacters() {
        validator.initialize(getAnnotation("defaultURL"));

        assertFalse(validator.isValid("http://example.com+", null)); // Plus sign
        assertFalse(validator.isValid("http://example.com-", null)); // Minus sign
        assertFalse(validator.isValid("http://example.com%", null)); // Percentage
        assertFalse(validator.isValid("http://example.com#", null)); // Hash symbol
        assertFalse(validator.isValid("http://example.com@", null)); // At symbol
        assertFalse(validator.isValid("http://example.com&", null)); // Ampersand
        assertFalse(validator.isValid("http://example.com*", null)); // Asterisk
    }

    @Test
    void testWhitespaceHandling() {
        validator.initialize(getAnnotation("defaultURL"));

        assertTrue(validator.isValid(" http://example.com ", null)); // Leading and trailing spaces
        assertTrue(validator.isValid("\thttp://example.com\t", null)); // Tabs
        assertTrue(validator.isValid("\nhttp://example.com\n", null)); // Newlines
        assertTrue(validator.isValid("  http://example.com  ", null)); // Multiple spaces
    }

    @Test
    void testRealWorldExamples() {
        validator.initialize(getAnnotation("defaultURL"));

        assertTrue(validator.isValid("https://www.google.com", null)); // Google
        assertTrue(validator.isValid("https://www.github.com", null)); // GitHub
        assertTrue(validator.isValid("https://www.stackoverflow.com", null)); // Stack Overflow
        assertTrue(validator.isValid("https://www.wikipedia.org", null)); // Wikipedia
        assertTrue(validator.isValid("https://www.amazon.com", null)); // Amazon
        assertTrue(validator.isValid("https://www.microsoft.com", null)); // Microsoft
        assertTrue(validator.isValid("https://www.apple.com", null)); // Apple
        assertTrue(validator.isValid("https://www.facebook.com", null)); // Facebook
        assertTrue(validator.isValid("https://www.twitter.com", null)); // Twitter
        assertTrue(validator.isValid("https://www.linkedin.com", null)); // LinkedIn
    }

    @Test
    void testURLsWithPorts() {
        validator.initialize(getAnnotation("defaultURL"));

        assertTrue(validator.isValid("http://example.com:80", null)); // HTTP port 80
        assertTrue(validator.isValid("https://example.com:443", null)); // HTTPS port 443
        assertTrue(validator.isValid("http://example.com:8080", null)); // HTTP port 8080
        assertTrue(validator.isValid("https://example.com:8443", null)); // HTTPS port 8443
        assertTrue(validator.isValid("http://example.com:3000", null)); // HTTP port 3000
        assertTrue(validator.isValid("https://example.com:3000", null)); // HTTPS port 3000
        assertTrue(validator.isValid("http://example.com:9000", null)); // HTTP port 9000
        assertTrue(validator.isValid("https://example.com:9000", null)); // HTTPS port 9000
    }

    @Test
    void testURLsWithPaths() {
        validator.initialize(getAnnotation("defaultURL"));

        assertTrue(validator.isValid("http://example.com/", null)); // Root path
        assertTrue(validator.isValid("https://example.com/", null)); // Root path
        assertTrue(validator.isValid("http://example.com/path", null)); // Single path
        assertTrue(validator.isValid("https://example.com/path", null)); // Single path
        assertTrue(validator.isValid("http://example.com/path/to/page", null)); // Deep path
        assertTrue(validator.isValid("https://example.com/path/to/page", null)); // Deep path
        assertTrue(validator.isValid("http://example.com/path/to/page.html", null)); // File path
        assertTrue(validator.isValid("https://example.com/path/to/page.html", null)); // File path
    }

    @Test
    void testURLsWithQueries() {
        validator.initialize(getAnnotation("defaultURL"));

        assertTrue(validator.isValid("http://example.com?param=value", null)); // Single query
        assertTrue(validator.isValid("https://example.com?param=value", null)); // Single query
        assertTrue(validator.isValid("http://example.com?param1=value1&param2=value2", null)); // Multiple queries
        assertTrue(validator.isValid("https://example.com?param1=value1&param2=value2", null)); // Multiple queries
        assertTrue(validator.isValid("http://example.com/path?param=value", null)); // Path with query
        assertTrue(validator.isValid("https://example.com/path?param=value", null)); // Path with query
    }

    @Test
    void testURLsWithFragments() {
        validator.initialize(getAnnotation("defaultURL"));

        assertTrue(validator.isValid("http://example.com#fragment", null)); // Single fragment
        assertTrue(validator.isValid("https://example.com#fragment", null)); // Single fragment
        assertTrue(validator.isValid("http://example.com/path#fragment", null)); // Path with fragment
        assertTrue(validator.isValid("https://example.com/path#fragment", null)); // Path with fragment
        assertTrue(validator.isValid("http://example.com?param=value#fragment", null)); // Query with fragment
        assertTrue(validator.isValid("https://example.com?param=value#fragment", null)); // Query with fragment
    }

    @Test
    void testURLsWithCredentials() {
        validator.initialize(getAnnotation("defaultURL"));

        assertTrue(validator.isValid("http://user:pass@example.com", null)); // HTTP with credentials
        assertTrue(validator.isValid("https://user:pass@example.com", null)); // HTTPS with credentials
        assertTrue(validator.isValid("http://user@example.com", null)); // HTTP with username only
        assertTrue(validator.isValid("https://user@example.com", null)); // HTTPS with username only
        assertTrue(validator.isValid("http://user:pass@example.com:8080", null)); // HTTP with credentials and port
        assertTrue(validator.isValid("https://user:pass@example.com:8443", null)); // HTTPS with credentials and port
    }
}
