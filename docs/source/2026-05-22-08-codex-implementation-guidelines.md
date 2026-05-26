# Codex Implementation Guidelines

This document contains instructions for Codex or any other coding agent working
in this repository.

## Decision Priority

If documents conflict:

1. Newer dated documents take priority over older notes on the same topic.
2. `2026-05-22-decision-log.md` takes priority over older concept notes.
3. MVP scope takes priority over later-stage ideas.
4. Do not implement features outside MVP without an explicit request.

## Implementation Style

- Prefer simple, readable code.
- Prefer DDD, domain-first thinking, and explicit modelling.
- Do not optimize only for the shortest implementation if the domain deserves
  separation.
- Do not add heavy dependencies without need.
- Keep game-domain logic separate from UI.
- Do not avoid additional classes when they improve clarity.
- Prefer a rich domain model over an anemic model when objects have business
  rules.
- Avoid god classes, generic utility containers, and giant service classes.
- Design TypeScript domain types so they can later map cleanly to Java models.
- Avoid storing large assets in the repository unless they are mocks or tests.
- Do not hardcode card content without metadata for type, spice level, target,
  and boundaries.

## Backend Architecture

- Use Hexagonal Architecture / Ports and Adapters.
- Keep `domain`, `application`, `infrastructure`, and `api` visible.
- Domain code must not depend on Spring, HTTP, persistence, or framework
  concerns.
- Infrastructure adapts to the domain, not the opposite.
- The application layer orchestrates use cases, commands, and queries.
- The domain owns entities, value objects, domain services, repository ports,
  domain policies, and business rules.

## Frontend Expectations

- React + TypeScript + Vite + PWA + Axios.
- Mobile-first components.
- UI comfortable on iPhone.
- PWA from the start or very early.
- Storybook for isolated UI development.
- MSW for mocking API flows and gameplay scenarios.
- Card data may start as local fixtures.
- Game state should be serializable.
- Game logic should be testable without UI.

## Backend Expectations

- Java 25 LTS + Spring Boot 4.x.
- REST for decks and configuration.
- WebSocket for multiplayer in a later stage.
- Do not complicate the early project with microservices.

API:

- Use a HATEOAS-inspired approach.
- API resources that expose links should use `RepresentationModel`; do not wrap
  API resources in `EntityModel`.
- Add links to API resources only through classes with the `LinkAssembler`
  suffix.
- API models are separate from domain models.
- Use the `Resource` suffix, for example `PlayerResource`,
  `GameSessionResource`, and `CardResource`.
- Do not use the `DTO` suffix for API models.

Backend tests:

- Groovy + Spock.
- `src/test/groovy/unit` for fast unit tests.
- `src/test/groovy/integration` for integration tests.
- Unit test classes use the `Test` suffix, extend `Specification`, and do not
  boot Spring context.
- Integration test classes use the `IT` suffix and extend
  `IntegrationTestSpecification`.
- `IntegrationTestSpecification` owns Spring context bootstrapping, future DB
  support, future security setup, shared fixtures, reusable utilities, and
  integration helpers.
- Maximize domain tests; integration tests verify wiring.

Backend integrations:

- WireMock for external dependencies.
- RestTemplate for integration/test calls.

## Content Safety

Treat player boundaries as hard constraints.

Do not draw cards that violate the boundaries of task participants.

## MVP First

Build the local playable prototype first:

1. Player configuration.
2. Mode and level selection.
3. Card dealing.
4. Player hand.
5. Play card.
6. Simple reaction/counter.
7. Dice roll.
8. Scoring.
9. Refusal.
10. End game.

Only after that:

- backend,
- multiplayer,
- accounts,
- payments,
- card editor.

AI features are not part of the MVP and must not be introduced unless explicitly
requested.

## Working Names

- Product: `Afterglow`.
- Frontend repository: `afterglow-ui`.
- Backend repository: `afterglow`.
- Main game-domain names may include `GameSession`, `Player`, `Card`, `Deck`,
  `Turn`, `Reaction`, `AtmosphereTrack`, and `ComfortProfile`.
