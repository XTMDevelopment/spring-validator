package id.xtramile.validator.enums;

/**
 * Password complexity rules for {@code @ValidPassword}.
 */
public enum PasswordType {
    /** No complexity requirement. */
    ANY,
    /** Letters and digits required. */
    ALPHANUMERIC,
    /** At least one letter and one digit. */
    LETTER_DIGIT,
    /** Upper and lower case letters required. */
    LETTER_MIXED_CASE,
    /** Letters, mixed case, digits, and symbols. */
    FULL,
    /** At least three of four character classes. */
    STRONG_3_OF_4
}
