import { useState } from 'react'
import { useTranslation } from 'react-i18next'
import { BoundaryPicker } from '../../settings/components/BoundaryPicker'
import type { GameSession } from '../../game-session/model/gameSession'
import { canStartSession } from '../../game-flow/model/gameplaySelectors'
import type { AddPlayerForm } from '../model/addPlayerForm'

type LobbyViewProps = {
  session: GameSession
  busy: boolean
  onAddPlayer: (form: AddPlayerForm) => void
  onStart: () => void
}

export function LobbyView({ session, busy, onAddPlayer, onStart }: LobbyViewProps) {
  const { t } = useTranslation()
  const [form, setForm] = useState<AddPlayerForm>({
    nickname: '',
    confirmedAdult: false,
    boundaries: [],
  })

  function submit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault()
    onAddPlayer({ ...form, nickname: form.nickname.trim() })
    setForm({ nickname: '', confirmedAdult: false, boundaries: [] })
  }

  const canAddPlayer =
    form.nickname.trim().length > 0 && form.confirmedAdult && session.players.length < session.settings.maxPlayers && !busy
  const canStart = canStartSession(session) && !busy

  return (
    <section className="screen lobby-screen" aria-labelledby="lobby-title">
      <div className="session-code-panel">
        <p className="eyebrow">{t('lobby.sessionCode')}</p>
        <h2 id="lobby-title">{session.code}</h2>
        <p>{t('lobby.playerCount', { current: session.players.length, max: session.settings.maxPlayers })}</p>
      </div>

      <div className="lobby-layout">
        <section className="flow-panel" aria-labelledby="players-title">
          <div className="panel-heading">
            <p className="eyebrow">{t('lobby.setup')}</p>
            <h3 id="players-title">{t('lobby.players')}</h3>
          </div>

          <ul className="player-list">
            {session.players.map((player) => (
              <li className="player-row" key={player.id}>
                <div>
                  <strong>{player.nickname}</strong>
                  <span>{player.host ? t('lobby.host') : t('common.player')}</span>
                </div>
                <div className="chip-row">
                  {player.comfortProfile.boundaries.length === 0 ? (
                    <span className="chip chip--quiet">{t('lobby.openWithinRoomLimits')}</span>
                  ) : (
                    player.comfortProfile.boundaries.map((boundary) => (
                      <span className="chip" key={boundary}>
                        {t(`game.boundary.${boundary}`)}
                      </span>
                    ))
                  )}
                </div>
              </li>
            ))}
          </ul>

          <button className="primary-action" disabled={!canStart} type="button" onClick={onStart}>
            {t('lobby.startGame')}
          </button>
        </section>

        <form className="flow-panel" onSubmit={submit}>
          <div className="panel-heading">
            <p className="eyebrow">{t('lobby.addPlayer')}</p>
            <h3>{t('lobby.consentFirst')}</h3>
          </div>

          <label className="field">
            <span>{t('lobby.nickname')}</span>
            <input
              autoComplete="off"
              maxLength={40}
              required
              value={form.nickname}
              onChange={(event) => setForm((current) => ({ ...current, nickname: event.target.value }))}
            />
          </label>

          <label className="confirm-row">
            <input
              required
              type="checkbox"
              checked={form.confirmedAdult}
              onChange={(event) => setForm((current) => ({ ...current, confirmedAdult: event.target.checked }))}
            />
            <span>{t('lobby.confirmAdult')}</span>
          </label>

          <BoundaryPicker
            compact
            selected={form.boundaries}
            onChange={(boundaries) => setForm((current) => ({ ...current, boundaries }))}
          />

          <button className="secondary-action" disabled={!canAddPlayer} type="submit">
            {t('lobby.addPlayer')}
          </button>
        </form>
      </div>
    </section>
  )
}
