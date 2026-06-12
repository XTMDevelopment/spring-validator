package id.xtramile.validator.web.messages;

import id.xtramile.validator.annotation.location.*;
import id.xtramile.validator.web.MessageResourceResolver;

import java.util.Map;

import static id.xtramile.validator.enums.Group.LOCATION;

final class LocationConstraintMessages extends AbstractGroupConstraintMessages {

    LocationConstraintMessages(MessageResourceResolver messageResolver) {
        super(messageResolver, LOCATION);
    }

    @Override
    public String resolve(String field, Class<?> type, Map<String, Object> attrs, Class<?> dtoClass) {
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
}
