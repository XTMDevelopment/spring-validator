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
import id.xtramile.validator.util.DateUtils;
import jakarta.validation.constraints.*;

import java.util.Map;

import static id.xtramile.validator.enums.Group.*;

final class ConstraintAnnotationMessages {

    private static final String VALIDATION_DEFAULT = "validation.default";

    private final MessageResourceResolver messageResolver;
    private final ValidationFieldDisplayNames fieldNames;

    public ConstraintAnnotationMessages(MessageResourceResolver messageResolver, ValidationFieldDisplayNames fieldNames) {
        this.messageResolver = messageResolver;
        this.fieldNames = fieldNames;
    }

    public String resolveFromAnnotation(String field, Class<?> annotationType, Map<String, Object> attrs, Class<?> dtoClass) {
        if (annotationType == null) {
            return messageResolver.getMessage(VALIDATION_DEFAULT, field);
        }

        String springMessage = springValidatorMessage(field, annotationType, attrs);
        if (springMessage != null) {
            return springMessage;
        }

        String customMessage = customValidatorMessage(field, annotationType, attrs, dtoClass);
        if (customMessage != null) {
            return customMessage;
        }

        return messageResolver.getMessage(VALIDATION_DEFAULT, field);
    }

    private String springValidatorMessage(String field, Class<?> type, Map<String, Object> attrs) {
        if (type == NotBlank.class) {
            return messageResolver.getMessage(SPRING, "not-blank", field);
        }

        if (type == NotNull.class) {
            return messageResolver.getMessage(SPRING, "not-null", field);
        }

        if (type == Null.class) {
            return messageResolver.getMessage(SPRING, "null", field);
        }

        if (type == NotEmpty.class) {
            return messageResolver.getMessage(SPRING, "not-empty", field);
        }

        if (type == Email.class) {
            return messageResolver.getMessage(SPRING, "email", field);
        }

        if (type == Pattern.class) {
            return messageResolver.getMessage(SPRING, "pattern", field);
        }

        if (type == Size.class) {
            Map<String, Object> a = attrs != null ? attrs : Map.of();
            int min = intConstraintAttribute(a, "min", 0);
            int max = intConstraintAttribute(a, "max", Integer.MAX_VALUE);

            if (min > 0 && max < Integer.MAX_VALUE) {
                return messageResolver.getMessage(SPRING, "size", field, min, max);

            } else if (min > 0) {
                return messageResolver.getMessage(SPRING, "size.min", field, min);

            } else {
                return messageResolver.getMessage(SPRING, "size.max", field, max);
            }
        }

        if (type == Min.class) {
            Long value = (Long) attrs.getOrDefault("value", 0L);
            return messageResolver.getMessage(SPRING, "min", field, value);
        }

        if (type == Max.class) {
            Long value = (Long) attrs.getOrDefault("value", Long.MAX_VALUE);
            return messageResolver.getMessage(SPRING, "max", field, value);
        }

        if (type == DecimalMin.class) {
            String value = (String) attrs.getOrDefault("value", "0");

            Boolean inclusive = (Boolean) attrs.getOrDefault("inclusive", Boolean.TRUE);
            String key = inclusive ? "decimal-min" : "decimal-min.exclusive";

            return messageResolver.getMessage(SPRING, key, field, value);
        }

        if (type == DecimalMax.class) {
            String value = (String) attrs.getOrDefault("value", "0");

            Boolean inclusive = (Boolean) attrs.getOrDefault("inclusive", Boolean.TRUE);
            String key = inclusive ? "decimal-max" : "decimal-max.exclusive";

            return messageResolver.getMessage(SPRING, key, field, value);
        }

        if (type == Digits.class) {
            Integer integer = (Integer) attrs.getOrDefault("integer", 0);
            Integer fraction = (Integer) attrs.getOrDefault("fraction", 0);

            return messageResolver.getMessage(SPRING, "digits", field, integer, fraction);
        }

        if (type == Positive.class) {
            return messageResolver.getMessage(SPRING, "positive", field);
        }

        if (type == PositiveOrZero.class) {
            return messageResolver.getMessage(SPRING, "positive-zero", field);
        }

        if (type == Negative.class) {
            return messageResolver.getMessage(SPRING, "negative", field);
        }

        if (type == NegativeOrZero.class) {
            return messageResolver.getMessage(SPRING, "negative-zero", field);
        }

        if (type == AssertTrue.class) {
            return messageResolver.getMessage(SPRING, "true", field);
        }

        if (type == AssertFalse.class) {
            return messageResolver.getMessage(SPRING, "false", field);
        }

        if (type == Past.class) {
            return messageResolver.getMessage(SPRING, "past", field);
        }

        if (type == PastOrPresent.class) {
            return messageResolver.getMessage(SPRING, "past-present", field);
        }

        if (type == Future.class) {
            return messageResolver.getMessage(SPRING, "future", field);
        }

        if (type == FutureOrPresent.class) {
            return messageResolver.getMessage(SPRING, "future-present", field);
        }

        return null;
    }

    private String customValidatorMessage(String field, Class<?> type, Map<String, Object> attrs, Class<?> dtoClass) {
        String common = commonValidatorMessage(field, type, attrs);
        if (common != null) {
            return common;
        }

        String contact = contactValidatorMessage(field, type, attrs);
        if (contact != null) {
            return contact;
        }

        String cross = crossValidatorMessage(field, type, attrs, dtoClass);
        if (cross != null) {
            return cross;
        }

        String data = dataValidatorMessage(field, type);
        if (data != null) {
            return data;
        }

        String dateTime = dateTimeValidatorMessage(field, type, attrs, dtoClass);
        if (dateTime != null) {
            return dateTime;
        }

        String file = fileValidatorMessage(field, type, attrs);
        if (file != null) {
            return file;
        }

        String finance = financeValidatorMessage(field, type);
        if (finance != null) {
            return finance;
        }

        String kyc = kycValidatorMessage(field, type);
        if (kyc != null) {
            return kyc;
        }

        String location = locationValidatorMessage(field, type, attrs);
        if (location != null) {
            return location;
        }

        return networkValidatorMessage(field, type);
    }

    private String commonValidatorMessage(String field, Class<?> type, Map<String, Object> attrs) {
        if (type == InWhitelist.class) {
            String values = ValidationMessageArgsBuilder.joinSortedComma((String[]) attrs.get("values"));

            boolean ignoreCase = (Boolean) attrs.getOrDefault("ignoreCase", true);
            String key = ignoreCase ? "in-whitelist" : "in-whitelist.sensitive";

            return messageResolver.getMessage(COMMON, key, field, values);
        }

        if (type == NotInBlacklist.class) {
            String values = ValidationMessageArgsBuilder.joinSortedComma((String[]) attrs.get("values"));

            boolean ignoreCase = (Boolean) attrs.getOrDefault("ignoreCase", true);
            String key = ignoreCase ? "not-in-blacklist" : "not-in-blacklist.sensitive";

            return messageResolver.getMessage(COMMON, key, field, values);
        }

        if (type == ValidEnum.class) {
            @SuppressWarnings("unchecked")
            Class<? extends Enum<?>> enumClass = (Class<? extends Enum<?>>) attrs.get("enumClass");
            String label = enumClass != null ? enumClass.getSimpleName() : "";

            boolean ignoreCase = (Boolean) attrs.getOrDefault("ignoreCase", true);
            String key = ignoreCase ? "enum" : "enum.sensitive";

            return messageResolver.getMessage(COMMON, key, field, label);
        }

        if (type == UniqueElements.class) {
            return messageResolver.getMessage(COMMON, "unique-elements", field);
        }

        if (type == NotEmptyCollection.class) {
            return messageResolver.getMessage(COMMON, "not-empty-collection", field);
        }

        if (type == ValidUUID.class) {
            return messageResolver.getMessage(COMMON, "uuid", field);
        }

        return null;
    }

    private String contactValidatorMessage(String field, Class<?> type, Map<String, Object> attrs) {
        if (type == ValidPhoneNumber.class) {
            return messageResolver.getMessage(CONTACT, "phone-number", field);
        }

        if (type == ValidContactNumber.class) {
            return messageResolver.getMessage(CONTACT, "contact-number", field);
        }

        if (type == ValidEmail.class) {
            return messageResolver.getMessage(CONTACT, "email", field);
        }

        if (type == ValidOtp.class) {
            return messageResolver.getMessage(CONTACT, "otp", field);
        }

        if (type == ValidEmailDomain.class) {
            String domains = String.join(", ", (String[]) attrs.get("allowed"));

            boolean ignoreCase = (Boolean) attrs.getOrDefault("ignoreCase", true);
            String key = ignoreCase ? "email-domain" : "email-domain.sensitive";

            return messageResolver.getMessage(CONTACT, key, field, domains);
        }

        return null;
    }

    @SuppressWarnings("DuplicatedCode")
    private String crossValidatorMessage(String field, Class<?> type, Map<String, Object> attrs, Class<?> dtoClass) {
        if (type == FieldMatch.class) {
            String first = (String) attrs.get("first");
            String second = (String) attrs.get("second");

            String firstDisplay = fieldNames.resolve(dtoClass, first);
            String secondDisplay = fieldNames.resolve(dtoClass, second);

            return messageResolver.getMessage(CROSS, "field-match", firstDisplay, secondDisplay);
        }

        if (type == AtLeastOneOf.class) {
            String[] fields = (String[]) attrs.get("fields");
            String[] displayNames = new String[fields.length];

            for (int i = 0; i < fields.length; i++) {
                displayNames[i] = fieldNames.resolve(dtoClass, fields[i]);
            }

            String fieldsList = String.join(", ", displayNames);
            return messageResolver.getMessage(CROSS, "at-least-one", fieldsList);
        }

        if (type == DifferentFrom.class) {
            String first = (String) attrs.get("field");
            String second = (String) attrs.get("other");

            String firstDisplay = fieldNames.resolve(dtoClass, first);
            String secondDisplay = fieldNames.resolve(dtoClass, second);

            return messageResolver.getMessage(CROSS, "different-from", firstDisplay, secondDisplay);
        }

        if (type == OnlyOneOf.class) {
            String[] fields = (String[]) attrs.get("fields");
            String[] displayNames = new String[fields.length];

            for (int i = 0; i < fields.length; i++) {
                displayNames[i] = fieldNames.resolve(dtoClass, fields[i]);
            }

            String fieldsList = String.join(", ", displayNames);
            return messageResolver.getMessage(CROSS, "only-one", fieldsList);
        }

        if (type == RequiredWith.class) {
            String[] requiredFields = (String[]) attrs.get("require");
            String[] displayNames = new String[requiredFields.length];

            for (int i = 0; i < requiredFields.length; i++) {
                displayNames[i] = fieldNames.resolve(dtoClass, requiredFields[i]);
            }

            String fieldsList = String.join(", ", displayNames);
            return messageResolver.getMessage(CROSS, "required-with", field, fieldsList);
        }

        return null;
    }

    private String dataValidatorMessage(String field, Class<?> type) {
        if (type == ValidAccountNumber.class) {
            return messageResolver.getMessage(DATA, "account-number", field);
        }

        if (type == ValidBase64.class) {
            return messageResolver.getMessage(DATA, "base64", field);
        }

        if (type == ValidHexColor.class) {
            return messageResolver.getMessage(DATA, "hex-color", field);
        }

        if (type == ValidISOCode.class) {
            return messageResolver.getMessage(DATA, "iso-code", field);
        }

        if (type == ValidJSON.class) {
            return messageResolver.getMessage(DATA, "json", field);
        }

        if (type == ValidName.class) {
            return messageResolver.getMessage(DATA, "name", field);
        }

        if (type == ValidNationalID.class) {
            return messageResolver.getMessage(DATA, "national-id", field);
        }

        if (type == ValidPassword.class) {
            return messageResolver.getMessage(DATA, "password", field);
        }

        if (type == ValidPIN.class) {
            return messageResolver.getMessage(DATA, "pin", field);
        }

        if (type == ValidSlug.class) {
            return messageResolver.getMessage(DATA, "slug", field);
        }

        if (type == ValidTaxID.class) {
            return messageResolver.getMessage(DATA, "tax-id", field);
        }

        if (type == ValidUsername.class) {
            return messageResolver.getMessage(DATA, "username", field);
        }

        return null;
    }

    @SuppressWarnings("DuplicatedCode")
    private String dateTimeValidatorMessage(String field, Class<?> type, Map<String, Object> attrs, Class<?> dtoClass) {
        if (type == ValidDateTime.class) {
            String format = (String) attrs.get("pattern");
            return messageResolver.getMessage(DATETIME, "datetime", field, format);
        }

        if (type == ValidDate.class) {
            String format = (String) attrs.get("pattern");
            return messageResolver.getMessage(DATETIME, "date", field, format);
        }

        if (type == ValidTime.class) {
            String format = (String) attrs.get("pattern");
            return messageResolver.getMessage(DATETIME, "time", field, format);
        }

        if (type == ValidISO8601.class) {
            return messageResolver.getMessage(DATETIME, "iso8601", field);
        }

        if (type == ValidPastDate.class) {
            return messageResolver.getMessage(DATETIME, "past-date", field);
        }

        if (type == ValidFutureDate.class) {
            return messageResolver.getMessage(DATETIME, "future-date", field);
        }

        if (type == InvalidPastDate.class) {
            Integer tolerance = (Integer) attrs.getOrDefault("tolerance", 0);
            if (tolerance == 0) {
                return messageResolver.getMessage(DATETIME, "invalid-past-date", field);
            }

            return messageResolver.getMessage(DATETIME, "invalid-past-date.tolerance", field, tolerance);
        }

        if (type == InvalidFutureDate.class) {
            return messageResolver.getMessage(DATETIME, "invalid-future-date", field);
        }

        if (type == InvalidPastFutureDate.class) {
            Integer toleranceHours = (Integer) attrs.getOrDefault("toleranceHours", 0);

            if (toleranceHours == 0) {
                return messageResolver.getMessage(DATETIME, "invalid-past-future-date", field);
            }

            return messageResolver.getMessage(DATETIME, "invalid-past-future-date.tolerance", field, toleranceHours);
        }

        if (type == DateBefore.class) {
            String first = (String) attrs.get("first");
            String second = (String) attrs.get("second");

            String firstDisplay = fieldNames.resolve(dtoClass, first);
            String secondDisplay = fieldNames.resolve(dtoClass, second);

            Long maxDistance = (Long) attrs.getOrDefault("maxDistance", -1L);

            if (maxDistance >= 0) {
                String precisionStr = DateUtils.pluralLabel((Enum<?>) attrs.get("precision"));
                return messageResolver.getMessage(DATETIME, "date-before.distance", firstDisplay, secondDisplay, maxDistance, precisionStr);
            }

            return messageResolver.getMessage(DATETIME, "date-before", firstDisplay, secondDisplay);
        }

        if (type == DateAfter.class) {
            String first = (String) attrs.get("first");
            String second = (String) attrs.get("second");

            String firstDisplay = fieldNames.resolve(dtoClass, first);
            String secondDisplay = fieldNames.resolve(dtoClass, second);

            Long maxDistance = (Long) attrs.getOrDefault("maxDistance", -1L);

            if (maxDistance >= 0) {
                String precisionStr = DateUtils.pluralLabel((Enum<?>) attrs.get("precision"));
                return messageResolver.getMessage(DATETIME, "date-after.distance", firstDisplay, secondDisplay, maxDistance, precisionStr);
            }

            return messageResolver.getMessage(DATETIME, "date-after", firstDisplay, secondDisplay);
        }

        return null;
    }

    private String fileValidatorMessage(String field, Class<?> type, Map<String, Object> attrs) {
        if (type == ValidImageDimensions.class) {
            Integer minW = (Integer) attrs.get("minWidth");
            Integer minH = (Integer) attrs.get("minHeight");
            Integer maxW = (Integer) attrs.get("maxWidth");
            Integer maxH = (Integer) attrs.get("maxHeight");

            return messageResolver.getMessage(FILE, "image-dimension", field, minW, minH, maxW, maxH);
        }

        if (type == ValidFileExtension.class) {
            String[] extensions = (String[]) attrs.get("allowed");
            String extList = String.join(", ", extensions);

            return messageResolver.getMessage(FILE, "file-extension", field, extList);
        }

        if (type == ValidFileMimeType.class) {
            String[] mimeTypes = (String[]) attrs.get("allowed");
            String mimeList = String.join(", ", mimeTypes);

            return messageResolver.getMessage(FILE, "mime-type", field, mimeList);
        }

        if (type == ValidFileSize.class) {
            Long minBytes = (Long) attrs.getOrDefault("minBytes", 0L);
            Long maxBytes = (Long) attrs.getOrDefault("maxBytes", Long.MAX_VALUE);

            return messageResolver.getMessage(FILE, "file-size", field, minBytes, maxBytes);
        }

        return null;
    }

    private String financeValidatorMessage(String field, Class<?> type) {
        if (type == ValidCardExpiry.class) {
            return messageResolver.getMessage(FINANCE, "card-expiry", field);
        }

        if (type == ValidCardNumber.class) {
            return messageResolver.getMessage(FINANCE, "card-number", field);
        }

        if (type == ValidCurrencyCode.class) {
            return messageResolver.getMessage(FINANCE, "currency-code", field);
        }

        if (type == ValidCVV.class) {
            return messageResolver.getMessage(FINANCE, "cvv", field);
        }

        if (type == ValidIBAN.class) {
            return messageResolver.getMessage(FINANCE, "iban", field);
        }

        if (type == ValidPaymentReference.class) {
            return messageResolver.getMessage(FINANCE, "payment-reference", field);
        }

        if (type == ValidSwiftCode.class) {
            return messageResolver.getMessage(FINANCE, "swift-code", field);
        }

        if (type == ValidTransactionAmount.class) {
            return messageResolver.getMessage(FINANCE, "transaction-amount", field);
        }

        return null;
    }

    private String kycValidatorMessage(String field, Class<?> type) {
        if (type == ValidIDImage.class) {
            return messageResolver.getMessage(KYC, "image-id", field);
        }

        if (type == ValidSelfieImage.class) {
            return messageResolver.getMessage(KYC, "image-selfie", field);
        }

        return null;
    }

    private String locationValidatorMessage(String field, Class<?> type, Map<String, Object> attrs) {
        if (type == ValidCoordinates.class) {
            boolean flipCoordinates = (Boolean) attrs.getOrDefault("flipCoordinates", false);
            String format = flipCoordinates ? "latitude, longitude" : "longitude, latitude";

            return messageResolver.getMessage(LOCATION, "coordinates", field, format);
        }

        if (type == ValidLatitude.class) {
            return messageResolver.getMessage(LOCATION, "latitude", field);
        }

        if (type == ValidLongitude.class) {
            return messageResolver.getMessage(LOCATION, "longitude", field);
        }

        if (type == ValidPostalCode.class) {
            return messageResolver.getMessage(LOCATION, "postal-code", field);
        }

        if (type == ValidRTRW.class) {
            return messageResolver.getMessage(LOCATION, "rt-rw", field);
        }

        return null;
    }

    private String networkValidatorMessage(String field, Class<?> type) {
        if (type == ValidCIDR.class) {
            return messageResolver.getMessage(NETWORK, "cidr", field);
        }

        if (type == ValidIPAddress.class) {
            return messageResolver.getMessage(NETWORK, "ip-address", field);
        }

        if (type == ValidIPv4Address.class) {
            return messageResolver.getMessage(NETWORK, "ipv4-address", field);
        }

        if (type == ValidIPv6Address.class) {
            return messageResolver.getMessage(NETWORK, "ipv6-address", field);
        }

        if (type == ValidMacAddress.class) {
            return messageResolver.getMessage(NETWORK, "mac-address", field);
        }

        if (type == ValidPort.class) {
            return messageResolver.getMessage(NETWORK, "port", field);
        }

        if (type == ValidURL.class) {
            return messageResolver.getMessage(NETWORK, "url", field);
        }

        if (type == ValidDomainName.class) {
            return messageResolver.getMessage(NETWORK, "domain-name", field);
        }

        return null;
    }

    private static int intConstraintAttribute(Map<String, Object> attrs, String key, int defaultValue) {
        Object v = attrs.get(key);
        if (v instanceof Number) {
            return ((Number) v).intValue();
        }

        return defaultValue;
    }
}
