package id.xtramile.validator.validator.cross;

import id.xtramile.validator.annotation.cross.FieldMatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FieldMatchValidatorTest {

    @FieldMatch(first = "password", second = "confirmPassword")
        private record PasswordMatchDummy(String password, String confirmPassword) {
    }

    @FieldMatch(first = "email", second = "emailConfirmation")
        private record EmailMatchDummy(String email, String emailConfirmation) {
    }

    @FieldMatch(first = "field1", second = "field2")
        private record GenericMatchDummy(Object field1, Object field2) {
    }

    private FieldMatchValidator validator;

    @BeforeEach
    void setUp() {
        validator = new FieldMatchValidator();
        validator.initialize(PasswordMatchDummy.class.getAnnotation(FieldMatch.class));
    }

    @Test
    void testValidMatchingStrings() {
        assertTrue(validator.isValid(new PasswordMatchDummy("password123", "password123"), null)); // Matching passwords
        assertTrue(validator.isValid(new PasswordMatchDummy("", ""), null)); // Empty strings
        assertTrue(validator.isValid(new PasswordMatchDummy("test", "test"), null)); // Simple matching strings
        assertTrue(validator.isValid(new PasswordMatchDummy("complex@password#123", "complex@password#123"), null)); // Complex matching strings
        assertTrue(validator.isValid(new PasswordMatchDummy("   ", "   "), null)); // Whitespace strings
        assertTrue(validator.isValid(new PasswordMatchDummy("123", "123"), null)); // Numeric strings
    }

    @Test
    void testValidMatchingNulls() {
        assertTrue(validator.isValid(new PasswordMatchDummy(null, null), null)); // Both null
        assertTrue(validator.isValid(new PasswordMatchDummy(null, null), null)); // Both null
    }

    @Test
    void testInvalidNonMatchingStrings() {
        assertFalse(validator.isValid(new PasswordMatchDummy("password123", "password456"), null)); // Different passwords
        assertFalse(validator.isValid(new PasswordMatchDummy("test", "test1"), null)); // Different strings
        assertFalse(validator.isValid(new PasswordMatchDummy("", "test"), null)); // Empty vs non-empty
        assertFalse(validator.isValid(new PasswordMatchDummy("test", ""), null)); // Non-empty vs empty
        assertFalse(validator.isValid(new PasswordMatchDummy("   ", "test"), null)); // Whitespace vs text
        assertFalse(validator.isValid(new PasswordMatchDummy("test", "   "), null)); // Text vs whitespace
    }

    @Test
    void testInvalidNullVsNonNull() {
        assertFalse(validator.isValid(new PasswordMatchDummy(null, "password"), null)); // Null vs non-null
        assertFalse(validator.isValid(new PasswordMatchDummy("password", null), null)); // Non-null vs null
        assertFalse(validator.isValid(new PasswordMatchDummy(null, ""), null)); // Null vs empty
        assertFalse(validator.isValid(new PasswordMatchDummy("", null), null)); // Empty vs null
    }

    @Test
    void testValidMatchingIntegers() {
        validator.initialize(GenericMatchDummy.class.getAnnotation(FieldMatch.class));
        assertTrue(validator.isValid(new GenericMatchDummy(123, 123), null)); // Matching integers
        assertTrue(validator.isValid(new GenericMatchDummy(0, 0), null)); // Matching zeros
        assertTrue(validator.isValid(new GenericMatchDummy(-1, -1), null)); // Matching negative numbers
        assertTrue(validator.isValid(new GenericMatchDummy(Integer.MAX_VALUE, Integer.MAX_VALUE), null)); // Matching max values
        assertTrue(validator.isValid(new GenericMatchDummy(Integer.MIN_VALUE, Integer.MIN_VALUE), null)); // Matching min values
    }

    @Test
    void testInvalidNonMatchingIntegers() {
        validator.initialize(GenericMatchDummy.class.getAnnotation(FieldMatch.class));
        assertFalse(validator.isValid(new GenericMatchDummy(123, 456), null)); // Different integers
        assertFalse(validator.isValid(new GenericMatchDummy(0, 1), null)); // Zero vs one
        assertFalse(validator.isValid(new GenericMatchDummy(-1, 1), null)); // Negative vs positive
        assertFalse(validator.isValid(new GenericMatchDummy(1, -1), null)); // Positive vs negative
        assertFalse(validator.isValid(new GenericMatchDummy(Integer.MAX_VALUE, Integer.MIN_VALUE), null)); // Max vs min
    }

    @Test
    void testValidMatchingDoubles() {
        validator.initialize(GenericMatchDummy.class.getAnnotation(FieldMatch.class));
        assertTrue(validator.isValid(new GenericMatchDummy(123.45, 123.45), null)); // Matching doubles
        assertTrue(validator.isValid(new GenericMatchDummy(0.0, 0.0), null)); // Matching zeros
        assertTrue(validator.isValid(new GenericMatchDummy(-1.5, -1.5), null)); // Matching negative doubles
        assertTrue(validator.isValid(new GenericMatchDummy(Double.MAX_VALUE, Double.MAX_VALUE), null)); // Matching max values
        assertTrue(validator.isValid(new GenericMatchDummy(Double.MIN_VALUE, Double.MIN_VALUE), null)); // Matching min values
    }

    @Test
    void testInvalidNonMatchingDoubles() {
        validator.initialize(GenericMatchDummy.class.getAnnotation(FieldMatch.class));
        assertFalse(validator.isValid(new GenericMatchDummy(123.45, 123.46), null)); // Different doubles
        assertFalse(validator.isValid(new GenericMatchDummy(0.0, 0.1), null)); // Zero vs small positive
        assertFalse(validator.isValid(new GenericMatchDummy(-1.5, 1.5), null)); // Negative vs positive
        assertFalse(validator.isValid(new GenericMatchDummy(1.5, -1.5), null)); // Positive vs negative
        assertFalse(validator.isValid(new GenericMatchDummy(Double.MAX_VALUE, Double.MIN_VALUE), null)); // Max vs min
    }

    @Test
    void testValidMatchingBooleans() {
        validator.initialize(GenericMatchDummy.class.getAnnotation(FieldMatch.class));
        assertTrue(validator.isValid(new GenericMatchDummy(true, true), null)); // Matching true values
        assertTrue(validator.isValid(new GenericMatchDummy(false, false), null)); // Matching false values
    }

    @Test
    void testInvalidNonMatchingBooleans() {
        validator.initialize(GenericMatchDummy.class.getAnnotation(FieldMatch.class));
        assertFalse(validator.isValid(new GenericMatchDummy(true, false), null)); // True vs false
        assertFalse(validator.isValid(new GenericMatchDummy(false, true), null)); // False vs true
    }

    @Test
    @SuppressWarnings("UnnecessaryLocalVariable")
    void testValidMatchingObjects() {
        validator.initialize(GenericMatchDummy.class.getAnnotation(FieldMatch.class));
        Object obj1 = new Object();
        Object obj2 = obj1; // Same reference
        assertTrue(validator.isValid(new GenericMatchDummy(obj1, obj2), null)); // Same object reference
    }

    @Test
    void testInvalidNonMatchingObjects() {
        validator.initialize(GenericMatchDummy.class.getAnnotation(FieldMatch.class));
        Object obj1 = new Object();
        Object obj2 = new Object();
        assertFalse(validator.isValid(new GenericMatchDummy(obj1, obj2), null)); // Different object references
    }

    @Test
    void testValidMatchingCollections() {
        validator.initialize(GenericMatchDummy.class.getAnnotation(FieldMatch.class));
        java.util.List<String> list1 = java.util.Arrays.asList("a", "b", "c");
        java.util.List<String> list2 = java.util.Arrays.asList("a", "b", "c");
        assertTrue(validator.isValid(new GenericMatchDummy(list1, list2), null)); // Matching lists
    }

    @Test
    void testInvalidNonMatchingCollections() {
        validator.initialize(GenericMatchDummy.class.getAnnotation(FieldMatch.class));
        java.util.List<String> list1 = java.util.Arrays.asList("a", "b", "c");
        java.util.List<String> list2 = java.util.Arrays.asList("a", "b", "d");
        assertFalse(validator.isValid(new GenericMatchDummy(list1, list2), null)); // Different lists
    }

    @Test
    void testNullBean() {
        assertTrue(validator.isValid(null, null)); // Null bean should be valid
    }

    @Test
    void testEmailMatchValidation() {
        validator.initialize(EmailMatchDummy.class.getAnnotation(FieldMatch.class));
        
        assertTrue(validator.isValid(new EmailMatchDummy("test@example.com", "test@example.com"), null)); // Matching emails
        assertTrue(validator.isValid(new EmailMatchDummy(null, null), null)); // Both null
        assertFalse(validator.isValid(new EmailMatchDummy("test@example.com", "test@example.org"), null)); // Different emails
        assertFalse(validator.isValid(new EmailMatchDummy("test@example.com", null), null)); // Email vs null
        assertFalse(validator.isValid(new EmailMatchDummy(null, "test@example.com"), null)); // Null vs email
    }

    @Test
    void testCaseSensitiveMatching() {
        assertFalse(validator.isValid(new PasswordMatchDummy("Password", "password"), null)); // Case sensitive
        assertFalse(validator.isValid(new PasswordMatchDummy("PASSWORD", "password"), null)); // Case sensitive
        assertFalse(validator.isValid(new PasswordMatchDummy("password", "PASSWORD"), null)); // Case sensitive
        assertTrue(validator.isValid(new PasswordMatchDummy("Password", "Password"), null)); // Exact case match
    }

    @Test
    void testWhitespaceSensitiveMatching() {
        assertFalse(validator.isValid(new PasswordMatchDummy("password", " password"), null)); // Leading space
        assertFalse(validator.isValid(new PasswordMatchDummy("password", "password "), null)); // Trailing space
        assertFalse(validator.isValid(new PasswordMatchDummy("password", " password "), null)); // Both spaces
        assertFalse(validator.isValid(new PasswordMatchDummy(" password", "password"), null)); // Leading space
        assertFalse(validator.isValid(new PasswordMatchDummy("password ", "password"), null)); // Trailing space
        assertFalse(validator.isValid(new PasswordMatchDummy(" password", "password"), null)); // Leading space
        assertTrue(validator.isValid(new PasswordMatchDummy("password", "password"), null)); // Exact match
    }

    @Test
    void testSpecialCharactersMatching() {
        assertTrue(validator.isValid(new PasswordMatchDummy("pass@word#123", "pass@word#123"), null)); // Special characters
        assertTrue(validator.isValid(new PasswordMatchDummy("pass word", "pass word"), null)); // Space in middle
        assertTrue(validator.isValid(new PasswordMatchDummy("pass\tword", "pass\tword"), null)); // Tab character
        assertTrue(validator.isValid(new PasswordMatchDummy("pass\nword", "pass\nword"), null)); // Newline character
        assertFalse(validator.isValid(new PasswordMatchDummy("pass@word#123", "pass@word#456"), null)); // Different special characters
    }

    @Test
    void testEmptyStringMatching() {
        assertTrue(validator.isValid(new PasswordMatchDummy("", ""), null)); // Both empty
        assertFalse(validator.isValid(new PasswordMatchDummy("", "test"), null)); // Empty vs non-empty
        assertFalse(validator.isValid(new PasswordMatchDummy("test", ""), null)); // Non-empty vs empty
    }

    @Test
    void testWhitespaceOnlyMatching() {
        assertTrue(validator.isValid(new PasswordMatchDummy("   ", "   "), null)); // Both whitespace
        assertTrue(validator.isValid(new PasswordMatchDummy("\t", "\t"), null)); // Both tabs
        assertTrue(validator.isValid(new PasswordMatchDummy("\n", "\n"), null)); // Both newlines
        assertFalse(validator.isValid(new PasswordMatchDummy("   ", "test"), null)); // Whitespace vs text
        assertFalse(validator.isValid(new PasswordMatchDummy("test", "   "), null)); // Text vs whitespace
    }
}
