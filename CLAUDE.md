# CFR API Adapter

## Project Scope

CFR API Adapter is a Spring Boot web service that scrapes the Romanian Railway Company (CFR) website and exposes train and station data via a clean REST API. It solves the problem of CFR having no public API, bridging that gap for programmatic consumers.

See [agent_docs/scope.md](agent_docs/scope.md) for full details.

## Technologies

Built with Java 25 and Spring Boot 4.0.3. Uses Spring Cloud OpenFeign for HTTP calls to CFR's website, JSoup for HTML parsing, Lombok for DTO boilerplate reduction, and Gradle as the build system. API documentation is provided by springdoc-openapi (Swagger UI at `/swagger-ui.html`). Code quality is enforced with Checkstyle, PMD, and JaCoCo (70% coverage minimum).

See [agent_docs/technologies.md](agent_docs/technologies.md) for full details.

## Architecture

The application uses a layered architecture: REST controllers → services → repositories → OpenFeign proxies that fetch CFR HTML pages → scrapers that parse the HTML → adapters that transform raw scraped data into response DTOs. A two-step GET+POST scraping flow is used to extract CSRF tokens before submitting form requests to CFR.

See [agent_docs/architecture.md](agent_docs/architecture.md) for full details.
