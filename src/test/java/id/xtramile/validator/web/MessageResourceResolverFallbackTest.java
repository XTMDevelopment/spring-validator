package id.xtramile.validator.web;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MessageResourceResolverFallbackTest {

    @Test
    void missingKeyInUnknownLocaleFallsBackToDefaultLocale() {
        MessageResourceResolver resolver = new MessageResourceResolver("fr");

        assertThat(resolver.getMessage("validation.default", "email"))
                .isEqualTo("email tidak valid");
    }

    @Test
    void unknownLocaleFallsBackToSameMessageAsDefaultLocale() {
        MessageResourceResolver unknownLocale = new MessageResourceResolver("fr");
        MessageResourceResolver defaultLocale = new MessageResourceResolver("id");

        assertThat(unknownLocale.getMessage("validation.spring.not-null", "email"))
                .isEqualTo(defaultLocale.getMessage("validation.spring.not-null", "email"));
    }

    @Test
    void missingKeyInBothLocalesReturnsKey() {
        MessageResourceResolver resolver = new MessageResourceResolver("en");
        String missingKey = "validation.nonexistent.key.xyz";

        assertThat(resolver.getMessage(missingKey)).isEqualTo(missingKey);
    }

    @Test
    void messageFormatErrorReturnsUnformattedMessage() {
        MessageResourceResolver resolver = new MessageResourceResolver("en");
        String template = resolver.getMessage("validation.spring.not-null");
        Object failingArg = new Object() {
            @Override
            public String toString() {
                throw new RuntimeException("format failure");
            }
        };

        String result = resolver.getMessage("validation.spring.not-null", failingArg);

        assertThat(result).isEqualTo(template);
        assertThat(result).isEqualTo("{0} is required");
    }
}
