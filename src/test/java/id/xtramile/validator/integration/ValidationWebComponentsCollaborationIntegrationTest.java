package id.xtramile.validator.integration;

import id.xtramile.validator.annotation.common.FieldName;
import id.xtramile.validator.annotation.common.InWhitelist;
import id.xtramile.validator.web.FriendlyMessageResolver;
import id.xtramile.validator.web.MessageResourceResolver;
import id.xtramile.validator.web.ValidationAnnotationTypeRegistry;
import id.xtramile.validator.web.ValidationFieldDisplayNames;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link FriendlyMessageResolver} and the extracted helpers
 * ({@link ValidationFieldDisplayNames}, message-args building, {@link ValidationAnnotationTypeRegistry})
 * using a real Jakarta {@link Validator} and Spring
 * {@link FieldError} binding.
 */
class ValidationWebComponentsCollaborationIntegrationTest {

    private static Validator validator;
    private static FriendlyMessageResolver friendlyResolver;

    @BeforeAll
    static void initValidator() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();

        MessageResourceResolver enMessages = new MessageResourceResolver("en");
        friendlyResolver = new FriendlyMessageResolver(enMessages);
    }

    @Test
    void constraintViolation_resolvesValidationTemplateWithFieldName() {
        MultiViolationDto dto = new MultiViolationDto("", "bad");

        Set<ConstraintViolation<MultiViolationDto>> violations = validator.validate(dto);
        assertThat(violations).isNotEmpty();

        for (ConstraintViolation<MultiViolationDto> v : violations) {
            String path = v.getPropertyPath() == null ? "request" : v.getPropertyPath().toString();
            String resolved = friendlyResolver.resolve(v, path, MultiViolationDto.class);

            String template = v.getMessageTemplate();
            if (template != null && template.startsWith("validation.")) {
                assertThat(resolved).isNotEqualTo(template);
                assertThat(resolved).doesNotStartWith("validation.");
            }
        }
    }

    @Test
    void fieldError_sameTemplateAsConstraintViolation_resolvesIdenticallyForInWhitelist() {
        MapLikeDto dto = new MapLikeDto();
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(dto, "dto");
        errors.rejectValue("channel", "InWhitelist", null, "validation.common.in-whitelist");

        FieldError fe = errors.getFieldError("channel");
        assertThat(fe).isNotNull();

        String fromFieldError = friendlyResolver.resolve(fe, MapLikeDto.class);

        ConstraintViolation<MapLikeDto> cv = validator.validate(MapLikeDto.withInvalidChannel()).iterator().next();
        String fromConstraint = friendlyResolver.resolve(cv, "channel", MapLikeDto.class);

        assertThat(fromFieldError).isEqualTo(fromConstraint);
    }

    @Test
    void validationAnnotationTypeRegistry_matchesValidatorConstraintCodes() {
        MultiViolationDto dto = new MultiViolationDto("", "bad");
        for (ConstraintViolation<MultiViolationDto> v : validator.validate(dto)) {
            String code = v.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName();
            assertThat(ValidationAnnotationTypeRegistry.resolve(code))
                    .isEqualTo(v.getConstraintDescriptor().getAnnotation().annotationType());
        }
    }

    @Test
    void fieldDisplayNames_component_matchesFriendlyResolverBehaviour() {
        ValidationFieldDisplayNames names = new ValidationFieldDisplayNames();
        assertThat(names.resolve(MultiViolationDto.class, "channel"))
                .isEqualTo("Channel label");
    }

    public static class MultiViolationDto {
        @FieldName("Channel label")
        @InWhitelist(values = {"sms", "email"})
        @NotBlank
        private final String channel;

        @FieldName("Code")
        @NotBlank
        private final String code;

        public MultiViolationDto(String channel, String code) {
            this.channel = channel;
            this.code = code;
        }

        public String channel() {
            return channel;
        }

        public String code() {
            return code;
        }
    }

    static class MapLikeDto {
        @FieldName("Channel label")
        @InWhitelist(values = {"sms", "email"})
        private String channel = "invalid";

        public String getChannel() {
            return channel;
        }

        public void setChannel(String channel) {
            this.channel = channel;
        }

        static MapLikeDto withInvalidChannel() {
            MapLikeDto d = new MapLikeDto();
            d.setChannel("invalid");
            return d;
        }
    }
}
