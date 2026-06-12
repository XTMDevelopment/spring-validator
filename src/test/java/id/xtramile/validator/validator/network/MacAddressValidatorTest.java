package id.xtramile.validator.validator.network;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MacAddressValidatorTest {

    private MacAddressValidator validator;

    @BeforeEach
    void setUp() {
        validator = new MacAddressValidator();
    }

    @Test
    void testValidMacAddressesWithHyphens() {
        assertTrue(validator.isValid("00:11:22:33:44:55", null)); // Standard format
        assertTrue(validator.isValid("aa:bb:cc:dd:ee:ff", null)); // Lowercase hex
        assertTrue(validator.isValid("AA:BB:CC:DD:EE:FF", null)); // Uppercase hex
        assertTrue(validator.isValid("Aa:Bb:Cc:Dd:Ee:Ff", null)); // Mixed case hex
        assertTrue(validator.isValid("00:00:00:00:00:00", null)); // All zeros
        assertTrue(validator.isValid("ff:ff:ff:ff:ff:ff", null)); // All Fs
    }

    @Test
    void testValidMacAddressesWithColons() {
        assertTrue(validator.isValid("00-11-22-33-44-55", null)); // Hyphen format
        assertTrue(validator.isValid("aa-bb-cc-dd-ee-ff", null)); // Lowercase hex
        assertTrue(validator.isValid("AA-BB-CC-DD-EE-FF", null)); // Uppercase hex
        assertTrue(validator.isValid("Aa-Bb-Cc-Dd-Ee-Ff", null)); // Mixed case hex
        assertTrue(validator.isValid("00-00-00-00-00-00", null)); // All zeros
        assertTrue(validator.isValid("ff-ff-ff-ff-ff-ff", null)); // All Fs
    }

    @Test
    void testValidMacAddressesMixedCase() {
        assertTrue(validator.isValid("00:11:22:33:44:55", null)); // Colon format
        assertTrue(validator.isValid("aa:bb:cc:dd:ee:ff", null)); // Lowercase
        assertTrue(validator.isValid("AA:BB:CC:DD:EE:FF", null)); // Uppercase
        assertTrue(validator.isValid("Aa:Bb:Cc:Dd:Ee:Ff", null)); // Mixed case
        assertTrue(validator.isValid("00:00:00:00:00:00", null)); // All zeros
        assertTrue(validator.isValid("ff:ff:ff:ff:ff:ff", null)); // All Fs
    }

    @Test
    void testInvalidMacAddressFormats() {
        assertFalse(validator.isValid("00:11:22:33:44", null)); // Too few octets
        assertFalse(validator.isValid("00:11:22:33:44:55:66", null)); // Too many octets
        assertFalse(validator.isValid("00:11:22:33:44:55:66:77", null)); // Too many octets
        assertFalse(validator.isValid("00:11:22:33:44:55:66:77:88", null)); // Too many octets
        assertFalse(validator.isValid("00:11:22:33:44:55:66:77:88:99", null)); // Too many octets
    }

    @Test
    void testInvalidMacAddressSeparators() {
        assertFalse(validator.isValid("00.11.22.33.44.55", null)); // Dot separator
        assertFalse(validator.isValid("00 11 22 33 44 55", null)); // Space separator
        assertFalse(validator.isValid("00_11_22_33_44_55", null)); // Underscore separator
        assertFalse(validator.isValid("001122334455", null)); // No separator
        assertFalse(validator.isValid("00|11|22|33|44|55", null)); // Pipe separator
        assertFalse(validator.isValid("00;11;22;33;44;55", null)); // Semicolon separator
    }

    @Test
    void testInvalidMacAddressCharacters() {
        assertFalse(validator.isValid("00:11:22:33:44:gg", null)); // Invalid hex character
        assertFalse(validator.isValid("gg:11:22:33:44:55", null)); // Invalid hex character
        assertFalse(validator.isValid("00:11:22:33:44:z", null)); // Invalid hex character
        assertFalse(validator.isValid("z:11:22:33:44:55", null)); // Invalid hex character
        assertFalse(validator.isValid("00:11:22:33:44:@", null)); // Special character
        assertFalse(validator.isValid("@:11:22:33:44:55", null)); // Special character
    }

    @Test
    void testInvalidMacAddressLength() {
        assertFalse(validator.isValid("0:11:22:33:44:55", null)); // Single digit octet
        assertFalse(validator.isValid("000:11:22:33:44:55", null)); // Triple digit octet
        assertFalse(validator.isValid("00:1:22:33:44:55", null)); // Single digit octet
        assertFalse(validator.isValid("00:111:22:33:44:55", null)); // Triple digit octet
        assertFalse(validator.isValid("00:11:2:33:44:55", null)); // Single digit octet
        assertFalse(validator.isValid("00:11:222:33:44:55", null)); // Triple digit octet
    }

    @Test
    void testSpecialCharacters() {
        assertFalse(validator.isValid("00:11:22:33:44:55+", null)); // Plus sign
        assertFalse(validator.isValid("00:11:22:33:44:55-", null)); // Minus sign
        assertFalse(validator.isValid("00:11:22:33:44:55%", null)); // Percentage
        assertFalse(validator.isValid("00:11:22:33:44:55#", null)); // Hash symbol
        assertFalse(validator.isValid("00:11:22:33:44:55@", null)); // At symbol
        assertFalse(validator.isValid("00:11:22:33:44:55&", null)); // Ampersand
        assertFalse(validator.isValid("00:11:22:33:44:55*", null)); // Asterisk
    }

    @Test
    void testWhitespaceHandling() {
        assertTrue(validator.isValid(" 00:11:22:33:44:55 ", null)); // Leading and trailing spaces
        assertTrue(validator.isValid("\t00:11:22:33:44:55\t", null)); // Tabs
        assertTrue(validator.isValid("\n00:11:22:33:44:55\n", null)); // Newlines
        assertTrue(validator.isValid("  00:11:22:33:44:55  ", null)); // Multiple spaces
    }

    @Test
    void testEdgeCaseValues() {
        assertTrue(validator.isValid("00:00:00:00:00:00", null)); // All zeros
        assertTrue(validator.isValid("ff:ff:ff:ff:ff:ff", null)); // All Fs
        assertTrue(validator.isValid("01:23:45:67:89:ab", null)); // Mixed values
        assertTrue(validator.isValid("fe:dc:ba:98:76:54", null)); // Mixed values
        assertTrue(validator.isValid("12:34:56:78:9a:bc", null)); // Mixed values
    }

    @Test
    void testRealWorldExamples() {
        assertTrue(validator.isValid("00:1b:44:11:3a:b7", null)); // Real MAC address
        assertTrue(validator.isValid("08:00:27:12:34:56", null)); // VirtualBox MAC
        assertTrue(validator.isValid("52:54:00:12:34:56", null)); // QEMU MAC
        assertTrue(validator.isValid("00:50:56:12:34:56", null)); // VMware MAC
        assertTrue(validator.isValid("00:15:5d:12:34:56", null)); // Hyper-V MAC
    }

    @Test
    void testMulticastAddresses() {
        assertTrue(validator.isValid("01:00:5e:12:34:56", null)); // Multicast MAC
        assertTrue(validator.isValid("33:33:12:34:56:78", null)); // IPv6 multicast MAC
        assertTrue(validator.isValid("01:80:c2:00:00:00", null)); // Bridge multicast MAC
        assertTrue(validator.isValid("01:00:5e:00:00:01", null)); // All systems multicast
    }

    @Test
    void testBroadcastAddresses() {
        assertTrue(validator.isValid("ff:ff:ff:ff:ff:ff", null)); // Broadcast MAC
        assertTrue(validator.isValid("00:00:00:00:00:00", null)); // All zeros MAC
        assertTrue(validator.isValid("01:00:5e:7f:ff:ff", null)); // Multicast range
    }

    @Test
    void testInvalidHexValues() {
        assertFalse(validator.isValid("gg:11:22:33:44:55", null)); // Invalid hex
        assertFalse(validator.isValid("00:gg:22:33:44:55", null)); // Invalid hex
        assertFalse(validator.isValid("00:11:gg:33:44:55", null)); // Invalid hex
        assertFalse(validator.isValid("00:11:22:gg:44:55", null)); // Invalid hex
        assertFalse(validator.isValid("00:11:22:33:gg:55", null)); // Invalid hex
        assertFalse(validator.isValid("00:11:22:33:44:gg", null)); // Invalid hex
    }

    @Test
    void testCaseInsensitive() {
        assertTrue(validator.isValid("00:11:22:33:44:55", null)); // Lowercase
        assertTrue(validator.isValid("00:11:22:33:44:55", null)); // Uppercase
        assertTrue(validator.isValid("00:11:22:33:44:55", null)); // Mixed case
        assertTrue(validator.isValid("AA:BB:CC:DD:EE:FF", null)); // All uppercase
        assertTrue(validator.isValid("aa:bb:cc:dd:ee:ff", null)); // All lowercase
        assertTrue(validator.isValid("Aa:Bb:Cc:Dd:Ee:Ff", null)); // Mixed case
    }
}
