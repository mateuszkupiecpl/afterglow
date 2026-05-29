# Frontend Guidelines

The frontend scaffold exists and is the first implementation surface for the
local one-device MVP.

## Stack

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

Do not use Bootstrap.

## Frontend Philosophy

The frontend should feel like a premium game experience rather than a
traditional form-based application. It is mobile-first, desktop-friendly,
card-first, immersive, and playful.

The frontend is not a thin rendering layer only. Game flow, client-side state,
and local interaction rules are part of the frontend domain.

Primary goals:

- Premium and immersive UX.
- Game-like interactions.
- Strong domain-oriented organization.
- Easy future support for multiplayer.
- Easy future support for offline mode.
- Support for many languages from early stages.
- Separation of business flow from presentation and infrastructure.

## Tool Responsibilities

- Tailwind CSS owns styling and design-system primitives.
- Motion for React owns animations and transitions.
- Zustand owns client application state.
- Axios owns backend communication.
- i18next owns localization.
- Storybook owns component development, documentation, and isolated visual
  states.
- MSW is allowed only for Storybook mocks, UI tests, and component isolation.
- PWA work owns installation, offline support, and app-like experience.

## Backend Communication

- Use Storybook for isolated UI component development, visual states, and
  interaction examples.
- Local development must communicate with the real backend API.
- Do not introduce a normal frontend mock mode.
- Avoid mock-driven local development.
- Use Axios for HTTP calls.
- Treat `/api` as the backend entry point.
- Prefer backend-provided HATEOAS links for session actions over hardcoded
  frontend URL construction.
- Keep local fixtures or MSW handlers clearly separate from production API
  client code.
- Avoid direct backend calls inside UI components.

## Architecture

- Use feature/domain-first organization.
- Organize code around business capabilities rather than file types.
- Keep gameplay/domain logic testable outside React components.
- Keep UI components focused on rendering and interaction.
- Keep API resources separate from domain types, even in TypeScript.
- Design TypeScript domain types so they can later map cleanly to Java domain
  concepts.
- State should remain serializable where it represents a game session.
- Business logic should live close to the feature it belongs to.
- Pages compose features.
- Shared modules contain reusable technical pieces and shared UI primitives.
- Domain APIs stay close to their features.
- Technical API infrastructure stays in `src/api`.

Recommended structure:

```text
src/
|-- app/
|   |-- App.tsx
|   |-- BrowserRouter.tsx
|   `-- providers/
|-- api/
|   |-- axiosClient.ts
|   |-- apiError.ts
|   `-- halClient.ts
|-- pages/
|-- features/
|   |-- cards/
|   |-- game-session/
|   |-- game-flow/
|   |-- players/
|   |-- dice/
|   `-- settings/
|-- shared/
|   |-- ui/
|   |-- hooks/
|   `-- utils/
|-- i18n/
|-- assets/
`-- main.tsx
```

Feature structure:

```text
feature/
|-- api/
|-- model/
|-- store/
|-- hooks/
`-- components/
```

Avoid:

- Giant `App.tsx`.
- Giant global components folders.
- Type-based organization only.
- Centralizing all components into one global folder.

## State Management

Use Zustand instead of React Context for application state.

Keep stores close to their domains/features, for example:

- `features/game-session/store/`
- `features/game-flow/store/`

Use React Context mostly for technical providers such as i18n, query providers,
and application providers.

## Component Strategy

Shared UI components belong in `shared/ui/`, for example:

- `Button`
- `Modal`
- `IconButton`
- `PageShell`

Feature-specific components belong inside their features, for example:

- `features/cards/components/GameCard/`
- `features/players/components/PlayerList/`

## Storybook Strategy

Stories should live close to components.

Preferred:

```text
GameCard.tsx
GameCard.stories.tsx
```

Avoid large centralized stories directories.

Use Storybook as:

- Component documentation.
- Isolated development.
- Visual testing.

## Localization

Localization should exist from early development.

Avoid hardcoded user-facing strings. Use translation keys.

Recommended structure:

```text
src/i18n/
`-- locales/
    |-- pl.json
    `-- en.json
```

## Design System Direction

The visual style should emphasize:

- Dark backgrounds.
- Premium feeling.
- Elegant style.
- Game-like interactions.
- Card-first UX.
- Gold accents.
- Burgundy and purple accents.
- Immersive atmosphere.

Avoid enterprise/admin panel appearance.

## Animation Guidelines

Focus animations on:

- Card draw.
- Card movement.
- Card play.
- Dice rolling.
- Transitions.
- Hover feedback.
- Touch feedback.

Prefer `transform` and `opacity`. Avoid expensive layout-triggering animations.

## Frontend Development Roadmap

Stage 1:

- Restructure frontend.
- Routing.
- Feature organization.
- Shared UI foundation.
- Tailwind integration.
- i18n setup.

Stage 2:

- Card components.
- Stores.
- Game flow.
- Design system.
- Animations.

Stage 3:

- PWA skeleton.
- Manifest.
- Standalone support.
- App shell cache.

Stage 4:

- Offline support.
- IndexedDB.
- Cache improvements.
- Multiplayer integration.

## PWA Strategy

Implement PWA support incrementally.

Early:

- Manifest.
- Icons.
- Standalone mode.
- Basic service worker.

Later:

- Offline decks.
- Cache versioning.
- IndexedDB persistence.
- Advanced asset cache.

## MVP Guidance

- Prioritize the local one-device loop.
- Do not introduce backend implementation work unless explicitly requested.
- Do not implement public UGC, matchmaking, chat, payments, or native iOS.
- Do not introduce AI features unless explicitly requested.
- Card content must include metadata for type, spice level, target, and
  boundaries.
