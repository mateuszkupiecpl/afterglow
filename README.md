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
- Repository shape: one monorepo with `frontend/` and `backend/` packages.
- Application code: frontend scaffold exists; backend scaffold is not created yet.
- Platform direction: Web/PWA accepted for the first version.
- MVP direction: local one-device gameplay first, backend/multiplayer later.
- Product name: Afterglow, currently proposed.
- Secondary project goal: practice domain-driven design, explicit modelling,
  and maintainable architecture while building the product.

## Planned Stack

- Frontend: React 19, TypeScript, Vite, PWA, React Router, Tailwind CSS,
  Motion for React, Zustand, Axios, and i18next.
- Frontend tooling: Storybook for isolated UI work and component documentation;
  MSW for Storybook mocks and UI tests only.
- Local storage/cache: IndexedDB/Dexie and Cache API.
- Backend: Java 25 LTS and Spring Boot 4.x in a later stage.
- Backend architecture: Hexagonal Architecture with clear domain, application,
  infrastructure, and API/resource boundaries.
- Backend testing: Groovy and Spock, with unit test classes ending in `Test`
  and Spring integration test classes ending in `IT`.
- Multiplayer: server rooms with WebSocket/STOMP, not Bluetooth.
- Database/cache for later backend stages: PostgreSQL and Redis, both TBD for
  timing and necessity.

## Frontend Direction

The frontend should feel like a premium game experience, not a traditional
form-based application. It is mobile-first, desktop-friendly, card-first,
immersive, and organized around domain features. Normal local development should
communicate with the real backend API; mock flows are reserved for Storybook,
UI tests, and component isolation.

## Documentation Map

- `docs/product/vision.md` - product vision and principles.
- `docs/product/mvp-scope.md` - MVP scope and exclusions.
- `docs/product/game-design.md` - working mechanics and gameplay direction.
- `docs/product/consent-boundaries.md` - consent, refusal, and card filtering
  rules.
- `docs/product/open-decisions.md` - unresolved decisions.
- `docs/README.md` - canonical docs reading order and priority rules.
- `docs/architecture/architecture.md` - canonical architecture rules.
- `docs/architecture/tech-stack.md` - planned technical stack.
- `docs/architecture/domain-model.md` - working domain model draft.
- `docs/architecture/backend-guidelines.md` - backend modelling and API
  guidance.
- `docs/architecture/frontend-guidelines.md` - frontend stack and tooling
  guidance.
- `docs/architecture/testing.md` - backend testing strategy.
- `docs/decisions/decision-log.md` - accepted product and architecture
  decisions.
- `docs/decisions/ADR-001-platform.md` - platform decision record.
- `frontend/` - React, TypeScript, and Vite frontend scaffold.
- `backend/` - backend placeholder, no app scaffold yet.
