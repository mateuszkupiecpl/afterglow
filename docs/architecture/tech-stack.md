# Tech Stack

## Frontend

- React 19.
- TypeScript.
- Vite.
- PWA.
- React Router.
- Tailwind CSS for styling and design-system primitives.
- Motion for React for animations and transitions.
- Zustand for client application state.
- Axios for backend communication.
- i18next for localization.
- Storybook for isolated UI development.
- MSW for Storybook mocks and UI tests only.
- Mobile-first UI, especially comfortable on iPhone.

Do not use Bootstrap.

Normal local development must communicate with the real backend API. Do not add
a frontend mock mode for normal local development. Mocks are allowed only for
Storybook, UI tests, and component isolation.

## Local Data and Cache

- IndexedDB, preferably through Dexie, for decks, card text, game state,
  settings, and private user cards.
- Cache API for app shell, fonts, images, icons, and other assets.
- localStorage only for small settings such as theme, last room code, or last
  selected deck.

## Backend

- Java 25 LTS.
- Spring Boot 4.x.
- Hexagonal Architecture / Ports and Adapters.
- Domain-first design with separate domain, application, infrastructure, and API
  resource concerns.
- REST for decks and configuration.
- WebSocket or STOMP for later multiplayer.
- Groovy and Spock for backend tests.
- WireMock for external dependency mocking.
- RestTemplate for integration/test calls.
- PostgreSQL later: TBD.
- Redis later for multi-instance rooms: TBD.
- Microservices: out of scope for the early project.

## Distribution

- Web/PWA through browser.
- On iPhone, users can add the app to the home screen through Safari.
- Do not assume App Store distribution for the spicy version.

## Assets

- WebP or AVIF for card art and backgrounds.
- SVG for icons and simple UI elements.
- PNG only when needed.
- Lazy-load card images and preload only nearby assets.

## Backend Endpoints Under Consideration

API-facing models must be separate resources, not domain objects. Use the
`Resource` suffix, for example `PlayerResource`, `GameSessionResource`, and
`CardResource`. Do not use the `DTO` suffix for API resources.

```http
GET /api/decks
GET /api/decks/{deckId}
GET /api/decks/{deckId}/cards
GET /assets/cards/{cardId}.webp
WS  /ws/game
```

Use a HATEOAS-inspired API style where resources expose meaningful identifiers,
state, and links or available actions when that improves client clarity.

## Unknowns

- Hosting provider: TBD.
- Exact deck and asset versioning contract: TBD.
