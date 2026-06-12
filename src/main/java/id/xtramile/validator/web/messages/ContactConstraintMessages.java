package id.xtramile.validator.web.messages;

import id.xtramile.validator.annotation.contact.*;
import id.xtramile.validator.web.MessageResourceResolver;

import java.util.Map;

import static id.xtramile.validator.enums.Group.CONTACT;

final class ContactConstraintMessages extends AbstractGroupConstraintMessages {

    ContactConstraintMessages(MessageResourceResolver messageResolver) {
        super(messageResolver, CONTACT);
    }

    @Override
    public String resolve(String field, Class<?> type, Map<String, Object> attrs, Class<?> dtoClass) {
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
}
