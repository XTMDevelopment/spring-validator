package id.xtramile.validator.web;

import id.xtramile.validator.support.LocaleMessageCodesTestSupport;
import static org.assertj.core.api.Assertions.assertThat;

class EnglishMessageCodesTest extends LocaleMessageCodesTestSupport {

    @Override
    protected String locale() {
        return "en";
    }

    @Override
    protected String resourceName() {
        return "messages_en.properties";
    }

    @Override
    protected void assertFormattedMessages(MessageResourceResolver resolver) {
        assertThat(resolver.getMessage("validation.spring.not-null", "email"))
                .isEqualTo("email is required");

        assertThat(resolver.getMessage("validation.spring.size", "password", 8, 20))
                .isEqualTo("password must be between 8 and 20 characters");

        assertThat(resolver.getMessage("validation.common.in-whitelist", "status", "active,inactive"))
                .isEqualTo("status must be one of: active,inactive");

        assertThat(resolver.getMessage("validation.data.tax-id.length", "taxId", "15-16"))
                .isEqualTo("taxId must have 15-16 digits");

        assertThat(resolver.getMessage("validation.datetime.invalid-past-date", "date"))
                .isEqualTo("date must be a date equals today");

        assertThat(resolver.getMessage("validation.datetime.invalid-future-date", "date"))
                .isEqualTo("date must be a date equals today");
    }

}
