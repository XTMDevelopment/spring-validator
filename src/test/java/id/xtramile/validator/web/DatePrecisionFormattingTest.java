package id.xtramile.validator.web;

import id.xtramile.validator.enums.DatePrecision;
import id.xtramile.validator.util.DateUtils;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DatePrecisionFormattingTest {

    @Test
    void pluralLabel_defaultsToDaysWhenNull() {
        assertThat(DateUtils.pluralLabel(null)).isEqualTo("days");
    }

    @Test
    void pluralLabel_appendsSWhenNameDoesNotEndWithS() {
        assertThat(DateUtils.pluralLabel(DatePrecision.DAYS)).isEqualTo("days");
        assertThat(DateUtils.pluralLabel(DatePrecision.HOURS)).isEqualTo("hours");
    }

    @Test
    void pluralLabel_keepsMinutesAndSeconds() {
        assertThat(DateUtils.pluralLabel(DatePrecision.MINUTES)).isEqualTo("minutes");
        assertThat(DateUtils.pluralLabel(DatePrecision.SECONDS)).isEqualTo("seconds");
    }
}
