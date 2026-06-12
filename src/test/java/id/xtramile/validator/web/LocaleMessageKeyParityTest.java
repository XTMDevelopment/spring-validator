package id.xtramile.validator.web;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class LocaleMessageKeyParityTest {

    @Test
    void englishAndIndonesianBundlesHaveSameKeys() throws IOException {
        Set<String> enKeys = loadKeys("messages_en.properties");
        Set<String> idKeys = loadKeys("messages_id.properties");

        Set<String> onlyInEn = new TreeSet<>(enKeys);
        onlyInEn.removeAll(idKeys);

        Set<String> onlyInId = new TreeSet<>(idKeys);
        onlyInId.removeAll(enKeys);

        assertThat(onlyInEn)
                .as("Keys in messages_en.properties missing from messages_id.properties: %s", onlyInEn)
                .isEmpty();
        assertThat(onlyInId)
                .as("Keys in messages_id.properties missing from messages_en.properties: %s", onlyInId)
                .isEmpty();
    }

    private Set<String> loadKeys(String resourceName) throws IOException {
        Properties props = new Properties();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourceName)) {
            assertThat(is).as("Resource %s should exist on classpath", resourceName).isNotNull();
            props.load(is);
        }

        return props.stringPropertyNames().stream()
                .filter(key -> !key.startsWith("#") && !key.trim().isEmpty())
                .collect(Collectors.toSet());
    }
}
