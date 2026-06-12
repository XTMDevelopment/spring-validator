package id.xtramile.validator.web.messages;

import id.xtramile.validator.annotation.kyc.ValidIDImage;
import id.xtramile.validator.annotation.kyc.ValidSelfieImage;
import id.xtramile.validator.web.MessageResourceResolver;

import java.util.Map;

import static id.xtramile.validator.enums.Group.KYC;

final class KycConstraintMessages extends AbstractGroupConstraintMessages {

    KycConstraintMessages(MessageResourceResolver messageResolver) {
        super(messageResolver, KYC);
    }

    @Override
    public String resolve(String field, Class<?> type, Map<String, Object> attrs, Class<?> dtoClass) {
        if (type == ValidIDImage.class) {
            return messageResolver.getMessage(KYC, "image-id", field);
        }

        if (type == ValidSelfieImage.class) {
            return messageResolver.getMessage(KYC, "image-selfie", field);
        }

        return null;
    }
}
