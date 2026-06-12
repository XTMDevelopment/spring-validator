package id.xtramile.validator.integration;

import id.xtramile.validator.annotation.common.FieldName;
import id.xtramile.validator.annotation.common.InWhitelist;
import id.xtramile.validator.web.FriendlyMessageResolver;
import id.xtramile.validator.web.MessageResourceResolver;
import id.xtramile.validator.web.ValidationAnnotationTypeRegistry;
import id.xtramile.validator.web.ValidationFieldDisplayNames;
import id.xtramile.validator.web.ValidationMessageArgsBuilder;
import id.xtramile.validator.web.messages.CompositeConstraintMessageResolver;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
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
 * using a real Jakarta {@link Validator} and Spring {@link FieldError} binding.
 */
class ValidationWebComponentsCollaborationIntegrationTest {

    private static Validator VALIDATOR;
    private static ValidationFieldDisplayNames FIELD_NAMES;
    private static CompositeConstraintMessageResolver ANNOTATION_MESSAGES;
    private static FriendlyMessageResolver RESOLVER;

    @BeforeAll
    static void initCollaborators() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        VALIDATOR = factory.getValidator();

        MessageResourceResolver MESSAGES = new MessageResourceResolver("en");
        FIELD_NAMES = new ValidationFieldDisplayNames();

        ValidationMessageArgsBuilder MESSAGE_ARGS_BUILDER = new ValidationMessageArgsBuilder(FIELD_NAMES);
        ANNOTATION_MESSAGES = new CompositeConstraintMessageResolver(MESSAGES, FIELD_NAMES);
        RESOLVER = new FriendlyMessageResolver(MESSAGES, FIELD_NAMES, MESSAGE_ARGS_BUILDER, ANNOTATION_MESSAGES);
    }

    @Test
    void constraintViolation_resolvesValidationTemplateWithFieldName() {
        MultiViolationDto dto = new MultiViolationDto("", "bad");

        Set<ConstraintViolation<MultiViolationDto>> violations = VALIDATOR.validate(dto);
        assertThat(violations).isNotEmpty();

        for (ConstraintViolation<MultiViolationDto> v : violations) {
            String path = v.getPropertyPath() == null ? "request" : v.getPropertyPath().toString();
            String resolved = RESOLVER.resolve(v, path, MultiViolationDto.class);

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

        String fromFieldError = RESOLVER.resolve(fe, MapLikeDto.class);

        ConstraintViolation<MapLikeDto> cv = VALIDATOR.validate(MapLikeDto.withInvalidChannel()).iterator().next();
        String fromConstraint = RESOLVER.resolve(cv, "channel", MapLikeDto.class);

        assertThat(fromFieldError).isEqualTo(fromConstraint);
    }

    @Test
    void compositeResolver_resolvesFriendlyDefaultTemplateForNotBlank() {
        NotBlankOnlyDto dto = new NotBlankOnlyDto("");
        ConstraintViolation<NotBlankOnlyDto> violation = VALIDATOR.validate(dto).stream()
                .filter(v -> v.getConstraintDescriptor().getAnnotation().annotationType() == NotBlank.class)
                .findFirst()
                .orElseThrow();

        String field = violation.getPropertyPath().toString();
        String displayName = FIELD_NAMES.resolve(NotBlankOnlyDto.class, field);
        String fromComposite = ANNOTATION_MESSAGES.resolveFromAnnotation(
                displayName,
                NotBlank.class,
                violation.getConstraintDescriptor().getAttributes(),
                NotBlankOnlyDto.class
        );
        String fromFriendlyResolver = RESOLVER.resolve(violation, field, NotBlankOnlyDto.class);

        assertThat(fromComposite).isEqualTo(fromFriendlyResolver);
        assertThat(fromComposite).doesNotStartWith("validation.");
        assertThat(fromComposite).isNotBlank();
    }

    @Test
    void validationAnnotationTypeRegistry_matchesValidatorConstraintCodes() {
        MultiViolationDto dto = new MultiViolationDto("", "bad");
        for (ConstraintViolation<MultiViolationDto> v : VALIDATOR.validate(dto)) {
            String code = v.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName();
            assertThat(ValidationAnnotationTypeRegistry.resolve(code))
                    .isEqualTo(v.getConstraintDescriptor().getAnnotation().annotationType());
        }
    }

    @Test
    void fieldDisplayNames_component_matchesFriendlyResolverBehaviour() {
        assertThat(FIELD_NAMES.resolve(MultiViolationDto.class, "channel"))
                .isEqualTo("Channel label");
    }

    public record MultiViolationDto(
            @FieldName("Channel label") @InWhitelist(values = {"sms", "email"}) @NotBlank String channel,
            @FieldName("Code") @NotBlank String code) {
            public MultiViolationDto(String channel, String code) {
                this.channel = channel;
                this.code = code;
            }

            @Override
            public String channel() {
                return channel;
            }

            @Override
            public String code() {
                return code;
            }
        }

    record NotBlankOnlyDto(@FieldName("Email") @NotBlank String email) {
            NotBlankOnlyDto(String email) {
                this.email = email;
            }

            @Override
            public String email() {
                return email;
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
