# Open questions

Ten dokument zawiera pytania projektowe, które nie są jeszcze rozstrzygnięte.

## Game design

1. Jaki jest główny warunek zwycięstwa?
   - punkty,
   - tor atmosfery,
   - finałowe głosowanie,
   - brak zwycięzcy, gra tylko dla doświadczenia.

2. Czy 3 punkty akcji mają być od razu w MVP, czy dopiero po prostszym prototypie?

3. Ile kart na ręce w MVP?
   - 4 startowe i limit 5,
   - czy od razu 5 kart?

4. Czy event rundy ma być w MVP, czy w etapie 2?

5. Jak często mają pojawiać się przerywniki?
   - co rundę,
   - co X tur,
   - po wzroście toru atmosfery,
   - wyłącznie z kart.

6. Jak rozwiązać ukrywanie ręki na jednym telefonie?

7. Czy gra ma mieć timer przy zadaniach w MVP?

## Technologia

1. Czy frontend i backend będą w osobnych repozytoriach czy monorepo?

Aktualna rekomendacja: osobne repozytoria:

- `afterglow-ui`,
- `afterglow`.

2. Czy pierwsza wersja frontendowa ma działać w 100% lokalnie bez backendu?

Aktualna rekomendacja: tak, MVP lokalne na jednym urządzeniu.

3. Jaki state management wybrać dla React?

Kandydaci:

- Zustand,
- Redux Toolkit,
- TanStack Store,
- zwykły React Context na bardzo wczesnym prototypie.

4. Czy karty w MVP trzymać jako JSON, TS fixtures, czy backend mock?

## Content

1. Ile kart powinno być w pierwszej talii testowej?

Rekomendacja robocza:

- minimum 50,
- sensownie 100,
- docelowo więcej talii po 50-150 kart.

2. Czy tworzyć osobne talie dla trybu pary i imprezy?

3. Jak mocne mają być pierwsze treści MVP?

Rekomendacja: poziomy 1-3 na start, bez najcięższych kart finałowych.

## Branding

1. Czy nazwa `Afterglow` zostaje?
2. Czy styl UI idzie w stronę:
   - ciemny luksus,
   - burgund/złoto,
   - neon/noir,
   - minimalistyczny premium?
3. Czy nazwy poziomów mają być polskie czy angielskie?

## Biznes

1. Czy gra ma być tylko prywatnym projektem, czy produktem komercyjnym?
2. Czy będą płatne talie?
3. Czy użytkownicy będą mogli tworzyć własne talie?
4. Czy talie użytkowników będą prywatne, czy publiczne?

Aktualna rekomendacja: na start tylko prywatne talie, bez publicznego UGC.
