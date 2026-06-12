package id.xtramile.validator.web;

import id.xtramile.validator.annotation.common.FieldName;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ValidationFieldDisplayNamesTest {

    private ValidationFieldDisplayNames resolver;

    @BeforeEach
    void setUp() {
        resolver = new ValidationFieldDisplayNames();
    }

    @Test
    void returnsNullWhenFieldNameNull() {
        assertThat(resolver.resolve(SimpleDto.class, null)).isNull();
    }

    @Test
    void returnsFieldNameWhenDtoClassNull() {
        assertThat(resolver.resolve(null, "x")).isEqualTo("x");
    }

    @Test
    void returnsFieldNameWhenNoFieldNameAnnotation() {
        assertThat(resolver.resolve(SimpleDto.class, "plain")).isEqualTo("plain");
    }

    @Test
    void returnsFieldNameLabelWhenAnnotated() {
        assertThat(resolver.resolve(SimpleDto.class, "labeled")).isEqualTo("Display label");
    }

    @Test
    void resolvesNestedPathToInnerFieldLabel() {
        assertThat(resolver.resolve(NestedRootDto.class, "inner.email"))
                .isEqualTo("Email Address");
    }

    @Test
    void returnsFullPathWhenIntermediateSegmentMissing() {
        assertThat(resolver.resolve(SimpleDto.class, "missing.field")).isEqualTo("missing.field");
    }

    static class SimpleDto {
        String plain;

        @FieldName("Display label")
        String labeled;
    }

    static class NestedRootDto {
        InnerDto inner;
    }

    static class InnerDto {
        @FieldName("Email Address")
        @NotBlank
        String email;
    }
}
