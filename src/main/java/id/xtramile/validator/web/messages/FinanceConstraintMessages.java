package id.xtramile.validator.web.messages;

import id.xtramile.validator.annotation.finance.*;
import id.xtramile.validator.web.MessageResourceResolver;

import java.util.Map;

import static id.xtramile.validator.enums.Group.FINANCE;

final class FinanceConstraintMessages extends AbstractGroupConstraintMessages {

    FinanceConstraintMessages(MessageResourceResolver messageResolver) {
        super(messageResolver, FINANCE);
    }

    @Override
    public String resolve(String field, Class<?> type, Map<String, Object> attrs, Class<?> dtoClass) {
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
}
