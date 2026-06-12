package id.xtramile.validator.integration;

import id.xtramile.validator.annotation.network.*;
import id.xtramile.validator.support.ValidationMessageTestSupport;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

import static id.xtramile.validator.integration.ValidationMessageAssertions.assertNoRawValidationKey;
import static org.junit.jupiter.api.Assertions.assertEquals;

class NetworkValidatorMessageIntegrationTest {

    private static final ValidationMessageTestSupport SUPPORT = ValidationMessageTestSupport.EN;

    public record CidrDto(@ValidCIDR String value) {
            public CidrDto(String value) {
                this.value = value;
            }
        }

    public record IpAddressDto(@ValidIPAddress String value) {
            public IpAddressDto(String value) {
                this.value = value;
            }
        }

    public record Ipv4Dto(@ValidIPv4Address String value) {
            public Ipv4Dto(String value) {
                this.value = value;
            }
        }

    public record Ipv6Dto(@ValidIPv6Address String value) {
            public Ipv6Dto(String value) {
                this.value = value;
            }
        }

    public record MacAddressDto(@ValidMacAddress String value) {
            public MacAddressDto(String value) {
                this.value = value;
            }
        }

    public record PortIntDto(@ValidPort Integer value) {
            public PortIntDto(Integer value) {
                this.value = value;
            }
        }

    public record PortStringDto(@ValidPort String value) {
            public PortStringDto(String value) {
                this.value = value;
            }
        }

    public record UrlDto(@ValidURL String value) {
            public UrlDto(String value) {
                this.value = value;
            }
        }

    public record DomainNameDto(@ValidDomainName String value) {
            public DomainNameDto(String value) {
                this.value = value;
            }
        }

    @Test
    void cidr_pathB() {
        CidrDto dto = new CidrDto("192.168.0.0/33");
        ConstraintViolation<CidrDto> v = SUPPORT.firstViolation(dto);

        assertEquals("{friendly.default}", v.getMessageTemplate());

        String resolved = SUPPORT.resolver().resolve(v, "value", CidrDto.class);

        assertEquals("value must be a valid CIDR notation", resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void ipAddress_pathB() {
        IpAddressDto dto = new IpAddressDto("not-an-ip-address");
        ConstraintViolation<IpAddressDto> v = SUPPORT.firstViolation(dto);

        assertEquals("{friendly.default}", v.getMessageTemplate());

        String resolved = SUPPORT.resolver().resolve(v, "value", IpAddressDto.class);

        assertEquals("value must be a valid IP address", resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void ipv4_pathB() {
        Ipv4Dto dto = new Ipv4Dto("256.1.1.1");
        ConstraintViolation<Ipv4Dto> v = SUPPORT.firstViolation(dto);

        assertEquals("{friendly.default}", v.getMessageTemplate());

        String resolved = SUPPORT.resolver().resolve(v, "value", Ipv4Dto.class);

        assertEquals("value must be a valid IPv4 address", resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void ipv6_pathB() {
        Ipv6Dto dto = new Ipv6Dto("zzzz::1");
        ConstraintViolation<Ipv6Dto> v = SUPPORT.firstViolation(dto);

        assertEquals("{friendly.default}", v.getMessageTemplate());

        String resolved = SUPPORT.resolver().resolve(v, "value", Ipv6Dto.class);

        assertEquals("value must be a valid IPv6 address", resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void macAddress_pathB() {
        MacAddressDto dto = new MacAddressDto("00:00:00:00:00");
        ConstraintViolation<MacAddressDto> v = SUPPORT.firstViolation(dto);

        assertEquals("{friendly.default}", v.getMessageTemplate());

        String resolved = SUPPORT.resolver().resolve(v, "value", MacAddressDto.class);

        assertEquals("value must be a valid MAC address", resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void port_intOutOfRange() {
        PortIntDto dto = new PortIntDto(0);
        ConstraintViolation<PortIntDto> v = SUPPORT.firstViolation(dto);

        assertEquals("{friendly.default}", v.getMessageTemplate());

        String resolved = SUPPORT.resolver().resolve(v, "value", PortIntDto.class);

        assertEquals("value must be a valid port", resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void port_stringNotNumeric() {
        PortStringDto dto = new PortStringDto("not-a-port");
        ConstraintViolation<PortStringDto> v = SUPPORT.firstViolation(dto);

        assertEquals("{friendly.default}", v.getMessageTemplate());

        String resolved = SUPPORT.resolver().resolve(v, "value", PortStringDto.class);

        assertEquals("value must be a valid port", resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void url_pathB() {
        UrlDto dto = new UrlDto("https://");
        ConstraintViolation<UrlDto> v = SUPPORT.firstViolation(dto);

        assertEquals("{friendly.default}", v.getMessageTemplate());

        String resolved = SUPPORT.resolver().resolve(v, "value", UrlDto.class);

        assertEquals("value must be a valid URL", resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void domainName_pathB() {
        DomainNameDto dto = new DomainNameDto("nodots");
        ConstraintViolation<DomainNameDto> v = SUPPORT.firstViolation(dto);

        assertEquals("{friendly.default}", v.getMessageTemplate());

        String resolved = SUPPORT.resolver().resolve(v, "value", DomainNameDto.class);

        assertEquals("value must be a valid domain name", resolved);
        assertNoRawValidationKey(resolved);
    }
}
