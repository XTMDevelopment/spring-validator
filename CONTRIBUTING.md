# Contributing to Spring Validator

Thank you for contributing to **Validator** (`id.xtramile.validator`). This guide covers local setup, quality gates, and how to add a new constraint.

## Requirements

- **JDK 17+** (the build is tested on 17, 21, and 25)
- **Maven 3.9+**
- **Spring Boot 3.5.x** on the classpath when integrating the library

## Build commands

```bash
# Full build: tests, Javadoc, JaCoCo checks
mvn clean verify

# Faster local iteration (skips Javadoc and JaCoCo enforcement)
mvn clean verify -Pquick

# Individual quality commands
mvn javadoc:javadoc
mvn jacoco:report
mvn jacoco:check
```

CI runs `mvn clean verify` on Java 17 and `mvn clean test` on Java 17, 21, and 25.

## Source layout

| Path | Purpose |
|------|---------|
| `src/main/java/.../annotation/` | Constraint annotations |
| `src/main/java/.../validator/` | Constraint validator implementations |
| `src/main/java/.../web/` | Friendly messages, registry, MVC exception handling |
| `src/main/java/.../autoconfigure/` | Spring Boot auto-configuration |
| `src/test/java/.../support/` | Shared test fixtures (not published) |

### Source / test separation

- Production code lives only under `src/main/java`.
- Tests and fixtures live only under `src/test/java`.
- Do **not** reference `src/test` classes from `src/main`.
- Do **not** place test helpers under `src/main`.

## Adding a new validator

1. **Annotation** — create `@interface` under `annotation/<category>/` with `@Constraint(validatedBy = ...)`.
2. **Validator** — implement `ConstraintValidator` under `validator/<category>/`.
3. **Registry** — register the annotation in `AnnotationRegistry` (single source of truth).
4. **Messages** — add the friendly message key to:
   - `src/main/resources/messages_en.properties`
   - `src/main/resources/messages_id.properties`
   - the appropriate `*ConstraintMessages` class under `web/messages/`
5. **Tests** — add unit tests for the validator and an integration test for message resolution if needed.
6. **Completeness** — `AnnotationRegistryCompletenessTest` must pass (every `@Constraint` in `annotation/**` is registered).

## Pull request checklist

- [ ] Tests added or updated for changed behaviour
- [ ] English and Indonesian message keys kept in parity
- [ ] Public API Javadoc updated where touched
- [ ] `mvn clean verify` passes locally
- [ ] No version bump unless agreed with maintainers

## Questions

Open an issue on [GitHub](https://github.com/XTMDevelopment/spring-validator) for design questions before large changes.
