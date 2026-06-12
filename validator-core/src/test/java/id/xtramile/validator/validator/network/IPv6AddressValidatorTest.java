package id.xtramile.validator.validator.network;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IPv6AddressValidatorTest {

    private IPv6AddressValidator validator;

    @BeforeEach
    void setUp() {
        validator = new IPv6AddressValidator();
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
    void testValidCompressedIPv6() {
        assertTrue(validator.isValid("2001:db8::8a2e:370:7334", null)); // Single compression
        assertTrue(validator.isValid("2001:db8:85a3::8a2e:370:7334", null)); // Single compression
        assertTrue(validator.isValid("::1", null)); // All zeros compressed
        assertTrue(validator.isValid("::", null)); // All zeros compressed
        assertTrue(validator.isValid("2001::", null)); // Trailing zeros compressed
        assertTrue(validator.isValid("::2001", null)); // Leading zeros compressed
    }

    @Test
    void testValidIPv6WithZeros() {
        assertTrue(validator.isValid("2001:0db8:85a3:0000:0000:8a2e:0370:7334", null)); // Full with zeros
        assertTrue(validator.isValid("2001:0db8:85a3:0:0:8a2e:0370:7334", null)); // Single zeros
        assertTrue(validator.isValid("2001:0db8:85a3:0000:0000:8a2e:0370:7334", null)); // Mixed zeros
        assertTrue(validator.isValid("0000:0000:0000:0000:0000:0000:0000:0001", null)); // All zeros except last
        assertTrue(validator.isValid("0000:0000:0000:0000:0000:0000:0000:0000", null)); // All zeros
    }

    @Test
    void testInvalidIPv6Formats() {
        assertFalse(validator.isValid("2001:0db8:85a3:0000:0000:8a2e:0370:7334:extra", null)); // Too many groups
        assertFalse(validator.isValid("2001:0db8:85a3:0000:0000:8a2e:0370", null)); // Missing group
        assertFalse(validator.isValid("2001:0db8:85a3:0000:0000:8a2e:0370:7334:7335", null)); // Too many groups
        assertFalse(validator.isValid("2001:0db8:85a3:0000:0000:8a2e:0370:gggg", null)); // Invalid hex
        assertFalse(validator.isValid("2001:0db8:85a3:0000:0000:8a2e:0370:7334:", null)); // Trailing colon
        assertFalse(validator.isValid(":2001:0db8:85a3:0000:0000:8a2e:0370:7334", null)); // Leading colon
    }

    @Test
    void testInvalidIPv6Characters() {
        assertFalse(validator.isValid("2001:0db8:85a3:0000:0000:8a2e:0370:gggg", null)); // Invalid hex
        assertFalse(validator.isValid("2001:0db8:85a3:0000:0000:8a2e:0370:g", null)); // Single invalid char
        assertFalse(validator.isValid("2001:0db8:85a3:0000:0000:8a2e:0370:z", null)); // Invalid hex
        assertFalse(validator.isValid("2001:0db8:85a3:0000:0000:8a2e:0370:Z", null)); // Invalid hex uppercase
        assertFalse(validator.isValid("2001:0db8:85a3:0000:0000:8a2e:0370:@", null)); // Special character
        assertFalse(validator.isValid("2001:0db8:85a3:0000:0000:8a2e:0370:#", null)); // Special character
    }

    @Test
    void testInvalidIPv6Structure() {
        assertFalse(validator.isValid("2001:0db8:85a3:0000:0000:8a2e:0370:7334:7335", null)); // Too many groups
        assertFalse(validator.isValid("2001:0db8:85a3:0000:0000:8a2e:0370", null)); // Too few groups
        assertFalse(validator.isValid("2001:0db8:85a3:0000:0000:8a2e", null)); // Too few groups
        assertFalse(validator.isValid("2001:0db8:85a3:0000:0000", null)); // Too few groups
        assertFalse(validator.isValid("2001:0db8:85a3:0000", null)); // Too few groups
        assertFalse(validator.isValid("2001:0db8:85a3", null)); // Too few groups
        assertFalse(validator.isValid("2001:0db8", null)); // Too few groups
        assertFalse(validator.isValid("2001", null)); // Too few groups
    }

    @Test
    void testInvalidCompression() {
        assertFalse(validator.isValid("2001::db8::85a3", null)); // Multiple compressions
        assertFalse(validator.isValid("2001:::db8:85a3", null)); // Triple colon
        assertFalse(validator.isValid("2001::::db8:85a3", null)); // Quadruple colon
        assertFalse(validator.isValid("2001:db8:85a3::8a2e:370:7334::", null)); // Multiple compressions
        assertFalse(validator.isValid("::2001::db8", null)); // Multiple compressions
    }

    @Test
    void testSpecialCharacters() {
        assertFalse(validator.isValid("2001:0db8:85a3:0000:0000:8a2e:0370:7334+", null)); // Plus sign
        assertFalse(validator.isValid("2001:0db8:85a3:0000:0000:8a2e:0370:7334-", null)); // Minus sign
        assertFalse(validator.isValid("2001:0db8:85a3:0000:0000:8a2e:0370:7334%", null)); // Percentage
        assertFalse(validator.isValid("2001:0db8:85a3:0000:0000:8a2e:0370:7334#", null)); // Hash symbol
        assertFalse(validator.isValid("2001:0db8:85a3:0000:0000:8a2e:0370:7334@", null)); // At symbol
        assertFalse(validator.isValid("2001:0db8:85a3:0000:0000:8a2e:0370:7334&", null)); // Ampersand
        assertFalse(validator.isValid("2001:0db8:85a3:0000:0000:8a2e:0370:7334*", null)); // Asterisk
    }

    @Test
    void testWhitespaceHandling() {
        assertTrue(validator.isValid(" 2001:0db8:85a3:0000:0000:8a2e:0370:7334 ", null)); // Leading and trailing spaces
        assertTrue(validator.isValid("\t2001:0db8:85a3:0000:0000:8a2e:0370:7334\t", null)); // Tabs
        assertTrue(validator.isValid("\n2001:0db8:85a3:0000:0000:8a2e:0370:7334\n", null)); // Newlines
        assertTrue(validator.isValid("  2001:0db8:85a3:0000:0000:8a2e:0370:7334  ", null)); // Multiple spaces
    }

    @Test
    void testEdgeCaseValues() {
        assertTrue(validator.isValid("::", null)); // All zeros
        assertTrue(validator.isValid("::1", null)); // Loopback
        assertTrue(validator.isValid("ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff", null)); // All Fs
        assertTrue(validator.isValid("0000:0000:0000:0000:0000:0000:0000:0000", null)); // All zeros
        assertTrue(validator.isValid("2001:0db8:85a3:0000:0000:8a2e:0370:7334", null)); // Standard format
    }

    @Test
    void testRealWorldExamples() {
        assertTrue(validator.isValid("2001:4860:4860::8888", null)); // Google IPv6 DNS
        assertTrue(validator.isValid("2606:4700:4700::1111", null)); // Cloudflare IPv6 DNS
        assertTrue(validator.isValid("2001:4860:4860::8844", null)); // Google IPv6 DNS
        assertTrue(validator.isValid("2620:fe::fe", null)); // Quad9 IPv6 DNS
        assertTrue(validator.isValid("2001:4860:4860::8888", null)); // Google IPv6 DNS
    }

    @Test
    void testLinkLocalAddresses() {
        assertTrue(validator.isValid("fe80::1", null)); // Link-local
        assertTrue(validator.isValid("fe80::", null)); // Link-local
        assertTrue(validator.isValid("fe80::1234:5678:9abc:def0", null)); // Link-local
        assertTrue(validator.isValid("fe80:0:0:0:0:0:0:1", null)); // Link-local
    }

    @Test
    void testMulticastAddresses() {
        assertTrue(validator.isValid("ff02::1", null)); // All nodes multicast
        assertTrue(validator.isValid("ff02::2", null)); // All routers multicast
        assertTrue(validator.isValid("ff02::1:ff00:1234", null)); // Solicited node multicast
        assertTrue(validator.isValid("ff01::1", null)); // Interface-local all nodes
    }

    @Test
    void testIPv4MappedIPv6() {
        assertTrue(validator.isValid("::ffff:192.168.1.1", null)); // IPv4-mapped IPv6
        assertTrue(validator.isValid("::ffff:8.8.8.8", null)); // IPv4-mapped IPv6
        assertTrue(validator.isValid("::ffff:127.0.0.1", null)); // IPv4-mapped IPv6
        assertTrue(validator.isValid("::ffff:0.0.0.0", null)); // IPv4-mapped IPv6
    }

    @Test
    void testInvalidIPv4MappedIPv6() {
        assertFalse(validator.isValid("::ffff:256.1.1.1", null)); // Invalid IPv4 in mapped
        assertFalse(validator.isValid("::ffff:192.168.1", null)); // Incomplete IPv4 in mapped
        assertFalse(validator.isValid("::ffff:192.168.1.1.1", null)); // Extra octet in mapped
    }
}
