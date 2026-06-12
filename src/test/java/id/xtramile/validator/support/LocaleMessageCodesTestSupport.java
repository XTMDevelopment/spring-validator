package id.xtramile.validator.support;

import id.xtramile.validator.web.MessageResourceResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class LocaleMessageCodesTestSupport {

    protected static int countPlaceholders(String message) {
        if (message == null || message.isEmpty()) {
            return 0;
        }

        int maxIndex = -1;
        int index = 0;

        while ((index = message.indexOf("{", index)) != -1) {
            int endIndex = message.indexOf("}", index);
            if (endIndex != -1) {
                try {
                    String placeholder = message.substring(index + 1, endIndex);
                    int placeholderIndex = Integer.parseInt(placeholder);
                    maxIndex = Math.max(maxIndex, placeholderIndex);
                } catch (NumberFormatException ignored) {
                    // skip non-numeric placeholders
                }
                index = endIndex + 1;
            } else {
                break;
            }
        }

        return maxIndex + 1;
    }

    protected abstract String locale();

    protected abstract String resourceName();

    protected abstract List<FormattedMessageCase> formattedMessageCases();

    Stream<FormattedMessageCase> formattedMessageCasesStream() {
        return formattedMessageCases().stream();
    }

    @Test
    void shouldLoadAllMessages() throws IOException {
        Properties props = loadProperties();
        assertThat(props).isNotEmpty();

        Set<String> keys = props.stringPropertyNames();
        assertThat(keys).isNotEmpty();

        for (String key : keys) {
            if (!key.startsWith("#") && !key.trim().isEmpty()) {
                assertThat(key).startsWith("validation.");
            }
        }
    }

    @Test
    void shouldResolveAllMessages() throws IOException {
        MessageResourceResolver resolver = new MessageResourceResolver(locale());
        Properties props = loadProperties();

        for (String key : props.stringPropertyNames()) {
            if (key.startsWith("#") || key.trim().isEmpty()) {
                continue;
            }

            String message = props.getProperty(key);
            assertThat(message).isNotNull();

            if (message.trim().isEmpty()) {
                continue;
            }

            int placeholderCount = countPlaceholders(message);

            if (placeholderCount == 0) {
                String resolved = resolver.getMessage(key);
                assertThat(resolved).isNotNull().isNotEmpty();
                assertThat(resolved).isNotEqualTo(key);
            } else {
                Object[] args = new Object[placeholderCount];
                for (int i = 0; i < placeholderCount; i++) {
                    args[i] = "test" + i;
                }

                String resolved = resolver.getMessage(key, args);
                assertThat(resolved).isNotNull().isNotEmpty();
                assertThat(resolved).isNotEqualTo(key);

                for (int i = 0; i < placeholderCount; i++) {
                    assertThat(resolved).contains("test" + i);
                }
            }
        }
    }

    @Test
    void shouldHaveAllRequiredMessageKeys() throws IOException {
        Properties props = loadProperties();

        Set<String> keys = props.stringPropertyNames().stream()
                .filter(k -> !k.startsWith("#") && !k.trim().isEmpty())
                .collect(Collectors.toSet());

        assertThat(keys).contains("validation.default");
        assertThat(keys).anyMatch(k -> k.startsWith("validation.common."));
        assertThat(keys).anyMatch(k -> k.startsWith("validation.contact."));
        assertThat(keys).anyMatch(k -> k.startsWith("validation.cross."));
        assertThat(keys).anyMatch(k -> k.startsWith("validation.data."));
        assertThat(keys).anyMatch(k -> k.startsWith("validation.datetime."));
        assertThat(keys).anyMatch(k -> k.startsWith("validation.file."));
        assertThat(keys).anyMatch(k -> k.startsWith("validation.finance."));
        assertThat(keys).anyMatch(k -> k.startsWith("validation.kyc."));
        assertThat(keys).anyMatch(k -> k.startsWith("validation.location."));
        assertThat(keys).anyMatch(k -> k.startsWith("validation.network."));
        assertThat(keys).anyMatch(k -> k.startsWith("validation.spring."));
    }

    @ParameterizedTest
    @MethodSource("formattedMessageCasesStream")
    void shouldFormatMessageCorrectly(LocaleMessageCodesTestSupport.FormattedMessageCase testCase) {
        MessageResourceResolver resolver = new MessageResourceResolver(locale());
        assertThat(resolver.getMessage(testCase.key(), testCase.args()))
                .isEqualTo(testCase.expected());
    }

    private Properties loadProperties() throws IOException {
        Properties props = new Properties();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourceName())) {
            assertThat(is).isNotNull();
            props.load(is);
        }
        return props;
    }

    public record FormattedMessageCase(String key, Object[] args, String expected) {
    }
}
