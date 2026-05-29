# Decision Log

Update this file whenever an important product or architecture decision becomes
accepted. Accepted decisions here are canonical.

## 2026-05-22

### Decision: Web/PWA Instead Of Native iOS

Status: accepted

Rationale:

- Adult content may face App Store constraints.
- PWA works on iPhone, Android, and desktop.
- Decks and content can be updated more easily.
- The stack fits the React, TypeScript, and Java direction.

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

- App Store restrictions for sexual content.
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

### Decision: Working Product Name

Status: proposed

- Product name: `Afterglow`.

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
- Java backend code uses `var` for local variables when the type can be inferred
  clearly.
- Fields, constants, method parameters, return types, record components, and
  public API signatures keep explicit types.

### Decision: API Uses Resources, Not DTOs

Status: accepted

API-facing models must be separate classes and use the `Resource` suffix, for
example `PlayerResource`, `GameSessionResource`, and `CardResource`.

Do not use the `DTO` suffix for API-facing models.

Domain Model != API Resource.

Use a HATEOAS-inspired API approach. Backend responses use Spring HATEOAS links,
and `/api` is the API home resource for frontend entry links.

API resources that expose links use `RepresentationModel`; they are not wrapped
in `EntityModel`.

Links are added to API resources only through classes with the `LinkAssembler`
suffix, for example `GameSessionLinkAssembler`.

### Decision: Frontend Stack And Tooling

Status: accepted

Frontend stack:

- React 19.
- TypeScript.
- Vite.
- PWA.
- React Router.
- Tailwind CSS.
- Motion for React.
- Zustand.
- Axios.
- i18next.

Development tooling:

- Storybook for isolated UI development.
- MSW for Storybook, UI tests, and component isolation.

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

### Decision: Backend Layer Visibility Boundaries

Status: accepted

API code depends on application-layer contracts only: use cases, commands,
queries, snapshots/read models, and application exceptions.

Rules:

- API code must not import domain model or domain port types.
- Application services may use domain objects and domain ports internally.
- Application services return application snapshots/read models instead of
  exposing domain aggregates to controllers.
- Domain ports are public boundary interfaces for application and
  infrastructure.
- Domain ports may expose domain model types without making those types API
  contracts.
- Infrastructure adapter implementations should be package-private and wired
  through public port interfaces.
- Adapter classes stay in infrastructure.
- Java production classes, constructors, and methods must not be made public
  only for tests.

## 2026-05-29

### Decision: Monorepo Repository Structure

Status: accepted

Afterglow uses one monorepo with separate package directories:

- `frontend/` for the React, TypeScript, Vite, and PWA frontend.
- `backend/` for the Java and Spring Boot backend when backend implementation
  is active.

Use the monorepo package layout in current project docs.

### Decision: Frontend Stack And Architecture

Status: accepted

Frontend stack:

- React 19.
- TypeScript.
- Vite.
- PWA.
- React Router.
- Tailwind CSS.
- Motion for React.
- Zustand.
- Axios.
- i18next.
- Storybook.
- MSW.

Frontend architecture uses feature/domain-first organization. Code should be
organized around business capabilities rather than file types.

The frontend is not only a thin rendering layer. Game flow, client-side state,
and local interaction rules are part of the frontend domain.

Rules:

- Use Zustand for application state.
- Keep stores close to their domains/features.
- Use React Context mostly for technical providers.
- Keep pages as feature composition points.
- Keep domain APIs close to their features.
- Keep technical API infrastructure in `frontend/src/api`.
- Avoid direct backend calls inside UI components.
- Avoid giant `App.tsx`, giant global component folders, and type-based
  organization only.
- Do not use Bootstrap.

### Decision: Frontend Local Development Uses The Real Backend API

Status: accepted

Normal local frontend development must communicate with the real backend API.

Do not introduce a normal frontend mock mode.

MSW is allowed only for:

- Storybook.
- UI tests.
- Component isolation.

### Decision: Frontend Localization Starts Early

Status: accepted

Use i18next and translation keys for user-facing text from early development.
Avoid hardcoded user-facing strings.

### Decision: Frontend Design Direction

Status: accepted

The frontend should feel like a premium game experience rather than a
traditional form-based application.

The visual direction emphasizes dark backgrounds, premium atmosphere, elegant
styling, game-like interactions, card-first UX, gold accents, burgundy and
purple accents, and immersive atmosphere.

Avoid an enterprise/admin panel appearance.

### Decision: PWA Support Is Incremental

Status: accepted

Implement PWA support incrementally.

Early PWA scope:

- Manifest.
- Icons.
- Standalone mode.
- Basic service worker.

Later PWA scope:

- Offline decks.
- Cache versioning.
- IndexedDB persistence.
- Advanced asset cache.
