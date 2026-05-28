import type { GameSession } from '../domain/gameSession'

type DataSource = 'mock' | 'api'

type SessionHeaderProps = {
  source: DataSource
  onSourceChange: (source: DataSource) => void
  session: GameSession | null
}

export function SessionHeader({ source, onSourceChange, session }: SessionHeaderProps) {
  return (
    <header className="topbar">
      <div className="brand-lockup">
        <img alt="" className="brand-mark" src="/icon.svg" />
        <div>
          <p className="eyebrow">Afterglow</p>
          <h1>{session?.status === 'finished' ? 'Results' : 'One-device session'}</h1>
        </div>
      </div>

      <div className="source-switch" role="radiogroup" aria-label="Data source">
        <button
          aria-checked={source === 'mock'}
          className={source === 'mock' ? 'source-switch__option is-active' : 'source-switch__option'}
          role="radio"
          type="button"
          onClick={() => onSourceChange('mock')}
        >
          Local
        </button>
        <button
          aria-checked={source === 'api'}
          className={source === 'api' ? 'source-switch__option is-active' : 'source-switch__option'}
          role="radio"
          type="button"
          onClick={() => onSourceChange('api')}
        >
          API
        </button>
      </div>
    </header>
  )
}
