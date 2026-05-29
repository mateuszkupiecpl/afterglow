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
  const { t } = useTranslation()
  const player = currentPlayer(session)
  const [targetsByCard, setTargetsByCard] = useState<Record<string, string>>({})
  const [selectionError, setSelectionError] = useState<string | null>(null)
  const score = useMemo(() => scoreRows(session), [session])

  if (!player) {
    return (
      <section className="screen">
        <div className="flow-panel">
          <h2>{t('gameplay.waitingForTurnOrder')}</h2>
        </div>
      </section>
    )
  }

  function play(cardInstance: CardInstance) {
    const targetPlayerId = targetsByCard[cardInstance.instanceId]

    if (cardNeedsTarget(cardInstance.card) && !targetPlayerId) {
      setSelectionError(t('gameplay.chooseTargetBeforePlaying'))
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
            <p className="eyebrow">{t('gameplay.currentCard')}</p>
            <h2 id="resolution-title">{session.currentCard.card.title}</h2>
            <p className="card-text">{session.currentCard.card.text}</p>

            <div className="meta-grid">
              <span>{t(`game.cardType.${session.currentCard.card.type}`)}</span>
              <span>{t(`game.spice.${session.currentCard.card.spiceLevel}`)}</span>
              <span>{t(`game.target.${session.currentCard.card.target}`)}</span>
              <span>{t('gameplay.ap', { count: session.currentCard.card.actionPointCost })}</span>
            </div>

            <p className="resolution-line">
              {t('gameplay.resolutionLine', {
                owner: owner?.nickname ?? t('gameplay.playerFallback'),
                resolution: target
                  ? t('gameplay.resolutionWithTarget', { target: target.nickname })
                  : t('gameplay.isResolvingCard'),
              })}
            </p>

            <div className="action-row">
              <button className="primary-action" disabled={busy} type="button" onClick={onCompleteCurrentCard}>
                {t('gameplay.complete')}
              </button>
              <button className="refuse-action" disabled={busy} type="button" onClick={onRefuseCurrentCard}>
                {t('gameplay.refuse')}
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
          <p className="eyebrow">{t('gameplay.nextTurn')}</p>
          <h2 id="privacy-title">{t('gameplay.passToPlayer', { name: player.nickname })}</h2>
          <p>{t('gameplay.onlyActivePlayerHandShown')}</p>
          <button className="primary-action" disabled={busy} type="button" onClick={onRevealHand}>
            {t('gameplay.revealHand')}
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
            <p className="eyebrow">{t('gameplay.currentPlayer')}</p>
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
                      <p className="eyebrow">{t(`game.cardType.${cardInstance.card.type}`)}</p>
                      <h3>{cardInstance.card.title}</h3>
                    </div>
                    <p className="card-text">{cardInstance.card.text}</p>

                    <div className="meta-grid">
                      <span>{t(`game.spice.${cardInstance.card.spiceLevel}`)}</span>
                      <span>{t(`game.target.${cardInstance.card.target}`)}</span>
                      {cardInstance.card.requiresProps ? <span>{t('setup.allowProps')}</span> : null}
                    </div>

                    {cardInstance.card.boundaries.length > 0 ? (
                      <div className="chip-row">
                        {cardInstance.card.boundaries.map((boundary) => (
                          <span className="chip" key={boundary}>
                            {t(`game.boundary.${boundary}`)}
                          </span>
                        ))}
                      </div>
                    ) : null}

                    {needsTarget ? (
                      <label className="field">
                        <span>{t('gameplay.target')}</span>
                        <select
                          value={targetsByCard[cardInstance.instanceId] ?? ''}
                          onChange={(event) =>
                            setTargetsByCard((current) => ({
                              ...current,
                              [cardInstance.instanceId]: event.target.value,
                            }))
                          }
                        >
                          <option value="">{t('gameplay.choosePlayer')}</option>
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
                      {t('gameplay.playCard')}
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
  const { t } = useTranslation()

  return (
    <section className="status-strip" aria-label={t('gameplay.sessionStatus')}>
      <div>
        <span>{t('gameplay.round')}</span>
        <strong>{session.currentRound}</strong>
      </div>
      <div>
        <span>{t('gameplay.atmosphere')}</span>
        <strong>{session.atmosphereLevel}/5</strong>
      </div>
      <div>
        <span>{t('gameplay.spice')}</span>
        <strong>{t(`game.spice.${session.currentSpiceLevel}`)}</strong>
      </div>
      <div>
        <span>{t('gameplay.die')}</span>
        <strong>{lastRoll ? lastRoll.value : '-'}</strong>
      </div>
      <button className="quiet-action" type="button" onClick={onRollDie}>
        {t('gameplay.rollDie')}
      </button>
      <button className="quiet-action" type="button" onClick={onFinish}>
        {t('gameplay.finish')}
      </button>
    </section>
  )
}

type ScorePanelProps = {
  rows: Array<ReturnType<typeof scoreRows>[number]>
}

function ScorePanel({ rows }: ScorePanelProps) {
  const { t } = useTranslation()

  return (
    <aside className="score-panel" aria-labelledby="score-title">
      <p className="eyebrow">{t('gameplay.score')}</p>
      <h3 id="score-title">{t('gameplay.table')}</h3>
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
