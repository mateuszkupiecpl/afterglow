# Afterglow

Afterglow is a web/PWA 18+ social card game for adults. It is planned as a
mobile-first browser game for 2-8 players, especially comfortable on iPhone,
with support for Android and desktop through the same web app.

The product direction combines a card game, party game, couples game,
truth-or-dare style prompts, light TCG-like choices, and atmospheric breaks
such as flirt, conversation, dance, massage, role-play, and group activities.
The game should feel premium, sensual, consent-aware, and gradual rather than
like a simple random task generator.

## Current Status

- Project phase: documentation and structure.
- Application code: frontend scaffold exists; backend scaffold is not created yet.
- Platform direction: Web/PWA proposed for the first version.
- MVP direction: local one-device gameplay first, backend/multiplayer later.
- Product name: Afterglow, currently proposed.
- Secondary project goal: practice domain-driven design, explicit modelling,
  and maintainable architecture while building the product.

## Planned Stack

- Frontend: React, TypeScript, Vite, PWA, Axios.
- Frontend tooling: Storybook for isolated UI work and MSW for mocked API flows
  and gameplay scenarios.
- Local storage/cache: IndexedDB/Dexie and Cache API.
- Backend: Java 25 LTS and Spring Boot 4.x in a later stage.
- Backend architecture: Hexagonal Architecture with clear domain, application,
  infrastructure, and API/resource boundaries.
- Backend testing: Groovy and Spock, with unit test classes ending in `Test`
  and Spring integration test classes ending in `IT`.
- Multiplayer: server rooms with WebSocket/STOMP, not Bluetooth.
- Database/cache for later backend stages: PostgreSQL and Redis, both TBD for
  timing and necessity.

## Documentation Map

- `docs/product/vision.md` - product vision and principles.
- `docs/product/mvp-scope.md` - MVP scope and exclusions.
- `docs/product/open-decisions.md` - unresolved decisions.
- `docs/architecture/tech-stack.md` - planned technical stack.
- `docs/decisions/ADR-001-platform.md` - platform decision record.
- `docs/source/` - source-of-truth notes for product, architecture, testing,
  and implementation guidance.
- `docs/source/architecture.md` - canonical architecture rules.
- `docs/source/backend-guidelines.md` - backend modelling and API guidance.
- `docs/source/frontend-guidelines.md` - frontend stack and tooling guidance.
- `docs/source/testing.md` - backend testing strategy.
- `frontend/` - React, TypeScript, and Vite frontend scaffold.
- `backend/` - backend placeholder, no app scaffold yet.
