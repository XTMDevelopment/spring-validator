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
import id.xtramile.validator.enums.Group;
import jakarta.validation.constraints.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class AnnotationRegistry {

    public record Entry(String simpleName, Class<?> annotationType, Group messageGroup) {}

    private static final Map<String, Class<?>> BY_NAME;
    private static final Map<Class<?>, Entry> BY_CLASS;

    static {
        Map<String, Class<?>> byName = new HashMap<>();
        Map<Class<?>, Entry> byClass = new HashMap<>();

        register(byName, byClass, NotBlank.class, "NotBlank", Group.SPRING);
        register(byName, byClass, NotNull.class, "NotNull", Group.SPRING);
        register(byName, byClass, Null.class, "Null", Group.SPRING);
        register(byName, byClass, NotEmpty.class, "NotEmpty", Group.SPRING);
        register(byName, byClass, Email.class, "Email", Group.SPRING);
        register(byName, byClass, Pattern.class, "Pattern", Group.SPRING);
        register(byName, byClass, Size.class, "Size", Group.SPRING);
        register(byName, byClass, Min.class, "Min", Group.SPRING);
        register(byName, byClass, Max.class, "Max", Group.SPRING);
        register(byName, byClass, DecimalMin.class, "DecimalMin", Group.SPRING);
        register(byName, byClass, DecimalMax.class, "DecimalMax", Group.SPRING);
        register(byName, byClass, Digits.class, "Digits", Group.SPRING);
        register(byName, byClass, Positive.class, "Positive", Group.SPRING);
        register(byName, byClass, PositiveOrZero.class, "PositiveOrZero", Group.SPRING);
        register(byName, byClass, Negative.class, "Negative", Group.SPRING);
        register(byName, byClass, NegativeOrZero.class, "NegativeOrZero", Group.SPRING);
        register(byName, byClass, AssertTrue.class, "AssertTrue", Group.SPRING);
        register(byName, byClass, AssertFalse.class, "AssertFalse", Group.SPRING);
        register(byName, byClass, Past.class, "Past", Group.SPRING);
        register(byName, byClass, PastOrPresent.class, "PastOrPresent", Group.SPRING);
        register(byName, byClass, Future.class, "Future", Group.SPRING);
        register(byName, byClass, FutureOrPresent.class, "FutureOrPresent", Group.SPRING);

        register(byName, byClass, InWhitelist.class, "InWhitelist", Group.COMMON);
        register(byName, byClass, NotInBlacklist.class, "NotInBlacklist", Group.COMMON);
        register(byName, byClass, ValidEnum.class, "ValidEnum", Group.COMMON);
        register(byName, byClass, UniqueElements.class, "UniqueElements", Group.COMMON);
        register(byName, byClass, NotEmptyCollection.class, "NotEmptyCollection", Group.COMMON);
        register(byName, byClass, ValidUUID.class, "ValidUUID", Group.COMMON);

        register(byName, byClass, ValidPhoneNumber.class, "ValidPhoneNumber", Group.CONTACT);
        register(byName, byClass, ValidContactNumber.class, "ValidContactNumber", Group.CONTACT);
        register(byName, byClass, ValidEmail.class, "ValidEmail", Group.CONTACT);
        register(byName, byClass, ValidOtp.class, "ValidOtp", Group.CONTACT);
        register(byName, byClass, ValidEmailDomain.class, "ValidEmailDomain", Group.CONTACT);

        register(byName, byClass, FieldMatch.class, "FieldMatch", Group.CROSS);
        register(byName, byClass, AtLeastOneOf.class, "AtLeastOneOf", Group.CROSS);
        register(byName, byClass, DifferentFrom.class, "DifferentFrom", Group.CROSS);
        register(byName, byClass, OnlyOneOf.class, "OnlyOneOf", Group.CROSS);
        register(byName, byClass, RequiredWith.class, "RequiredWith", Group.CROSS);

        register(byName, byClass, ValidAccountNumber.class, "ValidAccountNumber", Group.DATA);
        register(byName, byClass, ValidBase64.class, "ValidBase64", Group.DATA);
        register(byName, byClass, ValidHexColor.class, "ValidHexColor", Group.DATA);
        register(byName, byClass, ValidISOCode.class, "ValidISOCode", Group.DATA);
        register(byName, byClass, ValidJSON.class, "ValidJSON", Group.DATA);
        register(byName, byClass, ValidName.class, "ValidName", Group.DATA);
        register(byName, byClass, ValidNationalID.class, "ValidNationalID", Group.DATA);
        register(byName, byClass, ValidPassword.class, "ValidPassword", Group.DATA);
        register(byName, byClass, ValidPIN.class, "ValidPIN", Group.DATA);
        register(byName, byClass, ValidSlug.class, "ValidSlug", Group.DATA);
        register(byName, byClass, ValidTaxID.class, "ValidTaxID", Group.DATA);
        register(byName, byClass, ValidUsername.class, "ValidUsername", Group.DATA);

        register(byName, byClass, ValidDateTime.class, "ValidDateTime", Group.DATETIME);
        register(byName, byClass, ValidDate.class, "ValidDate", Group.DATETIME);
        register(byName, byClass, ValidTime.class, "ValidTime", Group.DATETIME);
        register(byName, byClass, ValidISO8601.class, "ValidISO8601", Group.DATETIME);
        register(byName, byClass, ValidPastDate.class, "ValidPastDate", Group.DATETIME);
        register(byName, byClass, ValidFutureDate.class, "ValidFutureDate", Group.DATETIME);
        register(byName, byClass, InvalidPastDate.class, "InvalidPastDate", Group.DATETIME);
        register(byName, byClass, InvalidFutureDate.class, "InvalidFutureDate", Group.DATETIME);
        register(byName, byClass, InvalidPastFutureDate.class, "InvalidPastFutureDate", Group.DATETIME);
        register(byName, byClass, DateBefore.class, "DateBefore", Group.DATETIME);
        register(byName, byClass, DateAfter.class, "DateAfter", Group.DATETIME);

        register(byName, byClass, ValidImageDimensions.class, "ValidImageDimensions", Group.FILE);
        register(byName, byClass, ValidFileExtension.class, "ValidFileExtension", Group.FILE);
        register(byName, byClass, ValidFileMimeType.class, "ValidFileMimeType", Group.FILE);
        register(byName, byClass, ValidFileSize.class, "ValidFileSize", Group.FILE);

        register(byName, byClass, ValidCardExpiry.class, "ValidCardExpiry", Group.FINANCE);
        register(byName, byClass, ValidCardNumber.class, "ValidCardNumber", Group.FINANCE);
        register(byName, byClass, ValidCurrencyCode.class, "ValidCurrencyCode", Group.FINANCE);
        register(byName, byClass, ValidCVV.class, "ValidCVV", Group.FINANCE);
        register(byName, byClass, ValidIBAN.class, "ValidIBAN", Group.FINANCE);
        register(byName, byClass, ValidPaymentReference.class, "ValidPaymentReference", Group.FINANCE);
        register(byName, byClass, ValidSwiftCode.class, "ValidSwiftCode", Group.FINANCE);
        register(byName, byClass, ValidTransactionAmount.class, "ValidTransactionAmount", Group.FINANCE);

        register(byName, byClass, ValidIDImage.class, "ValidIDImage", Group.KYC);
        register(byName, byClass, ValidSelfieImage.class, "ValidSelfieImage", Group.KYC);

        register(byName, byClass, ValidCoordinates.class, "ValidCoordinates", Group.LOCATION);
        register(byName, byClass, ValidLatitude.class, "ValidLatitude", Group.LOCATION);
        register(byName, byClass, ValidLongitude.class, "ValidLongitude", Group.LOCATION);
        register(byName, byClass, ValidPostalCode.class, "ValidPostalCode", Group.LOCATION);
        register(byName, byClass, ValidRTRW.class, "ValidRTRW", Group.LOCATION);

        register(byName, byClass, ValidCIDR.class, "ValidCIDR", Group.NETWORK);
        register(byName, byClass, ValidIPAddress.class, "ValidIPAddress", Group.NETWORK);
        register(byName, byClass, ValidIPv4Address.class, "ValidIPv4Address", Group.NETWORK);
        register(byName, byClass, ValidIPv6Address.class, "ValidIPv6Address", Group.NETWORK);
        register(byName, byClass, ValidMacAddress.class, "ValidMacAddress", Group.NETWORK);
        register(byName, byClass, ValidPort.class, "ValidPort", Group.NETWORK);
        register(byName, byClass, ValidURL.class, "ValidURL", Group.NETWORK);
        register(byName, byClass, ValidDomainName.class, "ValidDomainName", Group.NETWORK);

        BY_NAME = Collections.unmodifiableMap(byName);
        BY_CLASS = Collections.unmodifiableMap(byClass);
    }

    private AnnotationRegistry() {}

    private static void register(Map<String, Class<?>> byName, Map<Class<?>, Entry> byClass,
                                 Class<?> type, String simpleName, Group group) {
        Entry entry = new Entry(simpleName, type, group);
        byClass.put(type, entry);
        byName.put(simpleName, type);
    }

    public static Class<?> resolve(String simpleName) {
        if (simpleName == null) {
            return null;
        }
        return BY_NAME.get(simpleName);
    }

    public static Entry get(Class<?> annotationType) {
        return BY_CLASS.get(annotationType);
    }

    public static Group getGroup(Class<?> annotationType) {
        Entry entry = get(annotationType);
        return entry != null ? entry.messageGroup() : null;
    }

    public static String getSimpleName(Class<?> annotationType) {
        Entry entry = get(annotationType);
        return entry != null ? entry.simpleName() : null;
    }

    public static boolean isInGroup(Class<?> annotationType, Group group) {
        Entry entry = get(annotationType);
        return entry != null && entry.messageGroup() == group;
    }

    public static Set<Class<?>> getAnnotationTypes(Group group) {
        return BY_CLASS.values().stream()
                .filter(entry -> entry.messageGroup() == group)
                .map(Entry::annotationType)
                .collect(java.util.stream.Collectors.toUnmodifiableSet());
    }
}
