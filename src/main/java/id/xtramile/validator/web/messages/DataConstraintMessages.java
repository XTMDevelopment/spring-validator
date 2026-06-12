package id.xtramile.validator.web.messages;

import id.xtramile.validator.annotation.data.*;
import id.xtramile.validator.web.MessageResourceResolver;

import java.util.Map;

import static id.xtramile.validator.enums.Group.DATA;

final class DataConstraintMessages extends AbstractGroupConstraintMessages {

    DataConstraintMessages(MessageResourceResolver messageResolver) {
        super(messageResolver, DATA);
    }

    @Override
    public String resolve(String field, Class<?> type, Map<String, Object> attrs, Class<?> dtoClass) {
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
}
