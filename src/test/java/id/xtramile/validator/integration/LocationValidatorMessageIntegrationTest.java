package id.xtramile.validator.integration;

import id.xtramile.validator.annotation.location.*;
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

class LocationValidatorMessageIntegrationTest {

    private static final Validator VALIDATOR;
    private static final FriendlyMessageResolver RESOLVER;

    static {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        VALIDATOR = factory.getValidator();

        MessageResourceResolver messageResourceResolver = new MessageResourceResolver("en");
        RESOLVER = new FriendlyMessageResolver(messageResourceResolver);
    }

    public static class CoordinatesDto {
        @ValidCoordinates
        private final String value;

        public CoordinatesDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class CoordinatesFlipDto {
        @ValidCoordinates(flipCoordinates = true)
        private final String value;

        public CoordinatesFlipDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class LatitudeDto {
        @ValidLatitude
        private final String value;

        public LatitudeDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class LongitudeDto {
        @ValidLongitude
        private final String value;

        public LongitudeDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class PostalCodeUnsupportedDto {
        @ValidPostalCode(country = "US")
        private final String value;

        public PostalCodeUnsupportedDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class PostalCodeBadFormatDto {
        @ValidPostalCode
        private final String value;

        public PostalCodeBadFormatDto(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public static class RtRwDto {
        @ValidRTRW
        private final String value;

        public RtRwDto(String value) {
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
    void coordinates_defaultOrder() {
        CoordinatesDto dto = new CoordinatesDto("91,0");
        ConstraintViolation<CoordinatesDto> v = firstViolation(dto);

        assertEquals("{friendly.default}", v.getMessageTemplate());

        String resolved = RESOLVER.resolve(v, "value", CoordinatesDto.class);

        assertEquals(
                "value must be valid coordinates in format: longitude, latitude",
                resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void coordinates_flippedOrder() {
        CoordinatesFlipDto dto = new CoordinatesFlipDto("0,91");
        ConstraintViolation<CoordinatesFlipDto> v = firstViolation(dto);

        assertEquals("{friendly.default}", v.getMessageTemplate());

        String resolved = RESOLVER.resolve(v, "value", CoordinatesFlipDto.class);

        assertEquals(
                "value must be valid coordinates in format: latitude, longitude",
                resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void latitude_pathB() {
        LatitudeDto dto = new LatitudeDto("91");
        ConstraintViolation<LatitudeDto> v = firstViolation(dto);

        assertEquals("{friendly.default}", v.getMessageTemplate());

        String resolved = RESOLVER.resolve(v, "value", LatitudeDto.class);

        assertEquals("value must be a valid latitude", resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void longitude_pathB() {
        LongitudeDto dto = new LongitudeDto("181");
        ConstraintViolation<LongitudeDto> v = firstViolation(dto);

        assertEquals("{friendly.default}", v.getMessageTemplate());

        String resolved = RESOLVER.resolve(v, "value", LongitudeDto.class);

        assertEquals("value must be a valid longitude", resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void postalCode_unsupportedCountry() {
        PostalCodeUnsupportedDto dto = new PostalCodeUnsupportedDto("12345");
        ConstraintViolation<PostalCodeUnsupportedDto> v = firstViolation(dto);

        assertEquals("{friendly.default}", v.getMessageTemplate());

        String resolved = RESOLVER.resolve(v, "value", PostalCodeUnsupportedDto.class);

        assertEquals("value must be a valid postal code", resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void postalCode_badFormat() {
        PostalCodeBadFormatDto dto = new PostalCodeBadFormatDto("1234");
        ConstraintViolation<PostalCodeBadFormatDto> v = firstViolation(dto);

        assertEquals("{friendly.default}", v.getMessageTemplate());

        String resolved = RESOLVER.resolve(v, "value", PostalCodeBadFormatDto.class);

        assertEquals("value must be a valid postal code", resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void rtRw_pathB() {
        RtRwDto dto = new RtRwDto("12");
        ConstraintViolation<RtRwDto> v = firstViolation(dto);

        assertEquals("{friendly.default}", v.getMessageTemplate());

        String resolved = RESOLVER.resolve(v, "value", RtRwDto.class);

        assertEquals("value must be a valid RT/RW number", resolved);
        assertNoRawValidationKey(resolved);
    }
}
