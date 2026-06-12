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

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ApiExceptionHandler {

    private final ErrorEnvelopeBuilder builder;
    private final FriendlyMessageResolver messageResolver;

    public ApiExceptionHandler(ErrorEnvelopeBuilder builder, FriendlyMessageResolver messageResolver) {
        this.builder = builder;
        this.messageResolver = messageResolver;
        System.out.println("ApiExceptionHandler loaded from id.xtramile.validator");
    }

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

    private static Class<?> resolveDtoClassFromBinding(MethodArgumentNotValidException ex) {
        Object target = ex.getBindingResult().getTarget();
        return target != null ? target.getClass() : Object.class;
    }
}
