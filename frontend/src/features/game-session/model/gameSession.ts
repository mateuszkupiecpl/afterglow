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
}

export const modeOptions: Option<GameMode>[] = [
  { value: 'party_warmup' },
  { value: 'night_of_tension' },
  { value: 'slow_burn' },
  { value: 'fantasy_finale' },
  { value: 'custom' },
]

export const spiceOptions: Option<SpiceLevel>[] = [
  { value: 'warmup' },
  { value: 'tension' },
  { value: 'courage' },
  { value: 'spicy' },
  { value: 'finale' },
]

export const paceOptions: Option<PaceCode>[] = [
  { value: 'fast' },
  { value: 'standard' },
  { value: 'chill' },
]

export const boundaryOptions: Option<BoundaryCode>[] = [
  { value: 'no_nudity' },
  { value: 'no_clothing_removal' },
  { value: 'no_touch' },
  { value: 'verbal_only' },
  { value: 'no_props' },
  { value: 'no_random_partner' },
  { value: 'partner_only' },
  { value: 'no_group_physical_tasks' },
]

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
