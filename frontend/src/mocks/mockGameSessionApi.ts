import type { GameSessionApi } from '../api/gameSessionApi'
import type {
  AddPlayerRequest,
  BoundaryCode,
  CardInstanceResource,
  CardResource,
  CreateGameSessionRequest,
  DiceRollRequest,
  DiceRollResource,
  GameSessionResource,
  GameSettingsResource,
  PlayCardRequest,
  PlayerResource,
  ResolveCurrentCardRequest,
  SpiceLevel,
} from '../api/gameSessionResources'
import { spiceLevels } from '../api/gameSessionResources'
import { starterCards } from './starterCards'

const apiRoot = '/api'
const minPlayers = 2
const maxPlayers = 8

type MutableSession = GameSessionResource & {
  drawCursor: number
}

export function createMockGameSessionApi(): GameSessionApi {
  const sessions = new Map<string, MutableSession>()
  let sessionSequence = 100
  let playerSequence = 1
  let cardInstanceSequence = 1

  function createSession(request: CreateGameSessionRequest): Promise<GameSessionResource> {
    if (!request.confirmedAdult) {
      return reject('Every player must confirm they are 18+ before joining.')
    }

    const now = new Date().toISOString()
    const sessionId = `local-${sessionSequence++}`
    const host = createPlayer(request.hostNickname, true, request.boundaries ?? [])
    const settings = buildSettings(request.settings)
    const session: MutableSession = {
      id: sessionId,
      code: `AGL-${String(sessionSequence).padStart(3, '0')}`,
      mode: request.mode ?? 'party_warmup',
      status: 'setup',
      players: [host],
      settings,
      deckIds: ['starter'],
      currentRound: 0,
      currentTurnPlayerId: null,
      atmosphereLevel: 1,
      currentSpiceLevel: settings.startSpiceLevel,
      score: { [host.id]: 0 },
      currentCard: null,
      createdAt: now,
      updatedAt: now,
      drawCursor: 0,
    }

    sessions.set(session.id, session)
    return resolveWithLinks(session)
  }

  function findByCode(code: string): Promise<GameSessionResource> {
    const session = [...sessions.values()].find((candidate) => candidate.code.toLowerCase() === code.toLowerCase())

    if (!session) {
      return reject('No local session uses that code.')
    }

    return resolveWithLinks(session)
  }

  function addPlayer(sessionResource: GameSessionResource, request: AddPlayerRequest): Promise<GameSessionResource> {
    return withSession(sessionResource.id, (session) => {
      if (session.status !== 'setup') {
        throw new Error('Players can only be added before the session starts.')
      }
      if (!request.confirmedAdult) {
        throw new Error('Every player must confirm they are 18+ before joining.')
      }
      if (session.players.length >= session.settings.maxPlayers) {
        throw new Error('This session already has the maximum number of players.')
      }

      const player = createPlayer(request.nickname, false, request.boundaries ?? [])
      session.players.push(player)
      session.score[player.id] = 0
      touch(session)
    })
  }

  function start(sessionResource: GameSessionResource): Promise<GameSessionResource> {
    return withSession(sessionResource.id, (session) => {
      if (session.players.length < session.settings.minPlayers) {
        throw new Error('At least two adult-confirmed players are required.')
      }

      session.status = 'in_progress'
      session.currentRound = 1
      session.currentTurnPlayerId = session.players[0]?.id ?? null
      session.currentSpiceLevel = session.settings.startSpiceLevel
      session.players = session.players.map((player) => ({
        ...player,
        hand: dealHand(session, player.id),
      }))
      touch(session)
    })
  }

  function playCard(sessionResource: GameSessionResource, request: PlayCardRequest): Promise<GameSessionResource> {
    return withSession(sessionResource.id, (session) => {
      assertInProgress(session)
      if (session.currentCard) {
        throw new Error('Resolve the current card before playing another.')
      }
      if (session.currentTurnPlayerId !== request.playerId) {
        throw new Error('Only the current turn player can play a card.')
      }

      const playerIndex = session.players.findIndex((player) => player.id === request.playerId)
      const player = session.players[playerIndex]

      if (!player) {
        throw new Error('The selected player is not in this session.')
      }

      const cardIndex = player.hand.findIndex((card) => card.instanceId === request.cardInstanceId)
      const cardInstance = player.hand[cardIndex]

      if (!cardInstance) {
        throw new Error('That card is not in the current player hand.')
      }

      validateCardPlay(session, cardInstance.card, request)

      const nextHand = [
        ...player.hand.slice(0, cardIndex),
        ...player.hand.slice(cardIndex + 1),
        drawCard(session, player.id),
      ]
      session.players[playerIndex] = { ...player, hand: nextHand }
      session.currentCard = {
        cardInstanceId: cardInstance.instanceId,
        playerId: request.playerId,
        targetPlayerId: request.targetPlayerId ?? null,
        card: cardInstance.card,
        playedAt: new Date().toISOString(),
      }
      touch(session)
    })
  }

  function completeCurrentCard(
    sessionResource: GameSessionResource,
    request: ResolveCurrentCardRequest,
  ): Promise<GameSessionResource> {
    return withSession(sessionResource.id, (session) => {
      assertResolvable(session, request.playerId)
      session.score[request.playerId] = (session.score[request.playerId] ?? 0) + 1
      session.players = session.players.map((player) =>
        player.id === request.playerId ? { ...player, points: (session.score[request.playerId] ?? 0) } : player,
      )
      session.atmosphereLevel = Math.min(5, session.atmosphereLevel + 1)
      session.currentSpiceLevel = spiceForAtmosphere(session)
      advanceTurn(session)
      touch(session)
    })
  }

  function refuseCurrentCard(
    sessionResource: GameSessionResource,
    request: ResolveCurrentCardRequest,
  ): Promise<GameSessionResource> {
    return withSession(sessionResource.id, (session) => {
      assertResolvable(session, request.playerId)
      session.score[request.playerId] = Math.max(0, (session.score[request.playerId] ?? 0) - 1)
      session.players = session.players.map((player) =>
        player.id === request.playerId ? { ...player, points: session.score[request.playerId] ?? 0 } : player,
      )
      advanceTurn(session)
      touch(session)
    })
  }

  function rollDie(sessionResource: GameSessionResource, request?: DiceRollRequest): Promise<DiceRollResource> {
    const session = sessions.get(sessionResource.id)

    if (!session || session.status !== 'in_progress') {
      return reject('A die can only be rolled during an active session.')
    }

    return Promise.resolve({
      sessionId: session.id,
      playerId: request?.playerId ?? session.currentTurnPlayerId,
      value: Math.floor(Math.random() * 6) + 1,
      rolledAt: new Date().toISOString(),
    })
  }

  function finish(sessionResource: GameSessionResource): Promise<GameSessionResource> {
    return withSession(sessionResource.id, (session) => {
      session.status = 'finished'
      session.currentCard = null
      touch(session)
    })
  }

  function createPlayer(nickname: string, host: boolean, boundaries: BoundaryCode[]): PlayerResource {
    const playerId = `player-${playerSequence++}`

    return {
      id: playerId,
      nickname: nickname.trim(),
      host,
      hand: [],
      points: 0,
      comfortProfile: {
        playerId,
        confirmedAdult: true,
        boundaries,
      },
    }
  }

  function withSession(
    sessionId: string,
    update: (session: MutableSession) => void,
  ): Promise<GameSessionResource> {
    const session = sessions.get(sessionId)

    if (!session) {
      return reject('The session could not be found.')
    }

    try {
      update(session)
      return resolveWithLinks(session)
    } catch (error: unknown) {
      return reject(error instanceof Error ? error.message : 'The session action failed.')
    }
  }

  function dealHand(session: MutableSession, playerId: string): CardInstanceResource[] {
    return Array.from({ length: 4 }, () => drawCard(session, playerId))
  }

  function drawCard(session: MutableSession, playerId: string): CardInstanceResource {
    const eligibleCards = starterCards.filter((card) => cardAllowedForSession(session, card))
    const deck = eligibleCards.length > 0 ? eligibleCards : starterCards.filter((card) => card.boundaries.length === 0)
    const card = deck[session.drawCursor % deck.length]
    session.drawCursor += 1

    return {
      instanceId: `card-${cardInstanceSequence++}`,
      cardId: card.id,
      ownerPlayerId: playerId,
      visibility: 'owner',
      card,
    }
  }

  return {
    createSession,
    findByCode,
    addPlayer,
    start,
    playCard,
    completeCurrentCard,
    refuseCurrentCard,
    rollDie,
    finish,
  }
}

function buildSettings(settings: Partial<GameSettingsResource> | undefined): GameSettingsResource {
  return {
    minPlayers,
    maxPlayers,
    startSpiceLevel: settings?.startSpiceLevel ?? 'warmup',
    maxSpiceLevel: settings?.maxSpiceLevel ?? 'courage',
    pace: settings?.pace ?? 'standard',
    allowProps: settings?.allowProps ?? false,
    allowPairTasks: settings?.allowPairTasks ?? true,
    allowGroupTasks: settings?.allowGroupTasks ?? true,
  }
}

function cardAllowedForSession(session: MutableSession, card: CardResource): boolean {
  if (card.requiresProps && !session.settings.allowProps) {
    return false
  }
  if (card.target === 'pair' && !session.settings.allowPairTasks) {
    return false
  }
  if ((card.target === 'group' || card.target === 'everyone') && !session.settings.allowGroupTasks) {
    return false
  }

  const sessionBoundaries = session.players.flatMap((player) => player.comfortProfile.boundaries)
  return !card.boundaries.some((boundary) => sessionBoundaries.includes(boundary))
}

function validateCardPlay(session: MutableSession, card: CardResource, request: PlayCardRequest) {
  if (card.target === 'chosen_player' && !request.targetPlayerId) {
    throw new Error('Choose a willing target player for this card.')
  }
  if (card.requiresProps && !session.settings.allowProps) {
    throw new Error('This session does not allow prop cards.')
  }

  const target = session.players.find((player) => player.id === request.targetPlayerId)

  if (target && card.boundaries.some((boundary) => target.comfortProfile.boundaries.includes(boundary))) {
    throw new Error('That card conflicts with the selected player boundaries.')
  }
}

function assertInProgress(session: MutableSession) {
  if (session.status !== 'in_progress') {
    throw new Error('This action needs an active session.')
  }
}

function assertResolvable(session: MutableSession, playerId: string) {
  assertInProgress(session)

  if (!session.currentCard) {
    throw new Error('There is no current card to resolve.')
  }
  if (session.currentCard.playerId !== playerId) {
    throw new Error('Only the player who played the card can resolve it.')
  }
}

function advanceTurn(session: MutableSession) {
  const currentIndex = session.players.findIndex((player) => player.id === session.currentTurnPlayerId)
  const nextIndex = currentIndex < 0 ? 0 : (currentIndex + 1) % session.players.length

  if (nextIndex === 0) {
    session.currentRound += 1
  }

  session.currentTurnPlayerId = session.players[nextIndex]?.id ?? null
  session.currentCard = null
}

function spiceForAtmosphere(session: MutableSession): SpiceLevel {
  const maxIndex = spiceLevels.indexOf(session.settings.maxSpiceLevel)
  const startIndex = spiceLevels.indexOf(session.settings.startSpiceLevel)
  const atmosphereIndex = Math.min(maxIndex, startIndex + Math.max(0, session.atmosphereLevel - 1))

  return spiceLevels[Math.max(0, atmosphereIndex)] ?? session.settings.startSpiceLevel
}

function touch(session: MutableSession) {
  session.updatedAt = new Date().toISOString()
}

function resolveWithLinks(session: MutableSession): Promise<GameSessionResource> {
  return Promise.resolve(copyResource(withLinks(session)))
}

function reject<T>(message: string): Promise<T> {
  return Promise.reject(new Error(message))
}

function withLinks(session: MutableSession): GameSessionResource {
  const links: GameSessionResource['_links'] = {
    self: { href: `${apiRoot}/game-sessions/${session.id}` },
    byCode: { href: `${apiRoot}/game-sessions/code/${session.code}` },
  }

  if (session.status === 'setup') {
    links.addPlayer = { href: `${apiRoot}/game-sessions/${session.id}/players` }
    links.start = { href: `${apiRoot}/game-sessions/${session.id}/start` }
  }

  if (session.status === 'in_progress') {
    links.playCard = { href: `${apiRoot}/game-sessions/${session.id}/cards/play` }
    links.completeCurrentCard = { href: `${apiRoot}/game-sessions/${session.id}/current-card/complete` }
    links.refuseCurrentCard = { href: `${apiRoot}/game-sessions/${session.id}/current-card/refuse` }
    links.rollDie = { href: `${apiRoot}/game-sessions/${session.id}/dice-rolls` }
    links.finish = { href: `${apiRoot}/game-sessions/${session.id}/finish` }
  }

  return {
    ...session,
    _links: links,
  }
}

function copyResource<T>(resource: T): T {
  return JSON.parse(JSON.stringify(resource)) as T
}
