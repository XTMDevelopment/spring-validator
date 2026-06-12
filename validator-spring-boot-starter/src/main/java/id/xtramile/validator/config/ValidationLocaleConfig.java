package id.xtramile.validator.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for validation message locale.
 * <p>
 * Bound from {@code id.xtramile.validator.locale}; defaults to Indonesian ({@code id}).
 */
@ConfigurationProperties(prefix = "id.xtramile.validator")
public class ValidationLocaleConfig {

    private String locale = "id";

    /**
     * Returns the configured message locale code.
     *
     * @return locale code such as {@code id} or {@code en}
     */
    public String getLocale() {
        return locale;
    }

    /**
     * Sets the message locale code; null values fall back to {@code id}.
     *
     * @param locale locale code to use
     */
    public void setLocale(String locale) {
        this.locale = locale != null ? locale.toLowerCase() : "id";
    }
}
