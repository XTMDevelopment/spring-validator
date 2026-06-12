package id.xtramile.validator.web;

import id.xtramile.validator.enums.Group;
import id.xtramile.validator.util.MessageUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Properties;

/**
 * Loads and formats validation messages from classpath {@code messages_&lt;locale&gt;.properties} files.
 */
public class MessageResourceResolver {

    private static final String MESSAGES_BASE = "messages_";
    private static final String DEFAULT_LOCALE = "id";

    private final Properties properties;
    private final Properties fallbackProperties;

    /**
     * Creates a resolver for the given locale, falling back to Indonesian if needed.
     *
     * @param locale the locale code (e.g. {@code "en"}), or {@code null} for default
     */
    public MessageResourceResolver(String locale) {
        String loc = locale != null ? locale.toLowerCase() : DEFAULT_LOCALE;

        this.properties = loadProperties(loc);
        this.fallbackProperties = loc.equals(DEFAULT_LOCALE) ? null : loadProperties(DEFAULT_LOCALE);
    }

    /**
     * Returns a formatted message for the given key.
     *
     * @param key  the message key
     * @param args optional {@link java.text.MessageFormat} arguments
     * @return the formatted message, or the key itself if not found
     */
    public String getMessage(String key, Object... args) {
        String message = properties.getProperty(key);
        if (message == null) {
            if (fallbackProperties != null) {
                message = fallbackProperties.getProperty(key);
            }

            if (message == null) {
                return key;
            }
        }

        if (args != null && args.length > 0) {
            try {
                return MessageFormat.format(message, args);

            } catch (Exception e) {
                return message;
            }
        }

        return message;
    }

    /**
     * Returns a formatted message for a validation group and sub-key.
     *
     * @param group the validation message group
     * @param key   the sub-key within the group
     * @param args  optional format arguments
     * @return the formatted message
     */
    public String getMessage(Group group, String key, Object... args) {
        String fullKey = MessageUtils.buildKey(group, key);
        return getMessage(fullKey, args);
    }

    private Properties loadProperties(String locale) {
        Properties props = new Properties();
        String resourceName = MESSAGES_BASE + locale + ".properties";

        try {
            Resource resource = new ClassPathResource(resourceName);
            if (resource.exists()) {
                try (InputStream is = resource.getInputStream()) {
                    props.load(is);
                }
            }

        } catch (IOException ignored) {
        }

        return props;
    }
}
