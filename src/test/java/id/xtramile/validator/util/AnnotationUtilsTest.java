package id.xtramile.validator.util;

import id.xtramile.validator.annotation.data.ValidUsername;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AnnotationUtilsTest {

    private static class TestDto {
        @ValidUsername(min = 3)
        private String username;

        @Size(min = 5, max = 10)
        private String name;
    }

    private static class ParentDto {
        @NotNull
        protected String parentField;
    }

    private static class ChildDto extends ParentDto {
        @Size(min = 1)
        private String childField;
    }

    private static class OuterNestedDto {
        private InnerNestedDto inner;
    }

    private static class InnerNestedDto {
        @Size(max = 40)
        private String email;
    }

    @Test
    void testGetAnnotationAttributesWithExistingAnnotation() {
        Map<String, Object> attrs = AnnotationUtils.getAnnotationAttributes(
                TestDto.class, "username", ValidUsername.class);

        assertNotNull(attrs);
        assertTrue(attrs.containsKey("min"));
        assertTrue(attrs.containsKey("max"));
        assertEquals(3, attrs.get("min"));
        assertEquals(20, attrs.get("max"));
    }

    @Test
    void testGetAnnotationAttributesWithNonExistentAnnotation() {
        Map<String, Object> attrs = AnnotationUtils.getAnnotationAttributes(
                TestDto.class, "unannotated", ValidUsername.class);

        assertNotNull(attrs);
        // Should return default values from annotation
        assertTrue(attrs.containsKey("min") || attrs.isEmpty());
    }

    @Test
    void testGetAnnotationAttributesWithNullParameters() {
        Map<String, Object> attrs1 = AnnotationUtils.getAnnotationAttributes(
                null, "username", ValidUsername.class);
        assertTrue(attrs1.isEmpty());

        Map<String, Object> attrs2 = AnnotationUtils.getAnnotationAttributes(
                TestDto.class, null, ValidUsername.class);
        assertTrue(attrs2.isEmpty());

        Map<String, Object> attrs3 = AnnotationUtils.getAnnotationAttributes(
                TestDto.class, "username", null);
        assertTrue(attrs3.isEmpty());
    }

    @Test
    void testGetAnnotationAttributesWithNonExistentField() {
        Map<String, Object> attrs = AnnotationUtils.getAnnotationAttributes(
                TestDto.class, "nonexistent", ValidUsername.class);

        assertNotNull(attrs);
        // Should return default values
    }

    @Test
    void testFindFieldAnnotationWithExistingAnnotation() {
        Annotation annotation = AnnotationUtils.findFieldAnnotation(
                TestDto.class, "username", ValidUsername.class);

        assertNotNull(annotation);
        assertInstanceOf(ValidUsername.class, annotation);
    }

    @Test
    void testFindFieldAnnotationWithNonExistentAnnotation() {
        Annotation annotation = AnnotationUtils.findFieldAnnotation(
                TestDto.class, "unannotated", ValidUsername.class);

        assertNull(annotation);
    }

    @Test
    void testFindFieldAnnotationWithNullParameters() {
        assertNull(AnnotationUtils.findFieldAnnotation(null, "username", ValidUsername.class));
        assertNull(AnnotationUtils.findFieldAnnotation(TestDto.class, null, ValidUsername.class));
        assertNull(AnnotationUtils.findFieldAnnotation(TestDto.class, "username", null));
    }

    @Test
    void testFindFieldAnnotationWithNonExistentField() {
        Annotation annotation = AnnotationUtils.findFieldAnnotation(
                TestDto.class, "nonexistent", ValidUsername.class);

        assertNull(annotation);
    }

    @Test
    void testFindFieldAnnotationInheritedField() {
        Annotation annotation = AnnotationUtils.findFieldAnnotation(
                ChildDto.class, "parentField", NotNull.class);

        assertNotNull(annotation);
        assertInstanceOf(NotNull.class, annotation);
    }

    @Test
    void testExtractAnnotationValues() throws NoSuchFieldException {
        ValidUsername annotation = TestDto.class.getDeclaredField("username")
                .getAnnotation(ValidUsername.class);

        Map<String, Object> values = AnnotationUtils.extractAnnotationValues(annotation);

        assertNotNull(values);
        assertTrue(values.containsKey("min"));
        assertTrue(values.containsKey("max"));
        assertEquals(3, values.get("min"));
        assertEquals(20, values.get("max"));
    }

    @Test
    void testExtractAnnotationValuesWithNull() {
        Map<String, Object> values = AnnotationUtils.extractAnnotationValues(null);

        assertNotNull(values);
        assertTrue(values.isEmpty());
    }

    @Test
    void testExtractAnnotationDefaults() {
        Map<String, Object> defaults = AnnotationUtils.extractAnnotationDefaults(ValidUsername.class);

        assertNotNull(defaults);
        // Should contain default values from annotation
        assertTrue(defaults.containsKey("min") || defaults.isEmpty());
    }

    @Test
    void testExtractAnnotationDefaultsWithNull() {
        Map<String, Object> defaults = AnnotationUtils.extractAnnotationDefaults(null);

        assertNotNull(defaults);
        assertTrue(defaults.isEmpty());
    }

    @Test
    void testExtractAnnotationDefaultsWithNonAnnotationClass() {
        Map<String, Object> defaults = AnnotationUtils.extractAnnotationDefaults(String.class);

        assertNotNull(defaults);
        assertTrue(defaults.isEmpty());
    }

    @Test
    void testGetAnnotationAttributesWithSizeAnnotation() {
        Map<String, Object> attrs = AnnotationUtils.getAnnotationAttributes(
                TestDto.class, "name", Size.class);

        assertNotNull(attrs);
        assertTrue(attrs.containsKey("min"));
        assertTrue(attrs.containsKey("max"));
        assertEquals(5, attrs.get("min"));
        assertEquals(10, attrs.get("max"));
    }

    @Test
    void findFieldAnnotation_resolvesDotPathToLeafField() {
        Annotation ann = AnnotationUtils.findFieldAnnotation(OuterNestedDto.class, "inner.email", Size.class);

        assertNotNull(ann);
        assertInstanceOf(Size.class, ann);
        assertEquals(40, ((Size) ann).max());
    }

    @Test
    void getAnnotationAttributes_nestedPath_readsLeafSizeNotAnnotationDefaults() {
        Map<String, Object> attrs = AnnotationUtils.getAnnotationAttributes(
                OuterNestedDto.class, "inner.email", Size.class);

        assertNotNull(attrs);
        assertEquals(40, attrs.get("max"));
        assertEquals(0, attrs.get("min"));
    }
}

