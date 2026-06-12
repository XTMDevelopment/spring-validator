package id.xtramile.validator.validator.data;

import id.xtramile.validator.annotation.data.ValidJSON;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JsonStringValidatorTest {

    private JsonStringValidator validator;

    private static ValidJSON getAnnotation(String fieldName) {
        try {
            Field f = JsonDummy.class.getDeclaredField(fieldName);
            return f.getAnnotation(ValidJSON.class);
        } catch (NoSuchFieldException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
        validator = new JsonStringValidator();
    }

    @Test
    void testValidJsonObjects() {
        validator.initialize(getAnnotation("jsonField"));

        assertTrue(validator.isValid("{}", null));
        assertTrue(validator.isValid("{\"key\": \"value\"}", null));
        assertTrue(validator.isValid("{\"name\": \"John\", \"age\": 30}", null));
        assertTrue(validator.isValid("{\"nested\": {\"key\": \"value\"}}", null));
        assertTrue(validator.isValid("{\"array\": [1, 2, 3]}", null));
    }

    @Test
    void testValidJsonArrays() {
        validator.initialize(getAnnotation("jsonField"));

        assertTrue(validator.isValid("[]", null));
        assertTrue(validator.isValid("[1, 2, 3]", null));
        assertTrue(validator.isValid("[\"a\", \"b\", \"c\"]", null));
        assertTrue(validator.isValid("[{\"key\": \"value\"}]", null));
        assertTrue(validator.isValid("[1, \"string\", true, null]", null));
    }

    @Test
    void testValidJsonPrimitives() {
        validator.initialize(getAnnotation("jsonField"));

        assertTrue(validator.isValid("\"string\"", null));
        assertTrue(validator.isValid("123", null));
        assertTrue(validator.isValid("123.45", null));
        assertTrue(validator.isValid("true", null));
        assertTrue(validator.isValid("false", null));
        assertTrue(validator.isValid("null", null));
    }

    @Test
    void testInvalidJsonStrings() {
        validator.initialize(getAnnotation("jsonField"));

        assertFalse(validator.isValid("{", null)); // incomplete object
        assertFalse(validator.isValid("}", null)); // incomplete object
        assertFalse(validator.isValid("[", null)); // incomplete array
        assertFalse(validator.isValid("]", null)); // incomplete array
        assertFalse(validator.isValid("{key: value}", null)); // unquoted keys
        assertFalse(validator.isValid("{'key': 'value'}", null)); // single quotes
        assertFalse(validator.isValid("{key: \"value\"}", null)); // unquoted key
        assertFalse(validator.isValid("{\"key\": value}", null)); // unquoted value
        assertFalse(validator.isValid("{\"key\": \"value\",}", null)); // trailing comma
        assertFalse(validator.isValid("{\"key\": \"value\" \"key2\": \"value2\"}", null)); // missing comma
    }

    @Test
    void testBlankValues() {
        validator.initialize(getAnnotation("jsonField"));

        assertTrue(validator.isValid("", null));
        assertTrue(validator.isValid("   ", null));
        assertTrue(validator.isValid(null, null));
    }

    @Test
    void testComplexJsonStructures() {
        validator.initialize(getAnnotation("jsonField"));

        assertTrue(validator.isValid("{\"users\": [{\"id\": 1, \"name\": \"John\"}, {\"id\": 2, \"name\": \"Jane\"}]}", null));
        assertTrue(validator.isValid("{\"config\": {\"database\": {\"host\": \"localhost\", \"port\": 5432}}}", null));
        assertTrue(validator.isValid("[{\"type\": \"object\", \"properties\": {\"name\": {\"type\": \"string\"}}}]", null));
    }

    @Test
    void testJsonWithSpecialCharacters() {
        validator.initialize(getAnnotation("jsonField"));

        assertTrue(validator.isValid("{\"message\": \"Hello, World!\"}", null));
        assertTrue(validator.isValid("{\"path\": \"C:\\\\Users\\\\John\"}", null));
        assertTrue(validator.isValid("{\"unicode\": \"\\u0041\\u0042\\u0043\"}", null));
        assertTrue(validator.isValid("{\"emoji\": \"\\ud83d\\ude00\"}", null));
    }

    @Test
    void testEdgeCases() {
        validator.initialize(getAnnotation("jsonField"));

        assertTrue(validator.isValid("0", null)); // number zero
        assertTrue(validator.isValid("-0", null)); // negative zero
        assertTrue(validator.isValid("0.0", null)); // decimal zero
        assertTrue(validator.isValid("\"\"", null)); // empty string
        assertTrue(validator.isValid("[]", null)); // empty array
        assertTrue(validator.isValid("{}", null)); // empty object
    }

    private static class JsonDummy {
        @ValidJSON
        String jsonField;
    }
}
