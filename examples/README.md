# Examples

Sample projects that consume Validator artifacts. **Not published** to Maven Central.

## spring-boot-starter-sample

Minimal Spring Boot app using `id.xtramile.validator:validator-spring-boot-starter`.

### Prerequisites

Install Validator modules to the local repository from the repository root:

```bash
mvn install -DskipTests -Dgpg.skip=true
```

### Run tests

```bash
mvn -f examples/spring-boot-starter-sample/pom.xml test
```

### Run the app

```bash
mvn -f examples/spring-boot-starter-sample/pom.xml spring-boot:run
```

POST invalid JSON to see friendly validation errors:

```bash
curl -s -X POST http://localhost:8080/register \
  -H "Content-Type: application/json" \
  -d "{\"name\":\"\",\"email\":\"bad\",\"password\":\"weak\"}"
```

Expected: HTTP 400 with Indonesian validation messages in the error envelope.

### Dependency used

```xml
<dependency>
    <groupId>id.xtramile.validator</groupId>
    <artifactId>validator-spring-boot-starter</artifactId>
    <version>1.0</version>
</dependency>
```

Use the same version as the parent POM (`1.x`) when consuming a release from Maven Central.
