# AGENTS.md

Guidance for AI assistants working in this repository.

## Current Project Phase

Afterglow is still primarily in documentation, planning, and architecture-shaping
mode. The frontend scaffold exists. Do not create a backend scaffold or large
new application structure unless the user explicitly asks for implementation.

## Source Priority

- Use `docs/` as the source of truth for product, architecture, decision, and
  implementation notes.
- Start with `docs/README.md` for the current reading order.
- `docs/decisions/decision-log.md` has priority for accepted decisions.
- Newer dated accepted decisions take priority over older notes on the same
  topic.
- MVP scope takes priority over later-stage ideas.
- Mark unresolved or unsupported details as `TBD`.

## Implementation Guidance

- Do not invent mechanics, requirements, content categories, or architecture.
- Do not introduce AI features.
- Do not implement features outside the MVP without an explicit request.
- Prefer simple, readable code when implementation begins.
- In Java backend code, use `var` for local variables when the type can be
  inferred clearly. Keep explicit types for fields, constants, method
  parameters, return types, record components, and public API signatures.
- Keep game-domain logic separate from UI.
- Use domain-first thinking and DDD vocabulary where it clarifies the model.
- Prefer explicit modelling over generic data bags or CRUD-shaped shortcuts.
- Use domain value object wrappers for concepts with validation, ranges, or
  behavior instead of raw primitives. Do not add a `ValueObject` suffix.
- Do not avoid additional classes when they communicate business meaning.
- Prefer a rich domain model over an anemic model when business rules are
  involved.
- Avoid god classes, generic utility containers, and giant service classes.
- Design TypeScript domain types so they can later map cleanly to Java models.
- Treat player consent and boundaries as hard constraints.
- Do not hardcode card content without metadata for type, spice level, target,
  and boundaries.

## Architecture Rules

- Use Hexagonal Architecture / Ports and Adapters for backend implementation.
- Keep `domain`, `application`, `infrastructure`, and `api` responsibilities
  visible without overengineering.
- Domain logic must not depend on Spring, HTTP, persistence, or framework
  concerns.
- Keep backend package boundaries enforced with ArchUnit tests when new layers
  or cross-layer dependencies are introduced.
- Infrastructure adapts to the domain, not the other way around.
- The application layer orchestrates use cases with commands and queries.
- The domain owns business rules, policies, entities, value objects, domain
  services, and repository ports.
- API-facing models are separate resources. Use the `Resource` suffix, for
  example `PlayerResource`, `GameSessionResource`, and `CardResource`.
- Do not use `DTO` suffixes for API-facing models.

## Testing Rules

- Backend tests use Groovy and Spock.
- Place backend tests under `src/test/groovy/unit` and
  `src/test/groovy/integration`.
- Unit test classes use the `Test` suffix, extend `Specification`, avoid Spring
  context, and stay fast.
- Integration test classes use the `IT` suffix and extend
  `IntegrationTestSpecification`.
- `IntegrationTestSpecification` should centralize Spring context bootstrapping,
  future DB/security support, shared fixtures, reusable test utilities, and
  integration helpers.
- Maximize domain unit tests; use integration tests to verify wiring and
  adapter behavior.

## Frontend Rules

- Frontend stack: React, TypeScript, Vite, PWA, Axios.
- Use Storybook for isolated UI development.
- Use MSW for mocked API flows and gameplay scenarios.
- Keep gameplay/domain logic testable outside React components.
- API resources and domain models remain separate even in TypeScript.

## Current Product Direction

- Product: adult web/PWA social card game.
- Players: 2-8.
- MVP: local one-device gameplay first.
- Frontend: React, TypeScript, Vite, PWA, Axios.
- Backend: Java 25 LTS and Spring Boot 4.x later, mainly for rooms and
  multiplayer.
- Public UGC, matchmaking, chat, payments, and native iOS are outside MVP.
