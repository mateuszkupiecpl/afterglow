# Architecture

This document is the canonical architecture guide for future implementation.
Afterglow is still primarily in documentation, planning, and architecture-shaping
mode, and the frontend local MVP comes before backend implementation.

## Project Goals

Afterglow is also a practice project for software design, Domain-Driven Design,
explicit modelling, and maintainable architecture.

Prefer:

- DDD and domain-first thinking.
- Explicit business modelling.
- Maintainable boundaries.
- Code that teaches the domain to the reader.

Avoid:

- Optimizing only for the shortest implementation.
- Collapsing meaningful domain behavior into CRUD-shaped flows.
- Hiding rules in generic services, controllers, or UI components.

## Architecture Style

Use Hexagonal Architecture / Ports and Adapters for backend implementation.

Backend responsibilities:

- `domain/` owns entities, value objects, domain services, repository ports,
  domain policies, and business rules.
- `application/` owns use cases, orchestration, commands, queries, snapshots,
  read models, and application exceptions.
- `infrastructure/` owns persistence, external integrations, Spring
  configuration, and port implementations.
- `api/` owns request/response resources and mapping to the application layer.

Do not overengineer the early project, but keep architectural boundaries visible
enough that backend and multiplayer work can grow cleanly later.

Frontend architecture should use feature/domain-first organization. The
frontend is not only a thin rendering layer: game flow, client-side state,
interaction rules, and presentation orchestration are part of the frontend
domain for the local one-device MVP.

Frontend code should be organized around business capabilities rather than file
types, with pages composing features and shared modules reserved for reusable
technical or UI primitives.

## Dependency Rules

- Domain logic must not depend on Spring, HTTP, persistence, or framework
  concerns.
- Infrastructure adapts to the domain, not the other way around.
- The application layer orchestrates use cases.
- The domain owns business rules, invariants, and policies.
- Game-domain logic stays separate from UI and transport concerns.
- Frontend UI components should not call the backend directly.
- Feature-level API adapters should stay close to their features, while
  technical API infrastructure belongs in `frontend/src/api`.
- API code depends on application contracts, not domain entities, value objects,
  or domain ports.
- Application services may use domain objects and domain ports internally, but
  expose application commands, queries, snapshots/read models, and application
  exceptions to the API layer.
- Port interfaces are public where adapters and application code need them.
- Adapter implementations should stay package-private in infrastructure.
- Production visibility must follow production dependency needs, not test
  convenience.

## Modelling Rules

- Create additional classes when they improve domain clarity.
- Prefer explicit modelling over generic structures.
- Prefer a rich domain model over an anemic model when behavior and invariants
  belong with the concept.
- Use value object wrappers for domain concepts with validation, ranges, or
  behavior instead of raw primitives.
- Do not add a `ValueObject` suffix; name wrappers after the domain concept.
- Avoid god classes, generic utility containers, and giant service classes.

## API Boundary

Domain Model != API Resource.

API-facing models must be separate classes and use the `Resource` suffix:

- `PlayerResource`
- `GameSessionResource`
- `CardResource`

Do not use `DTO` suffixes for API-facing models.

Use a HATEOAS-inspired API style. Resources may expose identifiers, current
state, links, and available actions when those make client flows clearer.

API resources that expose links should use `RepresentationModel`; do not wrap
API resources in `EntityModel`.

Add links to API resources only through classes with the `LinkAssembler` suffix,
for example `GameSessionLinkAssembler`.

Expose `/api` as the API home resource when backend implementation begins.

## Boundary Enforcement

When backend layers or cross-layer dependencies are introduced, add ArchUnit
tests to protect package boundaries.

## MVP Interpretation

The early implementation should stay simple. Simplicity means small, readable
code with clear boundaries, not generic data bags that erase important rules.
