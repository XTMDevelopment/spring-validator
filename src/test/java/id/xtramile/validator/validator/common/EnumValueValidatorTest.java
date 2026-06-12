package id.xtramile.validator.validator.common;

import id.xtramile.validator.annotation.common.ValidEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EnumValueValidatorTest {

    // Test enum for validation
    public enum TestStatus {
        ACTIVE, INACTIVE, PENDING, COMPLETED
    }

    public enum TestPriority {
        LOW, MEDIUM, HIGH, URGENT
    }

    private static class EnumDummy {
        @ValidEnum(enumClass = TestStatus.class)
        String statusField;

        @ValidEnum(enumClass = TestStatus.class)
        String statusIgnoreCase;

        @ValidEnum(enumClass = TestStatus.class, ignoreCase = false)
        String statusCaseSensitive;

        @ValidEnum(enumClass = TestPriority.class, ignoreCase = false)
        String priorityField;
    }

    private EnumValueValidator validator;

    private static ValidEnum getAnnotation(String fieldName) {
        try {
            Field f = EnumDummy.class.getDeclaredField(fieldName);
            return f.getAnnotation(ValidEnum.class);
        } catch (NoSuchFieldException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
        validator = new EnumValueValidator();
    }

    @Test
    void testValidEnumValues() {
        validator.initialize(getAnnotation("statusField"));

        assertTrue(validator.isValid("ACTIVE", null));
        assertTrue(validator.isValid("INACTIVE", null));
        assertTrue(validator.isValid("PENDING", null));
        assertTrue(validator.isValid("COMPLETED", null));
    }

    @Test
    void testInvalidEnumValues() {
        validator.initialize(getAnnotation("statusField"));

        assertFalse(validator.isValid("INVALID", null));
        assertFalse(validator.isValid("UNKNOWN", null));
        assertFalse(validator.isValid("ACTIVE_STATUS", null));
    }

    @Test
    void testIgnoreCaseTrue() {
        validator.initialize(getAnnotation("statusIgnoreCase"));

        assertTrue(validator.isValid("ACTIVE", null));
        assertTrue(validator.isValid("active", null));
        assertTrue(validator.isValid("Active", null));
        assertTrue(validator.isValid("AcTiVe", null));
        assertTrue(validator.isValid("INACTIVE", null));
        assertTrue(validator.isValid("inactive", null));
        assertTrue(validator.isValid("Inactive", null));
    }

    @Test
    void testIgnoreCaseFalse() {
        validator.initialize(getAnnotation("statusCaseSensitive"));

        assertTrue(validator.isValid("ACTIVE", null));
        assertTrue(validator.isValid("INACTIVE", null));
        assertTrue(validator.isValid("PENDING", null));
        assertTrue(validator.isValid("COMPLETED", null));

        assertFalse(validator.isValid("active", null));
        assertFalse(validator.isValid("Active", null));
        assertFalse(validator.isValid("inactive", null));
        assertFalse(validator.isValid("Inactive", null));
    }

    @Test
    void testBlankValues() {
        validator.initialize(getAnnotation("statusField"));

        assertTrue(validator.isValid("", null));
        assertTrue(validator.isValid("   ", null));
        assertTrue(validator.isValid(null, null));
    }

    @Test
    void testDifferentEnumClass() {
        validator.initialize(getAnnotation("priorityField"));

        assertTrue(validator.isValid("LOW", null));
        assertTrue(validator.isValid("MEDIUM", null));
        assertTrue(validator.isValid("HIGH", null));
        assertTrue(validator.isValid("URGENT", null));

        assertFalse(validator.isValid("ACTIVE", null)); // from different enum
        assertFalse(validator.isValid("INACTIVE", null)); // from different enum
    }

    @Test
    void testAllEnumValues() {
        validator.initialize(getAnnotation("statusField"));

        // Test all values from TestStatus enum
        for (TestStatus status : TestStatus.values()) {
            assertTrue(validator.isValid(status.name(), null));
        }
    }

    @Test
    void testCaseVariations() {
        validator.initialize(getAnnotation("statusIgnoreCase"));

        String[] variations = {"active", "Active", "ACTIVE", "AcTiVe", "aCtIvE"};
        for (String variation : variations) {
            assertTrue(validator.isValid(variation, null));
        }
    }

    @Test
    void testInvalidCaseSensitive() {
        validator.initialize(getAnnotation("statusCaseSensitive"));

        String[] invalidVariations = {"active", "Active", "AcTiVe", "aCtIvE"};
        for (String variation : invalidVariations) {
            assertFalse(validator.isValid(variation, null));
        }
    }

    @Test
    void testEdgeCases() {
        validator.initialize(getAnnotation("statusField"));

        assertFalse(validator.isValid("ACTIVE ", null)); // trailing space
        assertFalse(validator.isValid(" ACTIVE", null)); // leading space
        assertFalse(validator.isValid("ACTIVE_STATUS", null)); // partial match
        assertFalse(validator.isValid("STATUS_ACTIVE", null)); // partial match
    }

    @Test
    void testPriorityEnum() {
        validator.initialize(getAnnotation("priorityField"));

        assertTrue(validator.isValid("LOW", null));
        assertTrue(validator.isValid("MEDIUM", null));
        assertTrue(validator.isValid("HIGH", null));
        assertTrue(validator.isValid("URGENT", null));

        assertFalse(validator.isValid("low", null)); // case sensitive by default
        assertFalse(validator.isValid("INVALID_PRIORITY", null));
    }
}
