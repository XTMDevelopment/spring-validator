package id.xtramile.validator.validator.cross;

import id.xtramile.validator.annotation.cross.RequiredWith;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Focused edge-case coverage for {@link RequiredWithValidator} beyond {@link RequiredWithValidatorTest}.
 */
class RequiredWithValidatorCoverageTest {

    @RequiredWith(when = "trigger", require = {"email"})
    private static class SingleCompanionDummy {
        private final String trigger;
        private final String email;

        private SingleCompanionDummy(String trigger, String email) {
            this.trigger = trigger;
            this.email = email;
        }

        public String trigger() {
            return trigger;
        }

        public String email() {
            return email;
        }
    }

    @RequiredWith(when = "status", require = {"code", "label", "detail"})
    private static class MultiFieldDummy {
        private final String status;
        private final String code;
        private final String label;
        private final String detail;

        private MultiFieldDummy(String status, String code, String label, String detail) {
            this.status = status;
            this.code = code;
            this.label = label;
            this.detail = detail;
        }

        public String status() {
            return status;
        }

        public String code() {
            return code;
        }

        public String label() {
            return label;
        }

        public String detail() {
            return detail;
        }
    }

    @RequiredWith(when = "active", require = {})
    private static class EmptyRequireDummy {
        private final Boolean active;

        private EmptyRequireDummy(Boolean active) {
            this.active = active;
        }

        public Boolean active() {
            return active;
        }
    }

    private RequiredWithValidator validator;

    @BeforeEach
    void setUp() {
        validator = new RequiredWithValidator();
    }

    @Test
    void emptyStringTrigger_doesNotRequireCompanion() {
        validator.initialize(SingleCompanionDummy.class.getAnnotation(RequiredWith.class));

        assertTrue(validator.isValid(new SingleCompanionDummy(null, null), null));
        assertTrue(validator.isValid(new SingleCompanionDummy("", null), null));
        assertTrue(validator.isValid(new SingleCompanionDummy("   ", null), null));
    }

    @Test
    void nullCompanion_invalidWhenTriggerActive() {
        validator.initialize(SingleCompanionDummy.class.getAnnotation(RequiredWith.class));

        assertFalse(validator.isValid(new SingleCompanionDummy("yes", null), null));
        assertFalse(validator.isValid(new SingleCompanionDummy("yes", ""), null));
        assertFalse(validator.isValid(new SingleCompanionDummy("yes", "   "), null));
        assertTrue(validator.isValid(new SingleCompanionDummy("yes", "user@example.com"), null));
    }

    @Test
    void multipleRequiredFields_partialPresenceIsInvalid() {
        validator.initialize(MultiFieldDummy.class.getAnnotation(RequiredWith.class));

        assertTrue(validator.isValid(new MultiFieldDummy(null, null, null, null), null));
        assertTrue(validator.isValid(new MultiFieldDummy("", null, null, null), null));
        assertTrue(validator.isValid(new MultiFieldDummy("   ", null, null, null), null));

        assertFalse(validator.isValid(new MultiFieldDummy("open", "A1", null, "detail"), null));
        assertFalse(validator.isValid(new MultiFieldDummy("open", null, "Label", null), null));
        assertFalse(validator.isValid(new MultiFieldDummy("open", "", "Label", "detail"), null));

        assertTrue(validator.isValid(new MultiFieldDummy("open", "A1", "Label", "detail"), null));
    }

    @Test
    void emptyRequireArray_passesWhenTriggerActive() {
        validator.initialize(EmptyRequireDummy.class.getAnnotation(RequiredWith.class));

        assertTrue(validator.isValid(new EmptyRequireDummy(true), null));
        assertTrue(validator.isValid(new EmptyRequireDummy(false), null));
        assertTrue(validator.isValid(new EmptyRequireDummy(null), null));
    }
}
