package id.xtramile.validator.web;

import id.xtramile.validator.enums.ResponseType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultErrorEnvelopeBuilderTest {

    private DefaultErrorEnvelopeBuilder builder;

    @BeforeEach
    void setUp() {
        builder = new DefaultErrorEnvelopeBuilder();
    }

    @Test
    void shouldBuildValidationResponseWithSingleError() {
        // Given
        List<String> errors = List.of("Email is required");

        // When
        Map<String, Object> result = builder.validation(errors);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).containsKey("code");
        assertThat(result).containsKey("status");
        assertThat(result).containsKey("message");
        assertThat(result).containsKey("data");
        assertThat(result).containsKey("errors");

        // Verify response structure
        assertThat(result.get("code")).isEqualTo(ResponseType.VALIDATION_FAILED.getMessageCode());
        assertThat(result.get("status")).isEqualTo("ERROR");
        assertThat(result.get("data")).isNull();

        // Verify message structure
        @SuppressWarnings("unchecked")
        Map<String, String> message = (Map<String, String>) result.get("message");
        assertThat(message).containsKey("en");
        assertThat(message).containsKey("id");
        assertThat(message.get("en")).isEqualTo(ResponseType.VALIDATION_FAILED.getDescriptionEn());
        assertThat(message.get("id")).isEqualTo(ResponseType.VALIDATION_FAILED.getDescriptionId());

        // Verify errors array
        @SuppressWarnings("unchecked")
        List<String> errorsArray = (List<String>) result.get("errors");
        assertThat(errorsArray).hasSize(1);
        assertThat(errorsArray.get(0)).isEqualTo("Email is required");
    }

    @Test
    void shouldBuildValidationResponseWithMultipleErrors() {
        // Given
        List<String> errors = List.of(
                "Email is required",
                "Password must be at least 8 characters",
                "Age must be at least 18"
        );

        // When
        Map<String, Object> result = builder.validation(errors);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.get("code")).isEqualTo(ResponseType.VALIDATION_FAILED.getMessageCode());
        assertThat(result.get("status")).isEqualTo("ERROR");
        assertThat(result.get("data")).isNull();

        // Verify errors array
        @SuppressWarnings("unchecked")
        List<String> errorsArray = (List<String>) result.get("errors");
        assertThat(errorsArray).hasSize(3);
        assertThat(errorsArray.get(0)).isEqualTo("Email is required");
        assertThat(errorsArray.get(1)).isEqualTo("Password must be at least 8 characters");
        assertThat(errorsArray.get(2)).isEqualTo("Age must be at least 18");
    }

    @Test
    void shouldBuildValidationResponseWithEmptyErrors() {
        // Given
        List<String> errors = List.of();

        // When
        Map<String, Object> result = builder.validation(errors);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.get("code")).isEqualTo(ResponseType.VALIDATION_FAILED.getMessageCode());
        assertThat(result.get("status")).isEqualTo("ERROR");
        assertThat(result.get("data")).isNull();

        // Verify errors array
        @SuppressWarnings("unchecked")
        List<String> errorsArray = (List<String>) result.get("errors");
        assertThat(errorsArray).isEmpty();
    }

    @Test
    void shouldBuildValidationResponseWithEmptyErrorMessages() {
        // Given
        List<String> errors = List.of("", "Valid message");

        // When
        Map<String, Object> result = builder.validation(errors);

        // Then
        assertThat(result).isNotNull();
        
        @SuppressWarnings("unchecked")
        List<String> errorsArray = (List<String>) result.get("errors");
        assertThat(errorsArray).hasSize(2);
        assertThat(errorsArray.get(0)).isEmpty();
        assertThat(errorsArray.get(1)).isEqualTo("Valid message");
    }

    @Test
    void shouldBuildValidationResponseWithSpecialCharacters() {
        // Given
        List<String> errors = List.of(
                "Message with special chars: @#$%",
                "Array field message",
                "Message with spaces"
        );

        // When
        Map<String, Object> result = builder.validation(errors);

        // Then
        assertThat(result).isNotNull();
        
        @SuppressWarnings("unchecked")
        List<String> errorsArray = (List<String>) result.get("errors");
        assertThat(errorsArray).hasSize(3);
        assertThat(errorsArray.get(0)).isEqualTo("Message with special chars: @#$%");
        assertThat(errorsArray.get(1)).isEqualTo("Array field message");
        assertThat(errorsArray.get(2)).isEqualTo("Message with spaces");
    }

    @Test
    void shouldBuildUnknownErrorResponse() {
        // Given
        String cause = "Database connection failed";
        String error = "SQLException";

        // When
        Map<String, Object> result = builder.unknown(cause, error);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).containsKey("code");
        assertThat(result).containsKey("status");
        assertThat(result).containsKey("message");
        assertThat(result).containsKey("data");

        // Verify response structure
        assertThat(result.get("code")).isEqualTo(ResponseType.UNKNOWN_ERROR.getMessageCode());
        assertThat(result.get("status")).isEqualTo("ERROR");

        // Verify message structure
        @SuppressWarnings("unchecked")
        Map<String, String> message = (Map<String, String>) result.get("message");
        assertThat(message).containsKey("en");
        assertThat(message).containsKey("id");
        assertThat(message.get("en")).isEqualTo(ResponseType.UNKNOWN_ERROR.getDescriptionEn());
        assertThat(message.get("id")).isEqualTo(ResponseType.UNKNOWN_ERROR.getDescriptionId());

        // Verify data structure
        @SuppressWarnings("unchecked")
        Map<String, String> data = (Map<String, String>) result.get("data");
        assertThat(data).containsEntry("cause", cause);
        assertThat(data).containsEntry("error", error);
    }

    @Test
    void shouldBuildUnknownErrorResponseWithEmptyValues() {
        // Given
        String cause = "";
        String error = "";

        // When
        Map<String, Object> result = builder.unknown(cause, error);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.get("code")).isEqualTo(ResponseType.UNKNOWN_ERROR.getMessageCode());
        assertThat(result.get("status")).isEqualTo("ERROR");

        // Verify data structure
        @SuppressWarnings("unchecked")
        Map<String, String> data = (Map<String, String>) result.get("data");
        assertThat(data).containsEntry("cause", "");
        assertThat(data).containsEntry("error", "");
    }

    @Test
    void shouldBuildUnknownErrorResponseWithSpecialCharacters() {
        // Given
        String cause = "Error with special chars: @#$%^&*()";
        String error = "Exception<Type>";

        // When
        Map<String, Object> result = builder.unknown(cause, error);

        // Then
        assertThat(result).isNotNull();
        
        @SuppressWarnings("unchecked")
        Map<String, String> data = (Map<String, String>) result.get("data");
        assertThat(data).containsEntry("cause", cause);
        assertThat(data).containsEntry("error", error);
    }

    @Test
    void shouldBuildUnknownErrorResponseWithLongValues() {
        // Given
        String cause = "A".repeat(1000);
        String error = "B".repeat(500);

        // When
        Map<String, Object> result = builder.unknown(cause, error);

        // Then
        assertThat(result).isNotNull();
        
        @SuppressWarnings("unchecked")
        Map<String, String> data = (Map<String, String>) result.get("data");
        assertThat(data).containsEntry("cause", cause);
        assertThat(data).containsEntry("error", error);
    }

    @Test
    void shouldMaintainResponseOrder() {
        // Given
        List<String> errors = List.of(
                "First error",
                "Second error",
                "Third error"
        );

        // When
        Map<String, Object> result = builder.validation(errors);

        // Then
        @SuppressWarnings("unchecked")
        List<String> errorsArray = (List<String>) result.get("errors");
        assertThat(errorsArray).hasSize(3);
        
        // Verify order is maintained
        assertThat(errorsArray.get(0)).isEqualTo("First error");
        assertThat(errorsArray.get(1)).isEqualTo("Second error");
        assertThat(errorsArray.get(2)).isEqualTo("Third error");
    }

    @Test
    void shouldHandleLargeNumberOfErrors() {
        // Given
        List<String> errors = new java.util.ArrayList<>();
        for (int i = 0; i < 100; i++) {
            errors.add("Error message " + i);
        }

        // When
        Map<String, Object> result = builder.validation(errors);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.get("code")).isEqualTo(ResponseType.VALIDATION_FAILED.getMessageCode());
        assertThat(result.get("status")).isEqualTo("ERROR");

        @SuppressWarnings("unchecked")
        List<String> errorsArray = (List<String>) result.get("errors");
        assertThat(errorsArray).hasSize(100);
    }

    @SuppressWarnings("unchecked")
    @Test
    void shouldBeThreadSafe() throws InterruptedException {
        // Given
        int numberOfThreads = 10;
        Thread[] threads = new Thread[numberOfThreads];
        Map<String, Object>[] results = new Map[numberOfThreads];

        // When
        for (int i = 0; i < numberOfThreads; i++) {
            final int threadIndex = i;
            threads[i] = new Thread(() -> {
                List<String> errors = List.of("Error " + threadIndex);
                results[threadIndex] = builder.validation(errors);
            });
            threads[i].start();
        }

        // Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join();
        }

        // Then
        for (Map<String, Object> result : results) {
            assertThat(result).isNotNull();
            assertThat(result.get("code")).isEqualTo(ResponseType.VALIDATION_FAILED.getMessageCode());
            assertThat(result.get("status")).isEqualTo("ERROR");
        }
    }
}
