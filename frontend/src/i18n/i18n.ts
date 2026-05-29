import i18n from 'i18next'
import { initReactI18next } from 'react-i18next'
import { en } from './resources/en'
import { pl } from './resources/pl'

type SupportedLanguage = 'en' | 'pl'

function initialLanguage(): SupportedLanguage {
  const savedLanguage = window.localStorage.getItem('afterglow.language')

  if (savedLanguage === 'pl' || savedLanguage === 'en') {
    return savedLanguage
  }

  return navigator.language.toLowerCase().startsWith('pl') ? 'pl' : 'en'
}

void i18n.use(initReactI18next).init({
  fallbackLng: 'en',
  interpolation: {
    escapeValue: false,
  },
  lng: initialLanguage(),
  resources: {
    en: {
      translation: en,
    },
    pl: {
      translation: pl,
    },
  },
})

export default i18n
