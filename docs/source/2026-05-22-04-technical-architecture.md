# Technical Architecture

## Recommended Stack

### Frontend

- React.
- TypeScript.
- Vite.
- PWA.
- Axios.
- Storybook.
- MSW.
- IndexedDB, preferably through Dexie.
- Cache API.
- React Router.
- Local state management: TBD, candidates include Zustand, Redux Toolkit,
  TanStack Store, or React Context for an early prototype.

Storybook is for isolated UI development. MSW is for mocking API flows and
gameplay scenarios.

### Backend

- Java 25 LTS.
- Spring Boot 4.x.
- Hexagonal Architecture / Ports and Adapters.
- Groovy and Spock for tests.
- WireMock for external dependency mocking.
- RestTemplate for integration/test calls.
- WebSocket or STOMP for later multiplayer.
- PostgreSQL in a later stage.
- Redis in a later stage if game rooms must run across multiple instances.

## Architecture Style

Use Hexagonal Architecture with separation of domain, application,
infrastructure, and API concerns.

Suggested responsibilities:

- `domain/` - entities, value objects, domain services, repository ports,
  domain policies, and business rules.
- `application/` - use cases, orchestration, commands, and queries.
- `infrastructure/` - persistence, REST adapters, external integrations,
  configuration, and port implementations.
- `api/` - request/response resources and mapping to the application layer.

Rules:

- Domain logic must not depend on Spring, HTTP, persistence, or framework
  concerns.
- Infrastructure adapts to the domain, not the opposite.
- The application layer orchestrates use cases.
- The domain owns business rules.
- API code depends on application commands, queries, snapshots/read models, and
  application exceptions. It must not import domain model or domain port types.
- Domain ports are public boundary interfaces for application services and
  infrastructure adapters. Adapter implementations should stay package-private
  in infrastructure packages.
- Do not overengineer, but keep architectural boundaries visible.

## API Design

Use a HATEOAS-inspired API approach.

API resources that expose links should use `RepresentationModel`; do not wrap
API resources in `EntityModel`.

Add links to API resources only through classes with the `LinkAssembler` suffix,
for example `GameSessionLinkAssembler`.

Domain Model != API Resource.

API-facing models must be separate classes and use the `Resource` suffix:

- `PlayerResource`
- `GameSessionResource`
- `CardResource`

Do not use the `DTO` suffix for API-facing models.

Early candidate endpoints:

```http
GET /api/decks
GET /api/decks/{deckId}
GET /api/decks/{deckId}/cards
GET /assets/cards/{cardId}.webp
WS  /ws/game
```

## Distribution

The app should run through the browser. On iPhone, users can add it to the home
screen through Safari.

Do not assume App Store publication for the spicy version.

## Multiplayer

Do not use Bluetooth as the main multiplayer technology.

Target model:

1. Host creates a room.
2. Backend generates a room code, for example `HOT-482`.
3. Other players join by code or link.
4. Backend synchronizes game state through WebSocket.

Synchronized state may include:

- current round,
- current player,
- card hands,
- current card,
- timer,
- points,
- atmosphere level,
- spice level,
- boundary settings,
- reaction queue.

## PWA And Cache

Do not download all images on every startup.

Loading model:

1. Load app shell: HTML, CSS, JS, manifest, icons, logo, base backgrounds.
2. Load deck list and metadata.
3. Load text for the active deck.
4. Lazy-load card images.
5. Preload a few nearby card images.
6. Keep downloaded assets in cache.

## Local Storage

- Cache API: JS, CSS, HTML, fonts, images, icons, assets.
- IndexedDB: decks, card text, game state, settings, private user cards.
- localStorage: only small settings such as theme, last room code, or last deck.

Do not store large images in localStorage or as base64 inside JSON.

## Asset Format

Recommended:

- WebP or AVIF for card art and backgrounds.
- SVG for icons and simple UI elements.
- PNG only when needed.

Recommended sizes:

- card thumbnail: 300-600 px wide,
- full card: 800-1200 px wide,
- single image: around 50-200 KB.

## Safe Memory Assumptions

- MVP should work well with 50-200 MB cache.
- One offline deck: 20-80 MB.
- Several offline decks: 100-300 MB.
- Above 500 MB requires downloaded asset management.

## Deck And Asset Versioning

Each deck should have versions:

```json
{
  "deckId": "party-hot",
  "version": 12,
  "cardsVersion": 8,
  "assetsVersion": 4
}
```

The frontend should check deck versions and download only changed data.
