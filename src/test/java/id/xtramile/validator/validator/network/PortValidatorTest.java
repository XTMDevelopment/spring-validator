package id.xtramile.validator.validator.network;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PortValidatorTest {

    private PortValidator validator;

    @BeforeEach
    void setUp() {
        validator = new PortValidator();
    }

    @Test
    void testValidPortNumbers() {
        assertTrue(validator.isValid(1, null)); // Minimum valid port
        assertTrue(validator.isValid(80, null)); // HTTP port
        assertTrue(validator.isValid(443, null)); // HTTPS port
        assertTrue(validator.isValid(22, null)); // SSH port
        assertTrue(validator.isValid(25, null)); // SMTP port
        assertTrue(validator.isValid(53, null)); // DNS port
        assertTrue(validator.isValid(110, null)); // POP3 port
        assertTrue(validator.isValid(143, null)); // IMAP port
        assertTrue(validator.isValid(993, null)); // IMAPS port
        assertTrue(validator.isValid(995, null)); // POP3S port
        assertTrue(validator.isValid(3306, null)); // MySQL port
        assertTrue(validator.isValid(5432, null)); // PostgreSQL port
        assertTrue(validator.isValid(6379, null)); // Redis port
        assertTrue(validator.isValid(8080, null)); // Alternative HTTP port
        assertTrue(validator.isValid(65535, null)); // Maximum valid port
    }

    @Test
    void testValidPortStrings() {
        assertTrue(validator.isValid("1", null)); // Minimum valid port
        assertTrue(validator.isValid("80", null)); // HTTP port
        assertTrue(validator.isValid("443", null)); // HTTPS port
        assertTrue(validator.isValid("22", null)); // SSH port
        assertTrue(validator.isValid("25", null)); // SMTP port
        assertTrue(validator.isValid("53", null)); // DNS port
        assertTrue(validator.isValid("110", null)); // POP3 port
        assertTrue(validator.isValid("143", null)); // IMAP port
        assertTrue(validator.isValid("993", null)); // IMAPS port
        assertTrue(validator.isValid("995", null)); // POP3S port
        assertTrue(validator.isValid("3306", null)); // MySQL port
        assertTrue(validator.isValid("5432", null)); // PostgreSQL port
        assertTrue(validator.isValid("6379", null)); // Redis port
        assertTrue(validator.isValid("8080", null)); // Alternative HTTP port
        assertTrue(validator.isValid("65535", null)); // Maximum valid port
    }

    @Test
    void testValidPortNumbersAsIntegers() {
        assertTrue(validator.isValid(1, null)); // Minimum valid port
        assertTrue(validator.isValid(80, null)); // HTTP port
        assertTrue(validator.isValid(443, null)); // HTTPS port
        assertTrue(validator.isValid(22, null)); // SSH port
        assertTrue(validator.isValid(25, null)); // SMTP port
        assertTrue(validator.isValid(53, null)); // DNS port
        assertTrue(validator.isValid(110, null)); // POP3 port
        assertTrue(validator.isValid(143, null)); // IMAP port
        assertTrue(validator.isValid(993, null)); // IMAPS port
        assertTrue(validator.isValid(995, null)); // POP3S port
        assertTrue(validator.isValid(3306, null)); // MySQL port
        assertTrue(validator.isValid(5432, null)); // PostgreSQL port
        assertTrue(validator.isValid(6379, null)); // Redis port
        assertTrue(validator.isValid(8080, null)); // Alternative HTTP port
        assertTrue(validator.isValid(65535, null)); // Maximum valid port
    }

    @Test
    void testInvalidPortNumbers() {
        assertFalse(validator.isValid(0, null)); // Zero port
        assertFalse(validator.isValid(-1, null)); // Negative port
        assertFalse(validator.isValid(-100, null)); // Large negative port
        assertFalse(validator.isValid(65536, null)); // Above maximum
        assertFalse(validator.isValid(65537, null)); // Above maximum
        assertFalse(validator.isValid(100000, null)); // Way above maximum
        assertFalse(validator.isValid(Integer.MAX_VALUE, null)); // Maximum integer
        assertFalse(validator.isValid(Integer.MIN_VALUE, null)); // Minimum integer
    }

    @Test
    void testInvalidPortStrings() {
        assertFalse(validator.isValid("0", null)); // Zero port
        assertFalse(validator.isValid("-1", null)); // Negative port
        assertFalse(validator.isValid("-100", null)); // Large negative port
        assertFalse(validator.isValid("65536", null)); // Above maximum
        assertFalse(validator.isValid("65537", null)); // Above maximum
        assertFalse(validator.isValid("100000", null)); // Way above maximum
        assertFalse(validator.isValid("999999", null)); // Very large number
    }

    @Test
    void testInvalidPortFormats() {
        assertFalse(validator.isValid("abc", null)); // Non-numeric string
        assertFalse(validator.isValid("80.5", null)); // Decimal number
        assertFalse(validator.isValid("80,5", null)); // Comma decimal
        assertFalse(validator.isValid("80 80", null)); // Space in number
        assertFalse(validator.isValid("80-80", null)); // Hyphen in number
        assertFalse(validator.isValid("80:80", null)); // Colon in number
        assertFalse(validator.isValid("80#80", null)); // Hash in number
        assertFalse(validator.isValid("80@80", null)); // At symbol in number
    }

    @Test
    void testSpecialCharacters() {
        assertFalse(validator.isValid("80+", null)); // Plus sign
        assertFalse(validator.isValid("80-", null)); // Minus sign
        assertFalse(validator.isValid("80%", null)); // Percentage
        assertFalse(validator.isValid("80#", null)); // Hash symbol
        assertFalse(validator.isValid("80@", null)); // At symbol
        assertFalse(validator.isValid("80&", null)); // Ampersand
        assertFalse(validator.isValid("80*", null)); // Asterisk
    }

    @Test
    void testWhitespaceHandling() {
        assertTrue(validator.isValid(" 80 ", null)); // Leading and trailing spaces
        assertTrue(validator.isValid("\t80\t", null)); // Tabs
        assertTrue(validator.isValid("\n80\n", null)); // Newlines
        assertTrue(validator.isValid("  80  ", null)); // Multiple spaces
    }

    @Test
    void testEdgeCaseValues() {
        assertTrue(validator.isValid(1, null)); // Minimum boundary
        assertTrue(validator.isValid(65535, null)); // Maximum boundary
        assertFalse(validator.isValid(0, null)); // Just below minimum
        assertFalse(validator.isValid(65536, null)); // Just above maximum
        assertFalse(validator.isValid(-1, null)); // Just below minimum
        assertFalse(validator.isValid(65537, null)); // Just above maximum
    }

    @Test
    void testRealWorldPorts() {
        assertTrue(validator.isValid(21, null)); // FTP
        assertTrue(validator.isValid(22, null)); // SSH
        assertTrue(validator.isValid(23, null)); // Telnet
        assertTrue(validator.isValid(25, null)); // SMTP
        assertTrue(validator.isValid(53, null)); // DNS
        assertTrue(validator.isValid(67, null)); // DHCP
        assertTrue(validator.isValid(68, null)); // DHCP
        assertTrue(validator.isValid(80, null)); // HTTP
        assertTrue(validator.isValid(110, null)); // POP3
        assertTrue(validator.isValid(143, null)); // IMAP
        assertTrue(validator.isValid(443, null)); // HTTPS
        assertTrue(validator.isValid(993, null)); // IMAPS
        assertTrue(validator.isValid(995, null)); // POP3S
        assertTrue(validator.isValid(3306, null)); // MySQL
        assertTrue(validator.isValid(5432, null)); // PostgreSQL
        assertTrue(validator.isValid(6379, null)); // Redis
        assertTrue(validator.isValid(8080, null)); // Alternative HTTP
        assertTrue(validator.isValid(8443, null)); // Alternative HTTPS
    }

    @Test
    void testWellKnownPorts() {
        assertTrue(validator.isValid(1, null)); // TCPMUX
        assertTrue(validator.isValid(7, null)); // Echo
        assertTrue(validator.isValid(9, null)); // Discard
        assertTrue(validator.isValid(13, null)); // Daytime
        assertTrue(validator.isValid(17, null)); // Quote of the Day
        assertTrue(validator.isValid(19, null)); // Character Generator
        assertTrue(validator.isValid(20, null)); // FTP Data
        assertTrue(validator.isValid(21, null)); // FTP Control
        assertTrue(validator.isValid(22, null)); // SSH
        assertTrue(validator.isValid(23, null)); // Telnet
        assertTrue(validator.isValid(25, null)); // SMTP
        assertTrue(validator.isValid(53, null)); // DNS
        assertTrue(validator.isValid(67, null)); // DHCP
        assertTrue(validator.isValid(68, null)); // DHCP
        assertTrue(validator.isValid(80, null)); // HTTP
        assertTrue(validator.isValid(110, null)); // POP3
        assertTrue(validator.isValid(143, null)); // IMAP
        assertTrue(validator.isValid(443, null)); // HTTPS
        assertTrue(validator.isValid(993, null)); // IMAPS
        assertTrue(validator.isValid(995, null)); // POP3S
    }

    @Test
    void testRegisteredPorts() {
        assertTrue(validator.isValid(1024, null)); // Start of registered ports
        assertTrue(validator.isValid(2048, null)); // Registered port
        assertTrue(validator.isValid(4096, null)); // Registered port
        assertTrue(validator.isValid(8192, null)); // Registered port
        assertTrue(validator.isValid(16384, null)); // Registered port
        assertTrue(validator.isValid(32768, null)); // Registered port
        assertTrue(validator.isValid(49151, null)); // End of registered ports
    }

    @Test
    void testDynamicPorts() {
        assertTrue(validator.isValid(49152, null)); // Start of dynamic ports
        assertTrue(validator.isValid(50000, null)); // Dynamic port
        assertTrue(validator.isValid(55000, null)); // Dynamic port
        assertTrue(validator.isValid(60000, null)); // Dynamic port
        assertTrue(validator.isValid(65000, null)); // Dynamic port
        assertTrue(validator.isValid(65535, null)); // End of dynamic ports
    }

    @Test
    void testInvalidObjectTypes() {
        assertTrue(validator.isValid(null, null)); // Null value
        assertTrue(validator.isValid("", null)); // Empty string
        assertTrue(validator.isValid("   ", null)); // Whitespace string
        assertTrue(validator.isValid(new Object(), null)); // Non-numeric object
        assertTrue(validator.isValid(Boolean.TRUE, null)); // Boolean object
        assertTrue(validator.isValid(new java.util.Date(), null)); // Date object
    }
}
