import { useMemo, useState } from 'react'
import { useTranslation } from 'react-i18next'
import type { CardInstance, GameSession } from '../../game-session/model/gameSession'
import {
  cardNeedsTarget,
  currentPlayer,
  eligibleTargetsForCard,
  playerById,
  scoreRows,
} from '../model/gameplaySelectors'
import type { DiceRollResource } from '../../game-session/api/gameSessionResources'
import './GameplayView.css'

type GameplayViewProps = {
  session: GameSession
  busy: boolean
  handRevealed: boolean
  lastRoll: DiceRollResource | null
  onRevealHand: () => void
  onPlayCard: (cardInstanceId: string, targetPlayerId?: string) => void
  onCompleteCurrentCard: () => void
  onRefuseCurrentCard: () => void
  onRollDie: () => void
  onFinish: () => void
}

export function GameplayView({
  session,
  busy,
  handRevealed,
  lastRoll,
  onRevealHand,
  onPlayCard,
  onCompleteCurrentCard,
  onRefuseCurrentCard,
  onRollDie,
  onFinish,
}: GameplayViewProps) {
  const { t: translate } = useTranslation()
  const player = currentPlayer(session)
  const [targetsByCard, setTargetsByCard] = useState<Record<string, string>>({})
  const [selectionError, setSelectionError] = useState<string | null>(null)
  const score = useMemo(() => scoreRows(session), [session])

  if (!player) {
    return (
      <section className="screen">
        <div className="flow-panel">
          <h2>{translate('gameplay.waitingForTurnOrder')}</h2>
        </div>
      </section>
    )
  }

  function play(cardInstance: CardInstance) {
    const targetPlayerId = targetsByCard[cardInstance.instanceId]

    if (cardNeedsTarget(cardInstance.card) && !targetPlayerId) {
      setSelectionError(translate('gameplay.chooseTargetBeforePlaying'))
      return
    }

    setSelectionError(null)
    onPlayCard(cardInstance.instanceId, targetPlayerId)
  }

  if (session.currentCard) {
    const owner = playerById(session, session.currentCard.playerId)
    const target = playerById(session, session.currentCard.targetPlayerId)

    return (
      <section className="screen gameplay-screen" aria-labelledby="resolution-title">
        <GameStatusPanel session={session} lastRoll={lastRoll} onRollDie={onRollDie} onFinish={onFinish} />

        <div className="play-layout">
          <section className="current-card-panel">
            <p className="eyebrow">{translate('gameplay.currentCard')}</p>
            <h2 id="resolution-title">{session.currentCard.card.title}</h2>
            <p className="card-text">{session.currentCard.card.text}</p>

            <div className="meta-grid">
              <span>{translate(`game.cardType.${session.currentCard.card.type}`)}</span>
              <span>{translate(`game.spice.${session.currentCard.card.spiceLevel}`)}</span>
              <span>{translate(`game.target.${session.currentCard.card.target}`)}</span>
              <span>{translate('gameplay.ap', { count: session.currentCard.card.actionPointCost })}</span>
            </div>

            <p className="resolution-line">
              {translate('gameplay.resolutionLine', {
                owner: owner?.nickname ?? translate('gameplay.playerFallback'),
                resolution: target
                  ? translate('gameplay.resolutionWithTarget', { target: target.nickname })
                  : translate('gameplay.isResolvingCard'),
              })}
            </p>

            <div className="action-row">
              <button className="primary-action" disabled={busy} type="button" onClick={onCompleteCurrentCard}>
                {translate('gameplay.complete')}
              </button>
              <button className="refuse-action" disabled={busy} type="button" onClick={onRefuseCurrentCard}>
                {translate('gameplay.refuse')}
              </button>
            </div>
          </section>

          <ScorePanel rows={score} />
        </div>
      </section>
    )
  }

  if (!handRevealed) {
    return (
      <section className="screen privacy-screen" aria-labelledby="privacy-title">
        <GameStatusPanel session={session} lastRoll={lastRoll} onRollDie={onRollDie} onFinish={onFinish} />

        <div className="privacy-panel">
          <p className="eyebrow">{translate('gameplay.nextTurn')}</p>
          <h2 id="privacy-title">{translate('gameplay.passToPlayer', { name: player.nickname })}</h2>
          <p>{translate('gameplay.onlyActivePlayerHandShown')}</p>
          <button className="primary-action" disabled={busy} type="button" onClick={onRevealHand}>
            {translate('gameplay.revealHand')}
          </button>
        </div>
      </section>
    )
  }

  return (
    <section className="screen gameplay-screen" aria-labelledby="gameplay-title">
      <GameStatusPanel session={session} lastRoll={lastRoll} onRollDie={onRollDie} onFinish={onFinish} />

      <div className="play-layout">
        <section aria-labelledby="gameplay-title">
          <div className="panel-heading">
            <p className="eyebrow">{translate('gameplay.currentPlayer')}</p>
            <h2 id="gameplay-title">{player.nickname}</h2>
          </div>

          {selectionError ? (
            <p className="inline-error" role="status">
              {selectionError}
            </p>
          ) : null}

          <div className="hand-grid">
            {player.hand.map((cardInstance) => {
              const eligibleTargets = eligibleTargetsForCard(session, cardInstance.card, player.id)
              const needsTarget = cardNeedsTarget(cardInstance.card)
              const cannotTarget = needsTarget && eligibleTargets.length === 0

              return (
                <article className="game-card" key={cardInstance.instanceId}>
                  <div className="game-card__art" aria-hidden="true">
                    <span>{cardInstance.card.actionPointCost}</span>
                  </div>
                  <div className="game-card__body">
                    <div>
                      <p className="eyebrow">{translate(`game.cardType.${cardInstance.card.type}`)}</p>
                      <h3>{cardInstance.card.title}</h3>
                    </div>
                    <p className="card-text">{cardInstance.card.text}</p>

                    <div className="meta-grid">
                      <span>{translate(`game.spice.${cardInstance.card.spiceLevel}`)}</span>
                      <span>{translate(`game.target.${cardInstance.card.target}`)}</span>
                      {cardInstance.card.requiresProps ? <span>{translate('setup.allowProps')}</span> : null}
                    </div>

                    {cardInstance.card.boundaries.length > 0 ? (
                      <div className="chip-row">
                        {cardInstance.card.boundaries.map((boundary) => (
                          <span className="chip" key={boundary}>
                            {translate(`game.boundary.${boundary}`)}
                          </span>
                        ))}
                      </div>
                    ) : null}

                    {needsTarget ? (
                      <label className="field">
                        <span>{translate('gameplay.target')}</span>
                        <select
                          value={targetsByCard[cardInstance.instanceId] ?? ''}
                          onChange={(event) =>
                            setTargetsByCard((current) => ({
                              ...current,
                              [cardInstance.instanceId]: event.target.value,
                            }))
                          }
                        >
                          <option value="">{translate('gameplay.choosePlayer')}</option>
                          {eligibleTargets.map((target) => (
                            <option key={target.id} value={target.id}>
                              {target.nickname}
                            </option>
                          ))}
                        </select>
                      </label>
                    ) : null}

                    <button
                      className="secondary-action"
                      disabled={busy || cannotTarget}
                      type="button"
                      onClick={() => play(cardInstance)}
                    >
                      {translate('gameplay.playCard')}
                    </button>
                  </div>
                </article>
              )
            })}
          </div>
        </section>

        <ScorePanel rows={score} />
      </div>
    </section>
  )
}

type GameStatusPanelProps = {
  session: GameSession
  lastRoll: DiceRollResource | null
  onRollDie: () => void
  onFinish: () => void
}

function GameStatusPanel({ session, lastRoll, onRollDie, onFinish }: GameStatusPanelProps) {
  const { t: translate } = useTranslation()

  return (
    <section className="status-strip" aria-label={translate('gameplay.sessionStatus')}>
      <div>
        <span>{translate('gameplay.round')}</span>
        <strong>{session.currentRound}</strong>
      </div>
      <div>
        <span>{translate('gameplay.atmosphere')}</span>
        <strong>{session.atmosphereLevel}/5</strong>
      </div>
      <div>
        <span>{translate('gameplay.spice')}</span>
        <strong>{translate(`game.spice.${session.currentSpiceLevel}`)}</strong>
      </div>
      <div>
        <span>{translate('gameplay.die')}</span>
        <strong>{lastRoll ? lastRoll.value : '-'}</strong>
      </div>
      <button className="quiet-action" type="button" onClick={onRollDie}>
        {translate('gameplay.rollDie')}
      </button>
      <button className="quiet-action" type="button" onClick={onFinish}>
        {translate('gameplay.finish')}
      </button>
    </section>
  )
}

type ScorePanelProps = {
  rows: Array<ReturnType<typeof scoreRows>[number]>
}

function ScorePanel({ rows }: ScorePanelProps) {
  const { t: translate } = useTranslation()

  return (
    <aside className="score-panel" aria-labelledby="score-title">
      <p className="eyebrow">{translate('gameplay.score')}</p>
      <h3 id="score-title">{translate('gameplay.table')}</h3>
      <ol>
        {rows.map(({ player, points }) => (
          <li key={player.id}>
            <span>{player.nickname}</span>
            <strong>{points}</strong>
          </li>
        ))}
      </ol>
    </aside>
  )
}
