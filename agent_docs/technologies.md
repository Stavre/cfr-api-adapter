# Technologies

## Language & Runtime

- **Java 25** — minimum language version enforced via Gradle toolchain. Java records are used for immutable DTOs throughout the codebase.

## Core Framework

- **Spring Boot 4.0.3** — application framework providing auto-configuration, dependency injection, embedded Tomcat, and Spring Web MVC for REST endpoint exposure.

## HTTP Client

- **Spring Cloud OpenFeign 2025.1.0** — declarative HTTP client. Feign interfaces annotated with `@FeignClient` define the calls made to the CFR website. Full logging level is configured in `FeignConfig`. Enabled via `@EnableFeignClients` on the main application class.
  - Read timeout: 120 000 ms (2 minutes)
  - Connect timeout: 120 000 ms (2 minutes)
  - CFR base URL configured via `cfr.base-url` in `application.yaml` (value: `https://mersultrenurilor.infofer.ro`)

## HTML Parsing

- **JSoup 1.22.1** — used in the scraper layer to parse HTML responses from the CFR website. `ScraperUtils` provides shared helpers for selecting elements, extracting table rows, and reading attribute values.

## JSON

- **Jackson (tools.jackson)** — used for serializing REST responses and for deserializing static JSON resources (all trains list, all stations list) loaded from the classpath at service startup.

## API Documentation

- **springdoc-openapi-starter-webmvc-ui 2.8.9** — generates an interactive Swagger UI and machine-readable OpenAPI spec from the Spring MVC controllers at runtime.
  - Swagger UI: `http://localhost:8080/swagger-ui.html`
  - OpenAPI JSON spec: `http://localhost:8080/v3/api-docs`
  - API metadata (title, description, version) is configured in `config/OpenApiConfig.java`
  - Controllers are annotated with `@Tag` (endpoint grouping), `@Operation` (summary and description per endpoint), and `@Parameter` (query parameter descriptions)

## Boilerplate Reduction

- **Lombok** — annotation processor used on DTOs and other classes to generate builders (`@Builder`), getters (`@Getter`), and constructors (`@RequiredArgsConstructor`, `@AllArgsConstructor`) at compile time.

## Build System

- **Gradle** with the **Spring dependency management plugin** — resolves Spring Boot and Spring Cloud BOM versions. Build script is `build.gradle`; project name is defined in `settings.gradle`.

## Testing

- **JUnit 5** — test runner for all unit tests under `src/test/java`.
- **AssertJ** — fluent assertion library used alongside JUnit 5.
- Test output is configured to show `PASSED`, `SKIPPED`, and `FAILED` events.

## Code Quality

- **Checkstyle 13.2.0** — style enforcement. Configuration file: `checkstyle/checkstyle.xml`; suppressions: `checkstyle/suppressions.xml`. Applied to `main` and `test` source sets.
- **PMD 7.16.0** — static analysis. Rule sets: `category/java/errorprone.xml` and `category/java/bestpractices.xml`.
- **JaCoCo** — code coverage agent and verification. Minimum coverage requirement: **70%** on the `verify` task. Fails the build if coverage drops below this threshold.

## Configuration File

`src/main/resources/application.yaml`:

```yaml
spring:
  application:
    name: cfr-api-adapter
  cloud:
    openfeign:
      client:
        config:
          default:
            readTimeout: 120000
            connectTimeout: 120000

cfr:
  base-url: https://mersultrenurilor.infofer.ro

feign:
  client:
    config:
      default:
        readTimeout: 2000
        connectTimeout: 2000
```

Note: two Feign timeout configs coexist — `spring.cloud.openfeign` (120 000 ms) and `feign.client` (2 000 ms). Spring Cloud OpenFeign resolves these independently; the `spring.cloud.openfeign` block takes precedence for auto-configured clients.
