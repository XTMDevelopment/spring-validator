package id.xtramile.validator.web.messages;

import id.xtramile.validator.web.MessageResourceResolver;
import id.xtramile.validator.web.ValidationFieldDisplayNames;

import java.util.List;
import java.util.Map;

/**
 * Delegates constraint message resolution to group-specific resolvers.
 */
public class CompositeConstraintMessageResolver {

    private static final String VALIDATION_DEFAULT = "validation.default";

    private final MessageResourceResolver messageResolver;
    private final List<ConstraintMessageResolver> resolvers;

    /**
     * Creates a composite resolver with all built-in group resolvers.
     *
     * @param messageResolver loads localized message templates
     * @param fieldNames      resolves display names for cross-field constraints
     */
    public CompositeConstraintMessageResolver(MessageResourceResolver messageResolver, ValidationFieldDisplayNames fieldNames) {
        this.messageResolver = messageResolver;
        this.resolvers = List.of(
                new SpringBuiltinConstraintMessages(messageResolver),
                new CommonConstraintMessages(messageResolver),
                new ContactConstraintMessages(messageResolver),
                new CrossConstraintMessages(messageResolver, fieldNames),
                new DataConstraintMessages(messageResolver),
                new DateTimeConstraintMessages(messageResolver, fieldNames),
                new FileConstraintMessages(messageResolver),
                new FinanceConstraintMessages(messageResolver),
                new KycConstraintMessages(messageResolver),
                new LocationConstraintMessages(messageResolver),
                new NetworkConstraintMessages(messageResolver)
        );
    }

    /**
     * Resolves a message for the given constraint annotation and attributes.
     *
     * @param field          display name of the validated field
     * @param annotationType the constraint annotation class
     * @param attrs          constraint annotation attributes
     * @param dtoClass       the validated DTO class
     * @return the resolved message, or a default validation message
     */
    public String resolveFromAnnotation(String field, Class<?> annotationType, Map<String, Object> attrs, Class<?> dtoClass) {
        if (annotationType == null) {
            return messageResolver.getMessage(VALIDATION_DEFAULT, field);
        }

        for (ConstraintMessageResolver resolver : resolvers) {
            if (resolver.supports(annotationType)) {
                String message = resolver.resolve(field, annotationType, attrs, dtoClass);
                if (message != null) {
                    return message;
                }
            }
        }

        return messageResolver.getMessage(VALIDATION_DEFAULT, field);
    }
}
