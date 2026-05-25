# Technical architecture

## Rekomendowany stack

### Frontend

- React,
- TypeScript,
- Vite,
- PWA,
- IndexedDB, najlepiej przez Dexie,
- Cache API,
- React Router,
- lokalny state management dobrany później, np. Zustand lub Redux Toolkit.

### Backend

- Java 25 LTS,
- Spring Boot 4.x,
- WebSocket albo STOMP,
- PostgreSQL w późniejszym etapie,
- Redis w późniejszym etapie, jeśli pokoje gry mają działać na wielu instancjach.

## Dystrybucja

Aplikacja ma być uruchamiana przez przeglądarkę. Na iPhonie użytkownik może dodać ją do ekranu początkowego przez Safari.

Nie zakładać publikacji pikantnej wersji w App Store.

## Multiplayer

Nie używać Bluetooth jako głównej technologii multiplayer.

Docelowy model:

1. Host tworzy pokój.
2. Backend generuje kod pokoju, np. `HOT-482`.
3. Inni gracze dołączają przez kod lub link.
4. Backend synchronizuje stan gry przez WebSocket.

Synchronizowany stan:

- aktualna runda,
- aktualny gracz,
- ręce kart,
- aktualna karta,
- timer,
- punkty,
- poziom atmosfery,
- poziom pikantności,
- ustawienia granic,
- kolejka reakcji.

## Proponowane endpointy backendu

Na wczesnym etapie:

```http
GET /api/decks
GET /api/decks/{deckId}
GET /api/decks/{deckId}/cards
GET /assets/cards/{cardId}.webp
WS  /ws/game
```

W przyszłości można rozdzielić backend logiki od assetów:

- Spring Boot: logika gry, pokoje, WebSocket, użytkownicy, płatności, talie,
- CDN / S3 / Cloudflare R2: grafiki, audio, paczki offline.

## PWA i cache

Nie pobierać wszystkich grafik przy każdym starcie.

Model ładowania:

1. Przy starcie pobrać app shell: HTML, CSS, JS, manifest, ikony, logo, podstawowe tła.
2. Pobrać listę talii i metadane.
3. Pobrać teksty aktywnej talii.
4. Grafiki kart pobierać leniwie.
5. Preloadować kilka kolejnych grafik.
6. Trzymać pobrane assety w cache.

## Lokalny storage

- Cache API: JS, CSS, HTML, fonty, grafiki, ikony, assety.
- IndexedDB: talie, teksty kart, stan gry, ustawienia, własne karty użytkownika.
- localStorage: tylko małe ustawienia, np. motyw, ostatni kod pokoju, ostatnia talia.

Nie trzymać dużych grafik w localStorage ani jako base64 w JSON.

## Format grafik

Zalecane:

- WebP lub AVIF dla grafik kart i tła,
- SVG dla ikon i prostych elementów UI,
- PNG tylko gdy potrzebny.

Rekomendowane rozmiary:

- miniatura karty: 300-600 px szerokości,
- pełna karta: 800-1200 px szerokości,
- jedna grafika: około 50-200 KB.

## Bezpieczne założenia pamięciowe

- MVP powinno dobrze działać przy 50-200 MB cache.
- Pojedyncza talia offline: 20-80 MB.
- Kilka talii offline: 100-300 MB.
- Powyżej 500 MB wymagane jest zarządzanie pobranymi zasobami.

## Wersjonowanie talii i assetów

Każda talia powinna mieć wersje:

```json
{
  "deckId": "party-hot",
  "version": 12,
  "cardsVersion": 8,
  "assetsVersion": 4
}
```

Frontend powinien sprawdzać wersję talii i pobierać tylko zmienione dane.
