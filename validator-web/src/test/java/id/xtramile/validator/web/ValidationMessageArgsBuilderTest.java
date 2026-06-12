package id.xtramile.validator.web;

import id.xtramile.validator.annotation.common.FieldName;
import id.xtramile.validator.annotation.common.InWhitelist;
import id.xtramile.validator.annotation.data.ValidName;
import id.xtramile.validator.annotation.datetime.DateBefore;
import id.xtramile.validator.enums.DatePrecision;
import id.xtramile.validator.util.MessageUtils;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Size;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ValidationMessageArgsBuilderTest {

    private ValidationMessageArgsBuilder builder;

    @BeforeEach
    void setUp() {
        ValidationFieldDisplayNames fieldNames = new ValidationFieldDisplayNames();
        builder = new ValidationMessageArgsBuilder(fieldNames);
    }

    @AfterEach
    void tearDown() {
        MessageUtils.clearStoredArgs();
    }

    @Test
    void constraintAttributesForFieldError_nullDto_returnsEmptyMap() {
        assertThat(builder.constraintAttributesForFieldError(null, "f", InWhitelist.class)).isEmpty();
    }

    @Test
    void constraintAttributesForFieldError_readsFieldLevelAnnotation() {
        Map<String, Object> attrs = builder.constraintAttributesForFieldError(WhitelistDto.class, "channel", InWhitelist.class);
        assertThat(attrs.get("values")).isEqualTo(new String[]{"a", "b"});
    }

    @Test
    void constraintAttributesForFieldError_nestedFieldPath_readsSizeOnLeaf() {
        Map<String, Object> attrs = builder.constraintAttributesForFieldError(NestedParentDto.class, "dto.email", Size.class);

        assertThat(attrs.get("min")).isEqualTo(0);
        assertThat(attrs.get("max")).isEqualTo(40);
    }

    @Test
    void constraintAttributesForFieldError_readsTypeLevelDateBeforeOnClass() {
        Map<String, Object> attrs = builder.constraintAttributesForFieldError(DateBeforeForm.class, "start", DateBefore.class);
        assertThat(attrs.get("first")).isEqualTo("start");
        assertThat(attrs.get("second")).isEqualTo("end");
        assertThat(attrs.get("maxDistance")).isEqualTo(5L);
        assertThat(attrs.get("precision")).isEqualTo(DatePrecision.HOURS);
    }

    @Test
    void buildMessageArgs_inWhitelist_joinsValuesFromAttrs() {
        Map<String, Object> attrs = new HashMap<>();
        attrs.put("values", new String[]{"x", "y"});
        attrs.put("ignoreCase", true);

        Object[] args = builder.buildMessageArgs("Channel", attrs, "validation.common.in-whitelist", WhitelistDto.class);
        assertThat(args).containsExactly("Channel", "x, y");
    }

    @Test
    void buildMessageArgs_nameMin_usesMinFromAttrs() {
        Map<String, Object> attrs = builder.constraintAttributesForFieldError(NameDto.class, "fullName", ValidName.class);

        Object[] args = builder.buildMessageArgs("Nama", attrs, "validation.data.name.min", NameDto.class);
        assertThat(args).containsExactly("Nama", 4, 100);
    }

    @Test
    void buildMessageArgs_digits_appendsIntegerAndFraction() {
        Map<String, Object> attrs = builder.constraintAttributesForFieldError(DigitsDto.class, "code", Digits.class);

        Object[] args = builder.buildMessageArgs("Code", attrs, "validation.spring.digits", DigitsDto.class);
        assertThat(args).containsExactly("Code", 3, 1);
    }

    @Test
    void buildMessageArgs_dateBeforeDistance_usesFieldLabels() {
        Map<String, Object> attrs = builder.constraintAttributesForFieldError(DateBeforeDistanceForm.class, "a", DateBefore.class);

        Object[] args = builder.buildMessageArgs("ignored", attrs, "validation.datetime.date-before.distance", DateBeforeDistanceForm.class);
        assertThat(args).containsExactly("A start", "B end", 10L, "hours");
    }

    static class WhitelistDto {
        @InWhitelist(values = {"a", "b"})
        String channel;
    }

    static class NestedParentDto {
        NestedChildDto dto;
    }

    static class NestedChildDto {
        @Size(max = 40)
        String email;
    }

    @DateBefore(first = "start", second = "end", maxDistance = 5, precision = DatePrecision.HOURS)
    static class DateBeforeForm {
        @FieldName("Start")
        String start;
        String end;
    }

    static class NameDto {
        @FieldName("Nama")
        @ValidName(min = 4)
        String fullName;
    }

    static class DigitsDto {
        @FieldName("Code")
        @Digits(integer = 3, fraction = 1)
        String code;
    }

    @DateBefore(first = "a", second = "b", pattern = "yyyy-MM-dd", maxDistance = 10, precision = DatePrecision.HOURS)
    static class DateBeforeDistanceForm {
        @FieldName("A start")
        String a;

        @FieldName("B end")
        String b;
    }
}
