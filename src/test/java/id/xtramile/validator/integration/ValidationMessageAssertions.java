package id.xtramile.validator.integration;

import id.xtramile.validator.web.FriendlyMessageResolver;
import jakarta.validation.ConstraintViolation;

import static org.assertj.core.api.Assertions.assertThat;

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
        assertThat(resolvedMessage)
                .as("resolved message must not be null")
                .isNotNull();
        assertThat(resolvedMessage)
                .as("resolved message must not expose raw validation keys")
                .doesNotContain("validation.");
    }

    public static void assertResolvedMessage(
            FriendlyMessageResolver resolver,
            ConstraintViolation<?> violation,
            String field,
            Class<?> dto,
            String expected) {
        String resolved = resolver.resolve(violation, field, dto);
        assertThat(resolved).isEqualTo(expected);
        assertNoRawValidationKey(resolved);
    }
}
