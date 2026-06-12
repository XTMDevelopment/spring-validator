package id.xtramile.validator.web;

import id.xtramile.validator.annotation.common.FieldName;
import id.xtramile.validator.web.messages.CompositeConstraintMessageResolver;
import id.xtramile.validator.annotation.common.InWhitelist;
import id.xtramile.validator.annotation.datetime.DateBefore;
import id.xtramile.validator.annotation.datetime.ValidDate;
import id.xtramile.validator.enums.DatePrecision;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ConstraintAnnotationMessagesTest {

    private MessageResourceResolver messages;
    private CompositeConstraintMessageResolver resolver;

    @BeforeEach
    void setUp() {
        messages = new MessageResourceResolver("en");
        
        ValidationFieldDisplayNames fieldNames = new ValidationFieldDisplayNames();
        resolver = new CompositeConstraintMessageResolver(messages, fieldNames);
    }

    @Test
    void resolveFromAnnotation_nullAnnotationType_usesValidationDefault() {
        String out = resolver.resolveFromAnnotation("Any", null, new HashMap<>(), Object.class);
        assertThat(out).isEqualTo(messages.getMessage("validation.default", "Any"));
    }

    @Test
    void resolveFromAnnotation_springNotBlank() {
        Map<String, Object> attrs = new HashMap<>();
        attrs.put("message", "{jakarta.validation.constraints.NotBlank.message}");
        attrs.put("groups", new Class[0]);
        attrs.put("payload", new Class[0]);

        String out = resolver.resolveFromAnnotation("Username", NotBlank.class, attrs, Object.class);
        assertThat(out).isEqualTo("Username is required");
    }

    @Test
    void resolveFromAnnotation_inWhitelist() {
        Map<String, Object> attrs = new HashMap<>();
        attrs.put("values", new String[]{"on", "off"});
        attrs.put("ignoreCase", true);
        attrs.put("message", "{friendly.default}");
        attrs.put("groups", new Class[0]);
        attrs.put("payload", new Class[0]);

        String out = resolver.resolveFromAnnotation("Mode", InWhitelist.class, attrs, Object.class);
        assertThat(out).isEqualTo("Mode must be one of: off, on");
    }

    @Test
    void resolveFromAnnotation_validDateWithPattern() {
        Map<String, Object> attrs = new HashMap<>();
        attrs.put("pattern", "MM/dd/yyyy");
        attrs.put("message", "{friendly.default}");
        attrs.put("groups", new Class[0]);
        attrs.put("payload", new Class[0]);

        String out = resolver.resolveFromAnnotation("Birth", ValidDate.class, attrs, Object.class);
        assertThat(out).isEqualTo("Birth must be a valid date in format: MM/dd/yyyy");
    }

    @Test
    void resolveFromAnnotation_dateBeforeUsesDisplayNames() {
        Map<String, Object> attrs = new HashMap<>();
        attrs.put("first", "start");
        attrs.put("second", "end");
        attrs.put("pattern", "yyyy-MM-dd");
        attrs.put("maxDistance", -1L);
        attrs.put("precision", DatePrecision.DAYS);
        attrs.put("message", "{friendly.default}");
        attrs.put("groups", new Class[0]);
        attrs.put("payload", new Class[0]);

        String out = resolver.resolveFromAnnotation("ignored", DateBefore.class, attrs, DateBeforeDto.class);
        assertThat(out).isEqualTo("Period start must be before Period end");
    }

    @Test
    void resolveFromAnnotation_springConstraintsWithNullAttrs_useDefaults() {
        assertThat(resolver.resolveFromAnnotation("Age", Min.class, null, Object.class))
                .isEqualTo("Age must be at least 0");
        assertThat(resolver.resolveFromAnnotation("Age", Max.class, null, Object.class))
                .startsWith("Age must be at most ");
        assertThat(resolver.resolveFromAnnotation("Price", DecimalMin.class, null, Object.class))
                .isEqualTo("Price must be at least 0");
        assertThat(resolver.resolveFromAnnotation("Price", DecimalMax.class, null, Object.class))
                .isEqualTo("Price must be at most 0");
        assertThat(resolver.resolveFromAnnotation("Amount", Digits.class, null, Object.class))
                .isEqualTo("Amount must have at most 0 integer digits and at most 0 fractional digits");
        assertThat(resolver.resolveFromAnnotation("Nick", Size.class, null, Object.class))
                .startsWith("Nick must be at most ");
    }

    @Test
    void resolveFromAnnotation_sizeRange() {
        Map<String, Object> attrs = new HashMap<>();
        attrs.put("min", 2);
        attrs.put("max", 10);
        attrs.put("message", "{jakarta.validation.constraints.Size.message}");
        attrs.put("groups", new Class[0]);
        attrs.put("payload", new Class[0]);

        String out = resolver.resolveFromAnnotation("Nick", Size.class, attrs, Object.class);
        assertThat(out).isEqualTo("Nick must be between 2 and 10 characters");
    }

    static class DateBeforeDto {
        @FieldName("Period start")
        String start;

        @FieldName("Period end")
        String end;
    }
}
