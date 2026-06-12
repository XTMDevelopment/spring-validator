package id.xtramile.validator.autoconfigure;

import id.xtramile.validator.config.ValidationLocaleConfig;
import id.xtramile.validator.web.*;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnProperty(
        prefix = "id.xtramile.validator",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
@EnableConfigurationProperties(ValidationLocaleConfig.class)
public class ValidationAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(ErrorEnvelopeBuilder.class)
    public ErrorEnvelopeBuilder errorEnvelopeBuilder() {
        return new DefaultErrorEnvelopeBuilder();
    }

    @Bean
    @ConditionalOnMissingBean(MessageResourceResolver.class)
    public MessageResourceResolver messageResourceResolver(ValidationLocaleConfig localeConfig) {
        return new MessageResourceResolver(localeConfig.getLocale());
    }

    @Bean
    @ConditionalOnMissingBean(FriendlyMessageResolver.class)
    public FriendlyMessageResolver friendlyMessageResolver(MessageResourceResolver messageResourceResolver) {
        return new FriendlyMessageResolver(messageResourceResolver);
    }

    @Bean
    @ConditionalOnMissingBean(ApiExceptionHandler.class)
    public ApiExceptionHandler apiExceptionHandler(ErrorEnvelopeBuilder builder, FriendlyMessageResolver messageResolver) {
        return new ApiExceptionHandler(builder, messageResolver);
    }
}
