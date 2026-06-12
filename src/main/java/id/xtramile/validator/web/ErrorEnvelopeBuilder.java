package id.xtramile.validator.web;

import java.util.List;
import java.util.Map;

public interface ErrorEnvelopeBuilder {
    Map<String, Object> validation(List<String> errors);
    Map<String, Object> unknown(String cause, String error);
}
