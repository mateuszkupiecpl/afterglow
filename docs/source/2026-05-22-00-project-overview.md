# Project overview - Afterglow

## Cel projektu

Afterglow to webowa/PWA gra karciana 18+ przeznaczona dla dorosłych graczy. Ma działać przede wszystkim na telefonach, zwłaszcza iPhone, ale również na Androidzie i komputerze.

Projekt ma łączyć elementy:

- karcianki,
- gry imprezowej,
- gry dla par,
- truth or dare,
- lekkich mechanik TCG,
- lekkich mechanik planszowych,
- atmosferycznych przerywników: flirt, taniec, masaż, rozmowa, role-play, aktywności grupowe.

## Najważniejsze założenie produktowe

Gra nie ma być prostym generatorem losowych zadań. Główna wartość produktu to połączenie wyboru, atmosfery, progresji, zgody i interakcji między graczami.

Gracze nie powinni czuć presji ciągłego wykonywania poleceń. Gra ma budować napięcie stopniowo, dawać czas na rozmowę i przerywniki oraz pozwalać dostosować intensywność do komfortu uczestników.

## Platforma

Pierwsza wersja powinna być aplikacją webową/PWA, a nie natywną aplikacją iOS.

Powody:

- brak konieczności publikacji w App Store,
- brak review Apple dla treści 18+,
- łatwiejsze aktualizowanie talii i zawartości,
- jeden frontend dla iPhone, Androida i desktopu,
- zgodność ze stackiem autora: React, TypeScript, Java.

## Docelowa liczba graczy

Gra powinna obsługiwać od 2 do 8 graczy.

Tryby graczy:

- 2 osoby: tryb pary, wolniejszy, bardziej intymny.
- 3-4 osoby: mała grupa, dużo bezpośrednich interakcji.
- 5-8 osób: tryb imprezowy, więcej zadań grupowych, głosowań i losowań.

## Robocza nazwa i repozytoria

Aktualna rekomendacja robocza:

- nazwa produktu: `Afterglow`,
- repo frontend: `afterglow-ui`,
- repo backend: `afterglow`.
