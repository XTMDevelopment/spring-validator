package id.xtramile.validator.validator.common;

import id.xtramile.validator.annotation.common.NotEmptyCollection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NotEmptyCollectionValidatorTest {

    private static class Person {
        private final String name;
        private final int age;

        private Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String name() {
            return name;
        }

        public int age() {
            return age;
        }
    }

    private static class NotEmptyCollectionDummy {
        @NotEmptyCollection
        Collection<String> collectionField;
    }

    private NotEmptyCollectionValidator validator;

    private static NotEmptyCollection getAnnotation(String fieldName) {
        try {
            Field f = NotEmptyCollectionDummy.class.getDeclaredField(fieldName);
            return f.getAnnotation(NotEmptyCollection.class);
        } catch (NoSuchFieldException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
        validator = new NotEmptyCollectionValidator();
    }

    @Test
    void testNonEmptyCollections() {
        validator.initialize(getAnnotation("collectionField"));

        List<String> nonEmptyList = Arrays.asList("apple", "banana", "cherry");
        assertTrue(validator.isValid(nonEmptyList, null));

        Set<String> nonEmptySet = new HashSet<>(Arrays.asList("apple", "banana"));
        assertTrue(validator.isValid(nonEmptySet, null));

        List<Integer> nonEmptyNumbers = Arrays.asList(1, 2, 3);
        assertTrue(validator.isValid(nonEmptyNumbers, null));

        List<String> singleElement = List.of("single");
        assertTrue(validator.isValid(singleElement, null));
    }

    @Test
    void testEmptyCollections() {
        validator.initialize(getAnnotation("collectionField"));

        List<String> emptyList = new ArrayList<>();
        assertFalse(validator.isValid(emptyList, null));

        Set<String> emptySet = new HashSet<>();
        assertFalse(validator.isValid(emptySet, null));

        List<Integer> emptyNumbers = new ArrayList<>();
        assertFalse(validator.isValid(emptyNumbers, null));
    }

    @Test
    void testNullCollection() {
        validator.initialize(getAnnotation("collectionField"));

        assertFalse(validator.isValid(null, null));
    }

    @Test
    void testDifferentCollectionTypes() {
        validator.initialize(getAnnotation("collectionField"));

        // ArrayList
        List<String> arrayList = Arrays.asList("item1", "item2");
        assertTrue(validator.isValid(arrayList, null));

        // LinkedList
        List<String> linkedList = new LinkedList<>(Arrays.asList("item1", "item2"));
        assertTrue(validator.isValid(linkedList, null));

        // HashSet
        Set<String> hashSet = new HashSet<>(Arrays.asList("item1", "item2"));
        assertTrue(validator.isValid(hashSet, null));

        // TreeSet
        Set<String> treeSet = new TreeSet<>(Arrays.asList("item1", "item2"));
        assertTrue(validator.isValid(treeSet, null));

        // Vector
        Vector<String> vector = new Vector<>(Arrays.asList("item1", "item2"));
        assertTrue(validator.isValid(vector, null));
    }

    @Test
    void testEmptyDifferentCollectionTypes() {
        validator.initialize(getAnnotation("collectionField"));

        // Empty ArrayList
        List<String> emptyArrayList = new ArrayList<>();
        assertFalse(validator.isValid(emptyArrayList, null));

        // Empty LinkedList
        List<String> emptyLinkedList = new LinkedList<>();
        assertFalse(validator.isValid(emptyLinkedList, null));

        // Empty HashSet
        Set<String> emptyHashSet = new HashSet<>();
        assertFalse(validator.isValid(emptyHashSet, null));

        // Empty TreeSet
        Set<String> emptyTreeSet = new TreeSet<>();
        assertFalse(validator.isValid(emptyTreeSet, null));

        // Empty Vector
        Vector<String> emptyVector = new Vector<>();
        assertFalse(validator.isValid(emptyVector, null));
    }

    @Test
    void testSingleElementCollections() {
        validator.initialize(getAnnotation("collectionField"));

        List<String> singleList = List.of("single");
        assertTrue(validator.isValid(singleList, null));

        Set<String> singleSet = new HashSet<>(List.of("single"));
        assertTrue(validator.isValid(singleSet, null));

        List<String> singleArrayList = new ArrayList<>();
        singleArrayList.add("single");
        assertTrue(validator.isValid(singleArrayList, null));
    }

    @Test
    void testCollectionsWithNullElements() {
        validator.initialize(getAnnotation("collectionField"));

        List<String> withNulls = Arrays.asList("item1", null, "item2");
        assertTrue(validator.isValid(withNulls, null));

        List<String> onlyNulls = Collections.singletonList(null);
        assertTrue(validator.isValid(onlyNulls, null));

        List<String> mixedNulls = Arrays.asList(null, "item", null);
        assertTrue(validator.isValid(mixedNulls, null));
    }

    @Test
    void testComplexObjects() {
        validator.initialize(getAnnotation("collectionField"));

        List<Person> persons = Arrays.asList(
                new Person("John", 25),
                new Person("Jane", 30)
        );
        assertTrue(validator.isValid(persons, null));

        List<Person> singlePerson = List.of(new Person("John", 25));
        assertTrue(validator.isValid(singlePerson, null));
    }

    @Test
    void testEdgeCases() {
        validator.initialize(getAnnotation("collectionField"));

        // Collection with empty strings
        List<String> withEmptyStrings = Arrays.asList("", "item", "");
        assertTrue(validator.isValid(withEmptyStrings, null));

        // Collection with only empty strings
        List<String> onlyEmptyStrings = Arrays.asList("", "", "");
        assertTrue(validator.isValid(onlyEmptyStrings, null));
    }
}
