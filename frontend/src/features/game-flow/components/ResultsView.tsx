import { useTranslation } from 'react-i18next'
import type { GameSession } from '../../game-session/model/gameSession'
import { scoreRows } from '../model/gameplaySelectors'
import './ResultsView.css'

type ResultsViewProps = {
  session: GameSession
  onNewSession: () => void
}

export function ResultsView({ session, onNewSession }: ResultsViewProps) {
  const { t: translate } = useTranslation()
  const rows = scoreRows(session)
  const winner = rows[0]

  return (
    <section className="screen results-screen" aria-labelledby="results-title">
      <div className="result-hero">
        <p className="eyebrow">{translate('results.finalScore')}</p>
        <h2 id="results-title">{winner ? winner.player.nickname : translate('results.sessionComplete')}</h2>
        <p>{winner ? translate('common.points', { count: winner.points }) : translate('results.noScores')}</p>
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
            <dt>{translate('results.rounds')}</dt>
            <dd>{session.currentRound}</dd>
          </div>
          <div>
            <dt>{translate('results.atmosphere')}</dt>
            <dd>{session.atmosphereLevel}/5</dd>
          </div>
          <div>
            <dt>{translate('results.players')}</dt>
            <dd>{session.players.length}</dd>
          </div>
        </dl>

        <button className="primary-action" type="button" onClick={onNewSession}>
          {translate('results.newSession')}
        </button>
      </div>
    </section>
  )
}
