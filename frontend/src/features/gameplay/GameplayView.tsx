import { useMemo, useState } from 'react'
import {
  boundaryOptions,
  cardTypeLabels,
  spiceLabels,
  targetLabels,
  type CardInstance,
  type GameSession,
} from '../../domain/gameSession'
import {
  cardNeedsTarget,
  currentPlayer,
  eligibleTargetsForCard,
  playerById,
  scoreRows,
} from '../../domain/gameplaySelectors'
import type { DiceRollResource } from '../../api/gameSessionResources'

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
  const player = currentPlayer(session)
  const [targetsByCard, setTargetsByCard] = useState<Record<string, string>>({})
  const [selectionError, setSelectionError] = useState<string | null>(null)
  const score = useMemo(() => scoreRows(session), [session])

  if (!player) {
    return (
      <section className="screen">
        <div className="flow-panel">
          <h2>Waiting for turn order.</h2>
        </div>
      </section>
    )
  }

  function play(cardInstance: CardInstance) {
    const targetPlayerId = targetsByCard[cardInstance.instanceId]

    if (cardNeedsTarget(cardInstance.card) && !targetPlayerId) {
      setSelectionError('Choose a target before playing that card.')
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
            <p className="eyebrow">Current card</p>
            <h2 id="resolution-title">{session.currentCard.card.title}</h2>
            <p className="card-text">{session.currentCard.card.text}</p>

            <div className="meta-grid">
              <span>{cardTypeLabels[session.currentCard.card.type]}</span>
              <span>{spiceLabels[session.currentCard.card.spiceLevel]}</span>
              <span>{targetLabels[session.currentCard.card.target]}</span>
              <span>{session.currentCard.card.actionPointCost} AP</span>
            </div>

            <p className="resolution-line">
              {owner?.nickname ?? 'Player'} {target ? `chose ${target.nickname}` : 'is resolving the card'}.
            </p>

            <div className="action-row">
              <button className="primary-action" disabled={busy} type="button" onClick={onCompleteCurrentCard}>
                Complete
              </button>
              <button className="refuse-action" disabled={busy} type="button" onClick={onRefuseCurrentCard}>
                Refuse
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
          <p className="eyebrow">Next turn</p>
          <h2 id="privacy-title">Pass to {player.nickname}.</h2>
          <p>Only the active player hand will be shown.</p>
          <button className="primary-action" disabled={busy} type="button" onClick={onRevealHand}>
            Reveal hand
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
            <p className="eyebrow">Current player</p>
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
                      <p className="eyebrow">{cardTypeLabels[cardInstance.card.type]}</p>
                      <h3>{cardInstance.card.title}</h3>
                    </div>
                    <p className="card-text">{cardInstance.card.text}</p>

                    <div className="meta-grid">
                      <span>{spiceLabels[cardInstance.card.spiceLevel]}</span>
                      <span>{targetLabels[cardInstance.card.target]}</span>
                      {cardInstance.card.requiresProps ? <span>Props</span> : null}
                    </div>

                    {cardInstance.card.boundaries.length > 0 ? (
                      <div className="chip-row">
                        {cardInstance.card.boundaries.map((boundary) => (
                          <span className="chip" key={boundary}>
                            {boundaryOptions.find((option) => option.value === boundary)?.label ?? boundary}
                          </span>
                        ))}
                      </div>
                    ) : null}

                    {needsTarget ? (
                      <label className="field">
                        <span>Target</span>
                        <select
                          value={targetsByCard[cardInstance.instanceId] ?? ''}
                          onChange={(event) =>
                            setTargetsByCard((current) => ({
                              ...current,
                              [cardInstance.instanceId]: event.target.value,
                            }))
                          }
                        >
                          <option value="">Choose player</option>
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
                      Play card
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
  return (
    <section className="status-strip" aria-label="Session status">
      <div>
        <span>Round</span>
        <strong>{session.currentRound}</strong>
      </div>
      <div>
        <span>Atmosphere</span>
        <strong>{session.atmosphereLevel}/5</strong>
      </div>
      <div>
        <span>Spice</span>
        <strong>{spiceLabels[session.currentSpiceLevel]}</strong>
      </div>
      <div>
        <span>Die</span>
        <strong>{lastRoll ? lastRoll.value : '-'}</strong>
      </div>
      <button className="quiet-action" type="button" onClick={onRollDie}>
        Roll die
      </button>
      <button className="quiet-action" type="button" onClick={onFinish}>
        Finish
      </button>
    </section>
  )
}

type ScorePanelProps = {
  rows: Array<ReturnType<typeof scoreRows>[number]>
}

function ScorePanel({ rows }: ScorePanelProps) {
  return (
    <aside className="score-panel" aria-labelledby="score-title">
      <p className="eyebrow">Score</p>
      <h3 id="score-title">Table</h3>
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
