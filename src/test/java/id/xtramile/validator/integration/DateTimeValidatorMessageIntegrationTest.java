package id.xtramile.validator.integration;

import id.xtramile.validator.annotation.datetime.*;
import id.xtramile.validator.enums.DatePrecision;
import id.xtramile.validator.support.ValidationMessageTestSupport;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DateTimeValidatorMessageIntegrationTest {

    private static final ValidationMessageTestSupport SUPPORT = ValidationMessageTestSupport.EN;

    @Test
    void datetime_pathB() {
        DateTimeDto dto = new DateTimeDto("not-a-datetime");
        ConstraintViolation<DateTimeDto> v = SUPPORT.firstViolation(dto);

        assertEquals("{friendly.default}", v.getMessageTemplate());

        String resolved = SUPPORT.resolver().resolve(v, "value", DateTimeDto.class);

        assertTrue(resolved.contains("yyyy-MM-dd HH:mm:ss"), "Pattern missing in message: " + resolved);
    }

    @Test
    void date_pathB() {
        DateDto dto = new DateDto("not-a-date");
        ConstraintViolation<DateDto> v = SUPPORT.firstViolation(dto);

        assertEquals("{friendly.default}", v.getMessageTemplate());

        String resolved = SUPPORT.resolver().resolve(v, "value", DateDto.class);

        assertTrue(resolved.contains("yyyy-MM-dd"), "Pattern missing in message: " + resolved);
    }

    @Test
    void time_pathB() {
        TimeDto dto = new TimeDto("not-a-time");
        ConstraintViolation<TimeDto> v = SUPPORT.firstViolation(dto);

        assertEquals("{friendly.default}", v.getMessageTemplate());

        String resolved = SUPPORT.resolver().resolve(v, "value", TimeDto.class);

        assertTrue(resolved.contains("HH:mm:ss"), "Pattern missing in message: " + resolved);
    }

    @Test
    void iso8601_pathB() {
        ISO8601Dto dto = new ISO8601Dto("not-iso8601");
        ConstraintViolation<ISO8601Dto> v = SUPPORT.firstViolation(dto);

        assertEquals("{friendly.default}", v.getMessageTemplate());
        assertEquals("value must be a valid ISO 8601", SUPPORT.resolver().resolve(v, "value", ISO8601Dto.class));
    }

    @Test
    void pastDate_dateInFuture() {
        PastDateDto dto = new PastDateDto("2099-12-31");
        ConstraintViolation<PastDateDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.datetime.past-date", v.getMessageTemplate());
        assertEquals("value must be a date in the past", SUPPORT.resolver().resolve(v, "value", PastDateDto.class));
    }

    @Test
    void pastDate_invalidFormat() {
        PastDateDto dto = new PastDateDto("not-a-date");
        ConstraintViolation<PastDateDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.datetime.past-date.pattern", v.getMessageTemplate());

        String resolved = SUPPORT.resolver().resolve(v, "value", PastDateDto.class);

        assertTrue(resolved.contains("yyyy-MM-dd"), "Pattern missing in message: " + resolved);
    }

    @Test
    void futureDate_dateInPast() {
        FutureDateDto dto = new FutureDateDto("2000-01-01");
        ConstraintViolation<FutureDateDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.datetime.future-date", v.getMessageTemplate());
        assertEquals("value must be a date in the future", SUPPORT.resolver().resolve(v, "value", FutureDateDto.class));
    }

    @Test
    void futureDate_invalidFormat() {
        FutureDateDto dto = new FutureDateDto("not-a-date");
        ConstraintViolation<FutureDateDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.datetime.future-date.pattern", v.getMessageTemplate());

        String resolved = SUPPORT.resolver().resolve(v, "value", FutureDateDto.class);

        assertTrue(resolved.contains("yyyy-MM-dd"), "Pattern missing in message: " + resolved);
    }

    @Test
    void invalidPastDate_tooFarInPast() {
        InvalidPastDateWithToleranceDto dto = new InvalidPastDateWithToleranceDto("1924-01-01");
        ConstraintViolation<InvalidPastDateWithToleranceDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.datetime.invalid-past-date.tolerance", v.getMessageTemplate());

        String resolved = SUPPORT.resolver().resolve(v, "value", InvalidPastDateWithToleranceDto.class);

        assertNotNull(resolved);
        assertTrue(resolved.contains("past"), "Expected 'past' in message: " + resolved);
    }

    @Test
    void invalidPastDate_invalidFormat() {
        InvalidPastDatePatternDto dto = new InvalidPastDatePatternDto("not-a-date");
        ConstraintViolation<InvalidPastDatePatternDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.datetime.invalid-past-date.pattern", v.getMessageTemplate());

        String resolved = SUPPORT.resolver().resolve(v, "value", InvalidPastDatePatternDto.class);

        assertTrue(resolved.contains("yyyy-MM-dd"), "Pattern missing in message: " + resolved);
    }

    @Test
    void invalidFutureDate_dateInFuture() {
        InvalidFutureDateDto dto = new InvalidFutureDateDto("2099-12-31");
        ConstraintViolation<InvalidFutureDateDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.datetime.invalid-future-date", v.getMessageTemplate());
        assertEquals("value must be a date equals today", SUPPORT.resolver().resolve(v, "value", InvalidFutureDateDto.class));
    }

    @Test
    void invalidFutureDate_invalidFormat() {
        InvalidFutureDatePatternDto dto = new InvalidFutureDatePatternDto("not-a-date");
        ConstraintViolation<InvalidFutureDatePatternDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.datetime.invalid-future-date.pattern", v.getMessageTemplate());

        String resolved = SUPPORT.resolver().resolve(v, "value", InvalidFutureDatePatternDto.class);

        assertTrue(resolved.contains("yyyy-MM-dd"), "Pattern missing in message: " + resolved);
    }

    @Test
    void invalidFutureDate_tolerance() {
        InvalidFutureDateTimeToleranceDto dto = new InvalidFutureDateTimeToleranceDto("2099-12-31 00:00:00");
        ConstraintViolation<InvalidFutureDateTimeToleranceDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.datetime.invalid-future-date.tolerance", v.getMessageTemplate());

        String resolved = SUPPORT.resolver().resolve(v, "value", InvalidFutureDateTimeToleranceDto.class);

        assertNotNull(resolved);
        assertTrue(resolved.contains("past"), "Expected 'past' in message: " + resolved);
    }

    @Test
    void invalidPastFutureDate_tolerance() {
        InvalidPastFutureDateDto dto = new InvalidPastFutureDateDto("2099-12-31");
        ConstraintViolation<InvalidPastFutureDateDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.datetime.invalid-past-future-date.tolerance", v.getMessageTemplate());

        String resolved = SUPPORT.resolver().resolve(v, "value", InvalidPastFutureDateDto.class);

        assertNotNull(resolved);
        assertTrue(resolved.contains("past"), "Expected 'past' in message: " + resolved);
    }

    @Test
    void invalidPastFutureDate_invalidFormat() {
        InvalidPastFutureDatePatternDto dto = new InvalidPastFutureDatePatternDto("not-a-datetime");
        ConstraintViolation<InvalidPastFutureDatePatternDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.datetime.invalid-past-future-date.pattern", v.getMessageTemplate());

        String resolved = SUPPORT.resolver().resolve(v, "value", InvalidPastFutureDatePatternDto.class);

        assertTrue(resolved.contains("yyyy-MM-dd"), "Pattern missing in message: " + resolved);
    }

    @Test
    void dateBefore_firstNotBeforeSecond() {
        DateBeforeDto dto = new DateBeforeDto("2025-06-01 10:00:00", "2025-01-01 10:00:00");
        ConstraintViolation<DateBeforeDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.datetime.date-before", v.getMessageTemplate());

        String resolved = SUPPORT.resolver().resolve(v, "startDate", DateBeforeDto.class);

        assertTrue(resolved.contains("startDate") || resolved.contains("before"),
                "Unexpected message: " + resolved);
    }

    @Test
    void dateBefore_patternInvalid() {
        DateBeforeDto dto = new DateBeforeDto("not-a-date", "2025-01-01 10:00:00");
        ConstraintViolation<DateBeforeDto> v = SUPPORT.firstViolation(dto);

        assertTrue(v.getMessageTemplate().startsWith("validation.datetime.date-before"),
                "Unexpected template: " + v.getMessageTemplate());

        String resolved = SUPPORT.resolver().resolve(v, "startDate", DateBeforeDto.class);

        assertNotNull(resolved);
    }

    @Test
    void dateBefore_distanceExceeded() {
        DateBeforeDistanceDto dto = new DateBeforeDistanceDto("2025-01-01 10:00:00", "2025-01-20 10:00:00");
        ConstraintViolation<DateBeforeDistanceDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.datetime.date-before.distance", v.getMessageTemplate());

        String resolved = SUPPORT.resolver().resolve(v, "startDate", DateBeforeDistanceDto.class);

        assertNotNull(resolved);
        assertTrue(resolved.contains("before") || resolved.contains("distance") || resolved.contains("5"),
                "Unexpected message: " + resolved);
    }

    @Test
    void dateAfter_firstNotAfterSecond() {
        DateAfterDto dto = new DateAfterDto("2025-06-01 10:00:00", "2025-01-01 10:00:00");
        ConstraintViolation<DateAfterDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.datetime.date-after", v.getMessageTemplate());

        String resolved = SUPPORT.resolver().resolve(v, "endDate", DateAfterDto.class);

        assertTrue(resolved.contains("endDate") || resolved.contains("after"),
                "Unexpected message: " + resolved);
    }

    @Test
    void dateAfter_patternInvalid() {
        DateAfterDto dto = new DateAfterDto("2025-06-01 10:00:00", "not-a-date");
        ConstraintViolation<DateAfterDto> v = SUPPORT.firstViolation(dto);

        assertTrue(v.getMessageTemplate().startsWith("validation.datetime.date-after"),
                "Unexpected template: " + v.getMessageTemplate());

        String resolved = SUPPORT.resolver().resolve(v, "endDate", DateAfterDto.class);

        assertNotNull(resolved);
    }

    @Test
    void dateAfter_distanceExceeded() {
        DateAfterDistanceDto dto = new DateAfterDistanceDto("2025-01-01 10:00:00", "2025-01-20 10:00:00");
        ConstraintViolation<DateAfterDistanceDto> v = SUPPORT.firstViolation(dto);

        assertEquals("validation.datetime.date-after.distance", v.getMessageTemplate());

        String resolved = SUPPORT.resolver().resolve(v, "endDate", DateAfterDistanceDto.class);

        assertNotNull(resolved);
        assertTrue(resolved.contains("after") || resolved.contains("distance") || resolved.contains("5"),
                "Unexpected message: " + resolved);
    }

    public record DateTimeDto(@ValidDateTime String value) {
        public DateTimeDto(String value) {
            this.value = value;
        }
    }

    public record DateDto(@ValidDate String value) {
        public DateDto(String value) {
            this.value = value;
        }
    }

    public record TimeDto(@ValidTime String value) {
        public TimeDto(String value) {
            this.value = value;
        }
    }

    public record ISO8601Dto(@ValidISO8601 String value) {
        public ISO8601Dto(String value) {
            this.value = value;
        }
    }

    public record PastDateDto(@ValidPastDate String value) {
        public PastDateDto(String value) {
            this.value = value;
        }
    }

    public record FutureDateDto(@ValidFutureDate String value) {
        public FutureDateDto(String value) {
            this.value = value;
        }
    }

    public record InvalidPastDateWithToleranceDto(@InvalidPastDate(tolerance = 1) String value) {
        public InvalidPastDateWithToleranceDto(String value) {
            this.value = value;
        }
    }

    public record InvalidPastDatePatternDto(@InvalidPastDate String value) {
        public InvalidPastDatePatternDto(String value) {
            this.value = value;
        }
    }

    public record InvalidFutureDateDto(@InvalidFutureDate(pattern = "yyyy-MM-dd") String value) {
        public InvalidFutureDateDto(String value) {
            this.value = value;
        }
    }

    public record InvalidFutureDatePatternDto(@InvalidFutureDate(pattern = "yyyy-MM-dd") String value) {
        public InvalidFutureDatePatternDto(String value) {
            this.value = value;
        }
    }

    public record InvalidFutureDateTimeToleranceDto(@InvalidFutureDate(pattern = "yyyy-MM-dd HH:mm:ss") String value) {
        public InvalidFutureDateTimeToleranceDto(String value) {
            this.value = value;
        }
    }

    public record InvalidPastFutureDateDto(
            @InvalidPastFutureDate(pattern = "yyyy-MM-dd", toleranceHours = 24) String value) {
        public InvalidPastFutureDateDto(String value) {
            this.value = value;
        }
    }

    public record InvalidPastFutureDatePatternDto(
            @InvalidPastFutureDate(pattern = "yyyy-MM-dd HH:mm:ss", toleranceHours = 1) String value) {
        public InvalidPastFutureDatePatternDto(String value) {
            this.value = value;
        }
    }

    @DateBefore(first = "startDate", second = "endDate")
    public record DateBeforeDto(String startDate, String endDate) {
    }

    @DateBefore(first = "startDate", second = "endDate", maxDistance = 5, precision = DatePrecision.DAYS)
    public record DateBeforeDistanceDto(String startDate, String endDate) {
    }

    @DateAfter(first = "endDate", second = "startDate")
    public record DateAfterDto(String startDate, String endDate) {
    }

    @DateAfter(first = "endDate", second = "startDate", maxDistance = 5, precision = DatePrecision.DAYS)
    public record DateAfterDistanceDto(String startDate, String endDate) {
    }
}
