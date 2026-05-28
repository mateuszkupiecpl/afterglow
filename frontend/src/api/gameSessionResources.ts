import type { HalResource } from './hal'

export const gameStatuses = ['setup', 'in_progress', 'paused', 'finished'] as const
export type GameStatus = (typeof gameStatuses)[number]

export const gameModes = ['party_warmup', 'night_of_tension', 'slow_burn', 'fantasy_finale', 'custom'] as const
export type GameMode = (typeof gameModes)[number]

export const spiceLevels = ['warmup', 'tension', 'courage', 'spicy', 'finale'] as const
export type SpiceLevel = (typeof spiceLevels)[number]

export const paceCodes = ['fast', 'standard', 'chill'] as const
export type PaceCode = (typeof paceCodes)[number]

export const cardTargets = [
  'self',
  'chosen_player',
  'random_player',
  'pair',
  'group',
  'everyone',
  'left_player',
  'right_player',
] as const
export type CardTarget = (typeof cardTargets)[number]

export const cardTypes = [
  'question',
  'challenge',
  'group',
  'interlude',
  'fantasy',
  'prop',
  'special',
  'reaction',
  'event',
] as const
export type CardType = (typeof cardTypes)[number]

export const boundaryCodes = [
  'no_nudity',
  'no_clothing_removal',
  'no_touch',
  'verbal_only',
  'no_props',
  'no_random_partner',
  'partner_only',
  'no_group_physical_tasks',
] as const
export type BoundaryCode = (typeof boundaryCodes)[number]

export type HomeResource = HalResource

export type GameSettingsResource = {
  minPlayers: number
  maxPlayers: number
  startSpiceLevel: SpiceLevel
  maxSpiceLevel: SpiceLevel
  pace: PaceCode
  allowProps: boolean
  allowPairTasks: boolean
  allowGroupTasks: boolean
}

export type ComfortProfileResource = {
  playerId: string
  confirmedAdult: boolean
  boundaries: BoundaryCode[]
}

export type CardResource = {
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

export type CardInstanceResource = {
  instanceId: string
  cardId: string
  ownerPlayerId: string
  visibility: string
  card: CardResource
}

export type PlayedCardResource = {
  cardInstanceId: string
  playerId: string
  targetPlayerId: string | null
  card: CardResource
  playedAt: string
}

export type PlayerResource = {
  id: string
  nickname: string
  host: boolean
  hand: CardInstanceResource[]
  points: number
  comfortProfile: ComfortProfileResource
}

export type GameSessionResource = HalResource & {
  id: string
  code: string
  mode: GameMode
  status: GameStatus
  players: PlayerResource[]
  settings: GameSettingsResource
  deckIds: string[]
  currentRound: number
  currentTurnPlayerId: string | null
  atmosphereLevel: number
  currentSpiceLevel: SpiceLevel
  score: Record<string, number>
  currentCard: PlayedCardResource | null
  createdAt: string
  updatedAt: string
}

export type DiceRollResource = {
  sessionId: string
  playerId: string | null
  value: number
  rolledAt: string
}

export type CreateGameSessionRequest = {
  hostNickname: string
  confirmedAdult: boolean
  mode?: GameMode
  settings?: Partial<GameSettingsRequest>
  boundaries?: BoundaryCode[]
}

export type GameSettingsRequest = {
  startSpiceLevel: SpiceLevel
  maxSpiceLevel: SpiceLevel
  pace: PaceCode
  allowProps: boolean
  allowPairTasks: boolean
  allowGroupTasks: boolean
}

export type AddPlayerRequest = {
  nickname: string
  confirmedAdult: boolean
  boundaries?: BoundaryCode[]
}

export type PlayCardRequest = {
  playerId: string
  cardInstanceId: string
  targetPlayerId?: string
}

export type ResolveCurrentCardRequest = {
  playerId: string
}

export type DiceRollRequest = {
  playerId?: string
}
