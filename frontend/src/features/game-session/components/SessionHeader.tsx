import { useTranslation } from 'react-i18next'
import type { GameSession } from '../model/gameSession'
import type { DataSource } from '../hooks/useGameSessionController'

type SessionHeaderProps = {
  source: DataSource
  onSourceChange: (source: DataSource) => void
  session: GameSession | null
}

export function SessionHeader({ source, onSourceChange, session }: SessionHeaderProps) {
  const { t: translate } = useTranslation()

  return (
    <header className="topbar">
      <div className="brand-lockup">
        <img alt="" className="brand-mark" src="/icon.svg" />
        <div>
          <p className="eyebrow">{translate('header.brand')}</p>
          <h1>{session?.status === 'finished' ? translate('header.results') : translate('header.oneDeviceSession')}</h1>
        </div>
      </div>

      <div className="source-switch" role="radiogroup" aria-label={translate('header.dataSource')}>
        <button
          aria-checked={source === 'mock'}
          className={source === 'mock' ? 'source-switch__option is-active' : 'source-switch__option'}
          role="radio"
          type="button"
          onClick={() => onSourceChange('mock')}
        >
          {translate('common.local')}
        </button>
        <button
          aria-checked={source === 'api'}
          className={source === 'api' ? 'source-switch__option is-active' : 'source-switch__option'}
          role="radio"
          type="button"
          onClick={() => onSourceChange('api')}
        >
          {translate('common.api')}
        </button>
      </div>
    </header>
  )
}
