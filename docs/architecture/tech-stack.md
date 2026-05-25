# Tech Stack

## Frontend

- React.
- TypeScript.
- Vite.
- PWA.
- React Router.
- Mobile-first UI, especially comfortable on iPhone.
- State management: TBD, candidates include Zustand, Redux Toolkit, TanStack
  Store, or React Context for an early prototype.

## Local Data and Cache

- IndexedDB, preferably through Dexie, for decks, card text, game state,
  settings, and private user cards.
- Cache API for app shell, fonts, images, icons, and other assets.
- localStorage only for small settings such as theme, last room code, or last
  selected deck.

## Backend

- Java 25 LTS.
- Spring Boot 4.x.
- REST for decks and configuration.
- WebSocket or STOMP for later multiplayer.
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

```http
GET /api/decks
GET /api/decks/{deckId}
GET /api/decks/{deckId}/cards
GET /assets/cards/{cardId}.webp
WS  /ws/game
```

## Unknowns

- Hosting provider: TBD.
- Final state management choice: TBD.
- Whether MVP has no backend or a mock API: TBD.
- Exact deck and asset versioning contract: TBD.
