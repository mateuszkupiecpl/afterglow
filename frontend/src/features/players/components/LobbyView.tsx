import { useState } from 'react'
import { useTranslation } from 'react-i18next'
import { BoundaryPicker } from '../../settings/components/BoundaryPicker'
import type { GameSession } from '../../game-session/model/gameSession'
import { canStartSession } from '../../game-flow/model/gameplaySelectors'
import type { AddPlayerForm } from '../model/addPlayerForm'
import * as React from "react";
import './LobbyView.css'

type LobbyViewProps = {
  session: GameSession
  busy: boolean
  onAddPlayer: (form: AddPlayerForm) => void
  onStart: () => void
}

export function LobbyView({ session, busy, onAddPlayer, onStart }: LobbyViewProps) {
  const { t: translate } = useTranslation()
  const [form, setForm] = useState<AddPlayerForm>({
    nickname: '',
    confirmedAdult: false,
    boundaries: [],
  })

  function submit(event: React.SubmitEvent<HTMLFormElement>) {
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
        <p className="eyebrow">{translate('lobby.sessionCode')}</p>
        <h2 id="lobby-title">{session.code}</h2>
        <p>{translate('lobby.playerCount', { current: session.players.length, max: session.settings.maxPlayers })}</p>
      </div>

      <div className="lobby-layout">
        <section className="flow-panel" aria-labelledby="players-title">
          <div className="panel-heading">
            <p className="eyebrow">{translate('lobby.setup')}</p>
            <h3 id="players-title">{translate('lobby.players')}</h3>
          </div>

          <ul className="player-list">
            {session.players.map((player) => (
              <li className="player-row" key={player.id}>
                <div>
                  <strong>{player.nickname}</strong>
                  <span>{player.host ? translate('lobby.host') : translate('common.player')}</span>
                </div>
                <div className="chip-row">
                  {player.comfortProfile.boundaries.length === 0 ? (
                    <span className="chip chip--quiet">{translate('lobby.openWithinRoomLimits')}</span>
                  ) : (
                    player.comfortProfile.boundaries.map((boundary) => (
                      <span className="chip" key={boundary}>
                        {translate(`game.boundary.${boundary}`)}
                      </span>
                    ))
                  )}
                </div>
              </li>
            ))}
          </ul>

          <button className="primary-action" disabled={!canStart} type="button" onClick={onStart}>
            {translate('lobby.startGame')}
          </button>
        </section>

        <form className="flow-panel" onSubmit={submit}>
          <div className="panel-heading">
            <p className="eyebrow">{translate('lobby.addPlayer')}</p>
            <h3>{translate('lobby.consentFirst')}</h3>
          </div>

          <label className="field">
            <span>{translate('lobby.nickname')}</span>
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
            <span>{translate('lobby.confirmAdult')}</span>
          </label>

          <BoundaryPicker
            compact
            selected={form.boundaries}
            onChange={(boundaries) => setForm((current) => ({ ...current, boundaries }))}
          />

          <button className="secondary-action" disabled={!canAddPlayer} type="submit">
            {translate('lobby.addPlayer')}
          </button>
        </form>
      </div>
    </section>
  )
}
