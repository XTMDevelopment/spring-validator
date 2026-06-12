package id.xtramile.validator.validator.data;

import id.xtramile.validator.annotation.data.ValidSlug;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SlugValidatorTest {

    private static class SlugDummy {
        @ValidSlug
        String defaultSlug;

        @ValidSlug(min = 5, max = 20)
        String customSlug;
    }

    private SlugValidator validator;

    private static ValidSlug getAnnotation(String fieldName) {
        try {
            Field f = SlugDummy.class.getDeclaredField(fieldName);
            return f.getAnnotation(ValidSlug.class);
        } catch (NoSuchFieldException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
        validator = new SlugValidator();
    }

    @Test
    void testValidSlugs() {
        validator.initialize(getAnnotation("defaultSlug"));

        assertTrue(validator.isValid("hello", null));
        assertTrue(validator.isValid("hello-world", null));
        assertTrue(validator.isValid("hello-world-123", null));
        assertTrue(validator.isValid("123", null));
        assertTrue(validator.isValid("a", null)); // single character
        assertTrue(validator.isValid("a".repeat(100), null)); // exactly max length
    }

    @Test
    void testInvalidLength() {
        validator.initialize(getAnnotation("defaultSlug"));

        assertFalse(validator.isValid("a".repeat(101), null)); // too long (101 > 100)
    }

    @Test
    void testInvalidFormat() {
        validator.initialize(getAnnotation("defaultSlug"));

        assertFalse(validator.isValid("Hello", null)); // uppercase not allowed
        assertFalse(validator.isValid("hello_world", null)); // underscore not allowed
        assertFalse(validator.isValid("hello world", null)); // space not allowed
        assertFalse(validator.isValid("hello.", null)); // dot not allowed
        assertFalse(validator.isValid("hello-", null)); // trailing hyphen
        assertFalse(validator.isValid("-hello", null)); // leading hyphen
        assertFalse(validator.isValid("hello--world", null)); // double hyphen
    }

    @Test
    void testTrimming() {
        validator.initialize(getAnnotation("defaultSlug"));

        assertTrue(validator.isValid("  hello  ", null));
        assertTrue(validator.isValid("  hello-world  ", null));
    }

    @Test
    void testBlankValues() {
        validator.initialize(getAnnotation("defaultSlug"));

        assertTrue(validator.isValid("", null));
        assertTrue(validator.isValid("   ", null));
        assertTrue(validator.isValid(null, null));
    }

    @Test
    void testCustomMinMax() {
        validator.initialize(getAnnotation("customSlug"));

        assertTrue(validator.isValid("hello", null)); // exactly min
        assertTrue(validator.isValid("a".repeat(20), null)); // exactly max
        assertTrue(validator.isValid("hello-world-123", null)); // in between

        assertFalse(validator.isValid("hell", null)); // too short (4 < 5)
        assertFalse(validator.isValid("a".repeat(21), null)); // too long (21 > 20)
    }

    @Test
    void testComplexSlugs() {
        validator.initialize(getAnnotation("defaultSlug"));

        assertTrue(validator.isValid("my-awesome-blog-post", null));
        assertTrue(validator.isValid("123-456-789", null));
        assertTrue(validator.isValid("a-b-c-d-e", null));
        assertTrue(validator.isValid("test123", null));
    }

    @Test
    void testEdgeCases() {
        validator.initialize(getAnnotation("defaultSlug"));

        assertTrue(validator.isValid("a", null)); // minimum valid
        assertTrue(validator.isValid("a".repeat(100), null)); // exactly 100 chars
        assertFalse(validator.isValid("a".repeat(101), null)); // 101 chars
    }
}
