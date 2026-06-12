package id.xtramile.validator.enums;

/**
 * API response codes and bilingual descriptions for error envelopes.
 */
public enum ResponseType {
    /**
     * Validation failure response.
     */
    VALIDATION_FAILED(98, "Validation failed", "Validasi gagal"),
    /**
     * Unhandled or server-side error response.
     */
    UNKNOWN_ERROR(99, "Unknown error", "Error tidak diketahui"),
    ;

    private final int messageCode;
    private final String descriptionEn;
    private final String descriptionId;

    ResponseType(int messageCode, String descriptionEn, String descriptionId) {
        this.messageCode = messageCode;
        this.descriptionEn = descriptionEn;
        this.descriptionId = descriptionId;
    }

    /**
     * Returns the numeric API message code.
     *
     * @return the message code
     */
    public int getMessageCode() {
        return messageCode;
    }

    /**
     * Returns the English description.
     *
     * @return the English description
     */
    public String getDescriptionEn() {
        return descriptionEn;
    }

    /**
     * Returns the Indonesian description.
     *
     * @return the Indonesian description
     */
    public String getDescriptionId() {
        return descriptionId;
    }
}
