import axios from 'axios'

export const apiBaseUrl = import.meta.env.VITE_API_BASE_URL ?? '/api'

export const axiosClient = axios.create({
  headers: {
    Accept: 'application/hal+json, application/json',
  },
})
