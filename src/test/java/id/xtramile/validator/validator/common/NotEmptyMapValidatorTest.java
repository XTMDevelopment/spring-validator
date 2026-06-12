package id.xtramile.validator.validator.common;

import id.xtramile.validator.annotation.common.NotEmptyCollection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NotEmptyMapValidatorTest {

    private NotEmptyMapValidator validator;

    private static NotEmptyCollection getAnnotation(String fieldName) {
        try {
            Field f = NotEmptyMapDummy.class.getDeclaredField(fieldName);
            return f.getAnnotation(NotEmptyCollection.class);
        } catch (NoSuchFieldException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
        validator = new NotEmptyMapValidator();
    }

    @Test
    void testNonEmptyMaps() {
        validator.initialize(getAnnotation("mapField"));

        Map<String, String> nonEmptyMap = new HashMap<>();
        nonEmptyMap.put("key1", "value1");
        nonEmptyMap.put("key2", "value2");
        assertTrue(validator.isValid(nonEmptyMap, null));

        Map<String, Integer> nonEmptyIntegerMap = new HashMap<>();
        nonEmptyIntegerMap.put("key1", 1);
        nonEmptyIntegerMap.put("key2", 2);
        assertTrue(validator.isValid(nonEmptyIntegerMap, null));

        Map<String, String> singleEntry = new HashMap<>();
        singleEntry.put("key", "value");
        assertTrue(validator.isValid(singleEntry, null));
    }

    @Test
    void testEmptyMaps() {
        validator.initialize(getAnnotation("mapField"));

        Map<String, String> emptyMap = new HashMap<>();
        assertFalse(validator.isValid(emptyMap, null));

        Map<String, Integer> emptyIntegerMap = new HashMap<>();
        assertFalse(validator.isValid(emptyIntegerMap, null));

        Map<String, Object> emptyObjectMap = new HashMap<>();
        assertFalse(validator.isValid(emptyObjectMap, null));
    }

    @Test
    void testNullMap() {
        validator.initialize(getAnnotation("mapField"));

        assertFalse(validator.isValid(null, null));
    }

    @Test
    void testDifferentMapTypes() {
        validator.initialize(getAnnotation("mapField"));

        // HashMap
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("key1", "value1");
        hashMap.put("key2", "value2");
        assertTrue(validator.isValid(hashMap, null));

        // LinkedHashMap
        Map<String, String> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("key1", "value1");
        linkedHashMap.put("key2", "value2");
        assertTrue(validator.isValid(linkedHashMap, null));

        // TreeMap
        Map<String, String> treeMap = new TreeMap<>();
        treeMap.put("key1", "value1");
        treeMap.put("key2", "value2");
        assertTrue(validator.isValid(treeMap, null));

        // Hashtable
        Map<String, String> hashtable = new Hashtable<>();
        hashtable.put("key1", "value1");
        hashtable.put("key2", "value2");
        assertTrue(validator.isValid(hashtable, null));
    }

    @Test
    void testEmptyDifferentMapTypes() {
        validator.initialize(getAnnotation("mapField"));

        // Empty HashMap
        Map<String, String> emptyHashMap = new HashMap<>();
        assertFalse(validator.isValid(emptyHashMap, null));

        // Empty LinkedHashMap
        Map<String, String> emptyLinkedHashMap = new LinkedHashMap<>();
        assertFalse(validator.isValid(emptyLinkedHashMap, null));

        // Empty TreeMap
        Map<String, String> emptyTreeMap = new TreeMap<>();
        assertFalse(validator.isValid(emptyTreeMap, null));

        // Empty Hashtable
        Map<String, String> emptyHashtable = new Hashtable<>();
        assertFalse(validator.isValid(emptyHashtable, null));
    }

    @Test
    void testSingleEntryMaps() {
        validator.initialize(getAnnotation("mapField"));

        Map<String, String> singleEntry = new HashMap<>();
        singleEntry.put("key", "value");
        assertTrue(validator.isValid(singleEntry, null));

        Map<String, Integer> singleIntegerEntry = new HashMap<>();
        singleIntegerEntry.put("number", 42);
        assertTrue(validator.isValid(singleIntegerEntry, null));
    }

    @Test
    void testMapsWithNullValues() {
        validator.initialize(getAnnotation("mapField"));

        Map<String, String> withNullValues = new HashMap<>();
        withNullValues.put("key1", "value1");
        withNullValues.put("key2", null);
        assertTrue(validator.isValid(withNullValues, null));

        Map<String, String> allNullValues = new HashMap<>();
        allNullValues.put("key1", null);
        allNullValues.put("key2", null);
        assertTrue(validator.isValid(allNullValues, null));
    }

    @Test
    void testMapsWithNullKeys() {
        validator.initialize(getAnnotation("mapField"));

        Map<String, String> withNullKeys = new HashMap<>();
        withNullKeys.put("key1", "value1");
        withNullKeys.put(null, "value2");
        assertTrue(validator.isValid(withNullKeys, null));

        Map<String, String> allNullKeys = new HashMap<>();
        allNullKeys.put(null, "value1");
        allNullKeys.put(null, "value2");
        assertTrue(validator.isValid(allNullKeys, null));
    }

    @Test
    void testComplexObjects() {
        validator.initialize(getAnnotation("mapField"));

        Map<String, Person> personMap = new HashMap<>();
        personMap.put("john", new Person("John", 25));
        personMap.put("jane", new Person("Jane", 30));
        assertTrue(validator.isValid(personMap, null));

        Map<Person, String> reversePersonMap = new HashMap<>();
        reversePersonMap.put(new Person("John", 25), "john");
        reversePersonMap.put(new Person("Jane", 30), "jane");
        assertTrue(validator.isValid(reversePersonMap, null));
    }

    @Test
    void testEdgeCases() {
        validator.initialize(getAnnotation("mapField"));

        // Map with empty string keys and values
        Map<String, String> emptyStrings = new HashMap<>();
        emptyStrings.put("", "");
        assertTrue(validator.isValid(emptyStrings, null));

        // Map with only empty string values
        Map<String, String> emptyValues = new HashMap<>();
        emptyValues.put("key1", "");
        emptyValues.put("key2", "");
        assertTrue(validator.isValid(emptyValues, null));
    }

    private record Person(String name, int age) {
    }

    private static class NotEmptyMapDummy {
        @NotEmptyCollection
        Map<String, String> mapField;
    }
}
