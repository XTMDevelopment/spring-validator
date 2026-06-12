package id.xtramile.validator.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ValidationLocaleConfigTest {

    @Test
    void testDefaultLocale() {
        ValidationLocaleConfig config = new ValidationLocaleConfig();
        assertEquals("id", config.getLocale());
    }

    @Test
    void testSetLocale() {
        ValidationLocaleConfig config = new ValidationLocaleConfig();
        config.setLocale("en");
        assertEquals("en", config.getLocale());
    }

    @Test
    void testSetLocaleLowerCase() {
        ValidationLocaleConfig config = new ValidationLocaleConfig();
        config.setLocale("EN");
        assertEquals("en", config.getLocale());
    }

    @Test
    void testSetLocaleNull() {
        ValidationLocaleConfig config = new ValidationLocaleConfig();
        config.setLocale(null);
        assertEquals("id", config.getLocale());
    }

    @Test
    void testSetLocaleMixedCase() {
        ValidationLocaleConfig config = new ValidationLocaleConfig();
        config.setLocale("En");
        assertEquals("en", config.getLocale());
    }

    @Test
    void testGetLocaleAfterMultipleSets() {
        ValidationLocaleConfig config = new ValidationLocaleConfig();
        config.setLocale("en");
        assertEquals("en", config.getLocale());

        config.setLocale("id");
        assertEquals("id", config.getLocale());

        config.setLocale("fr");
        assertEquals("fr", config.getLocale());
    }
}
