import { useMemo, useState } from 'react'
import type { DiceRollResource, GameSessionResource } from '../api/gameSessionResources'
import { backendGameSessionApi, type GameSessionApi } from '../api/gameSessionApi'
import { createLocalGameSessionApi } from '../api/localGameSessionApi'
import { mapGameSessionResource } from '../model/resourceMapper'
import { currentPlayer } from '../../game-flow/model/gameplaySelectors'
import type { CreateSessionForm } from '../../settings/model/createSessionForm'
import type { AddPlayerForm } from '../../players/model/addPlayerForm'

export type DataSource = 'mock' | 'api'

const localGameSessionApi = createLocalGameSessionApi()

export function useGameSessionController() {
  const [source, setSource] = useState<DataSource>('mock')
  const [sessionResource, setSessionResource] = useState<GameSessionResource | null>(null)
  const [busy, setBusy] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [handRevealed, setHandRevealed] = useState(false)
  const [lastRoll, setLastRoll] = useState<DiceRollResource | null>(null)

  const session = useMemo(() => (sessionResource ? mapGameSessionResource(sessionResource) : null), [sessionResource])
  const gameSessionApi: GameSessionApi = source === 'api' ? backendGameSessionApi : localGameSessionApi

  function changeSource(nextSource: DataSource) {
    setSource(nextSource)
    setSessionResource(null)
    setError(null)
    setHandRevealed(false)
    setLastRoll(null)
  }

  async function runSessionAction(action: () => Promise<GameSessionResource>, revealNextHand = false) {
    setBusy(true)
    setError(null)

    try {
      const nextSession = await action()
      setSessionResource(nextSession)
      setHandRevealed(revealNextHand)
    } catch (reason: unknown) {
      setError(reason instanceof Error ? reason.message : 'The session action failed.')
    } finally {
      setBusy(false)
    }
  }

  async function createSession(form: CreateSessionForm) {
    await runSessionAction(() =>
      gameSessionApi.createSession({
        hostNickname: form.hostNickname,
        confirmedAdult: form.confirmedAdult,
        mode: form.mode,
        settings: {
          startSpiceLevel: form.startSpiceLevel,
          maxSpiceLevel: form.maxSpiceLevel,
          pace: form.pace,
          allowProps: form.allowProps,
          allowPairTasks: form.allowPairTasks,
          allowGroupTasks: form.allowGroupTasks,
        },
        boundaries: form.boundaries,
      }),
    )
  }

  async function addPlayer(form: AddPlayerForm) {
    if (!sessionResource) {
      return
    }

    await runSessionAction(() =>
      gameSessionApi.addPlayer(sessionResource, {
        nickname: form.nickname,
        confirmedAdult: form.confirmedAdult,
        boundaries: form.boundaries,
      }),
    )
  }

  async function startSession() {
    if (!sessionResource) {
      return
    }

    await runSessionAction(() => gameSessionApi.start(sessionResource), false)
  }

  async function playCard(cardInstanceId: string, targetPlayerId?: string) {
    if (!sessionResource || !session) {
      return
    }

    const player = currentPlayer(session)

    if (!player) {
      return
    }

    await runSessionAction(() =>
      gameSessionApi.playCard(sessionResource, {
        playerId: player.id,
        cardInstanceId,
        targetPlayerId,
      }),
    )
  }

  async function completeCurrentCard() {
    if (!sessionResource || !session) {
      return
    }

    const playedBy = session.currentCard?.playerId

    if (!playedBy) {
      return
    }

    await runSessionAction(() => gameSessionApi.completeCurrentCard(sessionResource, { playerId: playedBy }), false)
  }

  async function refuseCurrentCard() {
    if (!sessionResource || !session) {
      return
    }

    const playedBy = session.currentCard?.playerId

    if (!playedBy) {
      return
    }

    await runSessionAction(() => gameSessionApi.refuseCurrentCard(sessionResource, { playerId: playedBy }), false)
  }

  async function rollDie() {
    if (!sessionResource || !session) {
      return
    }

    setBusy(true)
    setError(null)

    try {
      const roll = await gameSessionApi.rollDie(sessionResource, {
        playerId: session.currentTurnPlayerId ?? undefined,
      })
      setLastRoll(roll)
    } catch (reason: unknown) {
      setError(reason instanceof Error ? reason.message : 'The die roll failed.')
    } finally {
      setBusy(false)
    }
  }

  async function finishSession() {
    if (!sessionResource) {
      return
    }

    await runSessionAction(() => gameSessionApi.finish(sessionResource), false)
  }

  function resetSession() {
    setSessionResource(null)
    setError(null)
    setHandRevealed(false)
    setLastRoll(null)
  }

  return {
    source,
    session,
    busy,
    error,
    handRevealed,
    lastRoll,
    changeSource,
    createSession,
    addPlayer,
    startSession,
    playCard,
    completeCurrentCard,
    refuseCurrentCard,
    rollDie,
    finishSession,
    revealHand: () => setHandRevealed(true),
    resetSession,
  }
}
