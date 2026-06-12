package id.xtramile.validator.enums;

import id.xtramile.validator.util.MessageUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

class GroupEnumTest {

    @Test
    void eachGroupMapsToExpectedValidationKeyPrefix() throws IOException {
        Properties messages = loadMessages();

        for (Group group : Group.values()) {
            String prefix = "validation." + group.name().toLowerCase() + ".";

            assertThat(MessageUtils.buildKey(group, "sample"))
                    .as("MessageUtils.buildKey for %s", group)
                    .isEqualTo(prefix + "sample");

            boolean hasMessageKey = messages.stringPropertyNames().stream()
                    .anyMatch(key -> key.startsWith(prefix));

            assertThat(hasMessageKey)
                    .as("Expected at least one message key with prefix %s", prefix)
                    .isTrue();
        }
    }

    private Properties loadMessages() throws IOException {
        Properties props = new Properties();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("messages_en.properties")) {
            assertThat(is).isNotNull();
            props.load(is);
        }
        return props;
    }
}
