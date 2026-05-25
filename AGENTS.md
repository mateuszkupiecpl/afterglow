# AGENTS.md

Guidance for AI assistants working in this repository.

## Current Project Phase

Afterglow is in documentation and planning mode. The repository has placeholders
for frontend and backend work, but no application scaffold should be created
unless the user explicitly asks for it.

## Source Priority

- Use `docs/source/` as the source of truth for product and architecture notes.
- Newer dated source documents take priority over older notes.
- `docs/source/2026-05-22-decision-log.md` has priority for accepted decisions.
- MVP scope takes priority over later-stage ideas.
- Mark unresolved or unsupported details as `TBD`.

## Implementation Guidance

- Do not invent mechanics, requirements, content categories, or architecture.
- Do not introduce AI features.
- Do not implement features outside the MVP without an explicit request.
- Prefer simple, readable code when implementation begins.
- Keep game-domain logic separate from UI.
- Design TypeScript domain types so they can later map cleanly to Java models.
- Treat player consent and boundaries as hard constraints.
- Do not hardcode card content without metadata for type, spice level, target,
  and boundaries.

## Current Product Direction

- Product: adult web/PWA social card game.
- Players: 2-8.
- MVP: local one-device gameplay first.
- Frontend: React, TypeScript, Vite, PWA.
- Backend: Java 21 and Spring Boot 3 later, mainly for rooms and multiplayer.
- Public UGC, matchmaking, chat, payments, and native iOS are outside MVP.
