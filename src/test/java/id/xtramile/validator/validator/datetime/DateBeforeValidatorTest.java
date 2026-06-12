package id.xtramile.validator.validator.datetime;

import id.xtramile.validator.annotation.datetime.DateBefore;
import id.xtramile.validator.enums.DatePrecision;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DateBeforeValidatorTest {

    @DateBefore(first = "startDate", second = "endDate")
    private static class DateBeforeDummy {
        private final String startDate;
        private final String endDate;

        private DateBeforeDummy(String startDate, String endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
        }

        public String startDate() {
            return startDate;
        }

        public String endDate() {
            return endDate;
        }
    }

    @DateBefore(first = "startDate", second = "endDate",
            maxDistance = 30, precision = DatePrecision.DAYS)
    private static class DateBeforeWithDistanceDummy {
        private final String startDate;
        private final String endDate;

        private DateBeforeWithDistanceDummy(String startDate, String endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
        }

        public String startDate() {
            return startDate;
        }

        public String endDate() {
            return endDate;
        }
    }

    @DateBefore(first = "startDate", second = "endDate",
            maxDistance = 24, precision = DatePrecision.HOURS)
    private static class DateBeforeWithHoursDummy {
        private final String startDate;
        private final String endDate;

        private DateBeforeWithHoursDummy(String startDate, String endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
        }

        public String startDate() {
            return startDate;
        }

        public String endDate() {
            return endDate;
        }
    }

    @DateBefore(first = "startDate", second = "endDate",
            maxDistance = 60, precision = DatePrecision.MINUTES)
    private static class DateBeforeWithMinutesDummy {
        private final String startDate;
        private final String endDate;

        private DateBeforeWithMinutesDummy(String startDate, String endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
        }

        public String startDate() {
            return startDate;
        }

        public String endDate() {
            return endDate;
        }
    }

    @DateBefore(first = "startDate", second = "endDate",
            maxDistance = 3600, precision = DatePrecision.SECONDS)
    private static class DateBeforeWithSecondsDummy {
        private final String startDate;
        private final String endDate;

        private DateBeforeWithSecondsDummy(String startDate, String endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
        }

        public String startDate() {
            return startDate;
        }

        public String endDate() {
            return endDate;
        }
    }

    @DateBefore(first = "startDate", second = "endDate",
                pattern = "dd/MM/yyyy HH:mm:ss")
    private static class DateBeforeCustomPatternDummy {
        private final String startDate;
        private final String endDate;

        private DateBeforeCustomPatternDummy(String startDate, String endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
        }

        public String startDate() {
            return startDate;
        }

        public String endDate() {
            return endDate;
        }
    }

    private DateBeforeValidator validator;

    @BeforeEach
    void setUp() {
        validator = new DateBeforeValidator();
        validator.initialize(DateBeforeDummy.class.getAnnotation(DateBefore.class));
    }

    @Test
    void testValidDateBefore() {
        assertTrue(validator.isValid(new DateBeforeDummy("2023-01-01 10:00:00", "2023-01-01 11:00:00"), null));
        assertTrue(validator.isValid(new DateBeforeDummy("2023-01-01 00:00:00", "2023-01-02 00:00:00"), null));
        assertTrue(validator.isValid(new DateBeforeDummy("2023-01-01 10:00:00", "2023-12-31 23:59:59"), null));
        assertTrue(validator.isValid(new DateBeforeDummy("2023-06-15 12:30:00", "2023-06-15 12:30:01"), null)); // 1 second difference
    }

    @Test
    void testInvalidDateNotBefore() {
        assertFalse(validator.isValid(new DateBeforeDummy("2023-01-01 11:00:00", "2023-01-01 10:00:00"), null));
        assertFalse(validator.isValid(new DateBeforeDummy("2023-01-02 00:00:00", "2023-01-01 00:00:00"), null));
        assertFalse(validator.isValid(new DateBeforeDummy("2023-12-31 23:59:59", "2023-01-01 10:00:00"), null));
        assertFalse(validator.isValid(new DateBeforeDummy("2023-01-01 10:00:00", "2023-01-01 10:00:00"), null)); // Equal dates
    }

    @Test
    void testNullAndBlankValues() {
        assertTrue(validator.isValid(new DateBeforeDummy(null, null), null));
        assertTrue(validator.isValid(new DateBeforeDummy("", ""), null));
        assertTrue(validator.isValid(new DateBeforeDummy("   ", "   "), null));
        assertTrue(validator.isValid(new DateBeforeDummy(null, "2023-01-01 10:00:00"), null));
        assertTrue(validator.isValid(new DateBeforeDummy("2023-01-01 10:00:00", null), null));
        assertTrue(validator.isValid(null, null)); // Null bean
    }

    @Test
    void testInvalidDateFormats() {
        assertFalse(validator.isValid(new DateBeforeDummy("invalid-date", "2023-01-01 11:00:00"), null));
        assertFalse(validator.isValid(new DateBeforeDummy("2023-01-01 10:00:00", "invalid-date"), null));
        assertFalse(validator.isValid(new DateBeforeDummy("2023-13-01 10:00:00", "2023-01-01 11:00:00"), null)); // Invalid month
        assertFalse(validator.isValid(new DateBeforeDummy("2023-01-01 10:00:00", "2023-01-32 11:00:00"), null)); // Invalid day
        assertFalse(validator.isValid(new DateBeforeDummy("2023-01-01 24:00:00", "2023-01-01 11:00:00"), null)); // Invalid hour
        assertFalse(validator.isValid(new DateBeforeDummy("2023-01-01 10:60:00", "2023-01-01 11:00:00"), null)); // Invalid minute
        assertFalse(validator.isValid(new DateBeforeDummy("2023-01-01 10:00:60", "2023-01-01 11:00:00"), null)); // Invalid second
    }

    @Test
    void testMaxDistanceWithDays() {
        validator.initialize(DateBeforeWithDistanceDummy.class.getAnnotation(DateBefore.class));

        // Valid: within 30 days
        assertTrue(validator.isValid(new DateBeforeWithDistanceDummy("2023-01-01 10:00:00", "2023-01-31 10:00:00"), null)); // Exactly 30 days
        assertTrue(validator.isValid(new DateBeforeWithDistanceDummy("2023-01-01 10:00:00", "2023-01-15 10:00:00"), null)); // 14 days

        // Invalid: exceeds 30 days
        assertFalse(validator.isValid(new DateBeforeWithDistanceDummy("2023-01-01 10:00:00", "2023-02-01 10:00:00"), null)); // 31 days
        assertFalse(validator.isValid(new DateBeforeWithDistanceDummy("2023-01-01 10:00:00", "2023-02-05 10:00:00"), null)); // 35 days
    }

    @Test
    void testMaxDistanceWithHours() {
        validator.initialize(DateBeforeWithHoursDummy.class.getAnnotation(DateBefore.class));

        // Valid: within 24 hours
        assertTrue(validator.isValid(new DateBeforeWithHoursDummy("2023-01-01 10:00:00", "2023-01-02 10:00:00"), null)); // Exactly 24 hours
        assertTrue(validator.isValid(new DateBeforeWithHoursDummy("2023-01-01 10:00:00", "2023-01-01 12:00:00"), null)); // 2 hours

        // Invalid: exceeds 24 hours
        assertFalse(validator.isValid(new DateBeforeWithHoursDummy("2023-01-01 10:00:00", "2023-01-02 11:00:00"), null)); // 25 hours
        assertFalse(validator.isValid(new DateBeforeWithHoursDummy("2023-01-01 10:00:00", "2023-01-03 10:00:00"), null)); // 48 hours
    }

    @Test
    void testMaxDistanceWithMinutes() {
        validator.initialize(DateBeforeWithMinutesDummy.class.getAnnotation(DateBefore.class));

        // Valid: within 60 minutes
        assertTrue(validator.isValid(new DateBeforeWithMinutesDummy("2023-01-01 10:00:00", "2023-01-01 11:00:00"), null)); // Exactly 60 minutes
        assertTrue(validator.isValid(new DateBeforeWithMinutesDummy("2023-01-01 10:00:00", "2023-01-01 10:30:00"), null)); // 30 minutes

        // Invalid: exceeds 60 minutes
        assertFalse(validator.isValid(new DateBeforeWithMinutesDummy("2023-01-01 10:00:00", "2023-01-01 11:01:00"), null)); // 61 minutes
        assertFalse(validator.isValid(new DateBeforeWithMinutesDummy("2023-01-01 10:00:00", "2023-01-01 12:00:00"), null)); // 120 minutes
    }

    @Test
    void testMaxDistanceWithSeconds() {
        validator.initialize(DateBeforeWithSecondsDummy.class.getAnnotation(DateBefore.class));

        // Valid: within 3600 seconds (1 hour)
        assertTrue(validator.isValid(new DateBeforeWithSecondsDummy("2023-01-01 10:00:00", "2023-01-01 11:00:00"), null)); // Exactly 3600 seconds
        assertTrue(validator.isValid(new DateBeforeWithSecondsDummy("2023-01-01 10:00:00", "2023-01-01 10:30:00"), null)); // 1800 seconds

        // Invalid: exceeds 3600 seconds
        assertFalse(validator.isValid(new DateBeforeWithSecondsDummy("2023-01-01 10:00:00", "2023-01-01 11:00:01"), null)); // 3601 seconds
        assertFalse(validator.isValid(new DateBeforeWithSecondsDummy("2023-01-01 10:00:00", "2023-01-01 11:30:00"), null)); // 5400 seconds
    }

    @Test
    void testCustomPattern() {
        validator.initialize(DateBeforeCustomPatternDummy.class.getAnnotation(DateBefore.class));

        assertTrue(validator.isValid(new DateBeforeCustomPatternDummy("01/01/2023 10:00:00", "01/01/2023 11:00:00"), null));
        assertTrue(validator.isValid(new DateBeforeCustomPatternDummy("15/06/2023 12:30:00", "16/06/2023 12:30:00"), null));

        assertFalse(validator.isValid(new DateBeforeCustomPatternDummy("01/01/2023 11:00:00", "01/01/2023 10:00:00"), null));
        assertFalse(validator.isValid(new DateBeforeCustomPatternDummy("2023-01-01 10:00:00", "2023-01-01 11:00:00"), null)); // Wrong format
    }

    @Test
    void testLeapYear() {
        assertTrue(validator.isValid(new DateBeforeDummy("2024-02-28 10:00:00", "2024-02-29 10:00:00"), null)); // Leap year
        assertTrue(validator.isValid(new DateBeforeDummy("2024-02-29 10:00:00", "2024-03-01 10:00:00"), null)); // Leap year day

        assertFalse(validator.isValid(new DateBeforeDummy("2023-02-28 10:00:00", "2023-02-29 10:00:00"), null)); // Not leap year
    }

    @Test
    void testEdgeCases() {
        // Same date, different times
        assertTrue(validator.isValid(new DateBeforeDummy("2023-01-01 10:00:00", "2023-01-01 10:00:01"), null)); // 1 second difference
        assertTrue(validator.isValid(new DateBeforeDummy("2023-01-01 10:00:00", "2023-01-01 10:01:00"), null)); // 1 minute difference

        // Different dates
        assertTrue(validator.isValid(new DateBeforeDummy("2023-01-01 23:59:59", "2023-01-02 00:00:00"), null)); // End of day to start of next
        assertTrue(validator.isValid(new DateBeforeDummy("2023-12-31 23:59:59", "2024-01-01 00:00:00"), null)); // Year boundary
    }

    @Test
    void testDistanceAtBoundary() {
        validator.initialize(DateBeforeWithDistanceDummy.class.getAnnotation(DateBefore.class));

        // Exactly at the boundary (30 days)
        assertTrue(validator.isValid(new DateBeforeWithDistanceDummy("2023-01-01 10:00:00", "2023-01-31 10:00:00"), null));

        // With DAYS precision, 30 days + 1 second still counts as 30 days (truncated)
        // So this is still valid
        assertTrue(validator.isValid(new DateBeforeWithDistanceDummy("2023-01-01 10:00:00", "2023-01-31 10:00:01"), null)); // 30 days + 1 second

        // Actually over the boundary (31 days)
        assertFalse(validator.isValid(new DateBeforeWithDistanceDummy("2023-01-01 10:00:00", "2023-02-01 10:00:00"), null)); // 31 days
    }

    @Test
    void testNonStringFields() {
        // Non-string fields should be considered valid (validator returns true for non-strings)
        @DateBefore(first = "field1", second = "field2")
        class NonStringDummy {
            private final Integer field1;
            private final Integer field2;

            public NonStringDummy(Integer field1, Integer field2) {
                this.field1 = field1;
                this.field2 = field2;
            }

            public Integer field1() {
                return field1;
            }

            public Integer field2() {
                return field2;
            }
        }

        validator.initialize(NonStringDummy.class.getAnnotation(DateBefore.class));
        assertTrue(validator.isValid(new NonStringDummy(1, 2), null));
        assertTrue(validator.isValid(new NonStringDummy(null, null), null));
    }
}
