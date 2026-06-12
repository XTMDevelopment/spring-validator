# Validator

Indonesian-friendly Jakarta Bean Validation library for Spring Boot applications. Provides custom constraint annotations, localized validation messages (Indonesian and English), and Spring MVC integration for user-friendly error responses.

**Maven coordinates:** `id.xtramile.validator`

## Requirements

- **Version:** 1.x
- **Java 17+** (tested on 17, 21, and 25)
- **Spring Boot 3.5.14+** (3.5.x)

## Installation

Add the Spring Boot starter (recommended) to your `pom.xml`:

```xml
<dependency>
    <groupId>id.xtramile.validator</groupId>
    <artifactId>validator-spring-boot-starter</artifactId>
    <version>1.0</version>
</dependency>
```

### Module layout (1.x)

| Artifact | Purpose |
|----------|---------|
| `validator-spring-boot-starter` | **Recommended** — auto-configuration + validation + web integration |
| `validator-core` | Annotations and validators only (no Spring Boot) |
| `validator-web` | Message resolution and `ApiExceptionHandler` (Spring MVC) |

Cross-field validators (`@FieldMatch`, `@AtLeastOneOf`, etc.) require Spring Beans on the classpath; the starter provides this automatically.

Auto-configuration is registered via `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` and loads when Spring Boot is on the classpath.

## Quick Start

1. Add the dependency (see above).

2. Annotate your DTO fields with custom constraints:

```java
public class RegistrationRequest {

    @ValidEmail
    private String email;

    @ValidPassword
    private String password;

    @NotBlank
    private String name;
}
```

3. Enable or disable the library (enabled by default):

```properties
id.xtramile.validator.enabled=true
```

When enabled, `ValidationAutoConfiguration` registers `ApiExceptionHandler`, `FriendlyMessageResolver`, and related beans for localized validation error responses.

## Configuration

| Property | Default | Description |
|----------|---------|-------------|
| `id.xtramile.validator.enabled` | `true` | Enable or disable auto-configuration |
| `id.xtramile.validator.locale` | `id` | Message locale (`id` or `en`) |

Example:

```properties
id.xtramile.validator.locale=en
```

## Migration from `spring-validator` artifacts

If you previously used `id.xtramile:spring-validator*` coordinates, switch to `id.xtramile.validator`:

| Before | After |
|--------|-------|
| `id.xtramile:spring-validator-spring-boot-starter` | `id.xtramile.validator:validator-spring-boot-starter` |
| `id.xtramile:spring-validator-core` | `id.xtramile.validator:validator-core` |
| `id.xtramile:spring-validator-web` | `id.xtramile.validator:validator-web` |

Java packages (`id.xtramile.validator.*`) are unchanged.

## Quality Commands

```bash
# Full multi-module build: tests, Javadoc, JaCoCo aggregate checks
mvn clean verify -Dspring-boot.version=3.5.14

# Run tests only
mvn clean test

# Skip Javadoc and JaCoCo enforcement for faster local iteration
mvn clean verify -Pquick

# Individual reports
mvn javadoc:javadoc
mvn jacoco:report
```

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) for development setup, coding standards, and how to add new validators.

## License

MIT — see [LICENSE](LICENSE) if present, or the `pom.xml` license section.
