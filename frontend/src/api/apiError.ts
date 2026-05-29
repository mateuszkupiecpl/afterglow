import axios from 'axios'
import i18n from '../i18n/i18n'

type ApiErrorBody = {
  error?: string
  message?: string
}

export function normalizeApiError(error: unknown): Error {
  if (axios.isAxiosError<ApiErrorBody>(error)) {
    const body = error.response?.data
    const message = body?.message ?? body?.error ?? error.message
    return new Error(message)
  }

  return error instanceof Error ? error : new Error(i18n.t('api.errors.requestFailed'))
}
