package id.xtramile.validator.web;

import id.xtramile.validator.annotation.common.FieldName;
import id.xtramile.validator.annotation.contact.ValidEmail;
import id.xtramile.validator.annotation.data.ValidName;
import id.xtramile.validator.annotation.datetime.ValidDate;
import id.xtramile.validator.annotation.datetime.ValidFutureDate;
import id.xtramile.validator.annotation.datetime.ValidPastDate;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class FieldNameWithValidAnnotationTest {

    private Validator validator;
    private FriendlyMessageResolver messageResolver;

    @BeforeEach
    void setUp() {
        try (LocalValidatorFactoryBean factory = new LocalValidatorFactoryBean()) {
            factory.afterPropertiesSet();
            validator = factory.getValidator();
        }

        MessageResourceResolver resourceResolver = new MessageResourceResolver("en");
        messageResolver = new FriendlyMessageResolver(resourceResolver);
    }

    static class TestDtoWithoutFieldName {
        @ValidPastDate
        private String birthDate;

        public void setBirthDate(String birthDate) {
            this.birthDate = birthDate;
        }
    }

    static class TestDtoWithPastDate {
        @FieldName("Birth Date")
        @ValidPastDate
        private String birthDate;

        public void setBirthDate(String birthDate) {
            this.birthDate = birthDate;
        }
    }

    static class TestDtoWithFutureDate {
        @FieldName("Appointment Date")
        @ValidFutureDate
        private String appointmentDate;

        public void setAppointmentDate(String appointmentDate) {
            this.appointmentDate = appointmentDate;
        }
    }

    static class TestDtoWithDate {
        @FieldName("Registration Date")
        @ValidDate()
        private String registrationDate;

        public void setRegistrationDate(String registrationDate) {
            this.registrationDate = registrationDate;
        }
    }

    static class TestDtoWithEmail {
        @FieldName("Email Address")
        @ValidEmail
        private String email;

        public void setEmail(String email) {
            this.email = email;
        }
    }

    static class TestDtoWithName {
        @FieldName("Full Name")
        @ValidName
        private String name;

        public void setName(String name) {
            this.name = name;
        }
    }

    static class AddressDto {
        @FieldName("Street Address")
        @ValidName
        private String street;

        @FieldName("City Name")
        @ValidName
        private String city;

        public void setStreet(String street) {
            this.street = street;
        }

        public void setCity(String city) {
            this.city = city;
        }
    }

    static class ContactInfoDto {
        @FieldName("Contact Email")
        @ValidEmail
        private String email;

        @FieldName("Phone Number")
        @ValidEmail
        private String phone;

        public void setEmail(String email) {
            this.email = email;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }
    }

    static class UserProfileDto {
        @FieldName("Full Name")
        @ValidName
        private String name;

        @Valid
        private AddressDto address;

        @Valid
        private ContactInfoDto contact;

        public void setName(String name) {
            this.name = name;
        }

        public void setAddress(AddressDto address) {
            this.address = address;
        }

        public void setContact(ContactInfoDto contact) {
            this.contact = contact;
        }
    }

    @Test
    void testFieldNameWithValidPastDate_ErrorMessageContainsFieldName() {
        TestDtoWithPastDate dto = new TestDtoWithPastDate();

        LocalDate futureDate = LocalDate.now().plusDays(1);
        dto.setBirthDate(futureDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        Set<ConstraintViolation<TestDtoWithPastDate>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        ConstraintViolation<TestDtoWithPastDate> violation = violations.iterator().next();
        String errorMessage = messageResolver.resolve(violation, "birthDate", TestDtoWithPastDate.class);

        assertThat(errorMessage).contains("Birth Date");
        assertThat(errorMessage.toLowerCase()).doesNotContain("birthdate");
    }

    @Test
    void testFieldNameWithValidFutureDate_ErrorMessageContainsFieldName() {
        TestDtoWithFutureDate dto = new TestDtoWithFutureDate();

        LocalDate pastDate = LocalDate.now().minusDays(1);
        dto.setAppointmentDate(pastDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        Set<ConstraintViolation<TestDtoWithFutureDate>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        ConstraintViolation<TestDtoWithFutureDate> violation = violations.iterator().next();
        String errorMessage = messageResolver.resolve(violation, "appointmentDate", TestDtoWithFutureDate.class);

        assertThat(errorMessage).contains("Appointment Date");
        assertThat(errorMessage.toLowerCase()).doesNotContain("appointmentdate");
    }

    @Test
    void testFieldNameWithValidDate_ErrorMessageContainsFieldName() {
        TestDtoWithDate dto = new TestDtoWithDate();
        dto.setRegistrationDate("invalid-date-format");

        Set<ConstraintViolation<TestDtoWithDate>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        ConstraintViolation<TestDtoWithDate> violation = violations.iterator().next();
        String errorMessage = messageResolver.resolve(violation, "registrationDate", TestDtoWithDate.class);

        assertThat(errorMessage).contains("Registration Date");
        assertThat(errorMessage.toLowerCase()).doesNotContain("registrationdate");
    }

    @Test
    void testFieldNameWithValidEmail_ErrorMessageContainsFieldName() {
        TestDtoWithEmail dto = new TestDtoWithEmail();
        dto.setEmail("invalid-email");

        Set<ConstraintViolation<TestDtoWithEmail>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        ConstraintViolation<TestDtoWithEmail> violation = violations.iterator().next();
        String errorMessage = messageResolver.resolve(violation, "email", TestDtoWithEmail.class);

        assertThat(errorMessage).contains("Email Address");
        assertThat(errorMessage.toLowerCase()).contains("email address");
    }

    @Test
    void testFieldNameWithValidName_ErrorMessageContainsFieldName() {
        TestDtoWithName dto = new TestDtoWithName();
        dto.setName("AB");

        Set<ConstraintViolation<TestDtoWithName>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        ConstraintViolation<TestDtoWithName> violation = violations.iterator().next();
        String errorMessage = messageResolver.resolve(violation, "name", TestDtoWithName.class);

        assertThat(errorMessage).contains("Full Name");
        assertThat(errorMessage).contains("Full Name");
    }

    @Test
    void testFieldNameWithoutAnnotation_ErrorMessageContainsActualFieldName() {
        TestDtoWithoutFieldName dto = new TestDtoWithoutFieldName();

        LocalDate futureDate = LocalDate.now().plusDays(1);
        dto.setBirthDate(futureDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        Set<ConstraintViolation<TestDtoWithoutFieldName>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        ConstraintViolation<TestDtoWithoutFieldName> violation = violations.iterator().next();
        String errorMessage = messageResolver.resolve(violation, "birthDate", TestDtoWithoutFieldName.class);

        assertThat(errorMessage).contains("birthDate");
    }

    @Test
    void testFieldNameInNestedObject_ErrorMessageContainsFieldName() {
        AddressDto address = new AddressDto();
        address.setStreet("AB");
        
        UserProfileDto dto = new UserProfileDto();
        dto.setAddress(address);

        Set<ConstraintViolation<UserProfileDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();

        ConstraintViolation<UserProfileDto> violation = violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("address.street"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Expected violation for address.street not found. Violations: " + 
                    violations.stream().map(v -> v.getPropertyPath().toString()).collect(Collectors.toList())));

        String propertyPath = violation.getPropertyPath().toString();
        Class<?> rootClass = UserProfileDto.class;
        String errorMessage = messageResolver.resolve(violation, propertyPath, rootClass);

        assertThat(errorMessage.toLowerCase()).contains("street address");
        assertThat(errorMessage.toLowerCase()).doesNotContain(" street ");
    }

    @Test
    void testFieldNameInNestedObject_CityField() {
        AddressDto address = new AddressDto();
        address.setCity("AB");
        
        UserProfileDto dto = new UserProfileDto();
        dto.setAddress(address);

        Set<ConstraintViolation<UserProfileDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();

        ConstraintViolation<UserProfileDto> violation = violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("address.city"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Expected violation for address.city not found. Violations: " + 
                    violations.stream().map(v -> v.getPropertyPath().toString()).collect(Collectors.toList())));

        String propertyPath = violation.getPropertyPath().toString();
        Class<?> rootClass = UserProfileDto.class;
        String errorMessage = messageResolver.resolve(violation, propertyPath, rootClass);

        assertThat(errorMessage.toLowerCase()).contains("city name");
        assertThat(errorMessage.toLowerCase()).doesNotContain(" city ");
    }

    @Test
    void testFieldNameInNestedObject_ContactEmail() {
        ContactInfoDto contact = new ContactInfoDto();
        contact.setEmail("invalid-email");
        
        UserProfileDto dto = new UserProfileDto();
        dto.setContact(contact);

        Set<ConstraintViolation<UserProfileDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();

        ConstraintViolation<UserProfileDto> violation = violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("contact.email"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Expected violation for contact.email not found. Violations: " + 
                    violations.stream().map(v -> v.getPropertyPath().toString()).collect(Collectors.toList())));

        String propertyPath = violation.getPropertyPath().toString();
        Class<?> rootClass = UserProfileDto.class;
        String errorMessage = messageResolver.resolve(violation, propertyPath, rootClass);

        assertThat(errorMessage).contains("Contact Email");
        assertThat(errorMessage.toLowerCase()).contains("contact email");
    }

    @Test
    void testFieldNameInNestedObject_ContactPhone() {
        ContactInfoDto contact = new ContactInfoDto();
        contact.setPhone("invalid-phone");
        
        UserProfileDto dto = new UserProfileDto();
        dto.setContact(contact);

        Set<ConstraintViolation<UserProfileDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();

        ConstraintViolation<UserProfileDto> violation = violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("contact.phone"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Expected violation for contact.phone not found. Violations: " + 
                    violations.stream().map(v -> v.getPropertyPath().toString()).collect(Collectors.toList())));

        String propertyPath = violation.getPropertyPath().toString();
        Class<?> rootClass = UserProfileDto.class;
        String errorMessage = messageResolver.resolve(violation, propertyPath, rootClass);

        assertThat(errorMessage).contains("Phone Number");
        assertThat(errorMessage.toLowerCase()).contains("phone number");
    }

    @Test
    void testFieldNameInNestedObject_RootLevelField() {
        UserProfileDto dto = new UserProfileDto();
        dto.setName("AB"); // Invalid: too short

        Set<ConstraintViolation<UserProfileDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();

        ConstraintViolation<UserProfileDto> violation = violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("name"))
                .findFirst()
                .orElseThrow();

        String errorMessage = messageResolver.resolve(violation, "name", UserProfileDto.class);

        assertThat(errorMessage).contains("Full Name");
        assertThat(errorMessage).contains("Full Name");
    }
}
