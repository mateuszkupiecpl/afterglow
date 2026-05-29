import type {
  CardInstanceResource,
  CardResource,
  GameSessionResource,
  PlayedCardResource,
  PlayerResource,
} from '../api/gameSessionResources'
import type { Card, CardInstance, GameSession, PlayedCard, Player } from './gameSession'

export function mapGameSessionResource(resource: GameSessionResource): GameSession {
  return {
    id: resource.id,
    code: resource.code,
    mode: resource.mode,
    status: resource.status,
    players: resource.players.map(mapPlayerResource),
    settings: { ...resource.settings },
    deckIds: [...resource.deckIds],
    currentRound: resource.currentRound,
    currentTurnPlayerId: resource.currentTurnPlayerId,
    atmosphereLevel: resource.atmosphereLevel,
    currentSpiceLevel: resource.currentSpiceLevel,
    score: { ...resource.score },
    currentCard: resource.currentCard ? mapPlayedCardResource(resource.currentCard) : null,
    createdAt: resource.createdAt,
    updatedAt: resource.updatedAt,
  }
}

function mapPlayerResource(resource: PlayerResource): Player {
  return {
    id: resource.id,
    nickname: resource.nickname,
    host: resource.host,
    hand: resource.hand.map(mapCardInstanceResource),
    points: resource.points,
    comfortProfile: {
      playerId: resource.comfortProfile.playerId,
      confirmedAdult: resource.comfortProfile.confirmedAdult,
      boundaries: [...resource.comfortProfile.boundaries],
    },
  }
}

function mapCardInstanceResource(resource: CardInstanceResource): CardInstance {
  return {
    instanceId: resource.instanceId,
    cardId: resource.cardId,
    ownerPlayerId: resource.ownerPlayerId,
    visibility: resource.visibility,
    card: mapCardResource(resource.card),
  }
}

function mapPlayedCardResource(resource: PlayedCardResource): PlayedCard {
  return {
    cardInstanceId: resource.cardInstanceId,
    playerId: resource.playerId,
    targetPlayerId: resource.targetPlayerId,
    card: mapCardResource(resource.card),
    playedAt: resource.playedAt,
  }
}

function mapCardResource(resource: CardResource): Card {
  return {
    id: resource.id,
    deckId: resource.deckId,
    title: resource.title,
    type: resource.type,
    text: resource.text,
    spiceLevel: resource.spiceLevel,
    actionPointCost: resource.actionPointCost,
    target: resource.target,
    requiresProps: resource.requiresProps,
    boundaries: [...resource.boundaries],
  }
}
