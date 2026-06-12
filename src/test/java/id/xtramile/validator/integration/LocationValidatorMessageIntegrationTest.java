package id.xtramile.validator.integration;

import id.xtramile.validator.annotation.location.*;
import id.xtramile.validator.support.ValidationMessageTestSupport;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

import static id.xtramile.validator.integration.ValidationMessageAssertions.assertNoRawValidationKey;
import static org.junit.jupiter.api.Assertions.assertEquals;

class LocationValidatorMessageIntegrationTest {

    private static final ValidationMessageTestSupport SUPPORT = ValidationMessageTestSupport.EN;

    public record CoordinatesDto(@ValidCoordinates String value) {
            public CoordinatesDto(String value) {
                this.value = value;
            }
        }

    public record CoordinatesFlipDto(@ValidCoordinates(flipCoordinates = true) String value) {
            public CoordinatesFlipDto(String value) {
                this.value = value;
            }
        }

    public record LatitudeDto(@ValidLatitude String value) {
            public LatitudeDto(String value) {
                this.value = value;
            }
        }

    public record LongitudeDto(@ValidLongitude String value) {
            public LongitudeDto(String value) {
                this.value = value;
            }
        }

    public record PostalCodeUnsupportedDto(@ValidPostalCode(country = "US") String value) {
            public PostalCodeUnsupportedDto(String value) {
                this.value = value;
            }
        }

    public record PostalCodeBadFormatDto(@ValidPostalCode String value) {
            public PostalCodeBadFormatDto(String value) {
                this.value = value;
            }
        }

    public record RtRwDto(@ValidRTRW String value) {
            public RtRwDto(String value) {
                this.value = value;
            }
        }

    @Test
    void coordinates_defaultOrder() {
        CoordinatesDto dto = new CoordinatesDto("91,0");
        ConstraintViolation<CoordinatesDto> v = SUPPORT.firstViolation(dto);

        assertEquals("{friendly.default}", v.getMessageTemplate());

        String resolved = SUPPORT.resolver().resolve(v, "value", CoordinatesDto.class);

        assertEquals(
                "value must be valid coordinates in format: longitude, latitude",
                resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void coordinates_flippedOrder() {
        CoordinatesFlipDto dto = new CoordinatesFlipDto("0,91");
        ConstraintViolation<CoordinatesFlipDto> v = SUPPORT.firstViolation(dto);

        assertEquals("{friendly.default}", v.getMessageTemplate());

        String resolved = SUPPORT.resolver().resolve(v, "value", CoordinatesFlipDto.class);

        assertEquals(
                "value must be valid coordinates in format: latitude, longitude",
                resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void latitude_pathB() {
        LatitudeDto dto = new LatitudeDto("91");
        ConstraintViolation<LatitudeDto> v = SUPPORT.firstViolation(dto);

        assertEquals("{friendly.default}", v.getMessageTemplate());

        String resolved = SUPPORT.resolver().resolve(v, "value", LatitudeDto.class);

        assertEquals("value must be a valid latitude", resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void longitude_pathB() {
        LongitudeDto dto = new LongitudeDto("181");
        ConstraintViolation<LongitudeDto> v = SUPPORT.firstViolation(dto);

        assertEquals("{friendly.default}", v.getMessageTemplate());

        String resolved = SUPPORT.resolver().resolve(v, "value", LongitudeDto.class);

        assertEquals("value must be a valid longitude", resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void postalCode_unsupportedCountry() {
        PostalCodeUnsupportedDto dto = new PostalCodeUnsupportedDto("12345");
        ConstraintViolation<PostalCodeUnsupportedDto> v = SUPPORT.firstViolation(dto);

        assertEquals("{friendly.default}", v.getMessageTemplate());

        String resolved = SUPPORT.resolver().resolve(v, "value", PostalCodeUnsupportedDto.class);

        assertEquals("value must be a valid postal code", resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void postalCode_badFormat() {
        PostalCodeBadFormatDto dto = new PostalCodeBadFormatDto("1234");
        ConstraintViolation<PostalCodeBadFormatDto> v = SUPPORT.firstViolation(dto);

        assertEquals("{friendly.default}", v.getMessageTemplate());

        String resolved = SUPPORT.resolver().resolve(v, "value", PostalCodeBadFormatDto.class);

        assertEquals("value must be a valid postal code", resolved);
        assertNoRawValidationKey(resolved);
    }

    @Test
    void rtRw_pathB() {
        RtRwDto dto = new RtRwDto("12");
        ConstraintViolation<RtRwDto> v = SUPPORT.firstViolation(dto);

        assertEquals("{friendly.default}", v.getMessageTemplate());

        String resolved = SUPPORT.resolver().resolve(v, "value", RtRwDto.class);

        assertEquals("value must be a valid RT/RW number", resolved);
        assertNoRawValidationKey(resolved);
    }
}
