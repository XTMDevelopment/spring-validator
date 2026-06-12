package id.xtramile.validator.validator.cross;

import id.xtramile.validator.annotation.cross.AtLeastOneOf;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AtLeastOneOfValidatorTest {

    private AtLeastOneOfValidator validator;

    @BeforeEach
    void setUp() {
        validator = new AtLeastOneOfValidator();
        validator.initialize(ContactAtLeastOneDummy.class.getAnnotation(AtLeastOneOf.class));
    }

    @Test
    void testValidOnlyEmail() {
        assertTrue(validator.isValid(new ContactAtLeastOneDummy("test@example.com", null), null)); // Only email
        assertTrue(validator.isValid(new ContactAtLeastOneDummy("test@example.com", ""), null)); // Email with empty phone
        assertTrue(validator.isValid(new ContactAtLeastOneDummy("test@example.com", "   "), null)); // Email with whitespace phone
    }

    @Test
    void testValidOnlyPhone() {
        assertTrue(validator.isValid(new ContactAtLeastOneDummy(null, "+1234567890"), null)); // Only phone
        assertTrue(validator.isValid(new ContactAtLeastOneDummy("", "+1234567890"), null)); // Empty email with phone
        assertTrue(validator.isValid(new ContactAtLeastOneDummy("   ", "+1234567890"), null)); // Whitespace email with phone
    }

    @Test
    void testValidBothPresent() {
        assertTrue(validator.isValid(new ContactAtLeastOneDummy("test@example.com", "+1234567890"), null)); // Both email and phone
        assertTrue(validator.isValid(new ContactAtLeastOneDummy("test@example.com", "1234567890"), null)); // Both email and phone
        assertTrue(validator.isValid(new ContactAtLeastOneDummy("test@example.com", "phone"), null)); // Both email and phone
    }

    @Test
    void testInvalidNeitherPresent() {
        assertFalse(validator.isValid(new ContactAtLeastOneDummy(null, null), null)); // Neither email nor phone
        assertFalse(validator.isValid(new ContactAtLeastOneDummy("", ""), null)); // Both empty
        assertFalse(validator.isValid(new ContactAtLeastOneDummy("   ", "   "), null)); // Both whitespace
    }

    @Test
    void testThreeFieldsOnlyOne() {
        validator.initialize(UserAtLeastOneDummy.class.getAnnotation(AtLeastOneOf.class));

        assertTrue(validator.isValid(new UserAtLeastOneDummy("username", null, null), null)); // Only username
        assertTrue(validator.isValid(new UserAtLeastOneDummy(null, "test@example.com", null), null)); // Only email
        assertTrue(validator.isValid(new UserAtLeastOneDummy(null, null, "+1234567890"), null)); // Only phone
        assertTrue(validator.isValid(new UserAtLeastOneDummy("username", "", ""), null)); // Username with empty others
        assertTrue(validator.isValid(new UserAtLeastOneDummy("", "test@example.com", ""), null)); // Email with empty others
        assertTrue(validator.isValid(new UserAtLeastOneDummy("", "", "+1234567890"), null)); // Phone with empty others
    }

    @Test
    void testThreeFieldsMultiplePresent() {
        validator.initialize(UserAtLeastOneDummy.class.getAnnotation(AtLeastOneOf.class));

        assertTrue(validator.isValid(new UserAtLeastOneDummy("username", "test@example.com", null), null)); // Username and email
        assertTrue(validator.isValid(new UserAtLeastOneDummy("username", null, "+1234567890"), null)); // Username and phone
        assertTrue(validator.isValid(new UserAtLeastOneDummy(null, "test@example.com", "+1234567890"), null)); // Email and phone
        assertTrue(validator.isValid(new UserAtLeastOneDummy("username", "test@example.com", "+1234567890"), null)); // All three
    }

    @Test
    void testThreeFieldsNonePresent() {
        validator.initialize(UserAtLeastOneDummy.class.getAnnotation(AtLeastOneOf.class));

        assertFalse(validator.isValid(new UserAtLeastOneDummy(null, null, null), null)); // None present
        assertFalse(validator.isValid(new UserAtLeastOneDummy("", "", ""), null)); // All empty
        assertFalse(validator.isValid(new UserAtLeastOneDummy("   ", "   ", "   "), null)); // All whitespace
    }

    @Test
    void testFourFieldsOnlyOne() {
        validator.initialize(MultiFieldAtLeastOneDummy.class.getAnnotation(AtLeastOneOf.class));

        assertTrue(validator.isValid(new MultiFieldAtLeastOneDummy("value1", null, null, null), null)); // Only field1
        assertTrue(validator.isValid(new MultiFieldAtLeastOneDummy(null, "value2", null, null), null)); // Only field2
        assertTrue(validator.isValid(new MultiFieldAtLeastOneDummy(null, null, "value3", null), null)); // Only field3
        assertTrue(validator.isValid(new MultiFieldAtLeastOneDummy(null, null, null, "value4"), null)); // Only field4
        assertTrue(validator.isValid(new MultiFieldAtLeastOneDummy("value1", "", "", ""), null)); // Field1 with empty others
        assertTrue(validator.isValid(new MultiFieldAtLeastOneDummy("", "value2", "", ""), null)); // Field2 with empty others
        assertTrue(validator.isValid(new MultiFieldAtLeastOneDummy("", "", "value3", ""), null)); // Field3 with empty others
        assertTrue(validator.isValid(new MultiFieldAtLeastOneDummy("", "", "", "value4"), null)); // Field4 with empty others
    }

    @Test
    void testFourFieldsMultiplePresent() {
        validator.initialize(MultiFieldAtLeastOneDummy.class.getAnnotation(AtLeastOneOf.class));

        assertTrue(validator.isValid(new MultiFieldAtLeastOneDummy("value1", "value2", null, null), null)); // Field1 and field2
        assertTrue(validator.isValid(new MultiFieldAtLeastOneDummy("value1", null, "value3", null), null)); // Field1 and field3
        assertTrue(validator.isValid(new MultiFieldAtLeastOneDummy("value1", null, null, "value4"), null)); // Field1 and field4
        assertTrue(validator.isValid(new MultiFieldAtLeastOneDummy(null, "value2", "value3", null), null)); // Field2 and field3
        assertTrue(validator.isValid(new MultiFieldAtLeastOneDummy(null, "value2", null, "value4"), null)); // Field2 and field4
        assertTrue(validator.isValid(new MultiFieldAtLeastOneDummy(null, null, "value3", "value4"), null)); // Field3 and field4
        assertTrue(validator.isValid(new MultiFieldAtLeastOneDummy("value1", "value2", "value3", null), null)); // Three fields
        assertTrue(validator.isValid(new MultiFieldAtLeastOneDummy("value1", "value2", "value3", "value4"), null)); // All fields
    }

    @Test
    void testFourFieldsNonePresent() {
        validator.initialize(MultiFieldAtLeastOneDummy.class.getAnnotation(AtLeastOneOf.class));

        assertFalse(validator.isValid(new MultiFieldAtLeastOneDummy(null, null, null, null), null)); // None present
        assertFalse(validator.isValid(new MultiFieldAtLeastOneDummy("", "", "", ""), null)); // All empty
        assertFalse(validator.isValid(new MultiFieldAtLeastOneDummy("   ", "   ", "   ", "   "), null)); // All whitespace
    }

    @Test
    void testSingleFieldPresent() {
        validator.initialize(SingleFieldAtLeastOneDummy.class.getAnnotation(AtLeastOneOf.class));

        assertTrue(validator.isValid(new SingleFieldAtLeastOneDummy("value"), null)); // Single field present
        assertTrue(validator.isValid(new SingleFieldAtLeastOneDummy("test"), null)); // Single field present
        assertTrue(validator.isValid(new SingleFieldAtLeastOneDummy("123"), null)); // Single field present
    }

    @Test
    void testSingleFieldNotPresent() {
        validator.initialize(SingleFieldAtLeastOneDummy.class.getAnnotation(AtLeastOneOf.class));

        assertFalse(validator.isValid(new SingleFieldAtLeastOneDummy(null), null)); // Single field not present
        assertFalse(validator.isValid(new SingleFieldAtLeastOneDummy(""), null)); // Single field empty
        assertFalse(validator.isValid(new SingleFieldAtLeastOneDummy("   "), null)); // Single field whitespace
    }

    @Test
    void testNullBean() {
        assertTrue(validator.isValid(null, null)); // Null bean should be valid
    }

    @Test
    void testWhitespaceHandling() {
        assertTrue(validator.isValid(new ContactAtLeastOneDummy("test@example.com", "   "), null)); // Email with whitespace phone
        assertTrue(validator.isValid(new ContactAtLeastOneDummy("   ", "+1234567890"), null)); // Whitespace email with phone
        assertFalse(validator.isValid(new ContactAtLeastOneDummy("   ", "   "), null)); // Both whitespace
        assertTrue(validator.isValid(new ContactAtLeastOneDummy("test@example.com", "test@example.com"), null)); // Both same values
    }

    @Test
    void testEmptyStringHandling() {
        assertTrue(validator.isValid(new ContactAtLeastOneDummy("test@example.com", ""), null)); // Email with empty phone
        assertTrue(validator.isValid(new ContactAtLeastOneDummy("", "+1234567890"), null)); // Empty email with phone
        assertFalse(validator.isValid(new ContactAtLeastOneDummy("", ""), null)); // Both empty
        assertTrue(validator.isValid(new ContactAtLeastOneDummy("test@example.com", "test@example.com"), null)); // Both same values
    }

    @Test
    void testSpecialCharacters() {
        assertTrue(validator.isValid(new ContactAtLeastOneDummy("test@example.com", null), null)); // Email with special characters
        assertTrue(validator.isValid(new ContactAtLeastOneDummy(null, "+1-234-567-8900"), null)); // Phone with special characters
        assertTrue(validator.isValid(new ContactAtLeastOneDummy("test@example.com", "+1-234-567-8900"), null)); // Both with special characters
    }

    @Test
    void testNumericValues() {
        assertTrue(validator.isValid(new ContactAtLeastOneDummy("123@example.com", null), null)); // Numeric email
        assertTrue(validator.isValid(new ContactAtLeastOneDummy(null, "1234567890"), null)); // Numeric phone
        assertTrue(validator.isValid(new ContactAtLeastOneDummy("123@example.com", "1234567890"), null)); // Both numeric
    }

    @Test
    void testCaseSensitiveValues() {
        assertTrue(validator.isValid(new ContactAtLeastOneDummy("Test@Example.com", null), null)); // Case sensitive email
        assertTrue(validator.isValid(new ContactAtLeastOneDummy(null, "Test@Example.com"), null)); // Case sensitive phone
        assertTrue(validator.isValid(new ContactAtLeastOneDummy("Test@Example.com", "Test@Example.com"), null)); // Both same case
    }

    @Test
    void testLongValues() {
        String longEmail = "verylongemailaddressthatmightexceednormallimits@verylongdomainname.com";
        String longPhone = "+12345678901234567890";

        assertTrue(validator.isValid(new ContactAtLeastOneDummy(longEmail, null), null)); // Long email
        assertTrue(validator.isValid(new ContactAtLeastOneDummy(null, longPhone), null)); // Long phone
        assertTrue(validator.isValid(new ContactAtLeastOneDummy(longEmail, longPhone), null)); // Both long values
    }

    @Test
    void testUnicodeValues() {
        assertTrue(validator.isValid(new ContactAtLeastOneDummy("tëst@ëxämplë.com", null), null)); // Unicode email
        assertTrue(validator.isValid(new ContactAtLeastOneDummy(null, "tëst@ëxämplë.com"), null)); // Unicode phone
        assertTrue(validator.isValid(new ContactAtLeastOneDummy("tëst@ëxämplë.com", "tëst@ëxämplë.com"), null)); // Both unicode
    }

    @Test
    void testMixedDataTypes() {
        validator.initialize(MixedTypeDummy.class.getAnnotation(AtLeastOneOf.class));

        assertTrue(validator.isValid(new MixedTypeDummy("test", null, null), null)); // Only string
        assertTrue(validator.isValid(new MixedTypeDummy(null, 123, null), null)); // Only integer
        assertTrue(validator.isValid(new MixedTypeDummy(null, null, true), null)); // Only boolean
        assertTrue(validator.isValid(new MixedTypeDummy("test", 123, null), null)); // String and integer
        assertTrue(validator.isValid(new MixedTypeDummy("test", null, true), null)); // String and boolean
        assertTrue(validator.isValid(new MixedTypeDummy(null, 123, true), null)); // Integer and boolean
        assertTrue(validator.isValid(new MixedTypeDummy("test", 123, true), null)); // All three

        assertFalse(validator.isValid(new MixedTypeDummy(null, null, null), null)); // None present
        assertFalse(validator.isValid(new MixedTypeDummy("", null, null), null)); // Empty string
        assertFalse(validator.isValid(new MixedTypeDummy("   ", null, null), null)); // Whitespace string
    }

    @Test
    void testEdgeCaseValues() {
        assertTrue(validator.isValid(new ContactAtLeastOneDummy("a", null), null)); // Single character
        assertTrue(validator.isValid(new ContactAtLeastOneDummy(null, "1"), null)); // Single digit
        assertTrue(validator.isValid(new ContactAtLeastOneDummy("a", "1"), null)); // Both single characters
        assertFalse(validator.isValid(new ContactAtLeastOneDummy(null, null), null)); // None present
    }

    @Test
    void testNullVsEmptyVsWhitespace() {
        assertTrue(validator.isValid(new ContactAtLeastOneDummy("test", null), null)); // Valid string vs null
        assertTrue(validator.isValid(new ContactAtLeastOneDummy("test", ""), null)); // Valid string vs empty
        assertTrue(validator.isValid(new ContactAtLeastOneDummy("test", "   "), null)); // Valid string vs whitespace
        assertFalse(validator.isValid(new ContactAtLeastOneDummy(null, null), null)); // Both null
        assertFalse(validator.isValid(new ContactAtLeastOneDummy("", ""), null)); // Both empty
        assertFalse(validator.isValid(new ContactAtLeastOneDummy("   ", "   "), null)); // Both whitespace
    }

    @AtLeastOneOf(fields = {"email", "phone"})
    private record ContactAtLeastOneDummy(String email, String phone) {
    }

    @AtLeastOneOf(fields = {"username", "email", "phone"})
    private record UserAtLeastOneDummy(String username, String email, String phone) {
    }

    @AtLeastOneOf(fields = {"field1", "field2", "field3", "field4"})
    private record MultiFieldAtLeastOneDummy(String field1, String field2, String field3, String field4) {
    }

    @AtLeastOneOf(fields = {"value"})
    private record SingleFieldAtLeastOneDummy(String value) {
    }

    @AtLeastOneOf(fields = {"stringField", "intField", "boolField"})
    private record MixedTypeDummy(String stringField, Integer intField, Boolean boolField) {
    }
}
