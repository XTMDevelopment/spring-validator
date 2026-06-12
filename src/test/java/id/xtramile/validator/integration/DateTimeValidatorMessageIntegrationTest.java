package id.xtramile.validator.integration;

import id.xtramile.validator.annotation.datetime.*;
import id.xtramile.validator.enums.DatePrecision;
import id.xtramile.validator.web.FriendlyMessageResolver;
import id.xtramile.validator.web.MessageResourceResolver;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DateTimeValidatorMessageIntegrationTest {

    private static final Validator VALIDATOR;
    private static final FriendlyMessageResolver RESOLVER;

    static {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        VALIDATOR = factory.getValidator();

        MessageResourceResolver messageResourceResolver = new MessageResourceResolver("en");
        RESOLVER = new FriendlyMessageResolver(messageResourceResolver);
    }

    public static class DateTimeDto {
        @ValidDateTime
        private final String value;

        public DateTimeDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class DateDto {
        @ValidDate
        private final String value;

        public DateDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class TimeDto {
        @ValidTime
        private final String value;

        public TimeDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class ISO8601Dto {
        @ValidISO8601
        private final String value;

        public ISO8601Dto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class PastDateDto {
        @ValidPastDate
        private final String value;

        public PastDateDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class FutureDateDto {
        @ValidFutureDate
        private final String value;

        public FutureDateDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class InvalidPastDateWithToleranceDto {
        @InvalidPastDate(tolerance = 1)
        private final String value;

        public InvalidPastDateWithToleranceDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class InvalidPastDatePatternDto {
        @InvalidPastDate
        private final String value;

        public InvalidPastDatePatternDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class InvalidFutureDateDto {
        @InvalidFutureDate(pattern = "yyyy-MM-dd")
        private final String value;

        public InvalidFutureDateDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class InvalidFutureDatePatternDto {
        @InvalidFutureDate(pattern = "yyyy-MM-dd")
        private final String value;

        public InvalidFutureDatePatternDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class InvalidFutureDateTimeToleranceDto {
        @InvalidFutureDate(pattern = "yyyy-MM-dd HH:mm:ss")
        private final String value;

        public InvalidFutureDateTimeToleranceDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class InvalidPastFutureDateDto {
        @InvalidPastFutureDate(pattern = "yyyy-MM-dd", toleranceHours = 24)
        private final String value;

        public InvalidPastFutureDateDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class InvalidPastFutureDatePatternDto {
        @InvalidPastFutureDate(pattern = "yyyy-MM-dd HH:mm:ss", toleranceHours = 1)
        private final String value;

        public InvalidPastFutureDatePatternDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    @DateBefore(first = "startDate", second = "endDate")
    public static class DateBeforeDto {
        private final String startDate;
        private final String endDate;

        public DateBeforeDto(String startDate, String endDate) {
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

    @DateBefore(first = "startDate", second = "endDate", maxDistance = 5, precision = DatePrecision.DAYS)
    public static class DateBeforeDistanceDto {
        private final String startDate;
        private final String endDate;

        public DateBeforeDistanceDto(String startDate, String endDate) {
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

    @DateAfter(first = "endDate", second = "startDate")
    public static class DateAfterDto {
        private final String startDate;
        private final String endDate;

        public DateAfterDto(String startDate, String endDate) {
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

    @DateAfter(first = "endDate", second = "startDate", maxDistance = 5, precision = DatePrecision.DAYS)
    public static class DateAfterDistanceDto {
        private final String startDate;
        private final String endDate;

        public DateAfterDistanceDto(String startDate, String endDate) {
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

    private <T> ConstraintViolation<T> firstViolation(T dto) {
        Set<ConstraintViolation<T>> violations = VALIDATOR.validate(dto);

        assertFalse(violations.isEmpty(), "Expected at least one violation but got none");

        return violations.iterator().next();
    }

    @Test
    void datetime_pathB() {
        DateTimeDto dto = new DateTimeDto("not-a-datetime");
        ConstraintViolation<DateTimeDto> v = firstViolation(dto);

        assertEquals("{friendly.default}", v.getMessageTemplate());

        String resolved = RESOLVER.resolve(v, "value", DateTimeDto.class);

        assertTrue(resolved.contains("yyyy-MM-dd HH:mm:ss"), "Pattern missing in message: " + resolved);
    }

    @Test
    void date_pathB() {
        DateDto dto = new DateDto("not-a-date");
        ConstraintViolation<DateDto> v = firstViolation(dto);

        assertEquals("{friendly.default}", v.getMessageTemplate());

        String resolved = RESOLVER.resolve(v, "value", DateDto.class);

        assertTrue(resolved.contains("yyyy-MM-dd"), "Pattern missing in message: " + resolved);
    }

    @Test
    void time_pathB() {
        TimeDto dto = new TimeDto("not-a-time");
        ConstraintViolation<TimeDto> v = firstViolation(dto);

        assertEquals("{friendly.default}", v.getMessageTemplate());

        String resolved = RESOLVER.resolve(v, "value", TimeDto.class);

        assertTrue(resolved.contains("HH:mm:ss"), "Pattern missing in message: " + resolved);
    }

    @Test
    void iso8601_pathB() {
        ISO8601Dto dto = new ISO8601Dto("not-iso8601");
        ConstraintViolation<ISO8601Dto> v = firstViolation(dto);

        assertEquals("{friendly.default}", v.getMessageTemplate());
        assertEquals("value must be a valid ISO 8601", RESOLVER.resolve(v, "value", ISO8601Dto.class));
    }

    @Test
    void pastDate_dateInFuture() {
        PastDateDto dto = new PastDateDto("2099-12-31");
        ConstraintViolation<PastDateDto> v = firstViolation(dto);

        assertEquals("validation.datetime.past-date", v.getMessageTemplate());
        assertEquals("value must be a date in the past", RESOLVER.resolve(v, "value", PastDateDto.class));
    }

    @Test
    void pastDate_invalidFormat() {
        PastDateDto dto = new PastDateDto("not-a-date");
        ConstraintViolation<PastDateDto> v = firstViolation(dto);

        assertEquals("validation.datetime.past-date.pattern", v.getMessageTemplate());

        String resolved = RESOLVER.resolve(v, "value", PastDateDto.class);

        assertTrue(resolved.contains("yyyy-MM-dd"), "Pattern missing in message: " + resolved);
    }

    @Test
    void futureDate_dateInPast() {
        FutureDateDto dto = new FutureDateDto("2000-01-01");
        ConstraintViolation<FutureDateDto> v = firstViolation(dto);

        assertEquals("validation.datetime.future-date", v.getMessageTemplate());
        assertEquals("value must be a date in the future", RESOLVER.resolve(v, "value", FutureDateDto.class));
    }

    @Test
    void futureDate_invalidFormat() {
        FutureDateDto dto = new FutureDateDto("not-a-date");
        ConstraintViolation<FutureDateDto> v = firstViolation(dto);

        assertEquals("validation.datetime.future-date.pattern", v.getMessageTemplate());

        String resolved = RESOLVER.resolve(v, "value", FutureDateDto.class);

        assertTrue(resolved.contains("yyyy-MM-dd"), "Pattern missing in message: " + resolved);
    }

    @Test
    void invalidPastDate_tooFarInPast() {
        InvalidPastDateWithToleranceDto dto = new InvalidPastDateWithToleranceDto("1924-01-01");
        ConstraintViolation<InvalidPastDateWithToleranceDto> v = firstViolation(dto);

        assertEquals("validation.datetime.invalid-past-date.tolerance", v.getMessageTemplate());

        String resolved = RESOLVER.resolve(v, "value", InvalidPastDateWithToleranceDto.class);

        assertNotNull(resolved);
        assertTrue(resolved.contains("past"), "Expected 'past' in message: " + resolved);
    }

    @Test
    void invalidPastDate_invalidFormat() {
        InvalidPastDatePatternDto dto = new InvalidPastDatePatternDto("not-a-date");
        ConstraintViolation<InvalidPastDatePatternDto> v = firstViolation(dto);

        assertEquals("validation.datetime.invalid-past-date.pattern", v.getMessageTemplate());

        String resolved = RESOLVER.resolve(v, "value", InvalidPastDatePatternDto.class);

        assertTrue(resolved.contains("yyyy-MM-dd"), "Pattern missing in message: " + resolved);
    }

    @Test
    void invalidFutureDate_dateInFuture() {
        InvalidFutureDateDto dto = new InvalidFutureDateDto("2099-12-31");
        ConstraintViolation<InvalidFutureDateDto> v = firstViolation(dto);

        assertEquals("validation.datetime.invalid-future-date", v.getMessageTemplate());
        assertEquals("value must be a date equals today", RESOLVER.resolve(v, "value", InvalidFutureDateDto.class));
    }

    @Test
    void invalidFutureDate_invalidFormat() {
        InvalidFutureDatePatternDto dto = new InvalidFutureDatePatternDto("not-a-date");
        ConstraintViolation<InvalidFutureDatePatternDto> v = firstViolation(dto);

        assertEquals("validation.datetime.invalid-future-date.pattern", v.getMessageTemplate());

        String resolved = RESOLVER.resolve(v, "value", InvalidFutureDatePatternDto.class);

        assertTrue(resolved.contains("yyyy-MM-dd"), "Pattern missing in message: " + resolved);
    }

    @Test
    void invalidFutureDate_tolerance() {
        InvalidFutureDateTimeToleranceDto dto = new InvalidFutureDateTimeToleranceDto("2099-12-31 00:00:00");
        ConstraintViolation<InvalidFutureDateTimeToleranceDto> v = firstViolation(dto);

        assertEquals("validation.datetime.invalid-future-date.tolerance", v.getMessageTemplate());

        String resolved = RESOLVER.resolve(v, "value", InvalidFutureDateTimeToleranceDto.class);

        assertNotNull(resolved);
        assertTrue(resolved.contains("past"), "Expected 'past' in message: " + resolved);
    }

    @Test
    void invalidPastFutureDate_tolerance() {
        InvalidPastFutureDateDto dto = new InvalidPastFutureDateDto("2099-12-31");
        ConstraintViolation<InvalidPastFutureDateDto> v = firstViolation(dto);

        assertEquals("validation.datetime.invalid-past-future-date.tolerance", v.getMessageTemplate());

        String resolved = RESOLVER.resolve(v, "value", InvalidPastFutureDateDto.class);

        assertNotNull(resolved);
        assertTrue(resolved.contains("past"), "Expected 'past' in message: " + resolved);
    }

    @Test
    void invalidPastFutureDate_invalidFormat() {
        InvalidPastFutureDatePatternDto dto = new InvalidPastFutureDatePatternDto("not-a-datetime");
        ConstraintViolation<InvalidPastFutureDatePatternDto> v = firstViolation(dto);

        assertEquals("validation.datetime.invalid-past-future-date.pattern", v.getMessageTemplate());

        String resolved = RESOLVER.resolve(v, "value", InvalidPastFutureDatePatternDto.class);

        assertTrue(resolved.contains("yyyy-MM-dd"), "Pattern missing in message: " + resolved);
    }

    @Test
    void dateBefore_firstNotBeforeSecond() {
        DateBeforeDto dto = new DateBeforeDto("2025-06-01 10:00:00", "2025-01-01 10:00:00");
        ConstraintViolation<DateBeforeDto> v = firstViolation(dto);

        assertEquals("validation.datetime.date-before", v.getMessageTemplate());

        String resolved = RESOLVER.resolve(v, "startDate", DateBeforeDto.class);

        assertTrue(resolved.contains("startDate") || resolved.contains("before"),
                "Unexpected message: " + resolved);
    }

    @Test
    void dateBefore_patternInvalid() {
        DateBeforeDto dto = new DateBeforeDto("not-a-date", "2025-01-01 10:00:00");
        ConstraintViolation<DateBeforeDto> v = firstViolation(dto);

        assertTrue(v.getMessageTemplate().startsWith("validation.datetime.date-before"),
                "Unexpected template: " + v.getMessageTemplate());

        String resolved = RESOLVER.resolve(v, "startDate", DateBeforeDto.class);

        assertNotNull(resolved);
    }

    @Test
    void dateBefore_distanceExceeded() {
        DateBeforeDistanceDto dto = new DateBeforeDistanceDto("2025-01-01 10:00:00", "2025-01-20 10:00:00");
        ConstraintViolation<DateBeforeDistanceDto> v = firstViolation(dto);

        assertEquals("validation.datetime.date-before.distance", v.getMessageTemplate());

        String resolved = RESOLVER.resolve(v, "startDate", DateBeforeDistanceDto.class);

        assertNotNull(resolved);
        assertTrue(resolved.contains("before") || resolved.contains("distance") || resolved.contains("5"),
                "Unexpected message: " + resolved);
    }

    @Test
    void dateAfter_firstNotAfterSecond() {
        DateAfterDto dto = new DateAfterDto("2025-06-01 10:00:00", "2025-01-01 10:00:00");
        ConstraintViolation<DateAfterDto> v = firstViolation(dto);

        assertEquals("validation.datetime.date-after", v.getMessageTemplate());

        String resolved = RESOLVER.resolve(v, "endDate", DateAfterDto.class);

        assertTrue(resolved.contains("endDate") || resolved.contains("after"),
                "Unexpected message: " + resolved);
    }

    @Test
    void dateAfter_patternInvalid() {
        DateAfterDto dto = new DateAfterDto("2025-06-01 10:00:00", "not-a-date");
        ConstraintViolation<DateAfterDto> v = firstViolation(dto);

        assertTrue(v.getMessageTemplate().startsWith("validation.datetime.date-after"),
                "Unexpected template: " + v.getMessageTemplate());

        String resolved = RESOLVER.resolve(v, "endDate", DateAfterDto.class);

        assertNotNull(resolved);
    }

    @Test
    void dateAfter_distanceExceeded() {
        DateAfterDistanceDto dto = new DateAfterDistanceDto("2025-01-01 10:00:00", "2025-01-20 10:00:00");
        ConstraintViolation<DateAfterDistanceDto> v = firstViolation(dto);

        assertEquals("validation.datetime.date-after.distance", v.getMessageTemplate());

        String resolved = RESOLVER.resolve(v, "endDate", DateAfterDistanceDto.class);

        assertNotNull(resolved);
        assertTrue(resolved.contains("after") || resolved.contains("distance") || resolved.contains("5"),
                "Unexpected message: " + resolved);
    }
}
