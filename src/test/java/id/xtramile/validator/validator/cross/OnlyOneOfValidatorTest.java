package id.xtramile.validator.validator.cross;

import id.xtramile.validator.annotation.cross.OnlyOneOf;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OnlyOneOfValidatorTest {

    private OnlyOneOfValidator validator;

    @BeforeEach
    void setUp() {
        validator = new OnlyOneOfValidator();
        validator.initialize(ContactDummy.class.getAnnotation(OnlyOneOf.class));
    }

    @Test
    void testValidOnlyEmail() {
        assertTrue(validator.isValid(new ContactDummy("test@example.com", null), null)); // Only email
        assertTrue(validator.isValid(new ContactDummy("test@example.com", ""), null)); // Email with empty phone
        assertTrue(validator.isValid(new ContactDummy("test@example.com", "   "), null)); // Email with whitespace phone
    }

    @Test
    void testValidOnlyPhone() {
        assertTrue(validator.isValid(new ContactDummy(null, "+1234567890"), null)); // Only phone
        assertTrue(validator.isValid(new ContactDummy("", "+1234567890"), null)); // Empty email with phone
        assertTrue(validator.isValid(new ContactDummy("   ", "+1234567890"), null)); // Whitespace email with phone
    }

    @Test
    void testInvalidBothPresent() {
        assertFalse(validator.isValid(new ContactDummy("test@example.com", "+1234567890"), null)); // Both email and phone
        assertFalse(validator.isValid(new ContactDummy("test@example.com", "1234567890"), null)); // Both email and phone
        assertFalse(validator.isValid(new ContactDummy("test@example.com", "phone"), null)); // Both email and phone
    }

    @Test
    void testInvalidNeitherPresent() {
        assertFalse(validator.isValid(new ContactDummy(null, null), null)); // Neither email nor phone
        assertFalse(validator.isValid(new ContactDummy("", ""), null)); // Both empty
        assertFalse(validator.isValid(new ContactDummy("   ", "   "), null)); // Both whitespace
    }

    @Test
    void testThreeFieldsOnlyOne() {
        validator.initialize(UserDummy.class.getAnnotation(OnlyOneOf.class));

        assertTrue(validator.isValid(new UserDummy("username", null, null), null)); // Only username
        assertTrue(validator.isValid(new UserDummy(null, "test@example.com", null), null)); // Only email
        assertTrue(validator.isValid(new UserDummy(null, null, "+1234567890"), null)); // Only phone
        assertTrue(validator.isValid(new UserDummy("username", "", ""), null)); // Username with empty others
        assertTrue(validator.isValid(new UserDummy("", "test@example.com", ""), null)); // Email with empty others
        assertTrue(validator.isValid(new UserDummy("", "", "+1234567890"), null)); // Phone with empty others
    }

    @Test
    void testThreeFieldsMultiplePresent() {
        validator.initialize(UserDummy.class.getAnnotation(OnlyOneOf.class));

        assertFalse(validator.isValid(new UserDummy("username", "test@example.com", null), null)); // Username and email
        assertFalse(validator.isValid(new UserDummy("username", null, "+1234567890"), null)); // Username and phone
        assertFalse(validator.isValid(new UserDummy(null, "test@example.com", "+1234567890"), null)); // Email and phone
        assertFalse(validator.isValid(new UserDummy("username", "test@example.com", "+1234567890"), null)); // All three
    }

    @Test
    void testThreeFieldsNonePresent() {
        validator.initialize(UserDummy.class.getAnnotation(OnlyOneOf.class));

        assertFalse(validator.isValid(new UserDummy(null, null, null), null)); // None present
        assertFalse(validator.isValid(new UserDummy("", "", ""), null)); // All empty
        assertFalse(validator.isValid(new UserDummy("   ", "   ", "   "), null)); // All whitespace
    }

    @Test
    void testFourFieldsOnlyOne() {
        validator.initialize(MultiFieldDummy.class.getAnnotation(OnlyOneOf.class));

        assertTrue(validator.isValid(new MultiFieldDummy("value1", null, null, null), null)); // Only field1
        assertTrue(validator.isValid(new MultiFieldDummy(null, "value2", null, null), null)); // Only field2
        assertTrue(validator.isValid(new MultiFieldDummy(null, null, "value3", null), null)); // Only field3
        assertTrue(validator.isValid(new MultiFieldDummy(null, null, null, "value4"), null)); // Only field4
        assertTrue(validator.isValid(new MultiFieldDummy("value1", "", "", ""), null)); // Field1 with empty others
        assertTrue(validator.isValid(new MultiFieldDummy("", "value2", "", ""), null)); // Field2 with empty others
        assertTrue(validator.isValid(new MultiFieldDummy("", "", "value3", ""), null)); // Field3 with empty others
        assertTrue(validator.isValid(new MultiFieldDummy("", "", "", "value4"), null)); // Field4 with empty others
    }

    @Test
    void testFourFieldsMultiplePresent() {
        validator.initialize(MultiFieldDummy.class.getAnnotation(OnlyOneOf.class));

        assertFalse(validator.isValid(new MultiFieldDummy("value1", "value2", null, null), null)); // Field1 and field2
        assertFalse(validator.isValid(new MultiFieldDummy("value1", null, "value3", null), null)); // Field1 and field3
        assertFalse(validator.isValid(new MultiFieldDummy("value1", null, null, "value4"), null)); // Field1 and field4
        assertFalse(validator.isValid(new MultiFieldDummy(null, "value2", "value3", null), null)); // Field2 and field3
        assertFalse(validator.isValid(new MultiFieldDummy(null, "value2", null, "value4"), null)); // Field2 and field4
        assertFalse(validator.isValid(new MultiFieldDummy(null, null, "value3", "value4"), null)); // Field3 and field4
        assertFalse(validator.isValid(new MultiFieldDummy("value1", "value2", "value3", null), null)); // Three fields
        assertFalse(validator.isValid(new MultiFieldDummy("value1", "value2", "value3", "value4"), null)); // All fields
    }

    @Test
    void testFourFieldsNonePresent() {
        validator.initialize(MultiFieldDummy.class.getAnnotation(OnlyOneOf.class));

        assertFalse(validator.isValid(new MultiFieldDummy(null, null, null, null), null)); // None present
        assertFalse(validator.isValid(new MultiFieldDummy("", "", "", ""), null)); // All empty
        assertFalse(validator.isValid(new MultiFieldDummy("   ", "   ", "   ", "   "), null)); // All whitespace
    }

    @Test
    void testSingleFieldPresent() {
        validator.initialize(SingleFieldDummy.class.getAnnotation(OnlyOneOf.class));

        assertTrue(validator.isValid(new SingleFieldDummy("value"), null)); // Single field present
        assertTrue(validator.isValid(new SingleFieldDummy("test"), null)); // Single field present
        assertTrue(validator.isValid(new SingleFieldDummy("123"), null)); // Single field present
    }

    @Test
    void testSingleFieldNotPresent() {
        validator.initialize(SingleFieldDummy.class.getAnnotation(OnlyOneOf.class));

        assertFalse(validator.isValid(new SingleFieldDummy(null), null)); // Single field not present
        assertFalse(validator.isValid(new SingleFieldDummy(""), null)); // Single field empty
        assertFalse(validator.isValid(new SingleFieldDummy("   "), null)); // Single field whitespace
    }

    @Test
    void testNullBean() {
        assertTrue(validator.isValid(null, null)); // Null bean should be valid
    }

    @Test
    void testWhitespaceHandling() {
        assertTrue(validator.isValid(new ContactDummy("test@example.com", "   "), null)); // Email with whitespace phone
        assertTrue(validator.isValid(new ContactDummy("   ", "+1234567890"), null)); // Whitespace email with phone
        assertFalse(validator.isValid(new ContactDummy("   ", "   "), null)); // Both whitespace
        assertFalse(validator.isValid(new ContactDummy("test@example.com", "test@example.com"), null)); // Both same values
    }

    @Test
    void testEmptyStringHandling() {
        assertTrue(validator.isValid(new ContactDummy("test@example.com", ""), null)); // Email with empty phone
        assertTrue(validator.isValid(new ContactDummy("", "+1234567890"), null)); // Empty email with phone
        assertFalse(validator.isValid(new ContactDummy("", ""), null)); // Both empty
        assertFalse(validator.isValid(new ContactDummy("test@example.com", "test@example.com"), null)); // Both same values
    }

    @Test
    void testSpecialCharacters() {
        assertTrue(validator.isValid(new ContactDummy("test@example.com", null), null)); // Email with special characters
        assertTrue(validator.isValid(new ContactDummy(null, "+1-234-567-8900"), null)); // Phone with special characters
        assertFalse(validator.isValid(new ContactDummy("test@example.com", "+1-234-567-8900"), null)); // Both with special characters
    }

    @Test
    void testNumericValues() {
        assertTrue(validator.isValid(new ContactDummy("123@example.com", null), null)); // Numeric email
        assertTrue(validator.isValid(new ContactDummy(null, "1234567890"), null)); // Numeric phone
        assertFalse(validator.isValid(new ContactDummy("123@example.com", "1234567890"), null)); // Both numeric
    }

    @Test
    void testCaseSensitiveValues() {
        assertTrue(validator.isValid(new ContactDummy("Test@Example.com", null), null)); // Case sensitive email
        assertTrue(validator.isValid(new ContactDummy(null, "Test@Example.com"), null)); // Case sensitive phone
        assertFalse(validator.isValid(new ContactDummy("Test@Example.com", "Test@Example.com"), null)); // Both same case
    }

    @Test
    void testLongValues() {
        String longEmail = "verylongemailaddressthatmightexceednormallimits@verylongdomainname.com";
        String longPhone = "+12345678901234567890";

        assertTrue(validator.isValid(new ContactDummy(longEmail, null), null)); // Long email
        assertTrue(validator.isValid(new ContactDummy(null, longPhone), null)); // Long phone
        assertFalse(validator.isValid(new ContactDummy(longEmail, longPhone), null)); // Both long values
    }

    @Test
    void testUnicodeValues() {
        assertTrue(validator.isValid(new ContactDummy("tëst@ëxämplë.com", null), null)); // Unicode email
        assertTrue(validator.isValid(new ContactDummy(null, "tëst@ëxämplë.com"), null)); // Unicode phone
        assertFalse(validator.isValid(new ContactDummy("tëst@ëxämplë.com", "tëst@ëxämplë.com"), null)); // Both unicode
    }

    @OnlyOneOf(fields = {"email", "phone"})
    private record ContactDummy(String email, String phone) {
    }

    @OnlyOneOf(fields = {"username", "email", "phone"})
    private record UserDummy(String username, String email, String phone) {
    }

    @OnlyOneOf(fields = {"field1", "field2", "field3", "field4"})
    private record MultiFieldDummy(String field1, String field2, String field3, String field4) {
    }

    @OnlyOneOf(fields = {"value"})
    private record SingleFieldDummy(String value) {
    }
}
