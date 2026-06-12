package id.xtramile.validator.web;

import id.xtramile.validator.enums.Group;
import id.xtramile.validator.util.MessageUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Properties;

public class MessageResourceResolver {

    private static final String MESSAGES_BASE = "messages_";
    private static final String DEFAULT_LOCALE = "id";

    private final Properties properties;
    private final String locale;

    public MessageResourceResolver(String locale) {
        this.locale = locale != null ? locale.toLowerCase() : DEFAULT_LOCALE;
        this.properties = loadProperties(this.locale);
    }

    public String getMessage(String key, Object... args) {
        String message = properties.getProperty(key);
        if (message == null) {
            if (!locale.equals(DEFAULT_LOCALE)) {
                Properties defaultProps = loadProperties(DEFAULT_LOCALE);
                message = defaultProps.getProperty(key);
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

        } catch (IOException ignored) {}

        return props;
    }
}
