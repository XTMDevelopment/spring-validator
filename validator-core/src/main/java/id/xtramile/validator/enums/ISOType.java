package id.xtramile.validator.enums;

/**
 * ISO code categories validated by {@code @ValidISOCode}.
 */
public enum ISOType {
    /**
     * ISO 4217 currency code.
     */
    CURRENCY,
    /**
     * ISO 3166-1 alpha-2 country code.
     */
    COUNTRY_ALPHA2,
    /**
     * ISO 3166-1 alpha-3 country code.
     */
    COUNTRY_ALPHA3,
    /**
     * ISO 639-1 language code.
     */
    LANGUAGE
}
