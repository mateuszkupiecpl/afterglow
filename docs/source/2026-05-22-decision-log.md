# Decision log

Ten plik powinien być aktualizowany przy każdej ważnej decyzji projektowej.

## 2026-05-22

### Decyzja: aplikacja web/PWA zamiast natywnej aplikacji iOS

Status: accepted

Uzasadnienie:

- treści 18+ mogą mieć problem z App Store,
- PWA działa na iPhone, Androidzie i desktopie,
- łatwiejsze aktualizowanie treści,
- stack pasuje do kompetencji autora.

### Decyzja: roboczy stack frontend

Status: accepted

Frontend:

- React,
- TypeScript,
- Vite,
- PWA,
- IndexedDB/Dexie,
- Cache API.

### Decyzja: roboczy stack backend

Status: accepted

Backend:

- Java 21,
- Spring Boot 3,
- WebSocket / STOMP,
- PostgreSQL później,
- Redis później.

### Decyzja: multiplayer przez serwer, nie Bluetooth

Status: accepted

Uzasadnienie:

- Bluetooth na iOS/PWA jest złym kierunkiem,
- WebSocket i pokoje gry są prostsze, skalowalne i bardziej przewidywalne.

### Decyzja: pikantna wersja poza App Store

Status: accepted

Uzasadnienie:

- ograniczenia Apple dla treści seksualnych,
- większa swoboda web/PWA.

### Decyzja: gracze mają kilka kart na ręce

Status: accepted

Założenie:

- start: 4 karty,
- limit: 5 kart,
- dobieranie po zagraniu.

Cel:

- mniej losowości,
- większe poczucie kontroli,
- więcej strategii.

### Decyzja: rekomendowany fundament mechaniczny

Status: proposed / strong recommendation

- 5 kart na ręce,
- 3 punkty akcji,
- reakcje i kontry,
- event rundy,
- tor atmosfery,
- głosowania grupowe.

### Decyzja: nazwa robocza

Status: proposed

- Product name: `Afterglow`,
- Frontend repo: `afterglow-ui`,
- Backend repo: `afterglow`.

### Decyzja: publiczne UGC poza MVP

Status: accepted

Na start unikać publicznych talii użytkowników, publicznych profili, komentarzy, czatu i matchmakingu.

Uzasadnienie:

- moderacja,
- ryzyko prawne,
- złożoność produktu.

### Decyzja: granice graczy są twardym ograniczeniem

Status: accepted

System nie powinien losować lub wymuszać kart naruszających ustawione granice uczestników.
