package id.xtramile.validator.web;

import java.util.List;
import java.util.Map;

/**
 * Builds standardized API error response bodies.
 */
public interface ErrorEnvelopeBuilder {

    /**
     * Builds a validation-failure response envelope.
     *
     * @param errors resolved validation error messages
     * @return the response body map
     */
    Map<String, Object> validation(List<String> errors);

    /**
     * Builds an unknown-error response envelope.
     *
     * @param cause human-readable cause description
     * @param error exception or error type name
     * @return the response body map
     */
    Map<String, Object> unknown(String cause, String error);
}
