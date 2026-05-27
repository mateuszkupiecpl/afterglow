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

## Visibility And Dependency Boundaries

- Keep each layer's public surface intentional and small.
- `api/` may depend on public application use cases, commands, queries,
  application snapshots/read models, and application exceptions.
- `api/` must not import `domain/` or `domain/port/` types. Domain objects are
  not request or response contracts.
- `application/` may depend on the domain model and domain ports. It translates
  incoming codes and commands into domain values, orchestrates use cases, and
  returns application snapshots/read models instead of domain aggregates.
- `domain/port/` interfaces are public because application services and
  infrastructure adapters must compile against them. They may expose domain
  model types; that does not make those types API contracts.
- `infrastructure/` implements ports and owns Spring configuration. Adapter
  implementations should be package-private and wired through public port
  interfaces.
- Do not move adapter classes into `domain/` only to hide them. If a class
  talks to persistence, content storage, Spring, or external systems, it belongs
  in `infrastructure/`.
- Domain helper classes should be package-private when they are not needed by
  application services, ports, or adapters.
- Do not make Java production classes, constructors, or methods public only so
  Groovy/Spock tests can access them. Spock tests may exercise non-public Java
  types and members; production visibility should be driven by production
  dependency boundaries only.

## Domain Rules

- Domain code must not depend on Spring annotations, HTTP concepts, database
  mappings, or framework lifecycle.
- Consent, boundaries, spice limits, targeting rules, and card eligibility are
  domain rules.
- Use domain value object wrappers for concepts with validation, ranges, or
  behavior instead of raw primitives. Do not add a `ValueObject` suffix; name
  wrappers after the domain concept.
- Repository interfaces belong on the domain/application side as ports.
- Persistence classes and Spring repositories are adapter details.

## API Rules

- Use a HATEOAS-inspired API style.
- Use Spring HATEOAS for backend response links.
- API resource classes that expose links should use `RepresentationModel`;
  do not wrap API resources in `EntityModel`.
- Add links to API resources only through classes with the `LinkAssembler`
  suffix, for example `GameSessionLinkAssembler`.
- Expose `/api` as the API home resource for frontend entry links.
- Session resources should expose state-specific action links instead of
  requiring the frontend to construct every action URL.
- API-facing models are resources, not domain objects.
- Use the `Resource` suffix, for example `PlayerResource`,
  `GameSessionResource`, and `CardResource`.
- Do not use the `DTO` suffix for API-facing models.
- Map API resources to application commands/queries rather than letting
  controllers modify domain objects directly.

## Implementation Guidance

- Prefer explicit classes and names over generic maps or stringly typed flows.
- In Java backend code, use `var` for local variables when Java can infer the
  type clearly. Keep explicit types for fields, constants, method parameters,
  return types, record components, and public API signatures.
- Do not introduce microservices for the early project.
- Keep controllers thin.
- Keep orchestration in application use cases.
- Keep business rules in the domain.
- Treat unresolved implementation details as `TBD`.
