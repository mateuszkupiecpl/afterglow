import type { BoundaryCode, Card, GameSession, Player } from './gameSession'

const targetRequiresSpecificPlayer = new Set(['chosen_player'])

export function currentPlayer(session: GameSession): Player | undefined {
  return session.players.find((player) => player.id === session.currentTurnPlayerId)
}

export function playerById(session: GameSession, playerId: string | null | undefined): Player | undefined {
  return session.players.find((player) => player.id === playerId)
}

export function canStartSession(session: GameSession): boolean {
  return session.status === 'setup' && session.players.length >= session.settings.minPlayers
}

export function cardNeedsTarget(card: Card): boolean {
  return targetRequiresSpecificPlayer.has(card.target)
}

export function eligibleTargetsForCard(session: GameSession, card: Card, currentPlayerId: string): Player[] {
  if (!cardNeedsTarget(card)) {
    return []
  }

  return session.players.filter((player) => {
    return player.id !== currentPlayerId && !cardViolatesBoundaries(card, player.comfortProfile.boundaries)
  })
}

export function cardViolatesBoundaries(card: Card, boundaries: BoundaryCode[]): boolean {
  return card.boundaries.some((boundary) => boundaries.includes(boundary))
}

export function scoreRows(session: GameSession): Array<{ player: Player; points: number }> {
  return [...session.players]
    .map((player) => ({ player, points: session.score[player.id] ?? player.points }))
    .sort((left, right) => right.points - left.points || left.player.nickname.localeCompare(right.player.nickname))
}
