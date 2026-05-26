# Backend Guidelines

Backend implementation is later-stage work unless explicitly requested. When it
begins, follow these rules.

## Stack

- Java 25 LTS.
- Spring Boot 4.x.
- Hexagonal Architecture / Ports and Adapters.
- Groovy and Spock for tests.
- WireMock for external dependency mocking.
- RestTemplate for integration/test calls.

## Package Responsibilities

- `domain/` owns entities, value objects, domain services, repository ports,
  domain policies, and business rules.
- `application/` owns use cases, orchestration, commands, and queries.
- `infrastructure/` owns persistence, external integrations, Spring
  configuration, and implementations of ports.
- `api/` owns request/response resources and mapping to the application layer.

## Domain Rules

- Domain code must not depend on Spring annotations, HTTP concepts, database
  mappings, or framework lifecycle.
- Consent, boundaries, spice limits, targeting rules, and card eligibility are
  domain rules.
- Repository interfaces belong on the domain/application side as ports.
- Persistence classes and Spring repositories are adapter details.

## API Rules

- Use a HATEOAS-inspired API style.
- API-facing models are resources, not domain objects.
- Use the `Resource` suffix, for example `PlayerResource`,
  `GameSessionResource`, and `CardResource`.
- Do not use the `DTO` suffix for API-facing models.
- Map API resources to application commands/queries rather than letting
  controllers modify domain objects directly.

## Implementation Guidance

- Prefer explicit classes and names over generic maps or stringly typed flows.
- Do not introduce microservices for the early project.
- Keep controllers thin.
- Keep orchestration in application use cases.
- Keep business rules in the domain.
- Treat unresolved implementation details as `TBD`.
