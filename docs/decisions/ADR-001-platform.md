# ADR-001: Platform

## Status

Accepted

## Context

Afterglow is planned as an 18+ social card game for 2-8 adults. It should work
primarily on phones, especially iPhone, while also supporting Android and
desktop. The product needs frequent content updates and should avoid App Store
review constraints for the spicy version.

## Decision

Build the first version as a web/PWA application rather than a native iOS app.

The proposed frontend platform is React, TypeScript, Vite, and PWA. The MVP
should start as a local one-device game. Backend and multiplayer can come later
through Java 25 LTS, Spring Boot 4.x, and WebSocket/STOMP rooms.

Frontend API work should use Axios. Storybook is the expected tool for isolated
UI development, and MSW is the expected tool for mocked API flows and gameplay
scenarios.

Backend implementation should use Hexagonal Architecture / Ports and Adapters,
with domain, application, infrastructure, and API/resource concerns kept
separate.

## Rationale

- PWA can run on iPhone, Android, desktop, and tablet from one frontend.
- Players can open it in Safari and add it to the iPhone home screen.
- The spicy version does not need App Store distribution.
- Card decks and content can be updated more easily than in a native app.
- The stack matches the planned React, TypeScript, and Java direction.
- Bluetooth is not the preferred multiplayer path for iOS/PWA.
- The backend is also a vehicle for practicing DDD and explicit modelling, not
  only a delivery shortcut.

## Consequences

- The first version should prioritize browser and mobile PWA constraints.
- Native iOS features are not assumed.
- Storage/cache cannot be treated as permanent; the app must tolerate refreshes
  or re-downloads.
- Multiplayer should be designed around server rooms and WebSocket when it is
  added.
- API-facing models should be resources, not domain objects, and use the
  `Resource` suffix instead of `DTO`.
- A lighter App Store version remains TBD.
