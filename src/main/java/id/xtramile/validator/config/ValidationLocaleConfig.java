package id.xtramile.validator.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "id.xtramile.validator")
public class ValidationLocaleConfig {
    private String locale = "id";

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale != null ? locale.toLowerCase() : "id";
    }
}
