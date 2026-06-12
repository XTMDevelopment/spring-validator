package id.xtramile.validator.web;

import id.xtramile.validator.web.messages.CompositeConstraintMessageResolver;
import jakarta.validation.ConstraintViolation;
import org.springframework.util.StringUtils;
import org.springframework.validation.FieldError;

import java.util.Map;
import java.util.Objects;

public class FriendlyMessageResolver {

    private static final String VALIDATION_DEFAULT = "validation.default";

    private final MessageResourceResolver messageResolver;
    private final ValidationFieldDisplayNames fieldNames;
    private final ValidationMessageArgsBuilder messageArgsBuilder;
    private final CompositeConstraintMessageResolver annotationMessages;

    public FriendlyMessageResolver(
            MessageResourceResolver messageResolver,
            ValidationFieldDisplayNames fieldNames,
            ValidationMessageArgsBuilder messageArgsBuilder,
            CompositeConstraintMessageResolver annotationMessages) {
        this.messageResolver = messageResolver;
        this.fieldNames = fieldNames;
        this.messageArgsBuilder = messageArgsBuilder;
        this.annotationMessages = annotationMessages;
    }

    public FriendlyMessageResolver(MessageResourceResolver messageResolver) {
        this(messageResolver, defaultCollaborators(messageResolver));
    }

    private FriendlyMessageResolver(MessageResourceResolver messageResolver, DefaultCollaborators collaborators) {
        this(
                messageResolver,
                collaborators.fieldNames(),
                collaborators.messageArgsBuilder(),
                collaborators.annotationMessages()
        );
    }

    private static DefaultCollaborators defaultCollaborators(MessageResourceResolver messageResolver) {
        ValidationFieldDisplayNames fieldNames = new ValidationFieldDisplayNames();
        return new DefaultCollaborators(
                fieldNames,
                new ValidationMessageArgsBuilder(fieldNames),
                new CompositeConstraintMessageResolver(messageResolver, fieldNames)
        );
    }

    public String resolve(ConstraintViolation<?> v, String field, Class<?> dtoClass) {
        String template = v.getMessageTemplate();

        if (template != null && template.startsWith("validation.")) {
            String displayName = fieldNames.resolve(dtoClass, field);
            Map<String, Object> attrs = v.getConstraintDescriptor().getAttributes();

            Object[] args = messageArgsBuilder.buildMessageArgs(displayName, attrs, template, dtoClass);
            String resolved = messageResolver.getMessage(template, args);

            if (!resolved.equals(template)) {
                return resolved;
            }

            return messageResolver.getMessage(VALIDATION_DEFAULT, displayName);
        }

        if (!BeanValidationMessageDescriptors.isDefaultTemplate(template)) {
            return v.getMessage();
        }

        var ann = v.getConstraintDescriptor().getAnnotation();
        Class<?> type = ann.annotationType();
        Map<String, Object> attrs = v.getConstraintDescriptor().getAttributes();

        String displayName = fieldNames.resolve(dtoClass, field);

        return annotationMessages.resolveFromAnnotation(displayName, type, attrs, dtoClass);
    }

    public String resolve(FieldError err, Class<?> dtoClass) {
        String field = err.getField();
        String defaultMessage = err.getDefaultMessage();

        String annotationName = err.getCode();
        Class<?> annotationType = resolveAnnotationType(annotationName);
        Map<String, Object> attrs = null;

        if (annotationType != null && dtoClass != null) {
            attrs = messageArgsBuilder.constraintAttributesForFieldError(dtoClass, field, annotationType);
        }

        String displayName = fieldNames.resolve(dtoClass, field);

        String validationTemplate = "";
        if (StringUtils.hasText(defaultMessage)) {
            validationTemplate = Objects.requireNonNull(defaultMessage);
        }

        if (validationTemplate.startsWith("validation.") && attrs != null && !attrs.isEmpty()) {
            Object[] args = messageArgsBuilder.buildMessageArgs(displayName, attrs, validationTemplate, dtoClass);
            String resolved = messageResolver.getMessage(validationTemplate, args);

            if (!resolved.equals(validationTemplate)) {
                return resolved;
            }
        }

        boolean resolveFromAnnotation = !StringUtils.hasText(defaultMessage)
                || BeanValidationMessageDescriptors.isFriendlyDefault(defaultMessage)
                || BeanValidationMessageDescriptors.isDefaultTemplate(defaultMessage)
                || validationTemplate.startsWith("validation.")
                || BeanValidationMessageDescriptors.shouldResolveFieldErrorUsingConstraintDefaults(attrs);

        if (resolveFromAnnotation && annotationType != null && attrs != null) {
            return annotationMessages.resolveFromAnnotation(displayName, annotationType, attrs, dtoClass);
        }

        return StringUtils.hasText(defaultMessage) ? defaultMessage : messageResolver.getMessage(VALIDATION_DEFAULT, displayName);
    }

    private Class<?> resolveAnnotationType(String annotationName) {
        return AnnotationRegistry.resolve(annotationName);
    }

    private record DefaultCollaborators(
            ValidationFieldDisplayNames fieldNames,
            ValidationMessageArgsBuilder messageArgsBuilder,
            CompositeConstraintMessageResolver annotationMessages) {
    }
}
