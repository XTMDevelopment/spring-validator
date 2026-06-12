package id.xtramile.validator.validator.network;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IPAddressValidatorTest {

    private IPAddressValidator validator;

    @BeforeEach
    void setUp() {
        validator = new IPAddressValidator();
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
    }

    @Test
    void testValidIPv6Addresses() {
        assertTrue(validator.isValid("2001:0db8:85a3:0000:0000:8a2e:0370:7334", null)); // Full IPv6
        assertTrue(validator.isValid("2001:db8:85a3::8a2e:370:7334", null)); // Compressed IPv6
        assertTrue(validator.isValid("::1", null)); // IPv6 loopback
        assertTrue(validator.isValid("::", null)); // IPv6 all zeros
        assertTrue(validator.isValid("2001:db8::", null)); // IPv6 with zeros
        assertTrue(validator.isValid("fe80::1", null)); // Link-local
        assertTrue(validator.isValid("ff02::1", null)); // Multicast
    }

    @Test
    void testInvalidIPv4Addresses() {
        assertFalse(validator.isValid("256.1.1.1", null)); // Invalid octet
        assertFalse(validator.isValid("192.168.1", null)); // Missing octet
        assertFalse(validator.isValid("192.168.1.1.1", null)); // Extra octet
        assertFalse(validator.isValid("192.168.1.", null)); // Trailing dot
        assertFalse(validator.isValid(".192.168.1.1", null)); // Leading dot
        assertFalse(validator.isValid("192.168.1.1.", null)); // Trailing dot
        assertFalse(validator.isValid("192.168.1.1.1", null)); // Too many octets
    }

    @Test
    void testInvalidIPv6Addresses() {
        assertFalse(validator.isValid("2001:0db8:85a3:0000:0000:8a2e:0370:7334:extra", null)); // Too many groups
        assertFalse(validator.isValid("2001:0db8:85a3:0000:0000:8a2e:0370", null)); // Missing group
        assertFalse(validator.isValid("2001:0db8:85a3:0000:0000:8a2e:0370:7334:7335", null)); // Too many groups
        assertFalse(validator.isValid("2001:0db8:85a3:0000:0000:8a2e:0370:gggg", null)); // Invalid hex
        assertFalse(validator.isValid("2001:0db8:85a3:0000:0000:8a2e:0370:7334:", null)); // Trailing colon
        assertFalse(validator.isValid(":2001:0db8:85a3:0000:0000:8a2e:0370:7334", null)); // Leading colon
    }

    @Test
    void testInvalidFormats() {
        assertFalse(validator.isValid("not.an.ip", null)); // Invalid format
        assertFalse(validator.isValid("192.168.1.1:8080", null)); // Port included
        assertFalse(validator.isValid("http://192.168.1.1", null)); // Protocol included
        assertFalse(validator.isValid("192.168.1.1/24", null)); // CIDR included
        assertFalse(validator.isValid("192.168.1.1-192.168.1.10", null)); // Range format
        assertFalse(validator.isValid("192.168.1.1,192.168.1.2", null)); // Multiple IPs
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
    void testEdgeCaseValues() {
        assertTrue(validator.isValid("0.0.0.0", null)); // All zeros IPv4
        assertTrue(validator.isValid("255.255.255.255", null)); // All 255s IPv4
        assertTrue(validator.isValid("::", null)); // All zeros IPv6
        assertTrue(validator.isValid("::1", null)); // IPv6 loopback
        assertTrue(validator.isValid("fe80::", null)); // Link-local IPv6
    }

    @Test
    void testRealWorldExamples() {
        assertTrue(validator.isValid("8.8.8.8", null)); // Google DNS
        assertTrue(validator.isValid("1.1.1.1", null)); // Cloudflare DNS
        assertTrue(validator.isValid("208.67.222.222", null)); // OpenDNS
        assertTrue(validator.isValid("2001:4860:4860::8888", null)); // Google IPv6 DNS
        assertTrue(validator.isValid("2606:4700:4700::1111", null)); // Cloudflare IPv6 DNS
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
    void testInvalidHostnames() {
        assertFalse(validator.isValid("host name", null)); // Space in hostname
        assertFalse(validator.isValid("host@name", null)); // Invalid character
        assertFalse(validator.isValid("host#name", null)); // Invalid character
        assertFalse(validator.isValid("host%name", null)); // Invalid character
        assertFalse(validator.isValid("host&name", null)); // Invalid character
    }
}
