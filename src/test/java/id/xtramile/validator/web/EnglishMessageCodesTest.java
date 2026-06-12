package id.xtramile.validator.web;

import id.xtramile.validator.support.LocaleMessageCodesTestSupport;

import java.util.List;

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
    protected List<FormattedMessageCase> formattedMessageCases() {
        return List.of(
                new FormattedMessageCase("validation.spring.not-null", new Object[]{"email"}, "email is required"),
                new FormattedMessageCase("validation.spring.size", new Object[]{"password", 8, 20},
                        "password must be between 8 and 20 characters"),
                new FormattedMessageCase("validation.common.in-whitelist", new Object[]{"status", "active,inactive"},
                        "status must be one of: active,inactive"),
                new FormattedMessageCase("validation.data.tax-id.length", new Object[]{"taxId", "15-16"},
                        "taxId must have 15-16 digits"),
                new FormattedMessageCase("validation.datetime.invalid-past-date", new Object[]{"date"},
                        "date must be a date equals today"),
                new FormattedMessageCase("validation.datetime.invalid-future-date", new Object[]{"date"},
                        "date must be a date equals today")
        );
    }
}
