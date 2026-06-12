package id.xtramile.validator.enums;

public enum ResponseType {
    VALIDATION_FAILED(98, "Validation failed", "Validasi gagal"),
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

    public int getMessageCode() {
        return messageCode;
    }

    public String getDescriptionEn() {
        return descriptionEn;
    }

    public String getDescriptionId() {
        return descriptionId;
    }
}
