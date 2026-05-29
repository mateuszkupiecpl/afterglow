import { useTranslation } from 'react-i18next'
import type { GameSession } from '../../game-session/model/gameSession'
import { scoreRows } from '../model/gameplaySelectors'

type ResultsViewProps = {
  session: GameSession
  onNewSession: () => void
}

export function ResultsView({ session, onNewSession }: ResultsViewProps) {
  const { t } = useTranslation()
  const rows = scoreRows(session)
  const winner = rows[0]

  return (
    <section className="screen results-screen" aria-labelledby="results-title">
      <div className="result-hero">
        <p className="eyebrow">{t('results.finalScore')}</p>
        <h2 id="results-title">{winner ? winner.player.nickname : t('results.sessionComplete')}</h2>
        <p>{winner ? t('common.points', { count: winner.points }) : t('results.noScores')}</p>
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
            <dt>{t('results.rounds')}</dt>
            <dd>{session.currentRound}</dd>
          </div>
          <div>
            <dt>{t('results.atmosphere')}</dt>
            <dd>{session.atmosphereLevel}/5</dd>
          </div>
          <div>
            <dt>{t('results.players')}</dt>
            <dd>{session.players.length}</dd>
          </div>
        </dl>

        <button className="primary-action" type="button" onClick={onNewSession}>
          {t('results.newSession')}
        </button>
      </div>
    </section>
  )
}
