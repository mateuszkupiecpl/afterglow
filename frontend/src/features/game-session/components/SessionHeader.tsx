import { useTranslation } from 'react-i18next'
import type { GameSession } from '../model/gameSession'
import type { DataSource } from '../hooks/useGameSessionController'

type SessionHeaderProps = {
  source: DataSource
  onSourceChange: (source: DataSource) => void
  session: GameSession | null
}

export function SessionHeader({ source, onSourceChange, session }: SessionHeaderProps) {
  const { t } = useTranslation()

  return (
    <header className="topbar">
      <div className="brand-lockup">
        <img alt="" className="brand-mark" src="/icon.svg" />
        <div>
          <p className="eyebrow">{t('header.brand')}</p>
          <h1>{session?.status === 'finished' ? t('header.results') : t('header.oneDeviceSession')}</h1>
        </div>
      </div>

      <div className="source-switch" role="radiogroup" aria-label={t('header.dataSource')}>
        <button
          aria-checked={source === 'mock'}
          className={source === 'mock' ? 'source-switch__option is-active' : 'source-switch__option'}
          role="radio"
          type="button"
          onClick={() => onSourceChange('mock')}
        >
          {t('common.local')}
        </button>
        <button
          aria-checked={source === 'api'}
          className={source === 'api' ? 'source-switch__option is-active' : 'source-switch__option'}
          role="radio"
          type="button"
          onClick={() => onSourceChange('api')}
        >
          {t('common.api')}
        </button>
      </div>
    </header>
  )
}
