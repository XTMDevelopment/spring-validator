package id.xtramile.validator.validator.network;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CIDRValidatorTest {

    private CIDRValidator validator;

    @BeforeEach
    void setUp() {
        validator = new CIDRValidator();
    }

    @Test
    void testValidIPv4CIDR() {
        assertTrue(validator.isValid("192.168.1.0/24", null)); // Standard IPv4 CIDR
        assertTrue(validator.isValid("10.0.0.0/8", null)); // Class A private
        assertTrue(validator.isValid("172.16.0.0/12", null)); // Class B private
        assertTrue(validator.isValid("192.168.0.0/16", null)); // Class C private
        assertTrue(validator.isValid("0.0.0.0/0", null)); // Default route
        assertTrue(validator.isValid("192.168.1.1/32", null)); // Single host
        assertTrue(validator.isValid("10.0.0.0/8", null)); // Class A network
        assertTrue(validator.isValid("172.16.0.0/12", null)); // Class B network
        assertTrue(validator.isValid("192.168.0.0/16", null)); // Class C network
    }

    @Test
    void testValidIPv6CIDR() {
        assertTrue(validator.isValid("2001:db8::/32", null)); // Standard IPv6 CIDR
        assertTrue(validator.isValid("::/0", null)); // Default IPv6 route
        assertTrue(validator.isValid("2001:db8::/64", null)); // Standard IPv6 subnet
        assertTrue(validator.isValid("2001:db8::/128", null)); // Single IPv6 host
        assertTrue(validator.isValid("fe80::/64", null)); // Link-local IPv6
        assertTrue(validator.isValid("ff00::/8", null)); // IPv6 multicast
        assertTrue(validator.isValid("2001:db8:85a3::/48", null)); // IPv6 network
        assertTrue(validator.isValid("2001:db8:85a3:0::/64", null)); // IPv6 subnet
    }

    @Test
    void testValidIPv4PrefixLengths() {
        assertTrue(validator.isValid("192.168.1.0/0", null)); // /0 prefix
        assertTrue(validator.isValid("192.168.1.0/8", null)); // /8 prefix
        assertTrue(validator.isValid("192.168.1.0/16", null)); // /16 prefix
        assertTrue(validator.isValid("192.168.1.0/24", null)); // /24 prefix
        assertTrue(validator.isValid("192.168.1.0/32", null)); // /32 prefix
        assertTrue(validator.isValid("192.168.1.0/31", null)); // /31 prefix
        assertTrue(validator.isValid("192.168.1.0/30", null)); // /30 prefix
        assertTrue(validator.isValid("192.168.1.0/29", null)); // /29 prefix
        assertTrue(validator.isValid("192.168.1.0/28", null)); // /28 prefix
        assertTrue(validator.isValid("192.168.1.0/27", null)); // /27 prefix
        assertTrue(validator.isValid("192.168.1.0/26", null)); // /26 prefix
        assertTrue(validator.isValid("192.168.1.0/25", null)); // /25 prefix
    }

    @Test
    void testValidIPv6PrefixLengths() {
        assertTrue(validator.isValid("2001:db8::/0", null)); // /0 prefix
        assertTrue(validator.isValid("2001:db8::/8", null)); // /8 prefix
        assertTrue(validator.isValid("2001:db8::/16", null)); // /16 prefix
        assertTrue(validator.isValid("2001:db8::/32", null)); // /32 prefix
        assertTrue(validator.isValid("2001:db8::/48", null)); // /48 prefix
        assertTrue(validator.isValid("2001:db8::/64", null)); // /64 prefix
        assertTrue(validator.isValid("2001:db8::/96", null)); // /96 prefix
        assertTrue(validator.isValid("2001:db8::/112", null)); // /112 prefix
        assertTrue(validator.isValid("2001:db8::/128", null)); // /128 prefix
        assertTrue(validator.isValid("2001:db8::/127", null)); // /127 prefix
        assertTrue(validator.isValid("2001:db8::/126", null)); // /126 prefix
        assertTrue(validator.isValid("2001:db8::/125", null)); // /125 prefix
        assertTrue(validator.isValid("2001:db8::/124", null)); // /124 prefix
        assertTrue(validator.isValid("2001:db8::/123", null)); // /123 prefix
        assertTrue(validator.isValid("2001:db8::/122", null)); // /122 prefix
        assertTrue(validator.isValid("2001:db8::/121", null)); // /121 prefix
        assertTrue(validator.isValid("2001:db8::/120", null)); // /120 prefix
    }

    @Test
    void testInvalidIPv4PrefixLengths() {
        assertFalse(validator.isValid("192.168.1.0/-1", null)); // Negative prefix
        assertFalse(validator.isValid("192.168.1.0/33", null)); // Too large prefix
        assertFalse(validator.isValid("192.168.1.0/64", null)); // Too large prefix
        assertFalse(validator.isValid("192.168.1.0/128", null)); // Too large prefix
        assertFalse(validator.isValid("192.168.1.0/256", null)); // Way too large prefix
        assertFalse(validator.isValid("192.168.1.0/1000", null)); // Way too large prefix
    }

    @Test
    void testInvalidIPv6PrefixLengths() {
        assertFalse(validator.isValid("2001:db8::/-1", null)); // Negative prefix
        assertFalse(validator.isValid("2001:db8::/129", null)); // Too large prefix
        assertFalse(validator.isValid("2001:db8::/256", null)); // Too large prefix
        assertFalse(validator.isValid("2001:db8::/1000", null)); // Way too large prefix
        assertFalse(validator.isValid("2001:db8::/2000", null)); // Way too large prefix
    }

    @Test
    void testInvalidCIDRFormats() {
        assertFalse(validator.isValid("192.168.1.0", null)); // No prefix length
        assertFalse(validator.isValid("192.168.1.0/", null)); // Empty prefix length
        assertFalse(validator.isValid("192.168.1.0//24", null)); // Double slash
        assertFalse(validator.isValid("192.168.1.0/24/", null)); // Trailing slash
        assertFalse(validator.isValid("/192.168.1.0/24", null)); // Leading slash
        assertFalse(validator.isValid("192.168.1.0/24/32", null)); // Multiple prefix lengths
        assertFalse(validator.isValid("192.168.1.0:24", null)); // Wrong separator
        assertFalse(validator.isValid("192.168.1.0-24", null)); // Wrong separator
        assertFalse(validator.isValid("192.168.1.0 24", null)); // Space separator
    }

    @Test
    void testInvalidIPAddresses() {
        assertFalse(validator.isValid("256.1.1.0/24", null)); // Invalid IPv4
        assertFalse(validator.isValid("192.168.1.0.1/24", null)); // Extra octet
        assertFalse(validator.isValid("192.168.1/24", null)); // Missing octet
        assertFalse(validator.isValid("192.168.1.0.1.1/24", null)); // Too many octets
        assertFalse(validator.isValid("192.168.1.0.1.1.1/24", null)); // Way too many octets
        assertFalse(validator.isValid("192.168.1.0.1.1.1.1/24", null)); // Way too many octets
    }

    @Test
    void testInvalidPrefixLengthFormats() {
        assertFalse(validator.isValid("192.168.1.0/abc", null)); // Non-numeric prefix
        assertFalse(validator.isValid("192.168.1.0/24.5", null)); // Decimal prefix
        assertFalse(validator.isValid("192.168.1.0/24,5", null)); // Comma decimal prefix
        assertFalse(validator.isValid("192.168.1.0/24 5", null)); // Space in prefix
        assertFalse(validator.isValid("192.168.1.0/24-5", null)); // Hyphen in prefix
        assertFalse(validator.isValid("192.168.1.0/24:5", null)); // Colon in prefix
        assertFalse(validator.isValid("192.168.1.0/24#5", null)); // Hash in prefix
        assertFalse(validator.isValid("192.168.1.0/24@5", null)); // At symbol in prefix
    }

    @Test
    void testSpecialCharacters() {
        assertFalse(validator.isValid("192.168.1.0/24+", null)); // Plus sign
        assertFalse(validator.isValid("192.168.1.0/24-", null)); // Minus sign
        assertFalse(validator.isValid("192.168.1.0/24%", null)); // Percentage
        assertFalse(validator.isValid("192.168.1.0/24#", null)); // Hash symbol
        assertFalse(validator.isValid("192.168.1.0/24@", null)); // At symbol
        assertFalse(validator.isValid("192.168.1.0/24&", null)); // Ampersand
        assertFalse(validator.isValid("192.168.1.0/24*", null)); // Asterisk
    }

    @Test
    void testWhitespaceHandling() {
        assertTrue(validator.isValid(" 192.168.1.0/24 ", null)); // Leading and trailing spaces
        assertTrue(validator.isValid("\t192.168.1.0/24\t", null)); // Tabs
        assertTrue(validator.isValid("\n192.168.1.0/24\n", null)); // Newlines
        assertTrue(validator.isValid("  192.168.1.0/24  ", null)); // Multiple spaces
    }

    @Test
    void testRealWorldIPv4Examples() {
        assertTrue(validator.isValid("0.0.0.0/0", null)); // Default route
        assertTrue(validator.isValid("10.0.0.0/8", null)); // Private Class A
        assertTrue(validator.isValid("172.16.0.0/12", null)); // Private Class B
        assertTrue(validator.isValid("192.168.0.0/16", null)); // Private Class C
        assertTrue(validator.isValid("192.168.1.0/24", null)); // Common home network
        assertTrue(validator.isValid("192.168.1.1/32", null)); // Single host
        assertTrue(validator.isValid("203.0.113.0/24", null)); // Test network
        assertTrue(validator.isValid("198.51.100.0/24", null)); // Test network
    }

    @Test
    void testRealWorldIPv6Examples() {
        assertTrue(validator.isValid("::/0", null)); // Default IPv6 route
        assertTrue(validator.isValid("2001:db8::/32", null)); // Documentation network
        assertTrue(validator.isValid("2001:db8::/64", null)); // Documentation subnet
        assertTrue(validator.isValid("fe80::/64", null)); // Link-local
        assertTrue(validator.isValid("ff00::/8", null)); // Multicast
        assertTrue(validator.isValid("2001:db8:85a3::/48", null)); // Documentation network
        assertTrue(validator.isValid("2001:db8:85a3:0::/64", null)); // Documentation subnet
    }

    @Test
    void testEdgeCasePrefixLengths() {
        assertTrue(validator.isValid("192.168.1.0/0", null)); // Minimum IPv4 prefix
        assertTrue(validator.isValid("192.168.1.0/32", null)); // Maximum IPv4 prefix
        assertTrue(validator.isValid("2001:db8::/0", null)); // Minimum IPv6 prefix
        assertTrue(validator.isValid("2001:db8::/128", null)); // Maximum IPv6 prefix
        assertFalse(validator.isValid("192.168.1.0/-1", null)); // Just below minimum
        assertFalse(validator.isValid("192.168.1.0/33", null)); // Just above maximum IPv4
        assertFalse(validator.isValid("2001:db8::/-1", null)); // Just below minimum
        assertFalse(validator.isValid("2001:db8::/129", null)); // Just above maximum IPv6
    }

    @Test
    void testCommonNetworkSizes() {
        assertTrue(validator.isValid("192.168.1.0/30", null)); // /30 - 4 addresses
        assertTrue(validator.isValid("192.168.1.0/29", null)); // /29 - 8 addresses
        assertTrue(validator.isValid("192.168.1.0/28", null)); // /28 - 16 addresses
        assertTrue(validator.isValid("192.168.1.0/27", null)); // /27 - 32 addresses
        assertTrue(validator.isValid("192.168.1.0/26", null)); // /26 - 64 addresses
        assertTrue(validator.isValid("192.168.1.0/25", null)); // /25 - 128 addresses
        assertTrue(validator.isValid("192.168.1.0/24", null)); // /24 - 256 addresses
        assertTrue(validator.isValid("192.168.0.0/23", null)); // /23 - 512 addresses
        assertTrue(validator.isValid("192.168.0.0/22", null)); // /22 - 1024 addresses
        assertTrue(validator.isValid("192.168.0.0/21", null)); // /21 - 2048 addresses
        assertTrue(validator.isValid("192.168.0.0/20", null)); // /20 - 4096 addresses
    }
}
