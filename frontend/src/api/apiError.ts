import axios from 'axios'

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

  return error instanceof Error ? error : new Error('The API request failed.')
}
