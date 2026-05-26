# Architecture

This document is the canonical architecture guide for future implementation.
It complements the dated source notes and preserves the MVP-first direction.

## Project Goals

Afterglow is not only a working MVP. It is also a practice project for software
design, Domain-Driven Design, explicit modelling, and maintainable architecture.

Prefer:

- DDD and domain-first thinking.
- Explicit business modelling.
- Maintainable boundaries.
- Code that teaches the domain to the reader.

Avoid:

- Optimizing only for shortest implementation time.
- Collapsing meaningful domain behavior into simple CRUD.
- Hiding rules in generic services, controllers, or UI components.

## Architecture Style

Use Hexagonal Architecture / Ports and Adapters for backend implementation.

The backend should keep these responsibilities visible:

- `domain/` - entities, value objects, domain services, repository ports, domain
  policies, and business rules.
- `application/` - use cases, orchestration, commands, and queries.
- `infrastructure/` - persistence, REST adapters, external integrations,
  configuration, and port implementations.
- `api/` - request/response resources and mapping to the application layer.

Do not overengineer the early project, but keep architectural boundaries
visible enough that later backend and multiplayer work can grow cleanly.

## Dependency Rules

- Domain logic must not depend on Spring, HTTP, persistence, or framework
  concerns.
- Infrastructure adapts to the domain, not the opposite.
- The application layer orchestrates use cases.
- The domain owns business rules, invariants, and policies.
- Game-domain logic stays separate from UI and transport concerns.
- API code depends on application contracts, not domain entities, value
  objects, or domain ports.
- Application services may use domain objects and domain ports internally, but
  expose application commands, queries, snapshots/read models, and application
  exceptions to the API layer.
- Port interfaces are intentionally public where adapters and application code
  need them. Adapter implementations stay package-private in infrastructure.

## Modelling Rules

- Do not avoid creating additional classes when they improve clarity.
- Prefer explicit modelling over generic structures.
- Prefer a rich domain model over an anemic model when behavior and invariants
  belong with the concept.
- Naming should communicate business meaning.
- Avoid god classes, generic utility containers, and giant service classes.

## API Boundary

Domain Model != API Resource.

API-facing models must be separate classes and use the `Resource` suffix:

- `PlayerResource`
- `GameSessionResource`
- `CardResource`

Do not use `PlayerDTO`, `GameDTO`, `CardDTO`, or other `DTO` suffixes for
API-facing models.

Use a HATEOAS-inspired API approach. Resources may expose identifiers, current
state, links, and available actions when those make client flows clearer.
API resources that expose links should use `RepresentationModel`; do not wrap
API resources in `EntityModel`.
Add links to API resources only through classes with the `LinkAssembler` suffix,
for example `GameSessionLinkAssembler`.

## Migration Notes

Earlier notes emphasize keeping the first version simple. That remains true.
The updated interpretation is: keep the implementation simple while preserving
domain and architectural boundaries where the game rules deserve them.
