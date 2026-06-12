package id.xtramile.validator.web;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.metadata.ConstraintDescriptor;
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
import org.springframework.web.server.ResponseStatusException;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ApiExceptionHandlerTest {

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
    void shouldHandleConstraintViolationExceptionWithEmptyViolations() {
        // Given
        ConstraintViolationException exception = mock(ConstraintViolationException.class);
        when(exception.getConstraintViolations()).thenReturn(Set.of());

        when(mockBuilder.validation(List.of())).thenReturn(Map.of("test", "response"));

        // When
        ResponseEntity<Object> response = handler.onConstraintViolationException(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        verify(mockBuilder).validation(List.of());
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    void shouldHandleConstraintViolationExceptionWithViolations() {
        // Given
        Path path = mock(Path.class);
        when(path.toString()).thenReturn("email");

        ConstraintViolation violation = mock(ConstraintViolation.class);
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getRootBeanClass()).thenReturn(Object.class);
        when(violation.getMessageTemplate()).thenReturn("{friendly.default}");

        ConstraintDescriptor descriptor = mock(ConstraintDescriptor.class);
        when(violation.getConstraintDescriptor()).thenReturn(descriptor);
        
        Annotation annotation = mock(Annotation.class);
        when(descriptor.getAnnotation()).thenReturn(annotation);
        when(annotation.annotationType()).thenReturn((Class) NotNull.class);
        when(descriptor.getAttributes()).thenReturn(Map.of());
        
        ConstraintViolationException exception = mock(ConstraintViolationException.class);
        when(exception.getConstraintViolations()).thenReturn(Set.of(violation));
        
        when(mockMessageResolver.resolve(any(), eq("email"), eq(Object.class))).thenReturn("email is required");
        when(mockBuilder.validation(List.of("email is required"))).thenReturn(Map.of("test", "response"));

        // When
        ResponseEntity<Object> response = handler.onConstraintViolationException(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        verify(mockBuilder).validation(List.of("email is required"));
    }

    @Test
    void shouldHandleResponseStatusExceptionWith4xxError() {
        // Given
        ResponseStatusException exception = new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request data");

        List<String> expectedErrors = List.of("Invalid request data");

        when(mockBuilder.validation(expectedErrors)).thenReturn(Map.of("test", "response"));

        // When
        ResponseEntity<Object> response = handler.onResponseStatus(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        verify(mockBuilder).validation(expectedErrors);
    }

    @Test
    void shouldHandleResponseStatusExceptionWith4xxErrorAndNullReason() {
        // Given
        ResponseStatusException exception = new ResponseStatusException(HttpStatus.BAD_REQUEST);

        List<String> expectedErrors = List.of("Bad request");

        when(mockBuilder.validation(expectedErrors)).thenReturn(Map.of("test", "response"));

        // When
        ResponseEntity<Object> response = handler.onResponseStatus(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        verify(mockBuilder).validation(expectedErrors);
    }

    @Test
    void shouldHandleResponseStatusExceptionWith5xxError() {
        // Given
        ResponseStatusException exception = new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");

        when(mockBuilder.unknown("Internal server error", "ResponseStatusException"))
                .thenReturn(Map.of("test", "response"));

        // When
        ResponseEntity<Object> response = handler.onResponseStatus(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        verify(mockBuilder).unknown("Internal server error", "ResponseStatusException");
    }

    @Test
    void shouldHandleResponseStatusExceptionWith5xxErrorAndNullReason() {
        // Given
        ResponseStatusException exception = new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);

        when(mockBuilder.unknown(null, "ResponseStatusException"))
                .thenReturn(Map.of("test", "response"));

        // When
        ResponseEntity<Object> response = handler.onResponseStatus(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        verify(mockBuilder).unknown(null, "ResponseStatusException");
    }

    @Test
    void shouldHandleMethodArgumentNotValidException() {
        // Given
        FieldError fieldError1 = mock(FieldError.class);
        when(fieldError1.getField()).thenReturn("email");
        when(fieldError1.getDefaultMessage()).thenReturn("Email is required");

        FieldError fieldError2 = mock(FieldError.class);
        when(fieldError2.getField()).thenReturn("password");
        when(fieldError2.getDefaultMessage()).thenReturn("Password must be at least 8 characters");

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));
        when(bindingResult.getTarget()).thenReturn(new Object());

        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);

        when(mockMessageResolver.resolve(fieldError1, Object.class)).thenReturn("Email is required");
        when(mockMessageResolver.resolve(fieldError2, Object.class)).thenReturn("Password must be at least 8 characters");

        List<String> expectedErrors = List.of(
                "Email is required",
                "Password must be at least 8 characters"
        );

        when(mockBuilder.validation(expectedErrors)).thenReturn(Map.of("test", "response"));

        // When
        ResponseEntity<Object> response = handler.onValidationException(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        verify(mockBuilder).validation(expectedErrors);
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

    @Test
    void shouldHandleMethodArgumentNotValidExceptionWithFriendlyDefaultMessage() {
        // Given
        FieldError fieldError = mock(FieldError.class);
        when(fieldError.getField()).thenReturn("email");
        when(fieldError.getDefaultMessage()).thenReturn("{friendly.default}");
        when(fieldError.getCode()).thenReturn("NotNull");

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));
        when(bindingResult.getTarget()).thenReturn(new Object());

        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);

        when(mockMessageResolver.resolve(fieldError, Object.class)).thenReturn("email is required");

        // When
        ResponseEntity<Object> response = handler.onValidationException(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        verify(mockBuilder).validation(List.of("email is required"));
    }

    @Test
    void shouldHandleMethodArgumentNotValidExceptionWithEmptyDefaultMessage() {
        // Given
        FieldError fieldError = mock(FieldError.class);
        when(fieldError.getField()).thenReturn("email");
        when(fieldError.getDefaultMessage()).thenReturn("");

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));
        when(bindingResult.getTarget()).thenReturn(new Object());

        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);

        when(mockMessageResolver.resolve(fieldError, Object.class)).thenReturn("email tidak valid");

        List<String> expectedErrors = List.of("email tidak valid");

        when(mockBuilder.validation(expectedErrors)).thenReturn(Map.of("test", "response"));

        // When
        ResponseEntity<Object> response = handler.onValidationException(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        verify(mockBuilder).validation(expectedErrors);
    }

    @Test
    void shouldHandleMethodArgumentNotValidExceptionWithNullDefaultMessage() {
        // Given
        FieldError fieldError = mock(FieldError.class);
        when(fieldError.getField()).thenReturn("email");
        when(fieldError.getDefaultMessage()).thenReturn(null);

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));
        when(bindingResult.getTarget()).thenReturn(new Object());

        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);

        when(mockMessageResolver.resolve(fieldError, Object.class)).thenReturn("email tidak valid");

        List<String> expectedErrors = List.of("email tidak valid");

        when(mockBuilder.validation(expectedErrors)).thenReturn(Map.of("test", "response"));

        // When
        ResponseEntity<Object> response = handler.onValidationException(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        verify(mockBuilder).validation(expectedErrors);
    }

    @Test
    void shouldHandleMultipleFieldErrorsInMethodArgumentNotValidException() {
        // Given
        FieldError fieldError1 = mock(FieldError.class);
        when(fieldError1.getField()).thenReturn("email");
        when(fieldError1.getDefaultMessage()).thenReturn("Email is required");

        FieldError fieldError2 = mock(FieldError.class);
        when(fieldError2.getField()).thenReturn("password");
        when(fieldError2.getDefaultMessage()).thenReturn("Password must be at least 8 characters");

        FieldError fieldError3 = mock(FieldError.class);
        when(fieldError3.getField()).thenReturn("age");
        when(fieldError3.getDefaultMessage()).thenReturn("Age must be at least 18");

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2, fieldError3));
        when(bindingResult.getTarget()).thenReturn(new Object());

        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);

        when(mockMessageResolver.resolve(fieldError1, Object.class)).thenReturn("Email is required");
        when(mockMessageResolver.resolve(fieldError2, Object.class)).thenReturn("Password must be at least 8 characters");
        when(mockMessageResolver.resolve(fieldError3, Object.class)).thenReturn("Age must be at least 18");

        List<String> expectedErrors = List.of(
                "Email is required",
                "Password must be at least 8 characters",
                "Age must be at least 18"
        );

        when(mockBuilder.validation(expectedErrors)).thenReturn(Map.of("test", "response"));

        // When
        ResponseEntity<Object> response = handler.onValidationException(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        verify(mockBuilder).validation(expectedErrors);
    }

    @Test
    void shouldHandleResponseStatusExceptionWithDifferent4xxStatusCodes() {
        // Test 400 Bad Request
        ResponseStatusException badRequest = new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request");
        ResponseEntity<Object> response1 = handler.onResponseStatus(badRequest);
        assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        // Test 401 Unauthorized - should return 400 because it's 4xx
        ResponseStatusException unauthorized = new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        ResponseEntity<Object> response2 = handler.onResponseStatus(unauthorized);
        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        // Test 403 Forbidden - should return 400 because it's 4xx
        ResponseStatusException forbidden = new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        ResponseEntity<Object> response3 = handler.onResponseStatus(forbidden);
        assertThat(response3.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        // Test 404 Not Found - should return 400 because it's 4xx
        ResponseStatusException notFound = new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found");
        ResponseEntity<Object> response4 = handler.onResponseStatus(notFound);
        assertThat(response4.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldHandleResponseStatusExceptionWithDifferent5xxStatusCodes() {
        // Test 500 Internal Server Error
        ResponseStatusException internalError = new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error");
        ResponseEntity<Object> response1 = handler.onResponseStatus(internalError);
        assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

        // Test 502 Bad Gateway
        ResponseStatusException badGateway = new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Bad gateway");
        ResponseEntity<Object> response2 = handler.onResponseStatus(badGateway);
        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);

        // Test 503 Service Unavailable
        ResponseStatusException serviceUnavailable = new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Service unavailable");
        ResponseEntity<Object> response3 = handler.onResponseStatus(serviceUnavailable);
        assertThat(response3.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    void shouldHandleConstraintViolationExceptionWithNullPropertyPath() {
        // Given
        ConstraintViolation violation = mock(ConstraintViolation.class);
        when(violation.getPropertyPath()).thenReturn(null); // null property path
        when(violation.getRootBeanClass()).thenReturn(Object.class);
        when(violation.getMessageTemplate()).thenReturn("{friendly.default}");

        ConstraintDescriptor descriptor = mock(ConstraintDescriptor.class);
        when(violation.getConstraintDescriptor()).thenReturn(descriptor);
        
        Annotation annotation = mock(Annotation.class);
        when(descriptor.getAnnotation()).thenReturn(annotation);
        when(annotation.annotationType()).thenReturn((Class) NotNull.class);
        when(descriptor.getAttributes()).thenReturn(Map.of());
        
        ConstraintViolationException exception = mock(ConstraintViolationException.class);
        when(exception.getConstraintViolations()).thenReturn(Set.of(violation));
        
        when(mockMessageResolver.resolve(any(), eq("request"), eq(Object.class))).thenReturn("request is required");
        when(mockBuilder.validation(List.of("request is required"))).thenReturn(Map.of("test", "response"));

        // When
        ResponseEntity<Object> response = handler.onConstraintViolationException(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        verify(mockBuilder).validation(List.of("request is required"));
    }
}
