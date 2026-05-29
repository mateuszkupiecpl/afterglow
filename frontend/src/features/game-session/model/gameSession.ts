import type {
  BoundaryCode,
  CardTarget,
  CardType,
  GameMode,
  GameStatus,
  PaceCode,
  SpiceLevel,
} from '../api/gameSessionResources'

export type { BoundaryCode, CardTarget, CardType, GameMode, GameStatus, PaceCode, SpiceLevel }

export type Option<T extends string> = {
  value: T
  label: string
  note?: string
}

export const modeOptions: Option<GameMode>[] = [
  { value: 'party_warmup', label: 'Party warmup', note: 'Light and social' },
  { value: 'night_of_tension', label: 'Night of tension', note: 'Slower escalation' },
  { value: 'slow_burn', label: 'Slow burn', note: 'More pauses' },
  { value: 'fantasy_finale', label: 'Fantasy finale', note: 'Highest ceiling' },
  { value: 'custom', label: 'Custom', note: 'Manual settings' },
]

export const spiceOptions: Option<SpiceLevel>[] = [
  { value: 'warmup', label: 'Warmup' },
  { value: 'tension', label: 'Tension' },
  { value: 'courage', label: 'Courage' },
  { value: 'spicy', label: 'Spicy' },
  { value: 'finale', label: 'Finale' },
]

export const paceOptions: Option<PaceCode>[] = [
  { value: 'fast', label: 'Fast' },
  { value: 'standard', label: 'Standard' },
  { value: 'chill', label: 'Chill' },
]

export const boundaryOptions: Option<BoundaryCode>[] = [
  { value: 'no_nudity', label: 'No nudity' },
  { value: 'no_clothing_removal', label: 'No clothing removal' },
  { value: 'no_touch', label: 'No touch' },
  { value: 'verbal_only', label: 'Verbal only' },
  { value: 'no_props', label: 'No props' },
  { value: 'no_random_partner', label: 'No random partner' },
  { value: 'partner_only', label: 'Partner only' },
  { value: 'no_group_physical_tasks', label: 'No group physical tasks' },
]

export const cardTypeLabels: Record<CardType, string> = {
  question: 'Question',
  challenge: 'Challenge',
  group: 'Group',
  interlude: 'Interlude',
  fantasy: 'Fantasy',
  prop: 'Prop',
  special: 'Special',
  reaction: 'Reaction',
  event: 'Event',
}

export const targetLabels: Record<CardTarget, string> = {
  self: 'Self',
  chosen_player: 'Chosen player',
  random_player: 'Random player',
  pair: 'Pair',
  group: 'Group',
  everyone: 'Everyone',
  left_player: 'Left player',
  right_player: 'Right player',
}

export const spiceLabels: Record<SpiceLevel, string> = {
  warmup: 'Warmup',
  tension: 'Tension',
  courage: 'Courage',
  spicy: 'Spicy',
  finale: 'Finale',
}

export type GameSettings = {
  minPlayers: number
  maxPlayers: number
  startSpiceLevel: SpiceLevel
  maxSpiceLevel: SpiceLevel
  pace: PaceCode
  allowProps: boolean
  allowPairTasks: boolean
  allowGroupTasks: boolean
}

export type ComfortProfile = {
  playerId: string
  confirmedAdult: boolean
  boundaries: BoundaryCode[]
}

export type Card = {
  id: string
  deckId: string
  title: string
  type: CardType
  text: string
  spiceLevel: SpiceLevel
  actionPointCost: number
  target: CardTarget
  requiresProps: boolean
  boundaries: BoundaryCode[]
}

export type CardInstance = {
  instanceId: string
  cardId: string
  ownerPlayerId: string
  visibility: string
  card: Card
}

export type PlayedCard = {
  cardInstanceId: string
  playerId: string
  targetPlayerId: string | null
  card: Card
  playedAt: string
}

export type Player = {
  id: string
  nickname: string
  host: boolean
  hand: CardInstance[]
  points: number
  comfortProfile: ComfortProfile
}

export type GameSession = {
  id: string
  code: string
  mode: GameMode
  status: GameStatus
  players: Player[]
  settings: GameSettings
  deckIds: string[]
  currentRound: number
  currentTurnPlayerId: string | null
  atmosphereLevel: number
  currentSpiceLevel: SpiceLevel
  score: Record<string, number>
  currentCard: PlayedCard | null
  createdAt: string
  updatedAt: string
}
