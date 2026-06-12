package id.xtramile.validator.web;

import id.xtramile.validator.util.MessageUtils;
import jakarta.validation.ConstraintViolationException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Global exception handler that maps validation and client errors to API error envelopes.
 */
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ApiExceptionHandler {

    private final ErrorEnvelopeBuilder builder;
    private final FriendlyMessageResolver messageResolver;

    /**
     * Creates a handler with the given envelope builder and message resolver.
     *
     * @param builder         builds standardized error response bodies
     * @param messageResolver resolves user-friendly validation messages
     */
    public ApiExceptionHandler(ErrorEnvelopeBuilder builder, FriendlyMessageResolver messageResolver) {
        this.builder = builder;
        this.messageResolver = messageResolver;
    }

    private static Class<?> resolveDtoClassFromBinding(MethodArgumentNotValidException ex) {
        Object target = ex.getBindingResult().getTarget();
        return target != null ? target.getClass() : Object.class;
    }

    /**
     * Handles bean-validation constraint violations on method parameters.
     *
     * @param ex the constraint violation exception
     * @return a 400 response with resolved validation messages
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> onConstraintViolationException(ConstraintViolationException ex) {
        try {
            List<String> errors = ex.getConstraintViolations()
                    .stream()
                    .map(v -> {
                        String key = v.getPropertyPath() == null ? "request" : v.getPropertyPath().toString();
                        Class<?> rootBeanClass = v.getRootBeanClass();
                        return messageResolver.resolve(v, key, rootBeanClass);
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.badRequest().body(builder.validation(errors));
        } finally {
            MessageUtils.clearStoredArgs();
        }
    }

    /**
     * Handles {@link ResponseStatusException}; 4xx errors become validation responses.
     *
     * @param ex the response status exception
     * @return a response with an appropriate status and error envelope
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Object> onResponseStatus(ResponseStatusException ex) {
        if (ex.getStatusCode().is4xxClientError()) {
            String reason = ex.getReason() == null ? "Bad request" : ex.getReason();

            return ResponseEntity.badRequest().body(builder.validation(
                    List.of(reason)
            ));
        }

        return ResponseEntity.status(ex.getStatusCode())
                .body(builder.unknown(ex.getReason(), ex.getClass().getSimpleName()));
    }

    /**
     * Handles {@code @Valid} binding failures on request body or form fields.
     *
     * @param ex the method argument validation exception
     * @return a 400 response with resolved field error messages
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> onValidationException(MethodArgumentNotValidException ex) {
        try {
            Class<?> dtoClass = resolveDtoClassFromBinding(ex);
            List<String> errors = ex.getBindingResult()
                    .getFieldErrors()
                    .stream()
                    .map(err -> messageResolver.resolve(err, dtoClass))
                    .collect(Collectors.toList());

            return ResponseEntity.badRequest().body(builder.validation(errors));

        } finally {
            MessageUtils.clearStoredArgs();
        }
    }
}
