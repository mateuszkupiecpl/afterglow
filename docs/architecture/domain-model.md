# Domain Model

This is a working domain model draft. It is not the final API contract.

## Domain And API Boundary

Domain Model != API Resource.

API-facing models must be separate resources with the `Resource` suffix, for
example `PlayerResource`, `GameSessionResource`, and `CardResource`.

Do not use the `DTO` suffix for API-facing models.

## Main Concepts

### GameSession

Represents one game session.

```ts
type GameSession = {
  id: string;
  code?: string;
  mode: GameMode;
  status: GameStatus;
  players: Player[];
  settings: GameSettings;
  deckIds: string[];
  currentRound: number;
  currentTurnPlayerId: string;
  atmosphereLevel: number;
  currentSpiceLevel: SpiceLevel;
  score: Record<string, number>;
  createdAt: string;
  updatedAt: string;
};
```

### Player

```ts
type Player = {
  id: string;
  nickname: string;
  isHost: boolean;
  hand: CardInstance[];
  points: number;
  comfortProfile?: ComfortProfile;
  statusEffects?: PlayerStatusEffect[];
};
```

### GameSettings

```ts
type GameSettings = {
  minPlayers: number;
  maxPlayers: number;
  startSpiceLevel: SpiceLevel;
  maxSpiceLevel: SpiceLevel;
  pace: GamePace;
  interludeFrequency: InterludeFrequency;
  allowClothingRemoval: boolean;
  allowProps: boolean;
  allowPairTasks: boolean;
  allowGroupTasks: boolean;
  targetSelectionMode: TargetSelectionMode;
  scoringMode: ScoringMode;
};
```

### Card

```ts
type Card = {
  id: string;
  deckId: string;
  title: string;
  type: CardType;
  text: string;
  spiceLevel: SpiceLevel;
  actionPointCost: number;
  target: CardTarget;
  durationSeconds?: number;
  diceEffect?: DiceEffect;
  specialEffect?: CardEffect;
  requiredProps?: PropType[];
  boundaries: BoundaryTag[];
  imageUrl?: string;
  imageVersion?: string;
};
```

### CardInstance

A card instance may be needed for cards in player hands.

```ts
type CardInstance = {
  instanceId: string;
  cardId: string;
  ownerPlayerId?: string;
  visibility: CardVisibility;
};
```

## Working Enums

```ts
type GameMode =
  | 'party_warmup'
  | 'night_of_tension'
  | 'slow_burn'
  | 'fantasy_finale'
  | 'custom';

type GameStatus =
  | 'setup'
  | 'in_progress'
  | 'paused'
  | 'finished';

type GamePace =
  | 'fast'
  | 'standard'
  | 'chill';

type SpiceLevel =
  | 'warmup'
  | 'tension'
  | 'courage'
  | 'spicy'
  | 'finale';

type CardType =
  | 'question'
  | 'challenge'
  | 'group'
  | 'interlude'
  | 'fantasy'
  | 'prop'
  | 'special'
  | 'reaction'
  | 'event';

type ReactionType =
  | 'counter'
  | 'reflect'
  | 'redirect'
  | 'negotiate'
  | 'rescue'
  | 'boost';

type CardTarget =
  | 'self'
  | 'chosen_player'
  | 'random_player'
  | 'pair'
  | 'group'
  | 'everyone'
  | 'left_player'
  | 'right_player';

type TargetSelectionMode =
  | 'active_player_chooses'
  | 'random'
  | 'group_vote'
  | 'card_defined';

type ScoringMode =
  | 'points'
  | 'atmosphere_only'
  | 'group_final_vote';
```

## ComfortProfile

```ts
type ComfortProfile = {
  playerId: string;
  confirmedAdult: boolean;
  acceptedEroticContent: boolean;
  boundaries: BoundaryTag[];
};
```

## BoundaryTag

```ts
type BoundaryTag =
  | 'no_nudity'
  | 'no_clothing_removal'
  | 'no_touch'
  | 'verbal_only'
  | 'no_props'
  | 'no_random_partner'
  | 'partner_only'
  | 'no_group_physical_tasks';
```

## DiceEffect

```ts
type DiceEffect = {
  rollRanges: DiceRollRange[];
};

type DiceRollRange = {
  min: number;
  max: number;
  text: string;
  modifiesSpiceLevel?: SpiceLevel;
  bonusPoints?: number;
};
```

## Implementation Note

The first model may be smaller than this draft. Simplification is acceptable
when it keeps the local game loop moving, but it should not erase important
domain boundaries around consent, targeting, card eligibility, progression, or
session state.
