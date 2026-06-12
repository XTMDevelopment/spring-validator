# PLAN 2 — Multi-Module & Spring Boot 4 (Group 2)

**Project:** Validator (`id.xtramile.validator`)  
**Version line:** **1.x** — multi-module split does **not** require a 2.0 major bump; artifact coordinates change under the same `1.x` release line.  
**Prerequisite:** [PLAN_1.md](PLAN_1.md) fully complete.  
**Goal:** Split into publishable Maven modules; verify Spring Boot 4.0.x; publish all artifacts to Maven Central at **1.x**.

---

## Why PLAN 2 Is Separate

| Concern           | PLAN 1                       | PLAN 2                                |
|-------------------|------------------------------|---------------------------------------|
| SOLID within code | Refactor classes/packages    | Enforce via Maven module boundaries   |
| Spring Boot 3.5.x | Compile + CI baseline        | Default for parent BOM                |
| Spring Boot 4.0.x | Not tested                   | Full CI matrix                        |
| Artifact layout   | One JAR (legacy monolith) | Four artifacts + parent under `id.xtramile.validator` (all **1.x**) |
| Version           | `1.x`                        | **Same `1.x`** — no major bump        |

```mermaid
flowchart LR
    PLAN1[PLAN 1 Complete] --> P1[P1 MultiModule]
    P1 --> P2[P2 Boot4]
    P2 --> Done[1.x Release]
```

---

## Version & Consumer Migration (1.x — no 2.0 bump)

### Policy

- **Release version stays `1.x`** (e.g. `1.0`, `1.1`) across the multi-module split.
- Breaking change is **artifact coordinate** change, not semantic API package change (`id.xtramile.validator.*` unchanged).

### Artifact mapping

| Before (PLAN 1 monolith)           | After (PLAN 2)                                         | Version                  |
|------------------------------------|--------------------------------------------------------|--------------------------|
| `id.xtramile.validator:validator:1.x` | **Deprecated** — final monolith release optional       | `1.x` last monolith      |
| —                                  | `id.xtramile:validator-core:1.x`                | same `1.x`               |
| —                                  | `id.xtramile:validator-web:1.x`                 | same `1.x`               |
| —                                  | `id.xtramile:validator-spring-boot-starter:1.x` | same `1.x` (recommended) |

### Relocation POM (same 1.x line)

Publish a **relocation POM** at the old coordinate so existing consumers get a Maven relocation notice:

```xml
<!-- id.xtramile.validator:validator:1.x (relocation artifact) -->
<distributionManagement>
    <relocation>
        <groupId>id.xtramile</groupId>
        <artifactId>validator-spring-boot-starter</artifactId>
        <version>1.x</version>
        <message>Artifact moved to validator-spring-boot-starter</message>
    </relocation>
</distributionManagement>
```

**Tasks:**
1. Publish final monolith `spring-validator:1.x` (optional) with relocation metadata, **or** publish empty relocation-only POM at same version
2. Document in `README.md` and `CHANGELOG.md` under `## [1.x]` — **Migration** section
3. **Do not** bump to `2.0` unless API-breaking changes are introduced later

---

## Phase 1: Implement Multi-Module

**Objective:** Parent POM + four child modules; correct dependency graph; Central Publishing for all **1.x** artifacts.

### 1a. Target module layout

```
validator/                              (parent POM, packaging=pom, version 1.x)
├── pom.xml
├── PLAN_1.md
├── PLAN_2.md
├── README.md
├── CHANGELOG.md
├── CONTRIBUTING.md
├── config/
│   ├── checkstyle/
│   └── spotbugs/
├── validator-core/
├── validator-web/
├── validator-spring-boot-starter/
└── validator-test-support/
```

### 1b. Module responsibilities

| Module           | Artifact ID                            | Packages                                       | Dependencies                                                      |
|------------------|----------------------------------------|------------------------------------------------|-------------------------------------------------------------------|
| **core**         | `validator-core`                | `annotation/`, `validator/`, `util/`, `enums/` | `jakarta.validation-api`, `emailvalidator`; see **1c** for Spring |
| **web**          | `validator-web`                 | `web/`, `messages_*.properties`                | `core`, `spring-web` (optional), `spring-context` (optional)      |
| **starter**      | `validator-spring-boot-starter` | `autoconfigure/`, `config/`                    | `web`, `spring-boot-autoconfigure`, starters (optional)           |
| **test-support** | `validator-test-support`        | `support/` fixtures                            | `web`, test deps → **test-jar**                                   |

### 1c. Cross-validator / `BeanWrapperImpl` placement (resolved)

**Problem:** `FieldMatchValidator`, `AtLeastOneOfValidator`, etc. use `org.springframework.beans.BeanWrapperImpl` — Spring dependency in validator layer.

**Decision for 1.x (documented):**

| Option                                  | Choice       | Rationale                                                                                         |
|-----------------------------------------|--------------|---------------------------------------------------------------------------------------------------|
| A. `spring-beans` optional in `core`    | **Selected** | Minimal move; validators stay with annotations; `spring-beans` marked `<optional>true</optional>` |
| B. Move cross-validators to `web`       | Rejected     | Couples validation rules to web module                                                            |
| C. New `spring-validator-spring` module | Deferred     | Overkill for 1.x                                                                                  |

**`core/pom.xml`:**

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-beans</artifactId>
    <optional>true</optional>
</dependency>
```

**Document in README:** Cross-field validators (`@FieldMatch`, etc.) require Spring Beans on classpath (provided by Spring Boot starter).

### 1d. Parent POM — dependencyManagement

```xml
<properties>
    <java.version>17</java.version>
    <maven.compiler.release>17</maven.compiler.release>
    <spring-boot.version>3.5.14</spring-boot.version>
    <emailvalidator.version>1.0.1</emailvalidator.version>
    <!-- project version 1.x -->
</properties>

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-dependencies</artifactId>
            <version>${spring-boot.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
        <dependency>
            <groupId>io.github.rigsto</groupId>
            <artifactId>emailvalidator</artifactId>
            <version>${emailvalidator.version}</version>
        </dependency>
        <!-- Internal modules at ${project.version} (1.x) -->
        <dependency>
            <groupId>id.xtramile.validator</groupId>
            <artifactId>validator-core</artifactId>
            <version>${project.version}</version>
        </dependency>
        <dependency>
            <groupId>id.xtramile.validator</groupId>
            <artifactId>validator-web</artifactId>
            <version>${project.version}</version>
        </dependency>
        <dependency>
            <groupId>id.xtramile.validator</groupId>
            <artifactId>validator-spring-boot-starter</artifactId>
            <version>${project.version}</version>
        </dependency>
    </dependencies>
</dependencyManagement>
```

### 1e. `spring-boot-configuration-processor` (starter module)

Add to `validator-spring-boot-starter` for IDE metadata on `ValidationLocaleConfig`:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-configuration-processor</artifactId>
    <optional>true</optional>
</dependency>
```

Generates `META-INF/spring-configuration-metadata.json` for `id.xtramile.validator.*` properties.

### 1f. `test-support` publish policy

| Item                      | Decision                                                                                        |
|---------------------------|-------------------------------------------------------------------------------------------------|
| Publish to Maven Central? | **No** — internal build module only                                                             |
| Packaging                 | `test-jar` via `maven-jar-plugin` `test-jar` goal                                               |
| Consumer modules          | `core`, `web`, `starter` tests depend on it with `<type>test-jar</type>`, `<scope>test</scope>` |
| Version                   | Same `${project.version}` (1.x), not advertised in README consumer docs                         |

### 1g. Migration steps (ordered)

| Step | Action                                                                                                          |
|------|-----------------------------------------------------------------------------------------------------------------|
| 1    | Create parent POM `spring-validator-parent` (or keep root artifact as parent), `packaging=pom`, version **1.x** |
| 2    | Extract `validator-core` + move validator/util/enum tests                                                |
| 3    | Extract `validator-web` + messages + web/integration tests                                               |
| 4    | Extract `validator-spring-boot-starter` + `META-INF/spring/` + autoconfigure tests                       |
| 5    | Extract `validator-test-support`; move `support/` from PLAN 1                                            |
| 6    | Wire test-jar dependencies in child module test scopes                                                          |
| 7    | Add optional `spring-beans` to `core`; configuration-processor to `starter`                                     |
| 8    | Configure **multi-module** `central-publishing-maven-plugin` on parent                                          |
| 9    | Add **relocation POM** for old `spring-validator` coordinate (1.x)                                              |
| 10   | Update `README.md`, `CHANGELOG.md` migration section                                                            |
| 11   | `mvn clean verify` from parent — all modules green                                                              |

### 1h. Test placement

| Tests                                     | Module                             |
|-------------------------------------------|------------------------------------|
| `validator/**/*Test`, `util/*`, `enums/*` | `core/src/test`                    |
| `web/*`, `integration/*`                  | `web/src/test`                     |
| `autoconfigure/*`                         | `starter/src/test`                 |
| `support/*`, `MockMultipartFile`          | `test-support/src/main` (test-jar) |

`MockMultipartFile` moves to `test-support` (used by KYC/file validator tests in `core`).

### 1i. JaCoCo — multi-module reporting

**Per-module:** Each child inherits `jacoco-maven-plugin` from parent `pluginManagement`.

**Aggregate report on parent:**

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <executions>
        <execution>
            <id>report-aggregate</id>
            <phase>verify</phase>
            <goals><goal>report-aggregate</goal></goals>
        </execution>
    </executions>
</plugin>
```

| Rule                                     | Application                                                       |
|------------------------------------------|-------------------------------------------------------------------|
| Bundle thresholds (80% line, 70% branch) | **Parent aggregate** — same as PLAN 1                             |
| Per-module floors                        | If aggregate fails, check `core` ≥85% validator, `web` ≥80%, etc. |
| After split                              | Run baseline; ratchet if module split temporarily lowers coverage |

Upload `target/site/jacoco-aggregate/` in CI.

### 1j. Maven Central Publishing (multi-artifact, 1.x)

**Publish these coordinates** (all `${project.version}` = 1.x):

1. `validator-core`
2. `validator-web`
3. `validator-spring-boot-starter`
4. Relocation POM at `spring-validator` (optional final artifact)

**Do not publish:** `validator-test-support`

**Parent `pom.xml`:**

```xml
<plugin>
    <groupId>org.sonatype.central</groupId>
    <artifactId>central-publishing-maven-plugin</artifactId>
    <extensions>true</extensions>
    <configuration>
        <publishingServerId>central</publishingServerId>
        <autoPublish>true</autoPublish>  <!-- or false for manual promote -->
    </configuration>
</plugin>
```

**GPG (`maven-gpg-plugin`):**
- Bound to `verify` phase for **release** profile only
- CI: `-Drelease.skip=true` or `-Dgpg.skip=true`
- Local release: `mvn clean deploy -Prelease`

**Release profile:**

```xml
<profile>
    <id>release</id>
    <build>
        <plugins>
            <!-- gpg sign, source jar, javadoc jar already from PLAN 1 -->
        </plugins>
    </build>
</profile>
```

### 1k. Sample consumer smoke project

Add `examples/spring-boot-starter-sample/` (not published):

```
examples/spring-boot-starter-sample/
├── pom.xml          # depends on validator-spring-boot-starter:1.x
└── src/main/java/.../SampleApplication.java
    └── @RestController with @Valid DTO
```

**Purpose:**
- Manual smoke after `mvn install`
- CI optional job: `mvn -f examples/spring-boot-starter-sample/pom.xml verify`
- Validates auto-config + friendly 400 response

### 1l. Quality gates on parent build

From repository root:

```bash
mvn clean verify -Dspring-boot.version=3.5.14
```

Must run Checkstyle, Javadoc, SpotBugs, JaCoCo aggregate across **all** modules. Child modules do not skip quality plugins individually.

### Phase 1 success criteria

- [x] Parent + 4 modules build at version **1.x**
- [x] `core` has zero `spring-boot-*` compile dependencies (`spring-beans` optional only; test scope uses Boot test starter)
- [x] `starter` auto-config via `META-INF/spring/*.imports`
- [x] All PLAN 1 tests pass in new locations
- [x] JaCoCo aggregate meets PLAN 1 thresholds (parent `check-aggregate`, `inherited=false`)
- [x] Relocation POM documented; **no 2.0 bump**
- [x] `test-support` not published to Central (`maven.deploy.skip`, `central.publishing.skip`)
- [x] Sample consumer builds and returns friendly validation errors

---

## Phase 2: Support Spring Boot 4

**Objective:** Verify Spring Boot 4.0.x on **1.x** multi-module build. Single starter artifact compatible with Boot 3.5 and 4.0.

### 2a. Version context

| Version           | Java  | Status                         |
|-------------------|-------|--------------------------------|
| Spring Boot 3.5.x | 17–25 | Compile baseline               |
| Spring Boot 4.0.x | 17–25 | CI verification (4.0.5+ patch) |

### 2b. Strategy

1. Compile against Boot **3.5.14** (default `${spring-boot.version}`).
2. CI overrides `-Dspring-boot.version=4.0.5` for compatibility leg.
3. Publish **one** `validator-spring-boot-starter:1.x` for both Boot lines.

### 2c. CI matrix (full)

```yaml
# .github/workflows/ci.yml
strategy:
  fail-fast: false
  matrix:
    java: [17, 21, 25]
    spring-boot: ['3.5.14', '4.0.5']

steps:
  - run: mvn clean verify -Dspring-boot.version=${{ matrix.spring-boot }}
```

| Jobs              | Count                                                        |
|-------------------|--------------------------------------------------------------|
| Test matrix       | 6 (3 Java × 2 Boot)                                          |
| Quality aggregate | 1 (Java 17, Boot 3.5.14, includes checkstyle/javadoc/jacoco) |

**`build.yml` OS matrix:** Java 17 only, Boot 3.5.14, `mvn clean test` — cross-OS smoke.

### 2d. API compatibility

| API                     | Boot 3.5   | Boot 4.0        | Action                                  |
|-------------------------|------------|-----------------|-----------------------------------------|
| `@AutoConfiguration`    | Yes        | Yes             | None                                    |
| `@ConditionalOn*`       | Yes        | Yes             | None                                    |
| `@RestControllerAdvice` | Yes        | Yes             | Verify in sample + WebMvcTest           |
| Modular starters        | Monolithic | May split       | `boot4` profile if coordinates change   |
| `BeanWrapperImpl`       | SF 6       | SF 7            | Low risk                                |
| JSpecify nullability    | No         | Portfolio trend | **Out of scope 1.x** — future 1.x patch |

### 2e. Boot 4 verification tasks

| #   | Task                                                                   | Module         |
|-----|------------------------------------------------------------------------|----------------|
| 2.1 | Run full 6-job matrix; log failures                                    | parent         |
| 2.2 | Add `boot4` profile in `starter/pom.xml` if starter coordinates differ | starter        |
| 2.3 | `SpringBoot4AutoConfigurationIntegrationTest`                          | starter        |
| 2.4 | `ApiExceptionHandlerWebIntegrationTest` on Boot 4                      | web or starter |
| 2.5 | Sample consumer on Boot 4.0.5                                          | examples/      |
| 2.6 | Jakarta EE 11 / deprecation warnings audit                             | starter        |
| 2.7 | Update README — supported Boot 3.5.x and 4.0.x (**1.x** artifacts)     | root           |
| 2.8 | `CHANGELOG.md` entry under `[1.x]` for Boot 4 support                  | root           |

### 2f. `boot4` Maven profile (conditional)

```xml
<profile>
    <id>boot4</id>
    <properties>
        <spring-boot.version>4.0.5</spring-boot.version>
    </properties>
    <!-- Override starter deps if Boot 4 modular artifact IDs differ -->
</profile>
```

Activate in CI: `-Dspring-boot.version=4.0.5` (property override sufficient if coordinates unchanged).

### 2g. Fallback escalation

| Level | Solution                                             | When                               |
|-------|------------------------------------------------------|------------------------------------|
| 1     | `boot4` profile dependency overrides                 | Modular starter rename             |
| 2     | `SpringBoot4Configuration` adapter in `starter`      | Bean wiring differs                |
| 3     | `spring-validator-spring-boot4-starter` extra module | Last resort; still **1.x** version |

### 2h. Documentation (1.x)

README **Supported environments:**

> - **Version:** 1.x  
> - **Java:** 17, 21, 25  
> - **Spring Boot:** 3.5.14+ (3.5.x), 4.0.5+ (4.0.x)  
> - **Dependency:** `id.xtramile:validator-spring-boot-starter:1.x`

### Phase 2 success criteria

- [ ] `mvn clean verify -Dspring-boot.version=3.5.14` on Java 17, 21, 25
- [ ] `mvn clean verify -Dspring-boot.version=4.0.5` on Java 17, 21, 25
- [ ] CI 6-job matrix green
- [ ] Sample consumer works on Boot 3.5 and 4.0
- [ ] README + CHANGELOG updated; **version remains 1.x**

---

## PLAN 2 — Overall Success Criteria

| Category   | Criterion                                                         |
|------------|-------------------------------------------------------------------|
| Version    | **1.x** only — relocation POM, no 2.0 bump                        |
| Modules    | core, web, starter published; test-support internal               |
| Boot 3     | verify green 3.5.14 × Java 17/21/25                               |
| Boot 4     | verify green 4.0.5 × Java 17/21/25                                |
| Consumers  | Jakarta / Web / Boot apps use correct 1.x artifact                |
| Quality    | Parent `verify` — Checkstyle, Javadoc, JaCoCo aggregate, SpotBugs |
| Publishing | Central deploy all public 1.x artifacts + relocation              |
| Smoke      | `examples/spring-boot-starter-sample` passes                      |

---

## PLAN 2 — Task Checklist

- [x] **P1.1** Parent POM 1.x + module list
- [x] **P1.2** Extract `core` (+ optional `spring-beans`, `spring-web`, `jackson-databind`)
- [x] **P1.3** Extract `web`
- [x] **P1.4** Extract `starter` (+ configuration-processor)
- [x] **P1.5** Extract `test-support` (test-jar, not published)
- [x] **P1.6** Migrate tests + `MockMultipartFile`
- [x] **P1.7** JaCoCo per-module + aggregate on parent
- [x] **P1.8** Multi-module Central Publishing + release profile + GPG policy
- [x] **P1.9** Relocation POM `spring-validator` → starter (**1.x**)
- [x] **P1.10** README + CHANGELOG migration (no 2.0)
- [x] **P1.11** Sample consumer in `examples/`
- [ ] **P2.1** CI 6-job matrix (Java × Boot)
- [ ] **P2.2** Boot 4 fixes / `boot4` profile if needed
- [ ] **P2.3** Boot 4 integration + sample smoke
- [ ] **P2.4** Document Boot 3.5 + 4.0 support at 1.x

---

## Out of Scope (PLAN 2)

- Spring Boot 2.x / Java 11
- Spring Boot 4.1 milestones until GA
- Major version **2.0** bump
- Publishing `test-support` to Central
- JSpecify migration (future 1.x patch)

---

## Relationship to PLAN 1

| PLAN 1 deliverable              | PLAN 2                                       |
|---------------------------------|----------------------------------------------|
| SOLID code                      | Split by module                              |
| `support/`                      | `test-support` module (unpublished)          |
| `AnnotationRegistry`            | `web` module                                 |
| META-INF fix                    | `starter` module                             |
| Quality plugins                 | Parent `pluginManagement` + aggregate JaCoCo |
| README, CONTRIBUTING, CHANGELOG | Extended for modules + migration at **1.x**  |
| `-Pquick` profile               | Inherited from parent                        |

**Execute PLAN 1 completely before PLAN 2.**
