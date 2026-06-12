package id.xtramile.validator.web;

import id.xtramile.validator.enums.Group;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MessageResourceResolverTest {

    @Test
    void testConstructorWithLocale() {
        MessageResourceResolver resolver = new MessageResourceResolver("en");
        assertNotNull(resolver);
        assertEquals("validation.data.test", resolver.getMessage(Group.DATA, "test"));
    }

    @Test
    void testConstructorWithNullLocale() {
        MessageResourceResolver resolver = new MessageResourceResolver(null);
        assertNotNull(resolver);
    }

    @Test
    void testConstructorWithUpperCaseLocale() {
        MessageResourceResolver resolver = new MessageResourceResolver("EN");
        assertNotNull(resolver);
    }

    @Test
    void testGetMessageWithKey() {
        MessageResourceResolver resolver = new MessageResourceResolver("en");
        String message = resolver.getMessage("validation.data.test");

        assertNotNull(message);
    }

    @Test
    void testGetMessageWithKeyAndArgs() {
        MessageResourceResolver resolver = new MessageResourceResolver("en");
        String message = resolver.getMessage("validation.data.test", "arg1", "arg2");

        assertNotNull(message);
    }

    @Test
    void testGetMessageWithGroup() {
        MessageResourceResolver resolver = new MessageResourceResolver("en");
        String message = resolver.getMessage(Group.DATA, "test");

        assertNotNull(message);
    }

    @Test
    void testGetMessageWithGroupAndArgs() {
        MessageResourceResolver resolver = new MessageResourceResolver("en");
        String message = resolver.getMessage(Group.DATA, "test", "arg1", "arg2");

        assertNotNull(message);
    }

    @Test
    void testGetMessageNotFound() {
        MessageResourceResolver resolver = new MessageResourceResolver("en");
        String message = resolver.getMessage("nonexistent.key.that.does.not.exist");

        // Should return the key if not found
        assertEquals("nonexistent.key.that.does.not.exist", message);
    }

    @Test
    void testGetMessageWithNullArgs() {
        MessageResourceResolver resolver = new MessageResourceResolver("en");
        String message = resolver.getMessage("validation.data.test", (Object[]) null);

        assertNotNull(message);
    }

    @Test
    void testGetMessageWithEmptyArgs() {
        MessageResourceResolver resolver = new MessageResourceResolver("en");
        String message = resolver.getMessage("validation.data.test");

        assertNotNull(message);
    }

    @Test
    void testGetMessageFallbackToDefaultLocale() {
        MessageResourceResolver resolver = new MessageResourceResolver("fr"); // Non-existent locale
        String message = resolver.getMessage("validation.data.test");

        // Should fallback to default locale (id) if message not found
        assertNotNull(message);
    }

    @Test
    void testGetMessageWithInvalidFormat() {
        MessageResourceResolver resolver = new MessageResourceResolver("en");
        String message = resolver.getMessage("validation.data.test", "arg1", "arg2", "arg3");

        // Should handle format errors gracefully
        assertNotNull(message);
    }

    @Test
    void testIndonesianLocale() {
        MessageResourceResolver resolver = new MessageResourceResolver("id");
        String message = resolver.getMessage("validation.data.test");

        assertNotNull(message);
    }
}
