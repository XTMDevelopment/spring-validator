package id.xtramile.validator.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Regression tests for Phase 4 null {@code bindingResult.getTarget()} handling.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ApiExceptionHandlerNullTargetTest {

    @Mock
    private ErrorEnvelopeBuilder mockBuilder;

    @Mock
    private FriendlyMessageResolver mockMessageResolver;

    private ApiExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new ApiExceptionHandler(mockBuilder, mockMessageResolver);
    }

    @Test
    void shouldHandleMethodArgumentNotValidExceptionWithNullTarget() {
        FieldError fieldError = mock(FieldError.class);
        when(fieldError.getField()).thenReturn("email");
        when(fieldError.getDefaultMessage()).thenReturn("Email is required");

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));
        when(bindingResult.getTarget()).thenReturn(null);

        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);

        when(mockMessageResolver.resolve(fieldError, Object.class)).thenReturn("Email is required");
        when(mockBuilder.validation(List.of("Email is required"))).thenReturn(Map.of("test", "response"));

        ResponseEntity<Object> response = handler.onValidationException(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(mockMessageResolver).resolve(fieldError, Object.class);
        verify(mockBuilder).validation(List.of("Email is required"));
    }
}
