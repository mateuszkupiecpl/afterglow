import { useTranslation } from 'react-i18next'
import type { GameSession } from '../model/gameSession'
import type { DataSource } from '../hooks/useGameSessionController'

type SessionHeaderProps = {
  source: DataSource
  onSourceChange: (source: DataSource) => void
  session: GameSession | null
}

export function SessionHeader({ source, onSourceChange, session }: SessionHeaderProps) {
  const { i18n, t: translate } = useTranslation()

  function changeLanguage(language: 'en' | 'pl') {
    window.localStorage.setItem('afterglow.language', language)
    void i18n.changeLanguage(language)
  }

  return (
    <header className="topbar">
      <div className="brand-lockup">
        <img alt="" className="brand-mark" src="/icon.svg" />
        <div>
          <p className="eyebrow">{translate('header.brand')}</p>
          <h1>{session?.status === 'finished' ? translate('header.results') : translate('header.oneDeviceSession')}</h1>
        </div>
      </div>

      <div className="topbar-controls">
        <div className="source-switch" role="radiogroup" aria-label={translate('header.language')}>
          <button
            aria-checked={i18n.resolvedLanguage === 'en'}
            className={i18n.resolvedLanguage === 'en' ? 'source-switch__option is-active' : 'source-switch__option'}
            role="radio"
            type="button"
            onClick={() => changeLanguage('en')}
          >
            {translate('common.english')}
          </button>
          <button
            aria-checked={i18n.resolvedLanguage === 'pl'}
            className={i18n.resolvedLanguage === 'pl' ? 'source-switch__option is-active' : 'source-switch__option'}
            role="radio"
            type="button"
            onClick={() => changeLanguage('pl')}
          >
            {translate('common.polish')}
          </button>
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
      </div>
    </header>
  )
}
