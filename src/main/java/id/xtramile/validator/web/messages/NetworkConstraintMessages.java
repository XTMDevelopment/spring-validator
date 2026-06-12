package id.xtramile.validator.web.messages;

import id.xtramile.validator.annotation.network.*;
import id.xtramile.validator.web.MessageResourceResolver;

import java.util.Map;

import static id.xtramile.validator.enums.Group.NETWORK;

final class NetworkConstraintMessages extends AbstractGroupConstraintMessages {

    NetworkConstraintMessages(MessageResourceResolver messageResolver) {
        super(messageResolver, NETWORK);
    }

    @Override
    public String resolve(String field, Class<?> type, Map<String, Object> attrs, Class<?> dtoClass) {
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
}
