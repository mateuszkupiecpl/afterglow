import type { GameSession } from '../../game-session/model/gameSession'
import { scoreRows } from '../model/gameplaySelectors'

type ResultsViewProps = {
  session: GameSession
  onNewSession: () => void
}

export function ResultsView({ session, onNewSession }: ResultsViewProps) {
  const rows = scoreRows(session)
  const winner = rows[0]

  return (
    <section className="screen results-screen" aria-labelledby="results-title">
      <div className="result-hero">
        <p className="eyebrow">Final score</p>
        <h2 id="results-title">{winner ? winner.player.nickname : 'Session complete'}</h2>
        <p>{winner ? `${winner.points} points` : 'No scores were recorded.'}</p>
      </div>

      <div className="flow-panel">
        <ol className="result-list">
          {rows.map(({ player, points }) => (
            <li key={player.id}>
              <span>{player.nickname}</span>
              <strong>{points}</strong>
            </li>
          ))}
        </ol>

        <dl className="summary-grid">
          <div>
            <dt>Rounds</dt>
            <dd>{session.currentRound}</dd>
          </div>
          <div>
            <dt>Atmosphere</dt>
            <dd>{session.atmosphereLevel}/5</dd>
          </div>
          <div>
            <dt>Players</dt>
            <dd>{session.players.length}</dd>
          </div>
        </dl>

        <button className="primary-action" type="button" onClick={onNewSession}>
          New session
        </button>
      </div>
    </section>
  )
}
