# Codex implementation guidelines

Ten dokument zawiera instrukcje dla Codex lub innego agenta kodującego pracującego w repozytorium.

## Priorytet decyzji

Jeśli dokumenty są sprzeczne:

1. Nowszy dokument z datą `YYYY-MM-DD` ma pierwszeństwo.
2. `decision-log.md` ma pierwszeństwo nad starszymi opisami koncepcyjnymi.
3. MVP scope ma pierwszeństwo nad pomysłami „na kiedyś”.
4. Nie implementować funkcji poza MVP bez wyraźnego polecenia.

## Styl implementacji

- Preferować prosty, czytelny kod.
- Nie dodawać ciężkich zależności bez potrzeby.
- Oddzielać logikę domenową gry od UI.
- Projektować typy TypeScript tak, aby łatwo dało się je później zmapować na backend Java.
- Unikać trzymania dużych assetów w repozytorium, chyba że są mockami/testowymi.
- Nie kodować treści kart bez metadanych poziomu, typu i granic.

## Frontend - oczekiwania

- React + TypeScript + Vite.
- Komponenty mobilne-first.
- UI wygodne na iPhonie.
- PWA od początku albo bardzo wcześnie.
- Dane kart na start mogą być fixturem lokalnym.
- Stan gry powinien być możliwy do serializacji.
- Logika gry powinna być testowalna bez UI.

## Backend - oczekiwania

- Java 25 LTS + Spring Boot 4.x.
- REST dla talii i konfiguracji.
- WebSocket dla rozgrywki multiplayer w późniejszym etapie.
- Na starcie nie komplikować architektury mikroserwisami, bo świat już wystarczająco cierpi.

## Content safety

Przy implementacji filtrowania kart należy traktować granice graczy jako twarde ograniczenia.

Nie losować kart, które naruszają granice uczestników zadania.

## MVP first

Najpierw zbudować lokalny, grywalny prototyp:

1. konfiguracja graczy,
2. wybór trybu i poziomu,
3. rozdanie kart,
4. ręka gracza,
5. zagranie karty,
6. reakcja/kontra w prostej wersji,
7. rzut kością,
8. punktacja,
9. odmowa,
10. koniec gry.

Dopiero potem:

- backend,
- multiplayer,
- konta,
- płatności,
- edytor kart,
- AI.

## Nazewnictwo robocze

- Produkt: `Afterglow`.
- Frontend repository: `afterglow-ui`.
- Backend repository: `afterglow`.
- Główna domena gry w kodzie może używać nazw: `GameSession`, `Player`, `Card`, `Deck`, `Turn`, `Reaction`, `AtmosphereTrack`, `ComfortProfile`.
