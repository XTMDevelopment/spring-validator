package id.xtramile.validator.validator.cross;

import id.xtramile.validator.annotation.cross.RequiredWith;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RequiredWithValidatorTest {

    @RequiredWith(when = "hasEmail", require = {"email"})
    private static class EmailRequiredDummy {
        private final Boolean hasEmail;
        private final String email;

        private EmailRequiredDummy(Boolean hasEmail, String email) {
            this.hasEmail = hasEmail;
            this.email = email;
        }

        public Boolean hasEmail() {
            return hasEmail;
        }

        public String email() {
            return email;
        }
    }

    @RequiredWith(when = "hasPhone", require = {"phone", "phoneType"})
    private static class PhoneRequiredDummy {
        private final Boolean hasPhone;
        private final String phone;
        private final String phoneType;

        private PhoneRequiredDummy(Boolean hasPhone, String phone, String phoneType) {
            this.hasPhone = hasPhone;
            this.phone = phone;
            this.phoneType = phoneType;
        }

        public Boolean hasPhone() {
            return hasPhone;
        }

        public String phone() {
            return phone;
        }

        public String phoneType() {
            return phoneType;
        }
    }

    @RequiredWith(when = "isActive", require = {"username", "password"})
    private static class UserRequiredDummy {
        private final Boolean isActive;
        private final String username;
        private final String password;

        private UserRequiredDummy(Boolean isActive, String username, String password) {
            this.isActive = isActive;
            this.username = username;
            this.password = password;
        }

        public Boolean isActive() {
            return isActive;
        }

        public String username() {
            return username;
        }

        public String password() {
            return password;
        }
    }

    @RequiredWith(when = "hasAddress", require = {"street", "city", "zipCode"})
    private static class AddressRequiredDummy {
        private final Boolean hasAddress;
        private final String street;
        private final String city;
        private final String zipCode;

        private AddressRequiredDummy(Boolean hasAddress, String street, String city, String zipCode) {
            this.hasAddress = hasAddress;
            this.street = street;
            this.city = city;
            this.zipCode = zipCode;
        }

        public Boolean hasAddress() {
            return hasAddress;
        }

        public String street() {
            return street;
        }

        public String city() {
            return city;
        }

        public String zipCode() {
            return zipCode;
        }
    }

    private RequiredWithValidator validator;

    @BeforeEach
    void setUp() {
        validator = new RequiredWithValidator();
        validator.initialize(EmailRequiredDummy.class.getAnnotation(RequiredWith.class));
    }

    @Test
    void testValidWhenTriggerPresent() {
        assertTrue(validator.isValid(new EmailRequiredDummy(true, "test@example.com"), null)); // Trigger present, required present
        assertTrue(validator.isValid(new EmailRequiredDummy(true, "valid@email.com"), null)); // Trigger present, required present
        assertTrue(validator.isValid(new EmailRequiredDummy(true, "user@domain.org"), null)); // Trigger present, required present
    }

    @Test
    void testValidWhenTriggerNotPresent() {
        assertTrue(validator.isValid(new EmailRequiredDummy(false, null), null)); // Trigger not present, required not present
        assertTrue(validator.isValid(new EmailRequiredDummy(false, ""), null)); // Trigger not present, required empty
        assertTrue(validator.isValid(new EmailRequiredDummy(false, "   "), null)); // Trigger not present, required whitespace
        assertTrue(validator.isValid(new EmailRequiredDummy(null, null), null)); // Trigger null, required null
        assertTrue(validator.isValid(new EmailRequiredDummy(null, ""), null)); // Trigger null, required empty
    }

    @Test
    void testInvalidWhenTriggerPresentButRequiredNotPresent() {
        assertFalse(validator.isValid(new EmailRequiredDummy(true, null), null)); // Trigger present, required null
        assertFalse(validator.isValid(new EmailRequiredDummy(true, ""), null)); // Trigger present, required empty
        assertFalse(validator.isValid(new EmailRequiredDummy(true, "   "), null)); // Trigger present, required whitespace
    }

    @Test
    void testPhoneRequiredValidation() {
        validator.initialize(PhoneRequiredDummy.class.getAnnotation(RequiredWith.class));

        assertTrue(validator.isValid(new PhoneRequiredDummy(true, "+1234567890", "mobile"), null)); // Trigger present, both required present
        assertTrue(validator.isValid(new PhoneRequiredDummy(false, null, null), null)); // Trigger not present, both required not present
        assertTrue(validator.isValid(new PhoneRequiredDummy(false, "", ""), null)); // Trigger not present, both required empty
        assertTrue(validator.isValid(new PhoneRequiredDummy(null, null, null), null)); // Trigger null, both required null

        assertFalse(validator.isValid(new PhoneRequiredDummy(true, null, "mobile"), null)); // Trigger present, phone null
        assertFalse(validator.isValid(new PhoneRequiredDummy(true, "+1234567890", null), null)); // Trigger present, phoneType null
        assertFalse(validator.isValid(new PhoneRequiredDummy(true, "", "mobile"), null)); // Trigger present, phone empty
        assertFalse(validator.isValid(new PhoneRequiredDummy(true, "+1234567890", ""), null)); // Trigger present, phoneType empty
        assertFalse(validator.isValid(new PhoneRequiredDummy(true, null, null), null)); // Trigger present, both null
        assertFalse(validator.isValid(new PhoneRequiredDummy(true, "", ""), null)); // Trigger present, both empty
    }

    @Test
    void testUserRequiredValidation() {
        validator.initialize(UserRequiredDummy.class.getAnnotation(RequiredWith.class));

        assertTrue(validator.isValid(new UserRequiredDummy(true, "username", "password"), null)); // Trigger present, both required present
        assertTrue(validator.isValid(new UserRequiredDummy(false, null, null), null)); // Trigger not present, both required not present
        assertTrue(validator.isValid(new UserRequiredDummy(false, "", ""), null)); // Trigger not present, both required empty
        assertTrue(validator.isValid(new UserRequiredDummy(null, null, null), null)); // Trigger null, both required null

        assertFalse(validator.isValid(new UserRequiredDummy(true, null, "password"), null)); // Trigger present, username null
        assertFalse(validator.isValid(new UserRequiredDummy(true, "username", null), null)); // Trigger present, password null
        assertFalse(validator.isValid(new UserRequiredDummy(true, "", "password"), null)); // Trigger present, username empty
        assertFalse(validator.isValid(new UserRequiredDummy(true, "username", ""), null)); // Trigger present, password empty
        assertFalse(validator.isValid(new UserRequiredDummy(true, null, null), null)); // Trigger present, both null
        assertFalse(validator.isValid(new UserRequiredDummy(true, "", ""), null)); // Trigger present, both empty
    }

    @Test
    void testAddressRequiredValidation() {
        validator.initialize(AddressRequiredDummy.class.getAnnotation(RequiredWith.class));

        assertTrue(validator.isValid(new AddressRequiredDummy(true, "123 Main St", "City", "12345"), null)); // Trigger present, all required present
        assertTrue(validator.isValid(new AddressRequiredDummy(false, null, null, null), null)); // Trigger not present, all required not present
        assertTrue(validator.isValid(new AddressRequiredDummy(false, "", "", ""), null)); // Trigger not present, all required empty
        assertTrue(validator.isValid(new AddressRequiredDummy(null, null, null, null), null)); // Trigger null, all required null

        assertFalse(validator.isValid(new AddressRequiredDummy(true, null, "City", "12345"), null)); // Trigger present, street null
        assertFalse(validator.isValid(new AddressRequiredDummy(true, "123 Main St", null, "12345"), null)); // Trigger present, city null
        assertFalse(validator.isValid(new AddressRequiredDummy(true, "123 Main St", "City", null), null)); // Trigger present, zipCode null
        assertFalse(validator.isValid(new AddressRequiredDummy(true, "", "City", "12345"), null)); // Trigger present, street empty
        assertFalse(validator.isValid(new AddressRequiredDummy(true, "123 Main St", "", "12345"), null)); // Trigger present, city empty
        assertFalse(validator.isValid(new AddressRequiredDummy(true, "123 Main St", "City", ""), null)); // Trigger present, zipCode empty
        assertFalse(validator.isValid(new AddressRequiredDummy(true, null, null, null), null)); // Trigger present, all null
        assertFalse(validator.isValid(new AddressRequiredDummy(true, "", "", ""), null)); // Trigger present, all empty
    }

    @Test
    void testNullBean() {
        assertTrue(validator.isValid(null, null)); // Null bean should be valid
    }

    @Test
    void testBooleanTriggerValues() {
        assertTrue(validator.isValid(new EmailRequiredDummy(Boolean.TRUE, "test@example.com"), null)); // Boolean.TRUE
        assertTrue(validator.isValid(new EmailRequiredDummy(Boolean.FALSE, null), null)); // Boolean.FALSE
        assertTrue(validator.isValid(new EmailRequiredDummy(Boolean.FALSE, ""), null)); // Boolean.FALSE with empty
        assertTrue(validator.isValid(new EmailRequiredDummy(null, null), null)); // Boolean null

        assertFalse(validator.isValid(new EmailRequiredDummy(Boolean.TRUE, null), null)); // Boolean.TRUE with null required
        assertFalse(validator.isValid(new EmailRequiredDummy(Boolean.TRUE, ""), null)); // Boolean.TRUE with empty required
    }

    @Test
    void testStringTriggerValues() {
        @RequiredWith(when = "status", require = {"message"})
        class StatusDummy {
            private final String status;
            private final String message;

            public StatusDummy(String status, String message) {
                this.status = status;
                this.message = message;
            }

            public String status() {
                return status;
            }

            public String message() {
                return message;
            }
        }

        validator.initialize(StatusDummy.class.getAnnotation(RequiredWith.class));

        assertTrue(validator.isValid(new StatusDummy("active", "User is active"), null)); // String trigger present, required present
        assertTrue(validator.isValid(new StatusDummy(null, null), null)); // String trigger null, required null
        assertTrue(validator.isValid(new StatusDummy("", null), null)); // String trigger empty, required null
        assertTrue(validator.isValid(new StatusDummy("   ", null), null)); // String trigger whitespace, required null

        assertFalse(validator.isValid(new StatusDummy("active", null), null)); // String trigger present, required null
        assertFalse(validator.isValid(new StatusDummy("active", ""), null)); // String trigger present, required empty
        assertFalse(validator.isValid(new StatusDummy("active", "   "), null)); // String trigger present, required whitespace
    }

    @Test
    void testNumericTriggerValues() {
        @RequiredWith(when = "count", require = {"description"})
        class CountDummy {
            private final Integer count;
            private final String description;

            public CountDummy(Integer count, String description) {
                this.count = count;
                this.description = description;
            }

            public Integer count() {
                return count;
            }

            public String description() {
                return description;
            }
        }

        validator.initialize(CountDummy.class.getAnnotation(RequiredWith.class));

        assertTrue(validator.isValid(new CountDummy(5, "Five items"), null)); // Numeric trigger present, required present
        assertTrue(validator.isValid(new CountDummy(0, "Zero items"), null)); // Numeric trigger zero, required present
        assertTrue(validator.isValid(new CountDummy(null, null), null)); // Numeric trigger null, required null
        assertTrue(validator.isValid(new CountDummy(0, null), null)); // Numeric trigger zero, required null (zero is not considered present)

        assertFalse(validator.isValid(new CountDummy(5, null), null)); // Numeric trigger present, required null
        assertFalse(validator.isValid(new CountDummy(5, ""), null)); // Numeric trigger present, required empty
        assertFalse(validator.isValid(new CountDummy(5, "   "), null)); // Numeric trigger present, required whitespace
    }

    @Test
    void testWhitespaceHandling() {
        assertTrue(validator.isValid(new EmailRequiredDummy(true, "test@example.com"), null)); // Valid case
        assertTrue(validator.isValid(new EmailRequiredDummy(false, "   "), null)); // Trigger not present, required whitespace
        assertFalse(validator.isValid(new EmailRequiredDummy(true, "   "), null)); // Trigger present, required whitespace
    }

    @Test
    void testEmptyStringHandling() {
        assertTrue(validator.isValid(new EmailRequiredDummy(true, "test@example.com"), null)); // Valid case
        assertTrue(validator.isValid(new EmailRequiredDummy(false, ""), null)); // Trigger not present, required empty
        assertFalse(validator.isValid(new EmailRequiredDummy(true, ""), null)); // Trigger present, required empty
    }

    @Test
    void testSpecialCharacters() {
        assertTrue(validator.isValid(new EmailRequiredDummy(true, "test+tag@example.com"), null)); // Email with special characters
        assertTrue(validator.isValid(new EmailRequiredDummy(true, "user.name@domain.co.uk"), null)); // Email with dots
        assertTrue(validator.isValid(new EmailRequiredDummy(true, "test@sub.domain.com"), null)); // Email with subdomain
    }

    @Test
    void testUnicodeValues() {
        assertTrue(validator.isValid(new EmailRequiredDummy(true, "tëst@ëxämplë.com"), null)); // Unicode email
        assertTrue(validator.isValid(new EmailRequiredDummy(true, "用户@域名.com"), null)); // Chinese characters
        assertTrue(validator.isValid(new EmailRequiredDummy(true, "тест@пример.рф"), null)); // Cyrillic characters
    }

    @Test
    void testLongValues() {
        String longEmail = "verylongemailaddressthatmightexceednormallimits@verylongdomainname.com";
        assertTrue(validator.isValid(new EmailRequiredDummy(true, longEmail), null)); // Long email
        assertTrue(validator.isValid(new EmailRequiredDummy(false, longEmail), null)); // Trigger not present, long email
    }

    @Test
    void testCaseSensitiveValues() {
        assertTrue(validator.isValid(new EmailRequiredDummy(true, "Test@Example.com"), null)); // Case sensitive email
        assertTrue(validator.isValid(new EmailRequiredDummy(true, "TEST@EXAMPLE.COM"), null)); // Uppercase email
        assertTrue(validator.isValid(new EmailRequiredDummy(true, "test@example.com"), null)); // Lowercase email
    }
}
