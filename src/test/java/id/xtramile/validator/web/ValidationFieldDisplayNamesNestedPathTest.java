package id.xtramile.validator.web;

import id.xtramile.validator.annotation.common.FieldName;
import id.xtramile.validator.support.ValidationMessageTestSupport;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration coverage for dot-path field display names through the full resolver chain,
 * complementing unit tests on {@link ValidationFieldDisplayNames} and {@link id.xtramile.validator.util.AnnotationUtils}.
 */
class ValidationFieldDisplayNamesNestedPathTest {

    private static final ValidationMessageTestSupport SUPPORT = ValidationMessageTestSupport.EN;

    @Test
    void constraintViolation_nestedPath_usesInnerFieldDisplayName() {
        NestedRootDto dto = new NestedRootDto(new InnerDto(""));

        ConstraintViolation<NestedRootDto> violation = SUPPORT.validator().validate(dto).iterator().next();

        assertThat(violation.getPropertyPath().toString()).isEqualTo("inner.email");

        String resolved = SUPPORT.resolver().resolve(violation, "inner.email", NestedRootDto.class);

        assertThat(resolved).isEqualTo(SUPPORT.messages().getMessage("validation.spring.not-blank", "Email Address"));
        assertThat(resolved).contains("Email Address");
        assertThat(resolved).doesNotContain("validation.");
    }

    @Test
    void fieldError_nestedPath_usesInnerFieldDisplayNameAndLeafConstraintAttrs() {
        NestedSizeRootDto dto = new NestedSizeRootDto();
        InnerSizeDto inner = new InnerSizeDto();
        inner.setEmail("this-email-is-way-too-long-for-the-maximum-allowed");
        dto.setInner(inner);

        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(dto, "dto");
        errors.rejectValue("inner.email", "Size", null, "jakarta.validation.constraints.Size.message");

        FieldError fieldError = errors.getFieldError("inner.email");
        assertThat(fieldError).isNotNull();

        String resolved = SUPPORT.resolver().resolve(fieldError, NestedSizeRootDto.class);

        assertThat(resolved).isEqualTo(SUPPORT.messages().getMessage("validation.spring.size.max", "Email Address", 40));
        assertThat(resolved).contains("Email Address");
    }

    record NestedRootDto(@Valid InnerDto inner) {
            NestedRootDto(InnerDto inner) {
                this.inner = inner;
            }

            @Override
            public InnerDto inner() {
                return inner;
            }
        }

    record InnerDto(@FieldName("Email Address") @NotBlank String email) {
            InnerDto(String email) {
                this.email = email;
            }

            @Override
            public String email() {
                return email;
            }
        }

    static class NestedSizeRootDto {
        @Valid
        private InnerSizeDto inner;

        public InnerSizeDto getInner() {
            return inner;
        }

        public void setInner(InnerSizeDto inner) {
            this.inner = inner;
        }
    }

    static class InnerSizeDto {
        @FieldName("Email Address")
        @Size(max = 40)
        private String email;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
}
