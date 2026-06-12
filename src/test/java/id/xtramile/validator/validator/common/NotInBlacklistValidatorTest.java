package id.xtramile.validator.validator.common;

import id.xtramile.validator.annotation.common.NotInBlacklist;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NotInBlacklistValidatorTest {

    private NotInBlacklistValidator validator;

    private static NotInBlacklist getAnnotation(String fieldName) {
        try {
            Field f = NotInBlacklistDummy.class.getDeclaredField(fieldName);
            return f.getAnnotation(NotInBlacklist.class);
        } catch (NoSuchFieldException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
        validator = new NotInBlacklistValidator();
    }

    @Test
    void testValidValues() {
        validator.initialize(getAnnotation("defaultBlacklist"));

        assertTrue(validator.isValid("user", null));
        assertTrue(validator.isValid("guest", null));
        assertTrue(validator.isValid("normal", null));
        assertTrue(validator.isValid("john", null));
        assertTrue(validator.isValid("jane", null));
        assertTrue(validator.isValid("", null));
    }

    @Test
    void testBlacklistedValues() {
        validator.initialize(getAnnotation("defaultBlacklist"));

        assertFalse(validator.isValid("admin", null));
        assertFalse(validator.isValid("root", null));
        assertFalse(validator.isValid("system", null));
    }

    @Test
    void testIgnoreCaseTrue() {
        validator.initialize(getAnnotation("ignoreCaseBlacklist"));

        // Should be blacklisted (case insensitive)
        assertFalse(validator.isValid("admin", null));
        assertFalse(validator.isValid("ADMIN", null));
        assertFalse(validator.isValid("Admin", null));
        assertFalse(validator.isValid("AdMiN", null));

        assertFalse(validator.isValid("root", null));
        assertFalse(validator.isValid("ROOT", null));
        assertFalse(validator.isValid("Root", null));

        assertFalse(validator.isValid("system", null));
        assertFalse(validator.isValid("SYSTEM", null));
        assertFalse(validator.isValid("System", null));

        // Should be valid
        assertTrue(validator.isValid("user", null));
        assertTrue(validator.isValid("USER", null));
        assertTrue(validator.isValid("User", null));
    }

    @Test
    void testIgnoreCaseFalse() {
        validator.initialize(getAnnotation("caseSensitiveBlacklist"));

        // Should be blacklisted (exact case)
        assertFalse(validator.isValid("admin", null));
        assertFalse(validator.isValid("root", null));
        assertFalse(validator.isValid("system", null));

        // Should be valid (different case)
        assertTrue(validator.isValid("ADMIN", null));
        assertTrue(validator.isValid("Admin", null));
        assertTrue(validator.isValid("AdMiN", null));

        assertTrue(validator.isValid("ROOT", null));
        assertTrue(validator.isValid("Root", null));

        assertTrue(validator.isValid("SYSTEM", null));
        assertTrue(validator.isValid("System", null));
    }

    @Test
    void testBlankValues() {
        validator.initialize(getAnnotation("defaultBlacklist"));

        assertTrue(validator.isValid("", null));
        assertTrue(validator.isValid("   ", null));
    }

    @Test
    void testCustomBlacklist() {
        validator.initialize(getAnnotation("customBlacklist"));

        // Should be blacklisted
        assertFalse(validator.isValid("test", null));
        assertFalse(validator.isValid("demo", null));
        assertFalse(validator.isValid("sample", null));

        // Should be valid
        assertTrue(validator.isValid("admin", null)); // not in this blacklist
        assertTrue(validator.isValid("root", null)); // not in this blacklist
        assertTrue(validator.isValid("system", null)); // not in this blacklist
        assertTrue(validator.isValid("user", null));
        assertTrue(validator.isValid("guest", null));
    }

    @Test
    void testPartialMatches() {
        validator.initialize(getAnnotation("defaultBlacklist"));

        // Should be valid (partial matches)
        assertTrue(validator.isValid("administrator", null));
        assertTrue(validator.isValid("rootuser", null));
        assertTrue(validator.isValid("systemadmin", null));
        assertTrue(validator.isValid("admin123", null));
        assertTrue(validator.isValid("123admin", null));
    }

    @Test
    void testEdgeCases() {
        validator.initialize(getAnnotation("defaultBlacklist"));

        // Test with whitespace
        assertTrue(validator.isValid(" admin", null)); // leading space
        assertTrue(validator.isValid("admin ", null)); // trailing space
        assertTrue(validator.isValid(" admin ", null)); // both spaces

        // Test with special characters
        assertTrue(validator.isValid("admin@", null));
        assertTrue(validator.isValid("@admin", null));
        assertTrue(validator.isValid("admin-", null));
        assertTrue(validator.isValid("-admin", null));
    }

    @Test
    void testEmptyBlacklist() {
        validator.initialize(getAnnotation("defaultBlacklist"));

        // These values should be invalid because they are in the blacklist
        assertFalse(validator.isValid("admin", null));
        assertFalse(validator.isValid("root", null));
        assertFalse(validator.isValid("system", null));
    }

    @Test
    void testCaseVariations() {
        validator.initialize(getAnnotation("ignoreCaseBlacklist"));

        String[] adminVariations = {"admin", "ADMIN", "Admin", "AdMiN", "aDmIn"};
        for (String variation : adminVariations) {
            assertFalse(validator.isValid(variation, null));
        }

        String[] userVariations = {"user", "USER", "User", "UsEr", "uSeR"};
        for (String variation : userVariations) {
            assertTrue(validator.isValid(variation, null));
        }
    }

    @Test
    void testMultipleBlacklistValues() {
        validator.initialize(getAnnotation("defaultBlacklist"));

        // Test all blacklisted values
        String[] blacklistedValues = {"admin", "root", "system"};
        for (String value : blacklistedValues) {
            assertFalse(validator.isValid(value, null));
        }

        // Test non-blacklisted values
        String[] validValues = {"user", "guest", "normal", "john", "jane"};
        for (String value : validValues) {
            assertTrue(validator.isValid(value, null));
        }
    }

    private static class NotInBlacklistDummy {
        @NotInBlacklist(values = {"admin", "root", "system"})
        String defaultBlacklist;

        @NotInBlacklist(values = {"admin", "root", "system"})
        String ignoreCaseBlacklist;

        @NotInBlacklist(values = {"admin", "root", "system"}, ignoreCase = false)
        String caseSensitiveBlacklist;

        @NotInBlacklist(values = {"test", "demo", "sample"})
        String customBlacklist;
    }
}
