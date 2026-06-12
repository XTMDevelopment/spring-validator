# Validator

Indonesian-friendly Jakarta Bean Validation library for Spring Boot applications. Provides custom constraint annotations, localized validation messages (Indonesian and English), and Spring MVC integration for user-friendly error responses.

**Maven `groupId`:** `id.xtramile.validator`  
**Java packages:** `id.xtramile.validator.*` (unchanged across the multi-module split)

## Requirements

- **Version:** 1.x
- **Java:** 17, 21, 25
- **Spring Boot:** 3.5.14+ (3.5.x), 4.0.5+ (4.0.x)
- **Dependency:** `id.xtramile.validator:validator-spring-boot-starter:1.x`

## Installation

Add the Spring Boot starter (recommended) to your `pom.xml`:

```xml
<dependency>
    <groupId>id.xtramile.validator</groupId>
    <artifactId>validator-spring-boot-starter</artifactId>
    <version>1.0</version>
</dependency>
```

### Published artifacts (1.x)

| Artifact | Coordinates | Purpose |
|----------|-------------|---------|
| Starter | `id.xtramile.validator:validator-spring-boot-starter` | **Recommended** — auto-configuration, validation, web integration |
| Core | `id.xtramile.validator:validator-core` | Annotations and validators only (no Spring Boot) |
| Web | `id.xtramile.validator:validator-web` | Message resolution and `ApiExceptionHandler` (Spring MVC) |

Cross-field validators (`@FieldMatch`, `@AtLeastOneOf`, etc.) require Spring Beans on the classpath; the starter provides this automatically.

Auto-configuration is registered via `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` and loads when Spring Boot is on the classpath.

### Advanced: core or web only

Use the starter for Spring Boot apps. For custom wiring:

```xml
<!-- Annotations + validators only -->
<dependency>
    <groupId>id.xtramile.validator</groupId>
    <artifactId>validator-core</artifactId>
    <version>1.0</version>
</dependency>

<!-- MVC messages + ApiExceptionHandler (requires core transitively) -->
<dependency>
    <groupId>id.xtramile.validator</groupId>
    <artifactId>validator-web</artifactId>
    <version>1.0</version>
</dependency>
```

## Repository layout

```
validator/                          (parent POM: id.xtramile.validator:validator)
├── validator-test-support/         (internal test-jar, not published)
├── validator-core/                 annotation/, validator/, util/, enums/
├── validator-web/                  web/, messages_*.properties
├── validator-spring-boot-starter/  autoconfigure/, config/, META-INF/spring/
└── examples/spring-boot-starter-sample/
```

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
| `id.xtramile:spring-validator` (monolith) | `id.xtramile.validator:validator-spring-boot-starter` |
| `id.xtramile:spring-validator-spring-boot-starter` | `id.xtramile.validator:validator-spring-boot-starter` |
| `id.xtramile:spring-validator-core` | `id.xtramile.validator:validator-core` |
| `id.xtramile:spring-validator-web` | `id.xtramile.validator:validator-web` |

## Examples

Smoke-test consumer (not published):

```bash
mvn install -DskipTests -Dgpg.skip=true
mvn -f examples/spring-boot-starter-sample/pom.xml test
```

See [examples/README.md](examples/README.md) for details.

## Quality Commands

```bash
# Full multi-module build: tests, Javadoc, JaCoCo aggregate checks
mvn clean verify -Dspring-boot.version=3.5.14 -Dgpg.skip=true

# Verify Spring Boot 4.0.x compatibility (same 1.x artifacts)
mvn clean verify -Dspring-boot.version=4.0.5 -Dgpg.skip=true
# or: mvn clean verify -Pboot4 -Dgpg.skip=true

# Single module (with dependencies)
mvn clean verify -pl validator-web -am -Dgpg.skip=true

# Run tests only
mvn clean test

# Skip Javadoc and JaCoCo enforcement for faster local iteration
mvn clean verify -Pquick

# JaCoCo aggregate report (after verify)
# target/site/jacoco-aggregate/
```

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) for development setup, module boundaries, and how to add new validators.

## License

MIT — see [LICENSE](LICENSE) if present, or the `pom.xml` license section.
