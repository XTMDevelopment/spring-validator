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

public class ValidationAnnotationTypeRegistry {

    private ValidationAnnotationTypeRegistry() {}
    
    public static Class<?> resolve(String annotationName) {
        if (annotationName == null) {
            return null;
        }
        
        switch (annotationName) {
            case "NotBlank": return NotBlank.class;
            case "NotNull": return NotNull.class;
            case "Null": return Null.class;
            case "NotEmpty": return NotEmpty.class;
            case "Email": return Email.class;
            case "Pattern": return Pattern.class;
            case "Size": return Size.class;
            case "Min": return Min.class;
            case "Max": return Max.class;
            case "DecimalMin": return DecimalMin.class;
            case "DecimalMax": return DecimalMax.class;
            case "Digits": return Digits.class;
            case "Positive": return Positive.class;
            case "PositiveOrZero": return PositiveOrZero.class;
            case "Negative": return Negative.class;
            case "NegativeOrZero": return NegativeOrZero.class;
            case "AssertTrue": return AssertTrue.class;
            case "AssertFalse": return AssertFalse.class;
            case "Past": return Past.class;
            case "PastOrPresent": return PastOrPresent.class;
            case "Future": return Future.class;
            case "FutureOrPresent": return FutureOrPresent.class;

            case "InWhitelist": return InWhitelist.class;
            case "NotInBlacklist": return NotInBlacklist.class;
            case "ValidEnum": return ValidEnum.class;
            case "UniqueElements": return UniqueElements.class;
            case "NotEmptyCollection": return NotEmptyCollection.class;
            case "ValidUUID": return ValidUUID.class;

            case "ValidPhoneNumber": return ValidPhoneNumber.class;
            case "ValidContactNumber": return ValidContactNumber.class;
            case "ValidEmail": return ValidEmail.class;
            case "ValidOtp": return ValidOtp.class;
            case "ValidEmailDomain": return ValidEmailDomain.class;

            case "FieldMatch": return FieldMatch.class;
            case "AtLeastOneOf": return AtLeastOneOf.class;
            case "DifferentFrom": return DifferentFrom.class;
            case "OnlyOneOf": return OnlyOneOf.class;
            case "RequiredWith": return RequiredWith.class;

            case "ValidAccountNumber": return ValidAccountNumber.class;
            case "ValidBase64": return ValidBase64.class;
            case "ValidHexColor": return ValidHexColor.class;
            case "ValidISOCode": return ValidISOCode.class;
            case "ValidJSON": return ValidJSON.class;
            case "ValidName": return ValidName.class;
            case "ValidNationalID": return ValidNationalID.class;
            case "ValidPassword": return ValidPassword.class;
            case "ValidPIN": return ValidPIN.class;
            case "ValidSlug": return ValidSlug.class;
            case "ValidTaxID": return ValidTaxID.class;
            case "ValidUsername": return ValidUsername.class;

            case "ValidDateTime": return ValidDateTime.class;
            case "ValidDate": return ValidDate.class;
            case "ValidTime": return ValidTime.class;
            case "ValidISO8601": return ValidISO8601.class;
            case "ValidPastDate": return ValidPastDate.class;
            case "ValidFutureDate": return ValidFutureDate.class;
            case "InvalidPastDate": return InvalidPastDate.class;
            case "InvalidFutureDate": return InvalidFutureDate.class;
            case "InvalidPastFutureDate": return InvalidPastFutureDate.class;
            case "DateBefore": return DateBefore.class;
            case "DateAfter": return DateAfter.class;

            case "ValidImageDimensions": return ValidImageDimensions.class;
            case "ValidFileExtension": return ValidFileExtension.class;
            case "ValidFileMimeType": return ValidFileMimeType.class;
            case "ValidFileSize": return ValidFileSize.class;

            case "ValidCardExpiry": return ValidCardExpiry.class;
            case "ValidCardNumber": return ValidCardNumber.class;
            case "ValidCurrencyCode": return ValidCurrencyCode.class;
            case "ValidCVV": return ValidCVV.class;
            case "ValidIBAN": return ValidIBAN.class;
            case "ValidPaymentReference": return ValidPaymentReference.class;
            case "ValidSwiftCode": return ValidSwiftCode.class;
            case "ValidTransactionAmount": return ValidTransactionAmount.class;

            case "ValidIDImage": return ValidIDImage.class;
            case "ValidSelfieImage": return ValidSelfieImage.class;

            case "ValidCoordinates": return ValidCoordinates.class;
            case "ValidLatitude": return ValidLatitude.class;
            case "ValidLongitude": return ValidLongitude.class;
            case "ValidPostalCode": return ValidPostalCode.class;
            case "ValidRTRW": return ValidRTRW.class;

            case "ValidCIDR": return ValidCIDR.class;
            case "ValidIPAddress": return ValidIPAddress.class;
            case "ValidIPv4Address": return ValidIPv4Address.class;
            case "ValidIPv6Address": return ValidIPv6Address.class;
            case "ValidMacAddress": return ValidMacAddress.class;
            case "ValidPort": return ValidPort.class;
            case "ValidURL": return ValidURL.class;
            case "ValidDomainName": return ValidDomainName.class;

            default: return null;
        }
    }
}
