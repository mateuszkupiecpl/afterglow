# Decision Log

Update this file whenever an important product or architecture decision becomes
accepted. This file is currently authoritative for accepted decisions.

## 2026-05-22

### Decision: Web/PWA Instead Of Native iOS

Status: accepted

Rationale:

- 18+ content may face App Store constraints.
- PWA works on iPhone, Android, and desktop.
- Decks and content can be updated more easily.
- The stack fits the author's React, TypeScript, and Java direction.

### Decision: Working Frontend Stack

Status: accepted

Frontend:

- React.
- TypeScript.
- Vite.
- PWA.
- IndexedDB/Dexie.
- Cache API.

### Decision: Working Backend Stack

Status: accepted

Backend:

- Java 25 LTS.
- Spring Boot 4.x.
- WebSocket / STOMP.
- PostgreSQL later.
- Redis later.

### Decision: Multiplayer Through Server Rooms, Not Bluetooth

Status: accepted

Rationale:

- Bluetooth is a poor fit for iOS/PWA.
- WebSocket and game rooms are simpler, more scalable, and more predictable.

### Decision: Spicy Version Outside The App Store

Status: accepted

Rationale:

- Apple restrictions for sexual content.
- More freedom through web/PWA distribution.

### Decision: Players Have Several Cards In Hand

Status: accepted

Assumption:

- Start: 4 cards.
- Limit: 5 cards.
- Draw after playing.

Goal:

- Less randomness.
- More player control.
- More strategy.

### Decision: Recommended Mechanical Foundation

Status: proposed / strong recommendation

- 5-card hand.
- 3 action points.
- Reactions and counters.
- Round event.
- Atmosphere track.
- Group voting.

### Decision: Working Name

Status: proposed

- Product name: `Afterglow`.
- Frontend repo: `afterglow-ui`.
- Backend repo: `afterglow`.

### Decision: Public UGC Outside MVP

Status: accepted

Avoid public user decks, public profiles, comments, chat, and matchmaking in the
MVP.

Rationale:

- Moderation.
- Legal risk.
- Product complexity.

### Decision: Player Boundaries Are Hard Constraints

Status: accepted

The system must not draw or force cards that violate boundaries configured by
the participants of the task.

## 2026-05-26

### Decision: Project Has An Architectural Learning Goal

Status: accepted

Afterglow is not only about delivering a working MVP. It is also a project for
improving software design skills, practicing Domain-Driven Design, domain-first
thinking, maintainable architecture, and explicit modelling.

### Decision: Backend Uses Hexagonal Architecture

Status: accepted

Backend implementation should use Hexagonal Architecture / Ports and Adapters
with visible separation between `domain`, `application`, `infrastructure`, and
`api`.

Rules:

- Domain logic must not depend on Spring, HTTP, persistence, or framework
  concerns.
- Infrastructure adapts to the domain, not the opposite.
- The application layer orchestrates use cases.
- The domain owns business rules.

### Decision: API Uses Resources, Not DTOs

Status: accepted

API-facing models must be separate classes and use the `Resource` suffix, for
example `PlayerResource`, `GameSessionResource`, and `CardResource`.

Do not use the `DTO` suffix for API-facing models.

Domain Model != API Resource.

Use a HATEOAS-inspired API approach.

### Decision: Frontend Stack And Tooling

Status: accepted

Frontend stack:

- React.
- TypeScript.
- Vite.
- PWA.
- Axios.

Development tooling:

- Storybook for isolated UI development.
- MSW for mocked API flows and gameplay scenarios.

### Decision: Backend Tests Use Groovy And Spock

Status: accepted

Backend tests use Groovy and Spock.

Structure:

```text
src/test/groovy
|-- unit/
`-- integration/
```

Unit test classes use the `Test` suffix, extend `Specification`, stay fast and
isolated, and do not boot a Spring context.

Integration test classes use the `IT` suffix and extend
`IntegrationTestSpecification`.

`IntegrationTestSpecification` is responsible for Spring context bootstrapping,
future DB support, future security setup, shared fixtures, reusable test
utilities, and integration helpers.

### Decision: Backend Integration Strategy

Status: accepted

- WireMock for external dependency mocking.
- RestTemplate for integration/test calls.
