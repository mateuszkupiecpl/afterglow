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
- Application code: not scaffolded yet.
- Platform direction: Web/PWA proposed for the first version.
- MVP direction: local one-device gameplay first, backend/multiplayer later.
- Product name: Afterglow, currently proposed.

## Planned Stack

- Frontend: React, TypeScript, Vite, PWA.
- Local storage/cache: IndexedDB/Dexie and Cache API.
- Backend: Java 21 and Spring Boot 3 in a later stage.
- Multiplayer: server rooms with WebSocket/STOMP, not Bluetooth.
- Database/cache for later backend stages: PostgreSQL and Redis, both TBD for
  timing and necessity.

## Documentation Map

- `docs/product/vision.md` - product vision and principles.
- `docs/product/mvp-scope.md` - MVP scope and exclusions.
- `docs/product/open-decisions.md` - unresolved decisions.
- `docs/architecture/tech-stack.md` - planned technical stack.
- `docs/decisions/ADR-001-platform.md` - platform decision record.
- `docs/source/` - source notes used to derive project documentation.
- `frontend/` - frontend placeholder, no app scaffold yet.
- `backend/` - backend placeholder, no app scaffold yet.
