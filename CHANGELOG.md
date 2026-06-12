# Changelog

All notable changes to the **1.x** line of `spring-validator` are documented here.

## [Unreleased]

### Added

- `AnnotationRegistry` as the single source of truth for constraint metadata
- `ConstraintMessageResolver` SPI with category-specific message strategies
- Test support package (`ValidationMessageTestSupport`, `ValidatorTestSupport`, and related fixtures)
- `DateToleranceEvaluator` shared datetime parsing/tolerance helper
- Optional `slf4j-api` dependency for debug logging in the web layer
- JaCoCo coverage gates (bundle and package thresholds)
- Mandatory public Javadoc with `failOnWarnings`
- Compiler `-Xlint:all` and `-Werror`
- `CONTRIBUTING.md` and this changelog

### Changed

- Dependencies injected via `ValidationAutoConfiguration` (DIP)
- CI consolidated to `ci.yml` (primary) and `build.yml` (cross-OS smoke)
- Java 17 minimum; Spring Boot versions managed via BOM (`3.5.14`)
- `MessageUtilTest` renamed to `MessageUtilsTest`
- English/Indonesian message code tests parameterized via `LocaleMessageCodesTestSupport`

### Fixed

- `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` path for auto-configuration discovery
- NPE in constraint message resolution when annotation attributes are null (`Min`/`Max`/etc.)
- NPE in `ApiExceptionHandler` when `BindingResult.getTarget()` is null
- `MessageResourceResolver` reloading fallback locale properties on every cache miss
- `MessageUtils.clearStoredArgs` not removing `ThreadLocal` entries (thread-pool leak)
- `AnnotationUtils` silently swallowing all exceptions
- `ValidatorUtils.validateLeapYear` missing null guard on input date
