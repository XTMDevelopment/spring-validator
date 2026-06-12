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
import id.xtramile.validator.enums.DatePrecision;
import id.xtramile.validator.web.messages.CompositeConstraintMessageResolver;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.constraints.*;
import jakarta.validation.metadata.ConstraintDescriptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.validation.FieldError;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FriendlyMessageResolverTest {

    private FriendlyMessageResolver resolver;
    private CompositeConstraintMessageResolver annotationMessages;

    @BeforeEach
    void setUp() {
        MessageResourceResolver messageResolver = new MessageResourceResolver("en");
        resolver = new FriendlyMessageResolver(messageResolver);
        ValidationFieldDisplayNames fieldNames = new ValidationFieldDisplayNames();
        annotationMessages = new CompositeConstraintMessageResolver(messageResolver, fieldNames);
    }

    @Test
    void shouldResolveConstraintViolationWithCustomMessage() {
        // Given
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        when(violation.getMessageTemplate()).thenReturn("{custom.message}");
        when(violation.getMessage()).thenReturn("Custom error message");

        // When
        String result = resolver.resolve(violation, "testField", Object.class);

        // Then
        assertThat(result).isEqualTo("Custom error message");
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    void shouldResolveConstraintViolationWithDefaultTemplate() {
        // Given
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        when(violation.getMessageTemplate()).thenReturn("{jakarta.validation.constraints.NotNull.message}");
        
        ConstraintDescriptor descriptor = mock(ConstraintDescriptor.class);
        when(violation.getConstraintDescriptor()).thenReturn(descriptor);
        
        Annotation annotation = mock(Annotation.class);
        when(descriptor.getAnnotation()).thenReturn(annotation);
        when(annotation.annotationType()).thenAnswer(invocation -> NotNull.class);
        
        Map<String, Object> attributes = new HashMap<>();
        when(descriptor.getAttributes()).thenReturn(attributes);

        // When
        String result = resolver.resolve(violation, "testField", Object.class);

        // Then
        assertThat(result).isEqualTo("testField is required");
    }

    @Test
    void shouldResolveFieldErrorWithDefaultMessage() {
        // Given
        FieldError fieldError = mock(FieldError.class);
        when(fieldError.getField()).thenReturn("testField");
        when(fieldError.getDefaultMessage()).thenReturn("Default error message");

        // When
        String result = resolver.resolve(fieldError, Object.class);

        // Then
        assertThat(result).isEqualTo("Default error message");
    }

    @Test
    void shouldResolveFieldErrorWithFriendlyDefault() {
        // Given
        FieldError fieldError = mock(FieldError.class);
        when(fieldError.getField()).thenReturn("testField");
        when(fieldError.getDefaultMessage()).thenReturn("{friendly.default}");
        when(fieldError.getCode()).thenReturn("NotNull");

        // When
        String result = resolver.resolve(fieldError, Object.class);

        // Then
        assertThat(result).isEqualTo("testField is required");
    }

    @Test
    void shouldResolveFieldErrorWithEmptyDefaultMessage() {
        // Given
        FieldError fieldError = mock(FieldError.class);
        when(fieldError.getField()).thenReturn("testField");
        when(fieldError.getDefaultMessage()).thenReturn("");

        // When
        String result = resolver.resolve(fieldError, Object.class);

        // Then
        assertThat(result).isEqualTo("testField is invalid");
    }

    @Test
    void shouldResolveFromAnnotationWithSpringValidators() {
        // Test NotNull
        String result = annotationMessages.resolveFromAnnotation("testField", NotNull.class, new HashMap<>(), Object.class);
        assertThat(result).isEqualTo("testField is required");

        // Test NotBlank
        result = annotationMessages.resolveFromAnnotation("testField", NotBlank.class, new HashMap<>(), Object.class);
        assertThat(result).isEqualTo("testField is required");

        // Test Email
        result = annotationMessages.resolveFromAnnotation("testField", Email.class, new HashMap<>(), Object.class);
        assertThat(result).isEqualTo("testField must be a valid email address");

        // Test Size
        Map<String, Object> sizeAttrs = new HashMap<>();
        sizeAttrs.put("min", 5);
        sizeAttrs.put("max", 10);
        result = annotationMessages.resolveFromAnnotation("testField", Size.class, sizeAttrs, Object.class);
        assertThat(result).isEqualTo("testField must be between 5 and 10 characters");

        // Test Min
        Map<String, Object> minAttrs = new HashMap<>();
        minAttrs.put("value", 10L);
        result = annotationMessages.resolveFromAnnotation("testField", Min.class, minAttrs, Object.class);
        assertThat(result).isEqualTo("testField must be at least 10");

        // Test Max
        Map<String, Object> maxAttrs = new HashMap<>();
        maxAttrs.put("value", 100L);
        result = annotationMessages.resolveFromAnnotation("testField", Max.class, maxAttrs, Object.class);
        assertThat(result).isEqualTo("testField must be at most 100");

        // Test Positive
        result = annotationMessages.resolveFromAnnotation("testField", Positive.class, new HashMap<>(), Object.class);
        assertThat(result).isEqualTo("testField must be greater than 0");

        // Test Past
        result = annotationMessages.resolveFromAnnotation("testField", Past.class, new HashMap<>(), Object.class);
        assertThat(result).isEqualTo("testField must be in the past");

        // Test Future
        result = annotationMessages.resolveFromAnnotation("testField", Future.class, new HashMap<>(), Object.class);
        assertThat(result).isEqualTo("testField must be in the future");
    }

    @Test
    void shouldResolveFromAnnotationWithCommonValidators() {
        // Test InWhitelist
        Map<String, Object> whitelistAttrs = new HashMap<>();
        whitelistAttrs.put("values", new String[]{"value1", "value2"});
        whitelistAttrs.put("ignoreCase", true);
        String result = annotationMessages.resolveFromAnnotation("testField", InWhitelist.class, whitelistAttrs, Object.class);
        assertThat(result).isEqualTo("testField must be one of: value1, value2");

        // Test ValidEnum (Path B: annotation metadata only — uses enum class simple name)
        Map<String, Object> enumAttrs = new HashMap<>();
        enumAttrs.put("enumClass", id.xtramile.validator.enums.ISOType.class);
        enumAttrs.put("ignoreCase", true);
        result = annotationMessages.resolveFromAnnotation("testField", ValidEnum.class, enumAttrs, Object.class);
        assertThat(result).isEqualTo("testField must be one of the values in ISOType");

        // Test UniqueElements
        result = annotationMessages.resolveFromAnnotation("testField", UniqueElements.class, new HashMap<>(), Object.class);
        assertThat(result).isEqualTo("testField must have unique elements");

        // Test NotEmptyCollection
        result = annotationMessages.resolveFromAnnotation("testField", NotEmptyCollection.class, new HashMap<>(), Object.class);
        assertThat(result).isEqualTo("testField must not be empty");

        // Test ValidUUID
        result = annotationMessages.resolveFromAnnotation("testField", ValidUUID.class, new HashMap<>(), Object.class);
        assertThat(result).isEqualTo("testField must be a valid UUID");
    }

    @Test
    void shouldResolveFromAnnotationWithContactValidators() {
        // Test ValidPhoneNumber
        String result = annotationMessages.resolveFromAnnotation("testField", ValidPhoneNumber.class, new HashMap<>(), Object.class);
        assertThat(result).isEqualTo("testField must be a valid phone number");

        // Test ValidEmail
        result = annotationMessages.resolveFromAnnotation("testField", ValidEmail.class, new HashMap<>(), Object.class);
        assertThat(result).isEqualTo("testField must be a valid email address");

        // Test ValidOtp
        result = annotationMessages.resolveFromAnnotation("testField", ValidOtp.class, new HashMap<>(), Object.class);
        assertThat(result).isEqualTo("testField must be a valid OTP code");

        // Test ValidEmailDomain
        Map<String, Object> domainAttrs = new HashMap<>();
        domainAttrs.put("allowed", new String[]{"gmail.com", "yahoo.com"});
        domainAttrs.put("ignoreCase", true);
        result = annotationMessages.resolveFromAnnotation("testField", ValidEmailDomain.class, domainAttrs, Object.class);
        assertThat(result).isEqualTo("testField must be a valid email address with domain(s): gmail.com, yahoo.com");
    }

    @Test
    void shouldResolveFromAnnotationWithCrossValidators() {
        // Test FieldMatch
        Map<String, Object> fieldMatchAttrs = new HashMap<>();
        fieldMatchAttrs.put("first", "password");
        fieldMatchAttrs.put("second", "confirmPassword");
        String result = annotationMessages.resolveFromAnnotation("testField", FieldMatch.class, fieldMatchAttrs, Object.class);
        assertThat(result).isEqualTo("password and confirmPassword must match");

        // Test AtLeastOneOf
        Map<String, Object> atLeastOneAttrs = new HashMap<>();
        atLeastOneAttrs.put("fields", new String[]{"email", "phone"});
        result = annotationMessages.resolveFromAnnotation("testField", AtLeastOneOf.class, atLeastOneAttrs, Object.class);
        assertThat(result).isEqualTo("At least one of the following fields must be provided: email, phone");

        // Test OnlyOneOf
        Map<String, Object> onlyOneAttrs = new HashMap<>();
        onlyOneAttrs.put("fields", new String[]{"option1", "option2"});
        result = annotationMessages.resolveFromAnnotation("testField", OnlyOneOf.class, onlyOneAttrs, Object.class);
        assertThat(result).isEqualTo("Only one of the following fields can be provided: option1, option2");
    }

    @Test
    void shouldResolveFromAnnotationWithDataValidators() {
        // Test ValidAccountNumber
        String result = annotationMessages.resolveFromAnnotation("testField", ValidAccountNumber.class, new HashMap<>(), Object.class);
        assertThat(result).isEqualTo("testField must be a valid account number");

        // Test ValidBase64
        result = annotationMessages.resolveFromAnnotation("testField", ValidBase64.class, new HashMap<>(), Object.class);
        assertThat(result).isEqualTo("testField must be a valid Base64 encoded string");

        // Test ValidHexColor
        result = annotationMessages.resolveFromAnnotation("testField", ValidHexColor.class, new HashMap<>(), Object.class);
        assertThat(result).isEqualTo("testField must be a valid hex color code");

        // Test ValidJSON
        result = annotationMessages.resolveFromAnnotation("testField", ValidJSON.class, new HashMap<>(), Object.class);
        assertThat(result).isEqualTo("testField must be a valid JSON object");

        // Test ValidName
        result = annotationMessages.resolveFromAnnotation("testField", ValidName.class, new HashMap<>(), Object.class);
        assertThat(result).isEqualTo("testField must be a valid name");

        // Test ValidPassword
        result = annotationMessages.resolveFromAnnotation("testField", ValidPassword.class, new HashMap<>(), Object.class);
        assertThat(result).isEqualTo("testField must be a valid password");

        // Test ValidUsername
        result = annotationMessages.resolveFromAnnotation("testField", ValidUsername.class, new HashMap<>(), Object.class);
        assertThat(result).isEqualTo("testField must be a valid username");
    }

    @Test
    void shouldResolveFromAnnotationWithDateTimeValidators() {
        // Test ValidDateTime
        Map<String, Object> dateTimeAttrs = new HashMap<>();
        dateTimeAttrs.put("pattern", "yyyy-MM-dd HH:mm:ss");
        String result = annotationMessages.resolveFromAnnotation("testField", ValidDateTime.class, dateTimeAttrs, Object.class);
        assertThat(result).isEqualTo("testField must be a valid date-time in format: yyyy-MM-dd HH:mm:ss");

        // Test ValidDate
        Map<String, Object> dateAttrs = new HashMap<>();
        dateAttrs.put("pattern", "yyyy-MM-dd");
        result = annotationMessages.resolveFromAnnotation("testField", ValidDate.class, dateAttrs, Object.class);
        assertThat(result).isEqualTo("testField must be a valid date in format: yyyy-MM-dd");

        // Test ValidISO8601
        result = annotationMessages.resolveFromAnnotation("testField", ValidISO8601.class, new HashMap<>(), Object.class);
        assertThat(result).isEqualTo("testField must be a valid ISO 8601");

        // Test ValidPastDate
        result = annotationMessages.resolveFromAnnotation("testField", ValidPastDate.class, new HashMap<>(), Object.class);
        assertThat(result).isEqualTo("testField must be a date in the past");

        // Test ValidFutureDate
        result = annotationMessages.resolveFromAnnotation("testField", ValidFutureDate.class, new HashMap<>(), Object.class);
        assertThat(result).isEqualTo("testField must be a date in the future");

        // Test InvalidPastDate with tolerance
        Map<String, Object> invalidPastAttrs = new HashMap<>();
        invalidPastAttrs.put("tolerance", 5);
        result = annotationMessages.resolveFromAnnotation("testField", InvalidPastDate.class, invalidPastAttrs, Object.class);
        assertThat(result).isEqualTo("testField must be a date in the past within 5 hours");

        // Test InvalidPastDate without tolerance
        result = annotationMessages.resolveFromAnnotation("testField", InvalidPastDate.class, new HashMap<>(), Object.class);
        assertThat(result).isEqualTo("testField must be a date equals today");

        // Test InvalidFutureDate
        result = annotationMessages.resolveFromAnnotation("testField", InvalidFutureDate.class, new HashMap<>(), Object.class);
        assertThat(result).isEqualTo("testField must be a date equals today");
    }

    @Test
    void shouldResolveFromAnnotationWithFileValidators() {
        // Test ValidImageDimensions
        Map<String, Object> imageAttrs = new HashMap<>();
        imageAttrs.put("minWidth", 100);
        imageAttrs.put("minHeight", 100);
        imageAttrs.put("maxWidth", 2000);
        imageAttrs.put("maxHeight", 2000);
        String result = annotationMessages.resolveFromAnnotation("testField", ValidImageDimensions.class, imageAttrs, Object.class);
        assertThat(result).isEqualTo("testField must have dimensions between 100x100 and 2,000x2,000");

        // Test ValidFileExtension
        Map<String, Object> extensionAttrs = new HashMap<>();
        extensionAttrs.put("allowed", new String[]{"jpg", "png", "gif"});
        result = annotationMessages.resolveFromAnnotation("testField", ValidFileExtension.class, extensionAttrs, Object.class);
        assertThat(result).isEqualTo("testField must have one of the following extensions: jpg, png, gif");

        // Test ValidFileMimeType
        Map<String, Object> mimeAttrs = new HashMap<>();
        mimeAttrs.put("allowed", new String[]{"image/jpeg", "image/png"});
        result = annotationMessages.resolveFromAnnotation("testField", ValidFileMimeType.class, mimeAttrs, Object.class);
        assertThat(result).isEqualTo("testField must have one of the following MIME types: image/jpeg, image/png");

        // Test ValidFileSize
        Map<String, Object> sizeAttrs = new HashMap<>();
        sizeAttrs.put("minBytes", 1024L);
        sizeAttrs.put("maxBytes", 10485760L);
        result = annotationMessages.resolveFromAnnotation("testField", ValidFileSize.class, sizeAttrs, Object.class);
        assertThat(result).isEqualTo("testField must be between 1,024 and 10,485,760 bytes");
    }

    @Test
    void shouldResolveFromAnnotationWithFinanceValidators() {
        // Test ValidCardExpiry
        String result = annotationMessages.resolveFromAnnotation("testField", ValidCardExpiry.class, new HashMap<>(), Object.class);
        assertThat(result).isEqualTo("testField must be a valid card expiry date");

        // Test ValidCardNumber
        result = annotationMessages.resolveFromAnnotation("testField", ValidCardNumber.class, new HashMap<>(), Object.class);
        assertThat(result).isEqualTo("testField must be a valid card number");

        // Test ValidCurrencyCode
        result = annotationMessages.resolveFromAnnotation("testField", ValidCurrencyCode.class, new HashMap<>(), Object.class);
        assertThat(result).isEqualTo("testField must be a valid currency code");

        // Test ValidCVV
        result = annotationMessages.resolveFromAnnotation("testField", ValidCVV.class, new HashMap<>(), Object.class);
        assertThat(result).isEqualTo("testField must be a valid CVV");

        // Test ValidIBAN
        result = annotationMessages.resolveFromAnnotation("testField", ValidIBAN.class, new HashMap<>(), Object.class);
        assertThat(result).isEqualTo("testField must be a valid IBAN");

        // Test ValidPaymentReference
        result = annotationMessages.resolveFromAnnotation("testField", ValidPaymentReference.class, new HashMap<>(), Object.class);
        assertThat(result).isEqualTo("testField must be a valid payment reference");

        // Test ValidSwiftCode
        result = annotationMessages.resolveFromAnnotation("testField", ValidSwiftCode.class, new HashMap<>(), Object.class);
        assertThat(result).isEqualTo("testField must be a valid swift code");

        // Test ValidTransactionAmount
        result = annotationMessages.resolveFromAnnotation("testField", ValidTransactionAmount.class, new HashMap<>(), Object.class);
        assertThat(result).isEqualTo("testField must be a valid transaction amount");
    }

    @Test
    void shouldResolveFromAnnotationWithKycValidators() {
        // Test ValidIDImage
        String result = annotationMessages.resolveFromAnnotation("testField", ValidIDImage.class, new HashMap<>(), Object.class);
        assertThat(result).isEqualTo("testField must be a valid ID image");

        // Test ValidSelfieImage
        result = annotationMessages.resolveFromAnnotation("testField", ValidSelfieImage.class, new HashMap<>(), Object.class);
        assertThat(result).isEqualTo("testField must be a valid selfie image");
    }

    @Test
    void shouldResolveFromAnnotationWithLocationValidators() {
        // Test ValidCoordinates
        Map<String, Object> coordAttrs = new HashMap<>();
        coordAttrs.put("flipCoordinates", false);
        String result = annotationMessages.resolveFromAnnotation("testField", ValidCoordinates.class, coordAttrs, Object.class);
        assertThat(result).isEqualTo("testField must be valid coordinates in format: longitude, latitude");

        // Test ValidCoordinates with flip
        coordAttrs.put("flipCoordinates", true);
        result = annotationMessages.resolveFromAnnotation("testField", ValidCoordinates.class, coordAttrs, Object.class);
        assertThat(result).isEqualTo("testField must be valid coordinates in format: latitude, longitude");

        // Test ValidLatitude
        result = annotationMessages.resolveFromAnnotation("testField", ValidLatitude.class, new HashMap<>(), Object.class);
        assertThat(result).isEqualTo("testField must be a valid latitude");

        // Test ValidLongitude
        result = annotationMessages.resolveFromAnnotation("testField", ValidLongitude.class, new HashMap<>(), Object.class);
        assertThat(result).isEqualTo("testField must be a valid longitude");

        // Test ValidPostalCode
        result = annotationMessages.resolveFromAnnotation("testField", ValidPostalCode.class, new HashMap<>(), Object.class);
        assertThat(result).isEqualTo("testField must be a valid postal code");

        // Test ValidRTRW
        result = annotationMessages.resolveFromAnnotation("testField", ValidRTRW.class, new HashMap<>(), Object.class);
        assertThat(result).isEqualTo("testField must be a valid RT/RW number");
    }

    @Test
    void shouldResolveFromAnnotationWithNetworkValidators() {
        // Test ValidCIDR
        String result = annotationMessages.resolveFromAnnotation("testField", ValidCIDR.class, new HashMap<>(), Object.class);
        assertThat(result).isEqualTo("testField must be a valid CIDR notation");

        // Test ValidIPAddress
        result = annotationMessages.resolveFromAnnotation("testField", ValidIPAddress.class, new HashMap<>(), Object.class);
        assertThat(result).isEqualTo("testField must be a valid IP address");

        // Test ValidIPv4Address
        result = annotationMessages.resolveFromAnnotation("testField", ValidIPv4Address.class, new HashMap<>(), Object.class);
        assertThat(result).isEqualTo("testField must be a valid IPv4 address");

        // Test ValidIPv6Address
        result = annotationMessages.resolveFromAnnotation("testField", ValidIPv6Address.class, new HashMap<>(), Object.class);
        assertThat(result).isEqualTo("testField must be a valid IPv6 address");

        // Test ValidMacAddress
        result = annotationMessages.resolveFromAnnotation("testField", ValidMacAddress.class, new HashMap<>(), Object.class);
        assertThat(result).isEqualTo("testField must be a valid MAC address");

        // Test ValidPort
        result = annotationMessages.resolveFromAnnotation("testField", ValidPort.class, new HashMap<>(), Object.class);
        assertThat(result).isEqualTo("testField must be a valid port");

        // Test ValidURL
        result = annotationMessages.resolveFromAnnotation("testField", ValidURL.class, new HashMap<>(), Object.class);
        assertThat(result).isEqualTo("testField must be a valid URL");

        // Test ValidDomainName
        result = annotationMessages.resolveFromAnnotation("testField", ValidDomainName.class, new HashMap<>(), Object.class);
        assertThat(result).isEqualTo("testField must be a valid domain name");
    }

    @Test
    void shouldResolveAnnotationType() {
        // Test Jakarta built-in annotations
        assertThat(ValidationAnnotationTypeRegistry.resolve("NotBlank")).isEqualTo(NotBlank.class);
        assertThat(ValidationAnnotationTypeRegistry.resolve("NotNull")).isEqualTo(NotNull.class);
        assertThat(ValidationAnnotationTypeRegistry.resolve("Email")).isEqualTo(Email.class);
        assertThat(ValidationAnnotationTypeRegistry.resolve("Size")).isEqualTo(Size.class);

        // Test Common validators
        assertThat(ValidationAnnotationTypeRegistry.resolve("InWhitelist")).isEqualTo(InWhitelist.class);
        assertThat(ValidationAnnotationTypeRegistry.resolve("ValidEnum")).isEqualTo(ValidEnum.class);
        assertThat(ValidationAnnotationTypeRegistry.resolve("ValidUUID")).isEqualTo(ValidUUID.class);

        // Test Contact validators
        assertThat(ValidationAnnotationTypeRegistry.resolve("ValidPhoneNumber")).isEqualTo(ValidPhoneNumber.class);
        assertThat(ValidationAnnotationTypeRegistry.resolve("ValidEmail")).isEqualTo(ValidEmail.class);

        // Test Cross validators
        assertThat(ValidationAnnotationTypeRegistry.resolve("FieldMatch")).isEqualTo(FieldMatch.class);
        assertThat(ValidationAnnotationTypeRegistry.resolve("AtLeastOneOf")).isEqualTo(AtLeastOneOf.class);

        // Test Data validators
        assertThat(ValidationAnnotationTypeRegistry.resolve("ValidAccountNumber")).isEqualTo(ValidAccountNumber.class);
        assertThat(ValidationAnnotationTypeRegistry.resolve("ValidJSON")).isEqualTo(ValidJSON.class);

        // Test DateTime validators
        assertThat(ValidationAnnotationTypeRegistry.resolve("ValidDateTime")).isEqualTo(ValidDateTime.class);
        assertThat(ValidationAnnotationTypeRegistry.resolve("ValidDate")).isEqualTo(ValidDate.class);

        // Test File validators
        assertThat(ValidationAnnotationTypeRegistry.resolve("ValidFileExtension")).isEqualTo(ValidFileExtension.class);
        assertThat(ValidationAnnotationTypeRegistry.resolve("ValidImageDimensions")).isEqualTo(ValidImageDimensions.class);

        // Test Finance validators
        assertThat(ValidationAnnotationTypeRegistry.resolve("ValidCardNumber")).isEqualTo(ValidCardNumber.class);
        assertThat(ValidationAnnotationTypeRegistry.resolve("ValidIBAN")).isEqualTo(ValidIBAN.class);

        // Test KYC validators
        assertThat(ValidationAnnotationTypeRegistry.resolve("ValidIDImage")).isEqualTo(ValidIDImage.class);
        assertThat(ValidationAnnotationTypeRegistry.resolve("ValidSelfieImage")).isEqualTo(ValidSelfieImage.class);

        // Test Location validators
        assertThat(ValidationAnnotationTypeRegistry.resolve("ValidCoordinates")).isEqualTo(ValidCoordinates.class);
        assertThat(ValidationAnnotationTypeRegistry.resolve("ValidLatitude")).isEqualTo(ValidLatitude.class);

        // Test Network validators
        assertThat(ValidationAnnotationTypeRegistry.resolve("ValidIPAddress")).isEqualTo(ValidIPAddress.class);
        assertThat(ValidationAnnotationTypeRegistry.resolve("ValidURL")).isEqualTo(ValidURL.class);

        // Test unknown annotation
        assertThat(ValidationAnnotationTypeRegistry.resolve("UnknownAnnotation")).isNull();
        assertThat(ValidationAnnotationTypeRegistry.resolve(null)).isNull();
    }

    @Test
    void shouldReturnDefaultMessageForUnknownAnnotation() {
        // When
        String result = annotationMessages.resolveFromAnnotation("testField", null, new HashMap<>(), Object.class);

        // Then
        assertThat(result).isEqualTo("testField is invalid");
    }

    @Test
    void shouldHandleNullFieldName() {
        // When
        String result = annotationMessages.resolveFromAnnotation(null, NotNull.class, new HashMap<>(), Object.class);

        // Then
        assertThat(result).isEqualTo("null is required");
    }

    @Test
    void shouldResolveFromAnnotationWithMoreSpringValidators() {
        // Test Null
        String result = annotationMessages.resolveFromAnnotation("testField", Null.class, new HashMap<>(), Object.class);
        assertThat(result).isEqualTo("testField must be null");

        // Test NotEmpty
        result = annotationMessages.resolveFromAnnotation("testField", NotEmpty.class, new HashMap<>(), Object.class);
        assertThat(result).isEqualTo("testField must not be empty");

        // Test Pattern
        result = annotationMessages.resolveFromAnnotation("testField", Pattern.class, new HashMap<>(), Object.class);
        assertThat(result).isEqualTo("testField has an invalid format");

        // Test DecimalMin
        Map<String, Object> decimalMinAttrs = new HashMap<>();
        decimalMinAttrs.put("value", "10.5");
        decimalMinAttrs.put("inclusive", true);
        result = annotationMessages.resolveFromAnnotation("testField", DecimalMin.class, decimalMinAttrs, Object.class);
        assertThat(result).isEqualTo("testField must be at least 10.5");

        // Test DecimalMin exclusive
        decimalMinAttrs.put("inclusive", false);
        result = annotationMessages.resolveFromAnnotation("testField", DecimalMin.class, decimalMinAttrs, Object.class);
        assertThat(result).isEqualTo("testField must be greater than 10.5");

        // Test DecimalMax
        Map<String, Object> decimalMaxAttrs = new HashMap<>();
        decimalMaxAttrs.put("value", "100.5");
        decimalMaxAttrs.put("inclusive", true);
        result = annotationMessages.resolveFromAnnotation("testField", DecimalMax.class, decimalMaxAttrs, Object.class);
        assertThat(result).isEqualTo("testField must be at most 100.5");

        // Test DecimalMax exclusive
        decimalMaxAttrs.put("inclusive", false);
        result = annotationMessages.resolveFromAnnotation("testField", DecimalMax.class, decimalMaxAttrs, Object.class);
        assertThat(result).isEqualTo("testField must be less than 100.5");

        // Test Digits
        Map<String, Object> digitsAttrs = new HashMap<>();
        digitsAttrs.put("integer", 3);
        digitsAttrs.put("fraction", 2);
        result = annotationMessages.resolveFromAnnotation("testField", Digits.class, digitsAttrs, Object.class);
        assertThat(result).isEqualTo("testField must have at most 3 integer digits and at most 2 fractional digits");

        // Test PositiveOrZero
        result = annotationMessages.resolveFromAnnotation("testField", PositiveOrZero.class, new HashMap<>(), Object.class);
        assertThat(result).isEqualTo("testField must be greater than or equal to 0");

        // Test Negative
        result = annotationMessages.resolveFromAnnotation("testField", Negative.class, new HashMap<>(), Object.class);
        assertThat(result).isEqualTo("testField must be less than 0");

        // Test NegativeOrZero
        result = annotationMessages.resolveFromAnnotation("testField", NegativeOrZero.class, new HashMap<>(), Object.class);
        assertThat(result).isEqualTo("testField must be less than or equal to 0");

        // Test AssertTrue
        result = annotationMessages.resolveFromAnnotation("testField", AssertTrue.class, new HashMap<>(), Object.class);
        assertThat(result).isEqualTo("testField must be true");

        // Test AssertFalse
        result = annotationMessages.resolveFromAnnotation("testField", AssertFalse.class, new HashMap<>(), Object.class);
        assertThat(result).isEqualTo("testField must be false");

        // Test PastOrPresent
        result = annotationMessages.resolveFromAnnotation("testField", PastOrPresent.class, new HashMap<>(), Object.class);
        assertThat(result).isEqualTo("testField must be in the past or present");

        // Test FutureOrPresent
        result = annotationMessages.resolveFromAnnotation("testField", FutureOrPresent.class, new HashMap<>(), Object.class);
        assertThat(result).isEqualTo("testField must be in the future or present");
    }

    @Test
    void shouldResolveSizeWithDifferentCombinations() {
        // Test Size with only min
        Map<String, Object> sizeMinAttrs = new HashMap<>();
        sizeMinAttrs.put("min", 5);
        sizeMinAttrs.put("max", Integer.MAX_VALUE);
        String result = annotationMessages.resolveFromAnnotation("testField", Size.class, sizeMinAttrs, Object.class);
        assertThat(result).isEqualTo("testField must be at least 5 characters");

        // Test Size with only max
        Map<String, Object> sizeMaxAttrs = new HashMap<>();
        sizeMaxAttrs.put("min", 0);
        sizeMaxAttrs.put("max", 100);
        result = annotationMessages.resolveFromAnnotation("testField", Size.class, sizeMaxAttrs, Object.class);
        assertThat(result).isEqualTo("testField must be at most 100 characters");
    }

    @Test
    void shouldResolveCommonValidatorsWithSensitiveCase() {
        // Test InWhitelist with sensitive case
        Map<String, Object> whitelistAttrs = new HashMap<>();
        whitelistAttrs.put("values", new String[]{"value1", "value2"});
        whitelistAttrs.put("ignoreCase", false);
        String result = annotationMessages.resolveFromAnnotation("testField", InWhitelist.class, whitelistAttrs, Object.class);
        assertThat(result).isEqualTo("testField must be one of: value1, value2 (case-sensitive)");

        // Test NotInBlacklist with sensitive case
        Map<String, Object> blacklistAttrs = new HashMap<>();
        blacklistAttrs.put("values", new String[]{"bad1", "bad2"});
        blacklistAttrs.put("ignoreCase", false);
        result = annotationMessages.resolveFromAnnotation("testField", NotInBlacklist.class, blacklistAttrs, Object.class);
        assertThat(result).isEqualTo("testField must not be one of: bad1, bad2 (case-sensitive)");
    }

    @Test
    void shouldResolveContactValidatorsWithSensitiveCase() {
        // Test ValidEmailDomain with sensitive case
        Map<String, Object> domainAttrs = new HashMap<>();
        domainAttrs.put("allowed", new String[]{"gmail.com"});
        domainAttrs.put("ignoreCase", false);
        String result = annotationMessages.resolveFromAnnotation("testField", ValidEmailDomain.class, domainAttrs, Object.class);
        assertThat(result).isEqualTo("testField must be a valid email address with domain(s): gmail.com (case-sensitive)");
    }

    @Test
    void shouldResolveCrossValidatorsWithDifferentFrom() {
        // Test DifferentFrom
        Map<String, Object> differentAttrs = new HashMap<>();
        differentAttrs.put("field", "password");
        differentAttrs.put("other", "confirmPassword");
        String result = annotationMessages.resolveFromAnnotation("testField", DifferentFrom.class, differentAttrs, Object.class);
        assertThat(result).isEqualTo("password must be different from confirmPassword");
    }

    @Test
    void shouldResolveDateTimeValidatorsWithTolerance() {
        // Test InvalidPastDate with tolerance
        Map<String, Object> invalidPastAttrs = new HashMap<>();
        invalidPastAttrs.put("tolerance", 5);
        String result = annotationMessages.resolveFromAnnotation("testField", InvalidPastDate.class, invalidPastAttrs, Object.class);
        assertThat(result).isEqualTo("testField must be a date in the past within 5 hours");

        // Test InvalidPastFutureDate with tolerance
        Map<String, Object> invalidPastFutureAttrs = new HashMap<>();
        invalidPastFutureAttrs.put("toleranceHours", 3);
        result = annotationMessages.resolveFromAnnotation("testField", InvalidPastFutureDate.class, invalidPastFutureAttrs, Object.class);
        assertThat(result).isEqualTo("testField must be a date in the past within 3 hours");
    }

    @Test
    void shouldResolveDateTimeValidatorsWithDateBeforeAfter() {
        // Test DateBefore with distance
        Map<String, Object> dateBeforeAttrs = new HashMap<>();
        dateBeforeAttrs.put("first", "startDate");
        dateBeforeAttrs.put("second", "endDate");
        dateBeforeAttrs.put("maxDistance", 30L);
        dateBeforeAttrs.put("precision", id.xtramile.validator.enums.DatePrecision.DAYS);
        String result = annotationMessages.resolveFromAnnotation("testField", DateBefore.class, dateBeforeAttrs, Object.class);
        assertThat(result).isEqualTo("startDate must be before endDate with a maximum distance of 30 days");

        // Test DateAfter with distance
        Map<String, Object> dateAfterAttrs = new HashMap<>();
        dateAfterAttrs.put("first", "startDate");
        dateAfterAttrs.put("second", "endDate");
        dateAfterAttrs.put("maxDistance", 30L);
        dateAfterAttrs.put("precision", id.xtramile.validator.enums.DatePrecision.DAYS);
        result = annotationMessages.resolveFromAnnotation("testField", DateAfter.class, dateAfterAttrs, Object.class);
        assertThat(result).isEqualTo("startDate must be after endDate with a maximum distance of 30 days");
    }

    @Test
    void shouldResolveFileValidators() {
        // Test ValidFileExtension
        Map<String, Object> extensionAttrs = new HashMap<>();
        extensionAttrs.put("allowed", new String[]{"jpg", "png"});
        String result = annotationMessages.resolveFromAnnotation("testField", ValidFileExtension.class, extensionAttrs, Object.class);
        assertThat(result).isEqualTo("testField must have one of the following extensions: jpg, png");

        // Test ValidFileMimeType
        Map<String, Object> mimeAttrs = new HashMap<>();
        mimeAttrs.put("allowed", new String[]{"image/jpeg"});
        result = annotationMessages.resolveFromAnnotation("testField", ValidFileMimeType.class, mimeAttrs, Object.class);
        assertThat(result).isEqualTo("testField must have one of the following MIME types: image/jpeg");

        // Test ValidFileSize with defaults
        Map<String, Object> sizeAttrs = new HashMap<>();
        sizeAttrs.put("minBytes", 0L);
        sizeAttrs.put("maxBytes", Long.MAX_VALUE);
        result = annotationMessages.resolveFromAnnotation("testField", ValidFileSize.class, sizeAttrs, Object.class);
        assertThat(result).isEqualTo("testField must be between 0 and 9,223,372,036,854,775,807 bytes");
    }

    @Test
    void shouldResolveLocationValidators() {
        // Test ValidCoordinates with flipCoordinates
        Map<String, Object> coordAttrs = new HashMap<>();
        coordAttrs.put("flipCoordinates", true);
        String result = annotationMessages.resolveFromAnnotation("testField", ValidCoordinates.class, coordAttrs, Object.class);
        assertThat(result).isEqualTo("testField must be valid coordinates in format: latitude, longitude");
    }

    @Test
    void shouldResolveNetworkValidators() {
        // Test all network validators
        String result = annotationMessages.resolveFromAnnotation("testField", ValidCIDR.class, new HashMap<>(), Object.class);
        assertThat(result).isEqualTo("testField must be a valid CIDR notation");

        result = annotationMessages.resolveFromAnnotation("testField", ValidDomainName.class, new HashMap<>(), Object.class);
        assertThat(result).isEqualTo("testField must be a valid domain name");
    }

    @Test
    void shouldResolveFieldErrorWhenDefaultMessageIsUnbracedJakartaValidationKey() {
        // Spring sometimes leaves the technical key without braces; must still resolve via messages + @FieldName
        FieldError fieldError = mock(FieldError.class);
        when(fieldError.getField()).thenReturn("borrowerId");
        when(fieldError.getDefaultMessage()).thenReturn("jakarta.validation.constraints.NotBlank.message");
        when(fieldError.getCode()).thenReturn("NotBlank");

        String result = resolver.resolve(fieldError, BorrowerForm.class);
        assertThat(result).isEqualTo("Borrower ID is required");
    }

    @Test
    void shouldResolveFieldErrorWhenDefaultMessageIsMyboostValidationTemplateKey() {
        // MethodArgumentNotValidException: defaultMessage is the constraint template (same as ConstraintViolation),
        // not an interpolated user string — must resolve like validation.* in resolve(ConstraintViolation).
        FieldError fieldError = mock(FieldError.class);
        when(fieldError.getField()).thenReturn("channel");
        when(fieldError.getDefaultMessage()).thenReturn("validation.common.in-whitelist");
        when(fieldError.getCode()).thenReturn("InWhitelist");

        String result = resolver.resolve(fieldError, WhitelistChannelForm.class);
        assertThat(result).isEqualTo("Channel must be one of: sms, wa");
    }

    private static class WhitelistChannelForm {
        @FieldName("Channel")
        @InWhitelist(values = {"sms", "wa"})
        String channel;
    }

    @Test
    void shouldResolveFieldErrorWhenValidationTemplateIsNameMin() {
        FieldError fieldError = mock(FieldError.class);
        when(fieldError.getField()).thenReturn("name");
        when(fieldError.getDefaultMessage()).thenReturn("validation.data.name.min");
        when(fieldError.getCode()).thenReturn("ValidName");

        String result = resolver.resolve(fieldError, NameMinForm.class);
        assertThat(result).isEqualTo("Nama must have minimal 10 characters");
    }

    private static class NameMinForm {
        @FieldName("Nama")
        @ValidName(min = 10)
        String name;
    }

    @Test
    void shouldResolveFieldErrorWhenNotBlankMessageWasInterpolatedByHibernate() {
        FieldError fieldError = mock(FieldError.class);
        when(fieldError.getField()).thenReturn("gender");
        when(fieldError.getDefaultMessage()).thenReturn("must not be blank");
        when(fieldError.getCode()).thenReturn("NotBlank");

        String result = resolver.resolve(fieldError, GenderForm.class);
        assertThat(result).isEqualTo("Gender is required");
    }

    private static class GenderForm {
        @FieldName("Gender")
        @NotBlank
        String gender;
    }

    private static class BorrowerForm {
        @FieldName("Borrower ID")
        @NotBlank
        String borrowerId;
    }

    @Test
    void shouldResolveFieldErrorWithAnnotationCode() {
        // Test resolve with annotation code
        FieldError fieldError = mock(FieldError.class);
        when(fieldError.getField()).thenReturn("testField");
        when(fieldError.getDefaultMessage()).thenReturn("{friendly.default}");
        when(fieldError.getCode()).thenReturn("ValidName");
        
        String result = resolver.resolve(fieldError, Object.class);
        assertThat(result).isNotNull();
    }

    @Test
    void shouldResolveFieldErrorWithNonDefaultMessage() {
        // Test resolve with non-default message
        FieldError fieldError = mock(FieldError.class);
        when(fieldError.getField()).thenReturn("testField");
        when(fieldError.getDefaultMessage()).thenReturn("Custom error message");
        
        String result = resolver.resolve(fieldError, Object.class);
        assertThat(result).isEqualTo("Custom error message");
    }

    @Test
    void shouldResolveAllAnnotationTypes() {
        // Test all annotation types in resolveAnnotationType
        assertThat(ValidationAnnotationTypeRegistry.resolve("DecimalMin")).isEqualTo(DecimalMin.class);
        assertThat(ValidationAnnotationTypeRegistry.resolve("DecimalMax")).isEqualTo(DecimalMax.class);
        assertThat(ValidationAnnotationTypeRegistry.resolve("Digits")).isEqualTo(Digits.class);
        assertThat(ValidationAnnotationTypeRegistry.resolve("PositiveOrZero")).isEqualTo(PositiveOrZero.class);
        assertThat(ValidationAnnotationTypeRegistry.resolve("Negative")).isEqualTo(Negative.class);
        assertThat(ValidationAnnotationTypeRegistry.resolve("NegativeOrZero")).isEqualTo(NegativeOrZero.class);
        assertThat(ValidationAnnotationTypeRegistry.resolve("AssertTrue")).isEqualTo(AssertTrue.class);
        assertThat(ValidationAnnotationTypeRegistry.resolve("AssertFalse")).isEqualTo(AssertFalse.class);
        assertThat(ValidationAnnotationTypeRegistry.resolve("PastOrPresent")).isEqualTo(PastOrPresent.class);
        assertThat(ValidationAnnotationTypeRegistry.resolve("FutureOrPresent")).isEqualTo(FutureOrPresent.class);
        assertThat(ValidationAnnotationTypeRegistry.resolve("ValidContactNumber")).isEqualTo(ValidContactNumber.class);
        assertThat(ValidationAnnotationTypeRegistry.resolve("RequiredWith")).isEqualTo(RequiredWith.class);
        assertThat(ValidationAnnotationTypeRegistry.resolve("ValidISOCode")).isEqualTo(ValidISOCode.class);
        assertThat(ValidationAnnotationTypeRegistry.resolve("ValidNationalID")).isEqualTo(ValidNationalID.class);
        assertThat(ValidationAnnotationTypeRegistry.resolve("ValidPIN")).isEqualTo(ValidPIN.class);
        assertThat(ValidationAnnotationTypeRegistry.resolve("ValidTaxID")).isEqualTo(ValidTaxID.class);
        assertThat(ValidationAnnotationTypeRegistry.resolve("ValidTime")).isEqualTo(ValidTime.class);
        assertThat(ValidationAnnotationTypeRegistry.resolve("ValidPostalCode")).isEqualTo(ValidPostalCode.class);
        assertThat(ValidationAnnotationTypeRegistry.resolve("ValidRTRW")).isEqualTo(ValidRTRW.class);
    }

    @DateBefore(first = "start", second = "end", pattern = "yyyy-MM-dd")
    private static class DateBeforeRangeForm {
        @FieldName("Start date")
        String start;

        @FieldName("End date")
        String end;
    }

    @DateBefore(first = "start", second = "end", pattern = "yyyy-MM-dd", maxDistance = 14, precision = DatePrecision.DAYS)
    private static class DateBeforeDistanceForm {
        @FieldName("Period start")
        String start;

        @FieldName("Period end")
        String end;
    }

    /**
     * FieldError + {@code validation.*} templates: args rebuilt from annotation attributes must match
     * {@code messages_en.properties} (same wording as {@link MessageResourceResolver} with locale {@code en}).
     */
    @Nested
    class FieldErrorValidationTemplatesMatchMessagesEnglish {

        private String expectedEnglishMessage(String templateKey, Object... messageArgs) {
            MessageResourceResolver en = new MessageResourceResolver("en");
            return en.getMessage(templateKey, messageArgs);
        }

        @Test
        void fieldError_template_uses_pattern_for_validDate() {
            FieldError fe = mock(FieldError.class);
            when(fe.getField()).thenReturn("birthDate");
            when(fe.getDefaultMessage()).thenReturn("validation.datetime.date");
            when(fe.getCode()).thenReturn("ValidDate");

            String result = resolver.resolve(fe, ValidDatePatternForm.class);
            assertThat(result).isEqualTo(expectedEnglishMessage("validation.datetime.date", "Date of birth", "dd/MM/yyyy"));
        }

        @Test
        void fieldError_template_uses_pattern_for_pastDatePattern() {
            FieldError fe = mock(FieldError.class);
            when(fe.getField()).thenReturn("startedAt");
            when(fe.getDefaultMessage()).thenReturn("validation.datetime.past-date.pattern");
            when(fe.getCode()).thenReturn("ValidPastDate");

            String result = resolver.resolve(fe, PastDatePatternForm.class);
            assertThat(result).isEqualTo(expectedEnglishMessage("validation.datetime.past-date.pattern", "Started at", "yyyy-MM-dd HH:mm"));
        }

        @Test
        void fieldError_template_uses_tolerance_for_invalidPastDate() {
            FieldError fe = mock(FieldError.class);
            when(fe.getField()).thenReturn("due");
            when(fe.getDefaultMessage()).thenReturn("validation.datetime.invalid-past-date.tolerance");
            when(fe.getCode()).thenReturn("InvalidPastDate");

            String result = resolver.resolve(fe, InvalidPastDateToleranceForm.class);
            assertThat(result).isEqualTo(expectedEnglishMessage("validation.datetime.invalid-past-date.tolerance", "Due", 7));
        }

        @Test
        void fieldError_template_uses_toleranceHours_for_invalidPastFutureDate() {
            FieldError fe = mock(FieldError.class);
            when(fe.getField()).thenReturn("slot");
            when(fe.getDefaultMessage()).thenReturn("validation.datetime.invalid-past-future-date.tolerance");
            when(fe.getCode()).thenReturn("InvalidPastFutureDate");

            String result = resolver.resolve(fe, InvalidPastFutureToleranceForm.class);
            assertThat(result).isEqualTo(expectedEnglishMessage("validation.datetime.invalid-past-future-date.tolerance", "Slot", 12));
        }

        @Test
        void fieldError_template_uses_pattern_for_paymentReference() {
            FieldError fe = mock(FieldError.class);
            when(fe.getField()).thenReturn("reference");
            when(fe.getDefaultMessage()).thenReturn("validation.finance.payment-reference.pattern");
            when(fe.getCode()).thenReturn("ValidPaymentReference");

            String result = resolver.resolve(fe, PaymentRefPatternForm.class);
            assertThat(result).isEqualTo(expectedEnglishMessage("validation.finance.payment-reference.pattern", "Payment ref", "^[A-Z]+$"));
        }

        @Test
        void fieldError_template_uses_dimensions_for_image() {
            FieldError fe = mock(FieldError.class);
            when(fe.getField()).thenReturn("photo");
            when(fe.getDefaultMessage()).thenReturn("validation.file.image-dimension");
            when(fe.getCode()).thenReturn("ValidImageDimensions");

            String result = resolver.resolve(fe, ImageDimForm.class);
            assertThat(result).isEqualTo(expectedEnglishMessage("validation.file.image-dimension", "Photo", 100, 200, 800, 600));
        }

        @Test
        void fieldError_template_uses_allowed_for_fileExtension_and_mimeType() {
            FieldError fe1 = mock(FieldError.class);
            when(fe1.getField()).thenReturn("doc");
            when(fe1.getDefaultMessage()).thenReturn("validation.file.file-extension");
            when(fe1.getCode()).thenReturn("ValidFileExtension");

            assertThat(resolver.resolve(fe1, FileExtForm.class))
                    .isEqualTo(expectedEnglishMessage("validation.file.file-extension", "Document", "pdf, docx"));

            FieldError fe2 = mock(FieldError.class);
            when(fe2.getField()).thenReturn("upload");
            when(fe2.getDefaultMessage()).thenReturn("validation.file.mime-type");
            when(fe2.getCode()).thenReturn("ValidFileMimeType");

            assertThat(resolver.resolve(fe2, FileMimeForm.class))
                    .isEqualTo(expectedEnglishMessage("validation.file.mime-type", "Upload", "application/pdf, image/png"));
        }

        @Test
        void fieldError_template_uses_minBytes_maxBytes_for_fileSize() {
            FieldError fe = mock(FieldError.class);
            when(fe.getField()).thenReturn("payload");
            when(fe.getDefaultMessage()).thenReturn("validation.file.file-size");
            when(fe.getCode()).thenReturn("ValidFileSize");

            String result = resolver.resolve(fe, FileSizeForm.class);
            assertThat(result).isEqualTo(expectedEnglishMessage("validation.file.file-size", "Payload", 512L, 4096L));
        }

        @Test
        void fieldError_template_dateBefore_and_distance_use_class_level_annotation() {
            FieldError fe1 = mock(FieldError.class);
            when(fe1.getField()).thenReturn("start");
            when(fe1.getDefaultMessage()).thenReturn("validation.datetime.date-before");
            when(fe1.getCode()).thenReturn("DateBefore");

            assertThat(resolver.resolve(fe1, DateBeforeRangeForm.class))
                    .isEqualTo(expectedEnglishMessage("validation.datetime.date-before", "Start date", "End date"));

            FieldError fe2 = mock(FieldError.class);
            when(fe2.getField()).thenReturn("start");
            when(fe2.getDefaultMessage()).thenReturn("validation.datetime.date-before.distance");
            when(fe2.getCode()).thenReturn("DateBefore");

            assertThat(resolver.resolve(fe2, DateBeforeDistanceForm.class))
                    .isEqualTo(expectedEnglishMessage("validation.datetime.date-before.distance",
                            "Period start", "Period end", 14L, "days"));
        }

        @Test
        void fieldError_template_validationSpringDigits_uses_integer_and_fraction_attrs() {
            FieldError fe = mock(FieldError.class);
            when(fe.getField()).thenReturn("code");
            when(fe.getDefaultMessage()).thenReturn("validation.spring.digits");
            when(fe.getCode()).thenReturn("Digits");

            String result = resolver.resolve(fe, DigitsForm.class);
            assertThat(result).isEqualTo(expectedEnglishMessage("validation.spring.digits", "Code", 5, 2));
        }

        @Test
        void fieldError_nestedPropertyPath_size_resolvesMaxFromLeafAnnotation() {
            FieldError fe = mock(FieldError.class);
            when(fe.getField()).thenReturn("dto.email");
            when(fe.getDefaultMessage()).thenReturn("jakarta.validation.constraints.Size.message");
            when(fe.getCode()).thenReturn("Size");

            String result = resolver.resolve(fe, NestedParentForSizeForm.class);
            assertThat(result).isEqualTo(expectedEnglishMessage("validation.spring.size.max", "Email", 40));
        }
    }

    private static class ValidDatePatternForm {
        @FieldName("Date of birth")
        @ValidDate(pattern = "dd/MM/yyyy")
        String birthDate;
    }

    private static class PastDatePatternForm {
        @FieldName("Started at")
        @ValidPastDate(pattern = "yyyy-MM-dd HH:mm")
        String startedAt;
    }

    private static class InvalidPastDateToleranceForm {
        @FieldName("Due")
        @InvalidPastDate(tolerance = 7)
        String due;
    }

    private static class InvalidPastFutureToleranceForm {
        @FieldName("Slot")
        @InvalidPastFutureDate(pattern = "yyyy-MM-dd", toleranceHours = 12)
        String slot;
    }

    private static class PaymentRefPatternForm {
        @FieldName("Payment ref")
        @ValidPaymentReference(pattern = "^[A-Z]+$")
        String reference;
    }

    private static class ImageDimForm {
        @FieldName("Photo")
        @ValidImageDimensions(minWidth = 100, minHeight = 200, maxWidth = 800, maxHeight = 600)
        String photo;
    }

    private static class FileExtForm {
        @FieldName("Document")
        @ValidFileExtension(allowed = {"pdf", "docx"})
        String doc;
    }

    private static class FileMimeForm {
        @FieldName("Upload")
        @ValidFileMimeType(allowed = {"application/pdf", "image/png"})
        String upload;
    }

    private static class FileSizeForm {
        @FieldName("Payload")
        @ValidFileSize(minBytes = 512, maxBytes = 4096)
        String payload;
    }

    private static class DigitsForm {
        @FieldName("Code")
        @Digits(integer = 5, fraction = 2)
        String code;
    }

    private static class NestedParentForSizeForm {
        NestedChildForSizeForm dto;
    }

    private static class NestedChildForSizeForm {
        @FieldName("Email")
        @Size(max = 40)
        String email;
    }

}
