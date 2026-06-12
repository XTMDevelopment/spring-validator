package id.xtramile.validator.validator.network;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IPv4AddressValidatorTest {

    private IPv4AddressValidator validator;

    @BeforeEach
    void setUp() {
        validator = new IPv4AddressValidator();
    }

    @Test
    void testValidIPv4Addresses() {
        assertTrue(validator.isValid("192.168.1.1", null)); // Private IPv4
        assertTrue(validator.isValid("8.8.8.8", null)); // Google DNS
        assertTrue(validator.isValid("127.0.0.1", null)); // Loopback
        assertTrue(validator.isValid("0.0.0.0", null)); // All zeros
        assertTrue(validator.isValid("255.255.255.255", null)); // Broadcast
        assertTrue(validator.isValid("10.0.0.1", null)); // Private network
        assertTrue(validator.isValid("172.16.0.1", null)); // Private network
        assertTrue(validator.isValid("203.0.113.1", null)); // Test network
    }

    @Test
    void testValidBoundaryValues() {
        assertTrue(validator.isValid("0.0.0.0", null)); // Minimum values
        assertTrue(validator.isValid("255.255.255.255", null)); // Maximum values
        assertTrue(validator.isValid("1.1.1.1", null)); // All ones
        assertTrue(validator.isValid("254.254.254.254", null)); // All 254s
        assertTrue(validator.isValid("128.128.128.128", null)); // All 128s
    }

    @Test
    void testInvalidOctetValues() {
        assertFalse(validator.isValid("256.1.1.1", null)); // Octet > 255
        assertFalse(validator.isValid("1.256.1.1", null)); // Octet > 255
        assertFalse(validator.isValid("1.1.256.1", null)); // Octet > 255
        assertFalse(validator.isValid("1.1.1.256", null)); // Octet > 255
        assertFalse(validator.isValid("300.1.1.1", null)); // Octet > 255
        assertFalse(validator.isValid("1000.1.1.1", null)); // Octet > 255
        assertFalse(validator.isValid("999.1.1.1", null)); // Octet > 255
    }

    @Test
    void testInvalidFormat() {
        assertFalse(validator.isValid("192.168.1", null)); // Missing octet
        assertFalse(validator.isValid("192.168.1.1.1", null)); // Extra octet
        assertFalse(validator.isValid("192.168.1.", null)); // Trailing dot
        assertFalse(validator.isValid(".192.168.1.1", null)); // Leading dot
        assertFalse(validator.isValid("192.168.1.1.", null)); // Trailing dot
        assertFalse(validator.isValid("192.168.1.1.1.1", null)); // Too many octets
        assertFalse(validator.isValid("192.168", null)); // Too few octets
        assertFalse(validator.isValid("192", null)); // Single octet
    }

    @Test
    void testInvalidCharacters() {
        assertFalse(validator.isValid("192.168.1.a", null)); // Letter in octet
        assertFalse(validator.isValid("a.168.1.1", null)); // Letter in octet
        assertFalse(validator.isValid("192.a.1.1", null)); // Letter in octet
        assertFalse(validator.isValid("192.168.a.1", null)); // Letter in octet
        assertFalse(validator.isValid("192.168.1.1a", null)); // Letter after octet
        assertFalse(validator.isValid("a192.168.1.1", null)); // Letter before octet
    }

    @Test
    void testSpecialCharacters() {
        assertFalse(validator.isValid("192.168.1.1+", null)); // Plus sign
        assertFalse(validator.isValid("192.168.1.1-", null)); // Minus sign
        assertFalse(validator.isValid("192.168.1.1%", null)); // Percentage
        assertFalse(validator.isValid("192.168.1.1#", null)); // Hash symbol
        assertFalse(validator.isValid("192.168.1.1@", null)); // At symbol
        assertFalse(validator.isValid("192.168.1.1&", null)); // Ampersand
        assertFalse(validator.isValid("192.168.1.1*", null)); // Asterisk
    }

    @Test
    void testWhitespaceHandling() {
        assertTrue(validator.isValid(" 192.168.1.1 ", null)); // Leading and trailing spaces
        assertTrue(validator.isValid("\t192.168.1.1\t", null)); // Tabs
        assertTrue(validator.isValid("\n192.168.1.1\n", null)); // Newlines
        assertTrue(validator.isValid("  192.168.1.1  ", null)); // Multiple spaces
    }

    @Test
    void testEmptyOctets() {
        assertFalse(validator.isValid("192.168..1", null)); // Empty octet
        assertFalse(validator.isValid("192..168.1", null)); // Empty octet
        assertFalse(validator.isValid(".192.168.1", null)); // Empty first octet
        assertFalse(validator.isValid("192.168.1.", null)); // Empty last octet
        assertFalse(validator.isValid("192.168.1.1.", null)); // Empty last octet
    }

    @Test
    void testLeadingZeros() {
        assertTrue(validator.isValid("192.168.001.001", null)); // Leading zeros
        assertTrue(validator.isValid("192.168.01.01", null)); // Leading zeros
        assertTrue(validator.isValid("192.168.1.01", null)); // Leading zeros
        assertTrue(validator.isValid("192.168.01.1", null)); // Leading zeros
        assertTrue(validator.isValid("001.001.001.001", null)); // All leading zeros
    }

    @Test
    void testNegativeValues() {
        assertFalse(validator.isValid("-1.1.1.1", null)); // Negative octet
        assertFalse(validator.isValid("1.-1.1.1", null)); // Negative octet
        assertFalse(validator.isValid("1.1.-1.1", null)); // Negative octet
        assertFalse(validator.isValid("1.1.1.-1", null)); // Negative octet
        assertFalse(validator.isValid("-192.168.1.1", null)); // Negative first octet
    }

    @Test
    void testDecimalValues() {
        assertFalse(validator.isValid("192.168.1.1.5", null)); // Decimal in octet
        assertFalse(validator.isValid("192.5.168.1.1", null)); // Decimal in octet
        assertFalse(validator.isValid("192.168.1.5.1", null)); // Decimal in octet
        assertFalse(validator.isValid("192.168.1.1.5", null)); // Decimal in octet
    }

    @Test
    void testRealWorldExamples() {
        assertTrue(validator.isValid("8.8.8.8", null)); // Google DNS
        assertTrue(validator.isValid("1.1.1.1", null)); // Cloudflare DNS
        assertTrue(validator.isValid("208.67.222.222", null)); // OpenDNS
        assertTrue(validator.isValid("9.9.9.9", null)); // Quad9 DNS
        assertTrue(validator.isValid("76.76.19.19", null)); // Control D DNS
    }

    @Test
    void testPrivateNetworks() {
        assertTrue(validator.isValid("10.0.0.1", null)); // Class A private
        assertTrue(validator.isValid("172.16.0.1", null)); // Class B private
        assertTrue(validator.isValid("192.168.1.1", null)); // Class C private
        assertTrue(validator.isValid("169.254.1.1", null)); // Link-local
        assertTrue(validator.isValid("127.0.0.1", null)); // Loopback
    }

    @Test
    void testEdgeCaseBoundaries() {
        assertTrue(validator.isValid("0.0.0.0", null)); // Minimum boundary
        assertTrue(validator.isValid("255.255.255.255", null)); // Maximum boundary
        assertFalse(validator.isValid("256.0.0.0", null)); // Just over maximum
        assertFalse(validator.isValid("0.256.0.0", null)); // Just over maximum
        assertFalse(validator.isValid("0.0.256.0", null)); // Just over maximum
        assertFalse(validator.isValid("0.0.0.256", null)); // Just over maximum
    }
}
