package id.xtramile.validator.web;

import id.xtramile.validator.enums.ResponseType;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Default {@link ErrorEnvelopeBuilder} using {@link ResponseType} codes and bilingual messages.
 */
public class DefaultErrorEnvelopeBuilder implements ErrorEnvelopeBuilder {

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Object> validation(List<String> errors) {
        ResponseType validationError = ResponseType.VALIDATION_FAILED;

        Map<String, Object> body = buildResponse(validationError);
        body.put("data", null);
        body.put("errors", errors);

        return body;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Object> unknown(String cause, String error) {
        ResponseType unknownError = ResponseType.UNKNOWN_ERROR;

        Map<String, Object> body = buildResponse(unknownError);
        body.put("data", Map.of("cause", cause, "error", error));

        return body;
    }

    private Map<String, Object> buildResponse(ResponseType unknownError) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("code", unknownError.getMessageCode());
        body.put("status", "ERROR");

        Map<String, String> message = new LinkedHashMap<>();
        message.put("en", unknownError.getDescriptionEn());
        message.put("id", unknownError.getDescriptionId());

        body.put("message", message);

        return body;
    }
}
