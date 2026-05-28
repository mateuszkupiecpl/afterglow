import { useState } from 'react'
import { BoundaryPicker } from '../../components/BoundaryPicker'
import { boundaryOptions, type BoundaryCode, type GameSession } from '../../domain/gameSession'
import { canStartSession } from '../../domain/gameplaySelectors'

export type AddPlayerForm = {
  nickname: string
  confirmedAdult: boolean
  boundaries: BoundaryCode[]
}

type LobbyViewProps = {
  session: GameSession
  busy: boolean
  onAddPlayer: (form: AddPlayerForm) => void
  onStart: () => void
}

export function LobbyView({ session, busy, onAddPlayer, onStart }: LobbyViewProps) {
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
        <p className="eyebrow">Session code</p>
        <h2 id="lobby-title">{session.code}</h2>
        <p>
          {session.players.length}/{session.settings.maxPlayers} players
        </p>
      </div>

      <div className="lobby-layout">
        <section className="flow-panel" aria-labelledby="players-title">
          <div className="panel-heading">
            <p className="eyebrow">Setup</p>
            <h3 id="players-title">Players</h3>
          </div>

          <ul className="player-list">
            {session.players.map((player) => (
              <li className="player-row" key={player.id}>
                <div>
                  <strong>{player.nickname}</strong>
                  <span>{player.host ? 'Host' : 'Player'}</span>
                </div>
                <div className="chip-row">
                  {player.comfortProfile.boundaries.length === 0 ? (
                    <span className="chip chip--quiet">Open within room limits</span>
                  ) : (
                    player.comfortProfile.boundaries.map((boundary) => (
                      <span className="chip" key={boundary}>
                        {boundaryOptions.find((option) => option.value === boundary)?.label ?? boundary}
                      </span>
                    ))
                  )}
                </div>
              </li>
            ))}
          </ul>

          <button className="primary-action" disabled={!canStart} type="button" onClick={onStart}>
            Start game
          </button>
        </section>

        <form className="flow-panel" onSubmit={submit}>
          <div className="panel-heading">
            <p className="eyebrow">Add player</p>
            <h3>Consent first</h3>
          </div>

          <label className="field">
            <span>Nickname</span>
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
            <span>This player confirms 18+ and consents to play.</span>
          </label>

          <BoundaryPicker
            compact
            selected={form.boundaries}
            onChange={(boundaries) => setForm((current) => ({ ...current, boundaries }))}
          />

          <button className="secondary-action" disabled={!canAddPlayer} type="submit">
            Add player
          </button>
        </form>
      </div>
    </section>
  )
}
