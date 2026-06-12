package id.xtramile.validator.web;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test to verify all Indonesian message codes are properly defined and resolvable.
 */
class IndonesianMessageCodesTest {

    @Test
    void shouldLoadAllIndonesianMessages() throws IOException {
        Properties props = new Properties();
        try (InputStream is = getClass().getClassLoader()
                .getResourceAsStream("messages_id.properties")) {
            assertThat(is).isNotNull();
            props.load(is);
        }

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
    void shouldResolveAllIndonesianMessages() throws IOException {
        MessageResourceResolver resolver = new MessageResourceResolver("id");
        Properties props = new Properties();
        
        try (InputStream is = getClass().getClassLoader()
                .getResourceAsStream("messages_id.properties")) {
            props.load(is);
        }

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
        Properties props = new Properties();
        
        try (InputStream is = getClass().getClassLoader()
                .getResourceAsStream("messages_id.properties")) {
            props.load(is);
        }

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

    @Test
    void shouldFormatMessagesCorrectly() {
        MessageResourceResolver resolver = new MessageResourceResolver("id");

        String message1 = resolver.getMessage("validation.spring.not-null", "email");
        assertThat(message1).isEqualTo("email wajib diisi");

        String message2 = resolver.getMessage("validation.spring.size", "password", 8, 20);
        assertThat(message2).isEqualTo("password harus antara 8 dan 20 karakter");

        String message3 = resolver.getMessage("validation.common.in-whitelist", "status", "aktif,nonaktif");
        assertThat(message3).isEqualTo("status harus salah satu dari: aktif,nonaktif");

        String message4 = resolver.getMessage("validation.data.tax-id.length", "taxId", "15-16");
        assertThat(message4).isEqualTo("taxId harus memiliki 15-16 digit");

        String message5 = resolver.getMessage("validation.datetime.invalid-past-date", "tanggal");
        assertThat(message5).isEqualTo("tanggal harus berupa tanggal yang sama dengan hari ini");

        String message6 = resolver.getMessage("validation.datetime.invalid-future-date", "tanggal");
        assertThat(message6).isEqualTo("tanggal harus berupa tanggal yang sama dengan hari ini");
    }

    /**
     * Counts the number of placeholders ({0}, {1}, etc.) in a message string.
     */
    private int countPlaceholders(String message) {
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

                } catch (NumberFormatException ignored) {}

                index = endIndex + 1;

            } else {
                break;
            }
        }

        return maxIndex + 1;
    }
}
