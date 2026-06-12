package id.xtramile.validator.validator.cross;

import id.xtramile.validator.annotation.cross.DifferentFrom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DifferentFromValidatorTest {

    @DifferentFrom(field = "username", other = "email")
        private record UserDifferentDummy(String username, String email) {
    }

    @DifferentFrom(field = "password", other = "confirmPassword")
        private record PasswordDifferentDummy(String password, String confirmPassword) {
    }

    @DifferentFrom(field = "field1", other = "field2")
        private record GenericDifferentDummy(Object field1, Object field2) {
    }

    private DifferentFromValidator validator;

    @BeforeEach
    void setUp() {
        validator = new DifferentFromValidator();
        validator.initialize(UserDifferentDummy.class.getAnnotation(DifferentFrom.class));
    }

    @Test
    void testValidDifferentStrings() {
        assertTrue(validator.isValid(new UserDifferentDummy("username", "email@example.com"), null)); // Different strings
        assertTrue(validator.isValid(new UserDifferentDummy("user1", "user2"), null)); // Different usernames
        assertTrue(validator.isValid(new UserDifferentDummy("admin", "user@example.com"), null)); // Different values
        assertTrue(validator.isValid(new UserDifferentDummy("test", "test@example.com"), null)); // Different values
        assertTrue(validator.isValid(new UserDifferentDummy("", "test@example.com"), null)); // Empty vs non-empty
        assertTrue(validator.isValid(new UserDifferentDummy("test", ""), null)); // Non-empty vs empty
    }

    @Test
    void testValidBothNull() {
        assertTrue(validator.isValid(new UserDifferentDummy(null, null), null)); // Both null
        assertTrue(validator.isValid(new UserDifferentDummy(null, null), null)); // Both null
    }

    @Test
    void testValidOneNull() {
        assertTrue(validator.isValid(new UserDifferentDummy(null, "email@example.com"), null)); // Username null, email present
        assertTrue(validator.isValid(new UserDifferentDummy("username", null), null)); // Username present, email null
        assertTrue(validator.isValid(new UserDifferentDummy(null, ""), null)); // Username null, email empty
        assertTrue(validator.isValid(new UserDifferentDummy("", null), null)); // Username empty, email null
    }

    @Test
    void testInvalidSameStrings() {
        assertFalse(validator.isValid(new UserDifferentDummy("test", "test"), null)); // Same strings
        assertFalse(validator.isValid(new UserDifferentDummy("username", "username"), null)); // Same usernames
        assertFalse(validator.isValid(new UserDifferentDummy("", ""), null)); // Both empty
        assertFalse(validator.isValid(new UserDifferentDummy("   ", "   "), null)); // Both whitespace
    }

    @Test
    void testPasswordDifferentValidation() {
        validator.initialize(PasswordDifferentDummy.class.getAnnotation(DifferentFrom.class));

        assertTrue(validator.isValid(new PasswordDifferentDummy("password123", "password456"), null)); // Different passwords
        assertTrue(validator.isValid(new PasswordDifferentDummy("password", "confirm"), null)); // Different passwords
        assertTrue(validator.isValid(new PasswordDifferentDummy(null, "password"), null)); // Password null, confirm present
        assertTrue(validator.isValid(new PasswordDifferentDummy("password", null), null)); // Password present, confirm null
        assertTrue(validator.isValid(new PasswordDifferentDummy("", "password"), null)); // Password empty, confirm present
        assertTrue(validator.isValid(new PasswordDifferentDummy("password", ""), null)); // Password present, confirm empty

        assertFalse(validator.isValid(new PasswordDifferentDummy("password123", "password123"), null)); // Same passwords
        assertFalse(validator.isValid(new PasswordDifferentDummy("", ""), null)); // Both empty
        assertFalse(validator.isValid(new PasswordDifferentDummy("   ", "   "), null)); // Both whitespace
    }

    @Test
    void testValidDifferentIntegers() {
        validator.initialize(GenericDifferentDummy.class.getAnnotation(DifferentFrom.class));
        assertTrue(validator.isValid(new GenericDifferentDummy(123, 456), null)); // Different integers
        assertTrue(validator.isValid(new GenericDifferentDummy(0, 1), null)); // Zero vs one
        assertTrue(validator.isValid(new GenericDifferentDummy(-1, 1), null)); // Negative vs positive
        assertTrue(validator.isValid(new GenericDifferentDummy(1, -1), null)); // Positive vs negative
        assertTrue(validator.isValid(new GenericDifferentDummy(Integer.MAX_VALUE, Integer.MIN_VALUE), null)); // Max vs min
        assertTrue(validator.isValid(new GenericDifferentDummy(null, 123), null)); // Null vs integer
        assertTrue(validator.isValid(new GenericDifferentDummy(123, null), null)); // Integer vs null
    }

    @Test
    void testInvalidSameIntegers() {
        validator.initialize(GenericDifferentDummy.class.getAnnotation(DifferentFrom.class));
        assertFalse(validator.isValid(new GenericDifferentDummy(123, 123), null)); // Same integers
        assertFalse(validator.isValid(new GenericDifferentDummy(0, 0), null)); // Both zero
        assertFalse(validator.isValid(new GenericDifferentDummy(-1, -1), null)); // Both negative
        assertFalse(validator.isValid(new GenericDifferentDummy(Integer.MAX_VALUE, Integer.MAX_VALUE), null)); // Both max
        assertFalse(validator.isValid(new GenericDifferentDummy(Integer.MIN_VALUE, Integer.MIN_VALUE), null)); // Both min
    }

    @Test
    void testValidDifferentDoubles() {
        validator.initialize(GenericDifferentDummy.class.getAnnotation(DifferentFrom.class));
        assertTrue(validator.isValid(new GenericDifferentDummy(123.45, 123.46), null)); // Different doubles
        assertTrue(validator.isValid(new GenericDifferentDummy(0.0, 0.1), null)); // Zero vs small positive
        assertTrue(validator.isValid(new GenericDifferentDummy(-1.5, 1.5), null)); // Negative vs positive
        assertTrue(validator.isValid(new GenericDifferentDummy(1.5, -1.5), null)); // Positive vs negative
        assertTrue(validator.isValid(new GenericDifferentDummy(Double.MAX_VALUE, Double.MIN_VALUE), null)); // Max vs min
        assertTrue(validator.isValid(new GenericDifferentDummy(null, 123.45), null)); // Null vs double
        assertTrue(validator.isValid(new GenericDifferentDummy(123.45, null), null)); // Double vs null
    }

    @Test
    void testInvalidSameDoubles() {
        validator.initialize(GenericDifferentDummy.class.getAnnotation(DifferentFrom.class));
        assertFalse(validator.isValid(new GenericDifferentDummy(123.45, 123.45), null)); // Same doubles
        assertFalse(validator.isValid(new GenericDifferentDummy(0.0, 0.0), null)); // Both zero
        assertFalse(validator.isValid(new GenericDifferentDummy(-1.5, -1.5), null)); // Both negative
        assertFalse(validator.isValid(new GenericDifferentDummy(Double.MAX_VALUE, Double.MAX_VALUE), null)); // Both max
        assertFalse(validator.isValid(new GenericDifferentDummy(Double.MIN_VALUE, Double.MIN_VALUE), null)); // Both min
    }

    @Test
    void testValidDifferentBooleans() {
        validator.initialize(GenericDifferentDummy.class.getAnnotation(DifferentFrom.class));
        assertTrue(validator.isValid(new GenericDifferentDummy(true, false), null)); // True vs false
        assertTrue(validator.isValid(new GenericDifferentDummy(false, true), null)); // False vs true
        assertTrue(validator.isValid(new GenericDifferentDummy(null, true), null)); // Null vs true
        assertTrue(validator.isValid(new GenericDifferentDummy(true, null), null)); // True vs null
        assertTrue(validator.isValid(new GenericDifferentDummy(null, false), null)); // Null vs false
        assertTrue(validator.isValid(new GenericDifferentDummy(false, null), null)); // False vs null
    }

    @Test
    void testInvalidSameBooleans() {
        validator.initialize(GenericDifferentDummy.class.getAnnotation(DifferentFrom.class));
        assertFalse(validator.isValid(new GenericDifferentDummy(true, true), null)); // Both true
        assertFalse(validator.isValid(new GenericDifferentDummy(false, false), null)); // Both false
    }

    @Test
    void testValidDifferentObjects() {
        validator.initialize(GenericDifferentDummy.class.getAnnotation(DifferentFrom.class));
        Object obj1 = new Object();
        Object obj2 = new Object();
        assertTrue(validator.isValid(new GenericDifferentDummy(obj1, obj2), null)); // Different object references
        assertTrue(validator.isValid(new GenericDifferentDummy(null, obj1), null)); // Null vs object
        assertTrue(validator.isValid(new GenericDifferentDummy(obj1, null), null)); // Object vs null
    }

    @Test
    @SuppressWarnings("UnnecessaryLocalVariable")
    void testInvalidSameObjects() {
        validator.initialize(GenericDifferentDummy.class.getAnnotation(DifferentFrom.class));
        Object obj1 = new Object();
        Object obj2 = obj1; // Same reference
        assertFalse(validator.isValid(new GenericDifferentDummy(obj1, obj2), null)); // Same object reference
    }

    @Test
    void testValidDifferentCollections() {
        validator.initialize(GenericDifferentDummy.class.getAnnotation(DifferentFrom.class));
        java.util.List<String> list1 = java.util.Arrays.asList("a", "b", "c");
        java.util.List<String> list2 = java.util.Arrays.asList("a", "b", "d");
        assertTrue(validator.isValid(new GenericDifferentDummy(list1, list2), null)); // Different lists
        assertTrue(validator.isValid(new GenericDifferentDummy(null, list1), null)); // Null vs list
        assertTrue(validator.isValid(new GenericDifferentDummy(list1, null), null)); // List vs null
    }

    @Test
    void testInvalidSameCollections() {
        validator.initialize(GenericDifferentDummy.class.getAnnotation(DifferentFrom.class));
        java.util.List<String> list1 = java.util.Arrays.asList("a", "b", "c");
        java.util.List<String> list2 = java.util.Arrays.asList("a", "b", "c");
        assertFalse(validator.isValid(new GenericDifferentDummy(list1, list2), null)); // Same lists
    }

    @Test
    void testNullBean() {
        assertTrue(validator.isValid(null, null)); // Null bean should be valid
    }

    @Test
    void testCaseSensitiveComparison() {
        assertTrue(validator.isValid(new UserDifferentDummy("Username", "username"), null)); // Case sensitive
        assertTrue(validator.isValid(new UserDifferentDummy("USERNAME", "username"), null)); // Case sensitive
        assertTrue(validator.isValid(new UserDifferentDummy("username", "USERNAME"), null)); // Case sensitive
        assertFalse(validator.isValid(new UserDifferentDummy("username", "username"), null)); // Exact case match
    }

    @Test
    void testWhitespaceSensitiveComparison() {
        assertTrue(validator.isValid(new UserDifferentDummy("username", " username"), null)); // Leading space
        assertTrue(validator.isValid(new UserDifferentDummy("username", "username "), null)); // Trailing space
        assertTrue(validator.isValid(new UserDifferentDummy("username", " username "), null)); // Both spaces
        assertTrue(validator.isValid(new UserDifferentDummy(" username", "username"), null)); // Leading space
        assertTrue(validator.isValid(new UserDifferentDummy("username ", "username"), null)); // Trailing space
        assertFalse(validator.isValid(new UserDifferentDummy("username", "username"), null)); // Exact match
    }

    @Test
    void testSpecialCharacters() {
        assertTrue(validator.isValid(new UserDifferentDummy("user@name", "user#name"), null)); // Different special characters
        assertTrue(validator.isValid(new UserDifferentDummy("user name", "username"), null)); // Space vs no space
        assertTrue(validator.isValid(new UserDifferentDummy("user\tname", "username"), null)); // Tab vs no tab
        assertTrue(validator.isValid(new UserDifferentDummy("user\nname", "username"), null)); // Newline vs no newline
        assertFalse(validator.isValid(new UserDifferentDummy("user@name", "user@name"), null)); // Same special characters
    }

    @Test
    void testEmptyStringComparison() {
        assertTrue(validator.isValid(new UserDifferentDummy("", "test"), null)); // Empty vs non-empty
        assertTrue(validator.isValid(new UserDifferentDummy("test", ""), null)); // Non-empty vs empty
        assertFalse(validator.isValid(new UserDifferentDummy("", ""), null)); // Both empty
    }

    @Test
    void testWhitespaceOnlyComparison() {
        assertTrue(validator.isValid(new UserDifferentDummy("   ", "test"), null)); // Whitespace vs text
        assertTrue(validator.isValid(new UserDifferentDummy("test", "   "), null)); // Text vs whitespace
        assertFalse(validator.isValid(new UserDifferentDummy("   ", "   "), null)); // Both whitespace
    }

    @Test
    void testNumericStringComparison() {
        assertTrue(validator.isValid(new UserDifferentDummy("123", "456"), null)); // Different numbers
        assertTrue(validator.isValid(new UserDifferentDummy("123", "123.45"), null)); // Integer vs decimal
        assertTrue(validator.isValid(new UserDifferentDummy("123", "abc"), null)); // Number vs text
        assertFalse(validator.isValid(new UserDifferentDummy("123", "123"), null)); // Same numbers
    }

    @Test
    void testUnicodeComparison() {
        assertTrue(validator.isValid(new UserDifferentDummy("tëst", "test"), null)); // Unicode vs ASCII
        assertTrue(validator.isValid(new UserDifferentDummy("用户", "user"), null)); // Chinese vs English
        assertTrue(validator.isValid(new UserDifferentDummy("тест", "test"), null)); // Cyrillic vs ASCII
        assertFalse(validator.isValid(new UserDifferentDummy("tëst", "tëst"), null)); // Same unicode
    }

    @Test
    void testLongStringComparison() {
        String longString1 = "verylongstringthatexceedsnormallimitsandmightcauseproblems";
        String longString2 = "verylongstringthatexceedsnormallimitsandmightcauseproblems2";
        assertTrue(validator.isValid(new UserDifferentDummy(longString1, longString2), null)); // Different long strings
        assertFalse(validator.isValid(new UserDifferentDummy(longString1, longString1), null)); // Same long strings
    }
}
