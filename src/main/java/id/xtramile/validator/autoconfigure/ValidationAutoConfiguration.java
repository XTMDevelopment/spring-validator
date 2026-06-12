package id.xtramile.validator.autoconfigure;

import id.xtramile.validator.config.ValidationLocaleConfig;
import id.xtramile.validator.web.*;
import id.xtramile.validator.web.messages.CompositeConstraintMessageResolver;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Spring Boot auto-configuration for validation exception handling and message resolution.
 */
@AutoConfiguration
@ConditionalOnProperty(
        prefix = "id.xtramile.validator",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
@EnableConfigurationProperties(ValidationLocaleConfig.class)
public class ValidationAutoConfiguration {

    /**
     * Provides the default error envelope builder.
     *
     * @return the error envelope builder bean
     */
    @Bean
    @ConditionalOnMissingBean(ErrorEnvelopeBuilder.class)
    public ErrorEnvelopeBuilder errorEnvelopeBuilder() {
        return new DefaultErrorEnvelopeBuilder();
    }

    /**
     * Provides the locale-aware message resource resolver.
     *
     * @param localeConfig the locale configuration properties
     * @return the message resource resolver bean
     */
    @Bean
    @ConditionalOnMissingBean(MessageResourceResolver.class)
    public MessageResourceResolver messageResourceResolver(ValidationLocaleConfig localeConfig) {
        return new MessageResourceResolver(localeConfig.getLocale());
    }

    /**
     * Provides the field display name resolver.
     *
     * @return the field display names bean
     */
    @Bean
    @ConditionalOnMissingBean(ValidationFieldDisplayNames.class)
    public ValidationFieldDisplayNames validationFieldDisplayNames() {
        return new ValidationFieldDisplayNames();
    }

    /**
     * Provides the validation message argument builder.
     *
     * @param fieldNames the field display name resolver
     * @return the message args builder bean
     */
    @Bean
    @ConditionalOnMissingBean(ValidationMessageArgsBuilder.class)
    public ValidationMessageArgsBuilder validationMessageArgsBuilder(ValidationFieldDisplayNames fieldNames) {
        return new ValidationMessageArgsBuilder(fieldNames);
    }

    /**
     * Provides the composite constraint message resolver.
     *
     * @param messageResourceResolver the message resource resolver
     * @param fieldNames              the field display name resolver
     * @return the composite constraint message resolver bean
     */
    @Bean
    @ConditionalOnMissingBean(CompositeConstraintMessageResolver.class)
    public CompositeConstraintMessageResolver compositeConstraintMessageResolver(
            MessageResourceResolver messageResourceResolver,
            ValidationFieldDisplayNames fieldNames) {
        return new CompositeConstraintMessageResolver(messageResourceResolver, fieldNames);
    }

    /**
     * Provides the friendly validation message resolver.
     *
     * @param messageResourceResolver          the message resource resolver
     * @param fieldNames                       the field display name resolver
     * @param messageArgsBuilder               the message argument builder
     * @param compositeConstraintMessageResolver the composite constraint message resolver
     * @return the friendly message resolver bean
     */
    @Bean
    @ConditionalOnMissingBean(FriendlyMessageResolver.class)
    public FriendlyMessageResolver friendlyMessageResolver(
            MessageResourceResolver messageResourceResolver,
            ValidationFieldDisplayNames fieldNames,
            ValidationMessageArgsBuilder messageArgsBuilder,
            CompositeConstraintMessageResolver compositeConstraintMessageResolver) {
        return new FriendlyMessageResolver(
                messageResourceResolver,
                fieldNames,
                messageArgsBuilder,
                compositeConstraintMessageResolver
        );
    }

    /**
     * Provides the global API exception handler.
     *
     * @param builder          the error envelope builder
     * @param messageResolver  the friendly message resolver
     * @return the API exception handler bean
     */
    @Bean
    @ConditionalOnMissingBean(ApiExceptionHandler.class)
    public ApiExceptionHandler apiExceptionHandler(ErrorEnvelopeBuilder builder, FriendlyMessageResolver messageResolver) {
        return new ApiExceptionHandler(builder, messageResolver);
    }
}
