package id.xtramile.validator.integration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Assertions for validation message integration tests: API-facing text must never expose
 * raw bundle keys such as {@code validation.data.name}.
 */
public final class ValidationMessageAssertions {

    private ValidationMessageAssertions() {}

    /**
     * Ensures the resolved message is human-readable text, not a {@code validation.*} key.
     */
    public static void assertNoRawValidationKey(String resolvedMessage) {
        assertNotNull(resolvedMessage, "resolved message must not be null");
        assertFalse(
                resolvedMessage.contains("validation."),
                "Resolved message must not expose raw validation keys: " + resolvedMessage);
    }
}
