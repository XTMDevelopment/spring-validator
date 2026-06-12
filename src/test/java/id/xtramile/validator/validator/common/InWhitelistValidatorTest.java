package id.xtramile.validator.validator.common;

import id.xtramile.validator.annotation.common.InWhitelist;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InWhitelistValidatorTest {

    private InWhitelistValidator validator;

    private static InWhitelist getAnnotation(String fieldName) {
        try {
            Field f = InWhitelistDummy.class.getDeclaredField(fieldName);
            return f.getAnnotation(InWhitelist.class);
        } catch (NoSuchFieldException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
        validator = new InWhitelistValidator();
    }

    @Test
    void testWhitelistedValues() {
        validator.initialize(getAnnotation("defaultWhitelist"));

        assertTrue(validator.isValid("admin", null));
        assertTrue(validator.isValid("user", null));
        assertTrue(validator.isValid("guest", null));
    }

    @Test
    void testNonWhitelistedValues() {
        validator.initialize(getAnnotation("defaultWhitelist"));

        assertFalse(validator.isValid("root", null));
        assertFalse(validator.isValid("system", null));
        assertFalse(validator.isValid("normal", null));
        assertFalse(validator.isValid("john", null));
        assertFalse(validator.isValid("jane", null));
    }

    @Test
    void testIgnoreCaseTrue() {
        validator.initialize(getAnnotation("ignoreCaseWhitelist"));

        // Should be whitelisted (case insensitive)
        assertTrue(validator.isValid("admin", null));
        assertTrue(validator.isValid("ADMIN", null));
        assertTrue(validator.isValid("Admin", null));
        assertTrue(validator.isValid("AdMiN", null));

        assertTrue(validator.isValid("user", null));
        assertTrue(validator.isValid("USER", null));
        assertTrue(validator.isValid("User", null));

        assertTrue(validator.isValid("guest", null));
        assertTrue(validator.isValid("GUEST", null));
        assertTrue(validator.isValid("Guest", null));

        // Should be invalid
        assertFalse(validator.isValid("root", null));
        assertFalse(validator.isValid("ROOT", null));
        assertFalse(validator.isValid("Root", null));
    }

    @Test
    void testIgnoreCaseFalse() {
        validator.initialize(getAnnotation("caseSensitiveWhitelist"));

        // Should be whitelisted (exact case)
        assertTrue(validator.isValid("admin", null));
        assertTrue(validator.isValid("user", null));
        assertTrue(validator.isValid("guest", null));

        // Should be invalid (different case)
        assertFalse(validator.isValid("ADMIN", null));
        assertFalse(validator.isValid("Admin", null));
        assertFalse(validator.isValid("AdMiN", null));

        assertFalse(validator.isValid("USER", null));
        assertFalse(validator.isValid("User", null));

        assertFalse(validator.isValid("GUEST", null));
        assertFalse(validator.isValid("Guest", null));
    }

    @Test
    void testBlankValues() {
        validator.initialize(getAnnotation("defaultWhitelist"));

        assertTrue(validator.isValid("", null));
        assertTrue(validator.isValid("   ", null));
        assertTrue(validator.isValid(null, null));
    }

    @Test
    void testCustomWhitelist() {
        validator.initialize(getAnnotation("customWhitelist"));

        // Should be whitelisted
        assertTrue(validator.isValid("read", null));
        assertTrue(validator.isValid("write", null));
        assertTrue(validator.isValid("execute", null));

        // Should be invalid
        assertFalse(validator.isValid("admin", null)); // not in this whitelist
        assertFalse(validator.isValid("user", null)); // not in this whitelist
        assertFalse(validator.isValid("guest", null)); // not in this whitelist
        assertFalse(validator.isValid("delete", null));
        assertFalse(validator.isValid("update", null));
    }

    @Test
    void testPartialMatches() {
        validator.initialize(getAnnotation("defaultWhitelist"));

        // Should be invalid (partial matches)
        assertFalse(validator.isValid("administrator", null));
        assertFalse(validator.isValid("username", null));
        assertFalse(validator.isValid("guestuser", null));
        assertFalse(validator.isValid("admin123", null));
        assertFalse(validator.isValid("123admin", null));
    }

    @Test
    void testEdgeCases() {
        validator.initialize(getAnnotation("defaultWhitelist"));

        // Test with whitespace
        assertFalse(validator.isValid(" admin", null)); // leading space
        assertFalse(validator.isValid("admin ", null)); // trailing space
        assertFalse(validator.isValid(" admin ", null)); // both spaces

        // Test with special characters
        assertFalse(validator.isValid("admin@", null));
        assertFalse(validator.isValid("@admin", null));
        assertFalse(validator.isValid("admin-", null));
        assertFalse(validator.isValid("-admin", null));
    }

    @Test
    void testCaseVariations() {
        validator.initialize(getAnnotation("ignoreCaseWhitelist"));

        String[] adminVariations = {"admin", "ADMIN", "Admin", "AdMiN", "aDmIn"};
        for (String variation : adminVariations) {
            assertTrue(validator.isValid(variation, null));
        }

        String[] rootVariations = {"root", "ROOT", "Root", "RoOt", "rOoT"};
        for (String variation : rootVariations) {
            assertFalse(validator.isValid(variation, null));
        }
    }

    @Test
    void testMultipleWhitelistValues() {
        validator.initialize(getAnnotation("defaultWhitelist"));

        // Test all whitelisted values
        String[] whitelistedValues = {"admin", "user", "guest"};
        for (String value : whitelistedValues) {
            assertTrue(validator.isValid(value, null));
        }

        // Test non-whitelisted values
        String[] invalidValues = {"root", "system", "normal", "john", "jane"};
        for (String value : invalidValues) {
            assertFalse(validator.isValid(value, null));
        }
    }

    @Test
    void testEmptyStringHandling() {
        validator.initialize(getAnnotation("defaultWhitelist"));

        // Empty string should be valid (blank values are allowed)
        assertTrue(validator.isValid("", null));
        assertTrue(validator.isValid("   ", null));
        assertTrue(validator.isValid(null, null));
    }

    @Test
    void testSpecialCharacters() {
        validator.initialize(getAnnotation("defaultWhitelist"));

        // Values with special characters should not match
        assertFalse(validator.isValid("admin!", null));
        assertFalse(validator.isValid("user@", null));
        assertFalse(validator.isValid("guest#", null));
        assertFalse(validator.isValid("admin123", null));
        assertFalse(validator.isValid("user_name", null));
    }

    @Test
    void testWhitelistVsBlacklistBehavior() {
        validator.initialize(getAnnotation("defaultWhitelist"));

        // Only exact matches should be valid
        assertTrue(validator.isValid("admin", null));
        assertTrue(validator.isValid("user", null));
        assertTrue(validator.isValid("guest", null));

        // Everything else should be invalid
        assertFalse(validator.isValid("administrator", null));
        assertFalse(validator.isValid("username", null));
        assertFalse(validator.isValid("guestuser", null));
        assertFalse(validator.isValid("root", null));
        assertFalse(validator.isValid("system", null));
    }

    private static class InWhitelistDummy {
        @InWhitelist(values = {"admin", "user", "guest"})
        String defaultWhitelist;

        @InWhitelist(values = {"admin", "user", "guest"})
        String ignoreCaseWhitelist;

        @InWhitelist(values = {"admin", "user", "guest"}, ignoreCase = false)
        String caseSensitiveWhitelist;

        @InWhitelist(values = {"read", "write", "execute"})
        String customWhitelist;
    }
}
