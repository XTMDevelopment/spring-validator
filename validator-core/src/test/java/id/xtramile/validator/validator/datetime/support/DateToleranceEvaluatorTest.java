package id.xtramile.validator.validator.datetime.support;

import id.xtramile.validator.validator.datetime.support.DateToleranceEvaluator.ParseResult;
import id.xtramile.validator.validator.datetime.support.DateToleranceEvaluator.ParsedTemporal;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DateToleranceEvaluatorTest {

    private static final String PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final String DATE_PATTERN = "yyyy-MM-dd";

    @Test
    void parseStrictThenSmart_parsesZonedDateTimeOnStrictPath() {
        ParsedTemporal[] out = new ParsedTemporal[1];
        ParseResult result = DateToleranceEvaluator.parseStrictThenSmart(
                "2024-06-01T10:00:00+07:00",
                "yyyy-MM-dd'T'HH:mm:ssXXX",
                out
        );
        assertEquals(ParseResult.SUCCESS, result);
        assertTrue(out[0].isRecognized());
    }

    @Test
    void parseStrictThenSmart_parsesLocalDateTimeOnStrictPath() {
        ParsedTemporal[] out = new ParsedTemporal[1];
        ParseResult result = DateToleranceEvaluator.parseStrictThenSmart("2024-06-01 10:00:00", PATTERN, out);
        assertEquals(ParseResult.SUCCESS, result);
        assertTrue(out[0].isRecognized());
    }

    @Test
    void parseStrictThenSmart_parsesLocalDateOnStrictPath() {
        ParsedTemporal[] out = new ParsedTemporal[1];
        ParseResult result = DateToleranceEvaluator.parseStrictThenSmart("2024-06-01", DATE_PATTERN, out);
        assertEquals(ParseResult.SUCCESS, result);
        assertTrue(out[0].isRecognized());
    }

    @Test
    void parseStrictThenSmart_returnsPatternErrorForGarbageInput() {
        ParsedTemporal[] out = new ParsedTemporal[1];
        ParseResult result = DateToleranceEvaluator.parseStrictThenSmart("not-a-date", DATE_PATTERN, out);
        assertEquals(ParseResult.PATTERN_ERROR, result);
    }

    @Test
    void parseLocalDateTimeStrictThenSmart_parsesStrictValue() {
        LocalDateTime[] out = new LocalDateTime[1];
        assertEquals(ParseResult.SUCCESS,
                DateToleranceEvaluator.parseLocalDateTimeStrictThenSmart("2024-06-01 10:00:00", PATTERN, out));
        assertEquals(LocalDateTime.of(2024, 6, 1, 10, 0), out[0]);
    }

    @Test
    void parseLocalDateTimeStrictThenSmart_returnsPatternErrorForGarbageInput() {
        LocalDateTime[] out = new LocalDateTime[1];
        assertEquals(ParseResult.PATTERN_ERROR,
                DateToleranceEvaluator.parseLocalDateTimeStrictThenSmart("bad", PATTERN, out));
    }

    @Test
    void parseLocalDateStrictThenSmart_parsesStrictValue() {
        LocalDate[] out = new LocalDate[1];
        assertEquals(ParseResult.SUCCESS,
                DateToleranceEvaluator.parseLocalDateStrictThenSmart("2024-06-01", DATE_PATTERN, out));
        assertEquals(LocalDate.of(2024, 6, 1), out[0]);
    }

    @Test
    void parseLocalDateStrictThenSmart_returnsPatternErrorForGarbageInput() {
        LocalDate[] out = new LocalDate[1];
        assertEquals(ParseResult.PATTERN_ERROR,
                DateToleranceEvaluator.parseLocalDateStrictThenSmart("bad", DATE_PATTERN, out));
    }

    @Test
    void parsedTemporal_fromSupportsAllTemporalTypes() {
        ZonedDateTime zdt = ZonedDateTime.of(2024, 6, 1, 10, 0, 0, 0, ZoneId.of("UTC"));
        LocalDateTime ldt = LocalDateTime.of(2024, 6, 1, 10, 0);
        LocalDate ld = LocalDate.of(2024, 6, 1);

        assertTrue(ParsedTemporal.from(zdt).isRecognized());
        assertTrue(ParsedTemporal.from(ldt).isRecognized());
        assertTrue(ParsedTemporal.from(ld).isRecognized());
    }

    @Test
    void parsedTemporal_fromReturnsUnrecognizedForUnsupportedAccessor() {
        assertFalse(ParsedTemporal.from(java.time.Year.of(2024)).isRecognized());
    }

    @Test
    void isTooFarInPast_checksZonedLocalDateTimeAndLocalDate() {
        ParsedTemporal oldZoned = ParsedTemporal.from(ZonedDateTime.now().minusDays(10));
        ParsedTemporal oldDateTime = ParsedTemporal.from(LocalDateTime.now().minusDays(10));
        ParsedTemporal oldDate = ParsedTemporal.from(LocalDate.now().minusDays(10));
        ParsedTemporal empty = ParsedTemporal.from(java.time.Year.of(2024));

        assertTrue(DateToleranceEvaluator.isTooFarInPast(oldZoned, 5, false));
        assertTrue(DateToleranceEvaluator.isTooFarInPast(oldDateTime, 5, true));
        assertTrue(DateToleranceEvaluator.isTooFarInPast(oldDate, 5, false));
        assertTrue(DateToleranceEvaluator.isTooFarInPast(empty, 5, false));
    }

    @Test
    void isTooFarInPast_allowsRecentValues() {
        ParsedTemporal recent = ParsedTemporal.from(LocalDate.now());
        assertFalse(DateToleranceEvaluator.isTooFarInPast(recent, 1, false));
    }

    @Test
    void isStrictlyBeforeNow_checksAllTemporalKinds() {
        assertTrue(DateToleranceEvaluator.isStrictlyBeforeNow(
                ParsedTemporal.from(ZonedDateTime.now().minusDays(1))));
        assertTrue(DateToleranceEvaluator.isStrictlyBeforeNow(
                ParsedTemporal.from(LocalDateTime.now().minusDays(1))));
        assertTrue(DateToleranceEvaluator.isStrictlyBeforeNow(
                ParsedTemporal.from(LocalDate.now().minusDays(1))));
        assertFalse(DateToleranceEvaluator.isStrictlyBeforeNow(
                ParsedTemporal.from(java.time.Year.of(2024))));
    }

    @Test
    void isOutsidePastFutureWindow_checksParsedTemporalVariants() {
        ParsedTemporal futureZoned = ParsedTemporal.from(ZonedDateTime.now().plusHours(5));
        ParsedTemporal pastDateTime = ParsedTemporal.from(LocalDateTime.now().minusHours(5));
        ParsedTemporal futureDate = ParsedTemporal.from(LocalDate.now().plusDays(2));
        ParsedTemporal empty = ParsedTemporal.from(java.time.Year.of(2024));

        assertTrue(DateToleranceEvaluator.isOutsidePastFutureWindow(futureZoned, 1));
        assertTrue(DateToleranceEvaluator.isOutsidePastFutureWindow(pastDateTime, 1));
        assertTrue(DateToleranceEvaluator.isOutsidePastFutureWindow(futureDate, 0));
        assertTrue(DateToleranceEvaluator.isOutsidePastFutureWindow(empty, 1));
    }

    @Test
    void isOutsidePastFutureWindow_checksLocalDateTimeAndLocalDate() {
        assertTrue(DateToleranceEvaluator.isOutsidePastFutureWindow(
                LocalDateTime.now().minusHours(5), 1));
        assertTrue(DateToleranceEvaluator.isOutsidePastFutureWindow(
                LocalDate.now().plusDays(2), 0));
        assertFalse(DateToleranceEvaluator.isOutsidePastFutureWindow(
                LocalDateTime.now(), 24));
        assertFalse(DateToleranceEvaluator.isOutsidePastFutureWindow(
                LocalDate.now(), 24));
    }
}
