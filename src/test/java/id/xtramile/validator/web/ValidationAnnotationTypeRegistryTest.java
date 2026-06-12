package id.xtramile.validator.web;

import id.xtramile.validator.annotation.common.*;
import id.xtramile.validator.annotation.contact.*;
import id.xtramile.validator.annotation.cross.*;
import id.xtramile.validator.annotation.data.*;
import id.xtramile.validator.annotation.datetime.*;
import id.xtramile.validator.annotation.file.ValidFileExtension;
import id.xtramile.validator.annotation.file.ValidFileMimeType;
import id.xtramile.validator.annotation.file.ValidFileSize;
import id.xtramile.validator.annotation.file.ValidImageDimensions;
import id.xtramile.validator.annotation.finance.*;
import id.xtramile.validator.annotation.kyc.ValidIDImage;
import id.xtramile.validator.annotation.kyc.ValidSelfieImage;
import id.xtramile.validator.annotation.location.*;
import id.xtramile.validator.annotation.network.*;
import jakarta.validation.constraints.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class ValidationAnnotationTypeRegistryTest {

    private static Stream<Arguments> allRegisteredCodes() {
        return Stream.of(
                Arguments.of("NotBlank", NotBlank.class),
                Arguments.of("NotNull", NotNull.class),
                Arguments.of("Null", Null.class),
                Arguments.of("NotEmpty", NotEmpty.class),
                Arguments.of("Email", Email.class),
                Arguments.of("Pattern", Pattern.class),
                Arguments.of("Size", Size.class),
                Arguments.of("Min", Min.class),
                Arguments.of("Max", Max.class),
                Arguments.of("DecimalMin", DecimalMin.class),
                Arguments.of("DecimalMax", DecimalMax.class),
                Arguments.of("Digits", Digits.class),
                Arguments.of("Positive", Positive.class),
                Arguments.of("PositiveOrZero", PositiveOrZero.class),
                Arguments.of("Negative", Negative.class),
                Arguments.of("NegativeOrZero", NegativeOrZero.class),
                Arguments.of("AssertTrue", AssertTrue.class),
                Arguments.of("AssertFalse", AssertFalse.class),
                Arguments.of("Past", Past.class),
                Arguments.of("PastOrPresent", PastOrPresent.class),
                Arguments.of("Future", Future.class),
                Arguments.of("FutureOrPresent", FutureOrPresent.class),

                Arguments.of("InWhitelist", InWhitelist.class),
                Arguments.of("NotInBlacklist", NotInBlacklist.class),
                Arguments.of("ValidEnum", ValidEnum.class),
                Arguments.of("UniqueElements", UniqueElements.class),
                Arguments.of("NotEmptyCollection", NotEmptyCollection.class),
                Arguments.of("ValidUUID", ValidUUID.class),

                Arguments.of("ValidPhoneNumber", ValidPhoneNumber.class),
                Arguments.of("ValidContactNumber", ValidContactNumber.class),
                Arguments.of("ValidEmail", ValidEmail.class),
                Arguments.of("ValidOtp", ValidOtp.class),
                Arguments.of("ValidEmailDomain", ValidEmailDomain.class),

                Arguments.of("FieldMatch", FieldMatch.class),
                Arguments.of("AtLeastOneOf", AtLeastOneOf.class),
                Arguments.of("DifferentFrom", DifferentFrom.class),
                Arguments.of("OnlyOneOf", OnlyOneOf.class),
                Arguments.of("RequiredWith", RequiredWith.class),

                Arguments.of("ValidAccountNumber", ValidAccountNumber.class),
                Arguments.of("ValidBase64", ValidBase64.class),
                Arguments.of("ValidHexColor", ValidHexColor.class),
                Arguments.of("ValidISOCode", ValidISOCode.class),
                Arguments.of("ValidJSON", ValidJSON.class),
                Arguments.of("ValidName", ValidName.class),
                Arguments.of("ValidNationalID", ValidNationalID.class),
                Arguments.of("ValidPassword", ValidPassword.class),
                Arguments.of("ValidPIN", ValidPIN.class),
                Arguments.of("ValidSlug", ValidSlug.class),
                Arguments.of("ValidTaxID", ValidTaxID.class),
                Arguments.of("ValidUsername", ValidUsername.class),

                Arguments.of("ValidDateTime", ValidDateTime.class),
                Arguments.of("ValidDate", ValidDate.class),
                Arguments.of("ValidTime", ValidTime.class),
                Arguments.of("ValidISO8601", ValidISO8601.class),
                Arguments.of("ValidPastDate", ValidPastDate.class),
                Arguments.of("ValidFutureDate", ValidFutureDate.class),
                Arguments.of("InvalidPastDate", InvalidPastDate.class),
                Arguments.of("InvalidFutureDate", InvalidFutureDate.class),
                Arguments.of("InvalidPastFutureDate", InvalidPastFutureDate.class),
                Arguments.of("DateBefore", DateBefore.class),
                Arguments.of("DateAfter", DateAfter.class),

                Arguments.of("ValidImageDimensions", ValidImageDimensions.class),
                Arguments.of("ValidFileExtension", ValidFileExtension.class),
                Arguments.of("ValidFileMimeType", ValidFileMimeType.class),
                Arguments.of("ValidFileSize", ValidFileSize.class),

                Arguments.of("ValidCardExpiry", ValidCardExpiry.class),
                Arguments.of("ValidCardNumber", ValidCardNumber.class),
                Arguments.of("ValidCurrencyCode", ValidCurrencyCode.class),
                Arguments.of("ValidCVV", ValidCVV.class),
                Arguments.of("ValidIBAN", ValidIBAN.class),
                Arguments.of("ValidPaymentReference", ValidPaymentReference.class),
                Arguments.of("ValidSwiftCode", ValidSwiftCode.class),
                Arguments.of("ValidTransactionAmount", ValidTransactionAmount.class),

                Arguments.of("ValidIDImage", ValidIDImage.class),
                Arguments.of("ValidSelfieImage", ValidSelfieImage.class),

                Arguments.of("ValidCoordinates", ValidCoordinates.class),
                Arguments.of("ValidLatitude", ValidLatitude.class),
                Arguments.of("ValidLongitude", ValidLongitude.class),
                Arguments.of("ValidPostalCode", ValidPostalCode.class),
                Arguments.of("ValidRTRW", ValidRTRW.class),

                Arguments.of("ValidCIDR", ValidCIDR.class),
                Arguments.of("ValidIPAddress", ValidIPAddress.class),
                Arguments.of("ValidIPv4Address", ValidIPv4Address.class),
                Arguments.of("ValidIPv6Address", ValidIPv6Address.class),
                Arguments.of("ValidMacAddress", ValidMacAddress.class),
                Arguments.of("ValidPort", ValidPort.class),
                Arguments.of("ValidURL", ValidURL.class),
                Arguments.of("ValidDomainName", ValidDomainName.class)
        );
    }

    @Test
    void resolve_null_returnsNull() {
        assertThat(ValidationAnnotationTypeRegistry.resolve(null)).isNull();
    }

    @Test
    void resolve_unknown_returnsNull() {
        assertThat(ValidationAnnotationTypeRegistry.resolve("NoSuchConstraint")).isNull();
        assertThat(ValidationAnnotationTypeRegistry.resolve("")).isNull();
    }

    @ParameterizedTest
    @MethodSource("allRegisteredCodes")
    void resolve_mapsEveryRegisteredSpringCodeToAnnotationType(String code, Class<?> expectedType) {
        assertThat(ValidationAnnotationTypeRegistry.resolve(code)).isEqualTo(expectedType);
    }
}
