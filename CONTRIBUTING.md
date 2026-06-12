# Contributing to Validator

Thank you for contributing to **Validator** (`id.xtramile.validator`). This guide covers local setup, module boundaries, quality gates, and how to add a new constraint.

## Requirements

- **JDK 17+** (the build is tested on 17, 21, and 25)
- **Maven 3.9+**
- **Spring Boot 3.5.x** when working on the starter or integration tests

## Build commands

```bash
# Full reactor: tests, Javadoc, JaCoCo aggregate
mvn clean verify -Dspring-boot.version=3.5.14 -Dgpg.skip=true

# Single module and its dependencies (example: web)
mvn clean verify -pl validator-web -am -Dgpg.skip=true

# Faster local iteration (skips Javadoc and JaCoCo enforcement)
mvn clean verify -Pquick

# Install locally for the sample consumer
mvn install -DskipTests -Dgpg.skip=true
mvn -f examples/spring-boot-starter-sample/pom.xml test
```

CI runs `mvn clean verify` on Java 17 (with `-Dgpg.skip=true`) and `mvn clean test` across Java 17, 21, and 25.

## Multi-module layout

| Module | Path | Production code | Tests |
|--------|------|-----------------|-------|
| **Parent** | `pom.xml` | — | JaCoCo aggregate |
| **test-support** | `validator-test-support/` | — (test-jar only) | `ValidatorTestSupport`, `MockMultipartFile` |
| **core** | `validator-core/` | `annotation/`, `validator/`, `util/`, `enums/` | validator, util, enums unit tests |
| **web** | `validator-web/` | `web/`, `messages_*.properties` | `web/*`, `integration/*` (no starter), message support fixtures |
| **starter** | `validator-spring-boot-starter/` | `autoconfigure/`, `config/`, `META-INF/spring/` | `autoconfigure/*`, `config/*`, Boot integration tests |

**Reactor build order:** `validator-test-support` → `validator-core` → `validator-web` → `validator-spring-boot-starter`

### Dependency rules

- `validator-core` — no compile-scope Spring Boot dependencies; optional `spring-beans`, `spring-web`, `jackson-databind`, `slf4j-api`
- `validator-web` — depends on `validator-core`
- `validator-spring-boot-starter` — depends on `validator-web`
- `validator-test-support` — must **not** depend on `core` or `web` (avoids Maven reactor cycles); published only as `test-jar` to sibling module test scopes

### Source / test separation

- Production code lives only under each module's `src/main/java`.
- Tests live under `src/test/java` (or `validator-test-support` test-jar sources under `src/test/java`).
- Do **not** reference test classes from `src/main`.
- Web- or starter-specific test helpers stay in that module's `src/test/java/.../support/`, not in `validator-test-support`.

## Adding a new validator

1. **Annotation** — create `@interface` under `validator-core/src/main/java/.../annotation/<category>/` with `@Constraint(validatedBy = ...)`.
2. **Validator** — implement `ConstraintValidator` under `validator-core/src/main/java/.../validator/<category>/`.
3. **Registry** — register the annotation in `AnnotationRegistry` (`validator-web`).
4. **Messages** — add keys to:
   - `validator-web/src/main/resources/messages_en.properties`
   - `validator-web/src/main/resources/messages_id.properties`
   - the appropriate `*ConstraintMessages` class under `validator-web/.../web/messages/`
5. **Unit tests** — add tests under `validator-core/src/test/java/.../validator/<category>/`.
6. **Message integration** — add an integration test under `validator-web/src/test/java/.../integration/` if friendly message resolution should be verified.
7. **Completeness** — `AnnotationRegistryCompletenessTest` must pass (every `@Constraint` in `annotation/**` is registered).

If the validator needs Spring Boot auto-configuration changes, update `validator-spring-boot-starter` and add tests there.

## Pull request checklist

- [ ] Code placed in the correct module (`core`, `web`, or `starter`)
- [ ] Tests added or updated in the matching module
- [ ] English and Indonesian message keys kept in parity
- [ ] Public API Javadoc updated where touched
- [ ] `mvn clean verify -Dgpg.skip=true` passes locally
- [ ] No version bump unless agreed with maintainers

## Questions

Open an issue on [GitHub](https://github.com/XTMDevelopment/spring-validator) for design questions before large changes.
