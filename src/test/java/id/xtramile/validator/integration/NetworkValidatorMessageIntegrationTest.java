package id.xtramile.validator.integration;

import id.xtramile.validator.annotation.network.*;
import id.xtramile.validator.web.FriendlyMessageResolver;
import id.xtramile.validator.web.MessageResourceResolver;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static id.xtramile.validator.integration.ValidationMessageAssertions.assertNoRawValidationKey;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class NetworkValidatorMessageIntegrationTest {

    private static final Validator VALIDATOR;
    private static final FriendlyMessageResolver RESOLVER;

    static {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        VALIDATOR = factory.getValidator();

        MessageResourceResolver messageResourceResolver = new MessageResourceResolver("en");
        RESOLVER = new FriendlyMessageResolver(messageResourceResolver);
    }

    public static class CidrDto {
        @ValidCIDR
        private final String value;

        public CidrDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class IpAddressDto {
        @ValidIPAddress
        private final String value;

        public IpAddressDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class Ipv4Dto {
        @ValidIPv4Address
        private final String value;

        public Ipv4Dto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class Ipv6Dto {
        @ValidIPv6Address
        private final String value;

        public Ipv6Dto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class MacAddressDto {
        @ValidMacAddress
        private final String value;

        public MacAddressDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class PortIntDto {
        @ValidPort
        private final Integer value;

        public PortIntDto(Integer value) {
            this.value = value;
        }

        public Integer value() {
            return value;
        }
    }

    public static class PortStringDto {
        @ValidPort
        private final String value;

        public PortStringDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class UrlDto {
        @ValidURL
        private final String value;

        public UrlDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class DomainNameDto {
        @ValidDomainName
        private final String value;

        public DomainNameDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    private <T> ConstraintViolation<T> firstViolation(T dto) {
        Set<ConstraintViolation<T>> violations = VALIDATOR.validate(dto);

        assertFalse(violations.isEmpty(), "Expected at least one violation but got none");

        return violations.iterator().next();
    }

    @Test
    void cidr_pathB() {
        CidrDto dto = new CidrDto("192.168.0.0/33");
        ConstraintViolation<CidrDto> v = firstViolation(dto);

        assertEquals("{friendly.default}", v.getMessageTemplate());

        String resolved = RESOLVER.resolve(v, "value", CidrDto.class);

        assertEquals("value must be a valid CIDR notation", resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void ipAddress_pathB() {
        IpAddressDto dto = new IpAddressDto("not-an-ip-address");
        ConstraintViolation<IpAddressDto> v = firstViolation(dto);

        assertEquals("{friendly.default}", v.getMessageTemplate());

        String resolved = RESOLVER.resolve(v, "value", IpAddressDto.class);

        assertEquals("value must be a valid IP address", resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void ipv4_pathB() {
        Ipv4Dto dto = new Ipv4Dto("256.1.1.1");
        ConstraintViolation<Ipv4Dto> v = firstViolation(dto);

        assertEquals("{friendly.default}", v.getMessageTemplate());

        String resolved = RESOLVER.resolve(v, "value", Ipv4Dto.class);

        assertEquals("value must be a valid IPv4 address", resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void ipv6_pathB() {
        Ipv6Dto dto = new Ipv6Dto("zzzz::1");
        ConstraintViolation<Ipv6Dto> v = firstViolation(dto);

        assertEquals("{friendly.default}", v.getMessageTemplate());

        String resolved = RESOLVER.resolve(v, "value", Ipv6Dto.class);

        assertEquals("value must be a valid IPv6 address", resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void macAddress_pathB() {
        MacAddressDto dto = new MacAddressDto("00:00:00:00:00");
        ConstraintViolation<MacAddressDto> v = firstViolation(dto);

        assertEquals("{friendly.default}", v.getMessageTemplate());

        String resolved = RESOLVER.resolve(v, "value", MacAddressDto.class);

        assertEquals("value must be a valid MAC address", resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void port_intOutOfRange() {
        PortIntDto dto = new PortIntDto(0);
        ConstraintViolation<PortIntDto> v = firstViolation(dto);

        assertEquals("{friendly.default}", v.getMessageTemplate());

        String resolved = RESOLVER.resolve(v, "value", PortIntDto.class);

        assertEquals("value must be a valid port", resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void port_stringNotNumeric() {
        PortStringDto dto = new PortStringDto("not-a-port");
        ConstraintViolation<PortStringDto> v = firstViolation(dto);

        assertEquals("{friendly.default}", v.getMessageTemplate());

        String resolved = RESOLVER.resolve(v, "value", PortStringDto.class);

        assertEquals("value must be a valid port", resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void url_pathB() {
        UrlDto dto = new UrlDto("http://");
        ConstraintViolation<UrlDto> v = firstViolation(dto);

        assertEquals("{friendly.default}", v.getMessageTemplate());

        String resolved = RESOLVER.resolve(v, "value", UrlDto.class);

        assertEquals("value must be a valid URL", resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void domainName_pathB() {
        DomainNameDto dto = new DomainNameDto("nodots");
        ConstraintViolation<DomainNameDto> v = firstViolation(dto);

        assertEquals("{friendly.default}", v.getMessageTemplate());

        String resolved = RESOLVER.resolve(v, "value", DomainNameDto.class);

        assertEquals("value must be a valid domain name", resolved);
        assertNoRawValidationKey(resolved);
    }
}
