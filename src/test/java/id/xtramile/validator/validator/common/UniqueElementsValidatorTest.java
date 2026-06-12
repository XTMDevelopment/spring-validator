package id.xtramile.validator.validator.common;

import id.xtramile.validator.annotation.common.UniqueElements;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UniqueElementsValidatorTest {

    private record Person(String name, int age) {

        @Override
            public boolean equals(Object obj) {
                if (this == obj) {
                    return true;
                }
                if (!(obj instanceof Person other)) {
                    return false;
                }
            return age == other.age && Objects.equals(name, other.name);
            }

    }

    private static class UniqueElementsDummy {
        @UniqueElements
        Collection<String> collectionField;
    }

    private UniqueElementsValidator validator;

    private static UniqueElements getAnnotation(String fieldName) {
        try {
            Field f = UniqueElementsDummy.class.getDeclaredField(fieldName);
            return f.getAnnotation(UniqueElements.class);
        } catch (NoSuchFieldException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
        validator = new UniqueElementsValidator();
    }

    @Test
    void testUniqueElements() {
        validator.initialize(getAnnotation("collectionField"));

        List<String> uniqueList = Arrays.asList("apple", "banana", "cherry");
        assertTrue(validator.isValid(uniqueList, null));

        Set<String> uniqueSet = new HashSet<>(Arrays.asList("apple", "banana", "cherry"));
        assertTrue(validator.isValid(uniqueSet, null));

        List<Integer> uniqueNumbers = Arrays.asList(1, 2, 3, 4, 5);
        assertTrue(validator.isValid(uniqueNumbers, null));
    }

    @Test
    void testDuplicateElements() {
        validator.initialize(getAnnotation("collectionField"));

        List<String> duplicateList = Arrays.asList("apple", "banana", "apple");
        assertFalse(validator.isValid(duplicateList, null));

        List<String> multipleDuplicates = Arrays.asList("apple", "banana", "apple", "cherry", "banana");
        assertFalse(validator.isValid(multipleDuplicates, null));

        List<Integer> duplicateNumbers = Arrays.asList(1, 2, 3, 2, 4);
        assertFalse(validator.isValid(duplicateNumbers, null));
    }

    @Test
    void testEmptyCollections() {
        validator.initialize(getAnnotation("collectionField"));

        List<String> emptyList = new ArrayList<>();
        assertTrue(validator.isValid(emptyList, null));

        Set<String> emptySet = new HashSet<>();
        assertTrue(validator.isValid(emptySet, null));
    }

    @Test
    void testNullCollection() {
        validator.initialize(getAnnotation("collectionField"));

        assertTrue(validator.isValid(null, null));
    }

    @Test
    void testSingleElement() {
        validator.initialize(getAnnotation("collectionField"));

        List<String> singleElement = List.of("apple");
        assertTrue(validator.isValid(singleElement, null));

        Set<String> singleSet = new HashSet<>(List.of("apple"));
        assertTrue(validator.isValid(singleSet, null));
    }

    @Test
    void testAllSameElements() {
        validator.initialize(getAnnotation("collectionField"));

        List<String> allSame = Arrays.asList("apple", "apple", "apple");
        assertFalse(validator.isValid(allSame, null));

        List<Integer> allSameNumbers = Arrays.asList(1, 1, 1, 1);
        assertFalse(validator.isValid(allSameNumbers, null));
    }

    @Test
    void testDifferentCollectionTypes() {
        validator.initialize(getAnnotation("collectionField"));

        // ArrayList
        List<String> arrayList = Arrays.asList("apple", "banana", "cherry");
        assertTrue(validator.isValid(arrayList, null));

        // LinkedList
        List<String> linkedList = new LinkedList<>(Arrays.asList("apple", "banana", "cherry"));
        assertTrue(validator.isValid(linkedList, null));

        // HashSet (should always be unique)
        Set<String> hashSet = new HashSet<>(Arrays.asList("apple", "banana", "cherry"));
        assertTrue(validator.isValid(hashSet, null));

        // TreeSet (should always be unique)
        Set<String> treeSet = new TreeSet<>(Arrays.asList("apple", "banana", "cherry"));
        assertTrue(validator.isValid(treeSet, null));
    }

    @Test
    void testComplexObjects() {
        validator.initialize(getAnnotation("collectionField"));

        // Test with custom objects that have equals/hashCode
        List<Person> uniquePersons = Arrays.asList(
                new Person("John", 25),
                new Person("Jane", 30),
                new Person("Bob", 35)
        );
        assertTrue(validator.isValid(uniquePersons, null));

        List<Person> duplicatePersons = Arrays.asList(
                new Person("John", 25),
                new Person("Jane", 30),
                new Person("John", 25) // duplicate
        );
        assertFalse(validator.isValid(duplicatePersons, null));
    }

    @Test
    void testEdgeCases() {
        validator.initialize(getAnnotation("collectionField"));

        // Test with null elements (should be considered duplicates)
        List<String> withNulls = Arrays.asList("apple", null, "banana", null);
        assertFalse(validator.isValid(withNulls, null));

        // Test with empty strings
        List<String> withEmptyStrings = Arrays.asList("apple", "", "banana", "");
        assertFalse(validator.isValid(withEmptyStrings, null));
    }
}
