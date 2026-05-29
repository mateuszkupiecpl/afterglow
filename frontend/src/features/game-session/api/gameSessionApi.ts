import { apiBaseUrl, axiosClient } from '../../../api/axiosClient'
import { normalizeApiError } from '../../../api/apiError'
import { requireLinkHref, requestUrlFromHref } from '../../../api/halClient'
import type {
  AddPlayerRequest,
  CreateGameSessionRequest,
  DiceRollRequest,
  DiceRollResource,
  GameSessionResource,
  HomeResource,
  PlayCardRequest,
  ResolveCurrentCardRequest,
} from './gameSessionResources'

export type GameSessionApi = {
  createSession(request: CreateGameSessionRequest): Promise<GameSessionResource>
  findByCode(code: string): Promise<GameSessionResource>
  addPlayer(session: GameSessionResource, request: AddPlayerRequest): Promise<GameSessionResource>
  start(session: GameSessionResource): Promise<GameSessionResource>
  playCard(session: GameSessionResource, request: PlayCardRequest): Promise<GameSessionResource>
  completeCurrentCard(session: GameSessionResource, request: ResolveCurrentCardRequest): Promise<GameSessionResource>
  refuseCurrentCard(session: GameSessionResource, request: ResolveCurrentCardRequest): Promise<GameSessionResource>
  rollDie(session: GameSessionResource, request?: DiceRollRequest): Promise<DiceRollResource>
  finish(session: GameSessionResource): Promise<GameSessionResource>
}

let homeResource: Promise<HomeResource> | null = null

export const backendGameSessionApi: GameSessionApi = {
  async createSession(request) {
    const home = await loadHome()
    const href = requireLinkHref(home, 'createGameSession')
    return postSession(href, request)
  },

  async findByCode(code) {
    const home = await loadHome()
    const href = requireLinkHref(home, 'findGameSessionByCode', { code })
    return getSession(href)
  },

  async addPlayer(session, request) {
    return postSession(requireLinkHref(session, 'addPlayer'), request)
  },

  async start(session) {
    return postSession(requireLinkHref(session, 'start'))
  },

  async playCard(session, request) {
    return postSession(requireLinkHref(session, 'playCard'), request)
  },

  async completeCurrentCard(session, request) {
    return postSession(requireLinkHref(session, 'completeCurrentCard'), request)
  },

  async refuseCurrentCard(session, request) {
    return postSession(requireLinkHref(session, 'refuseCurrentCard'), request)
  },

  async rollDie(session, request) {
    return postResource<DiceRollResource>(requireLinkHref(session, 'rollDie'), request ?? {})
  },

  async finish(session) {
    return postSession(requireLinkHref(session, 'finish'))
  },
}

async function loadHome(): Promise<HomeResource> {
  homeResource ??= axiosClient
    .get<HomeResource>(apiBaseUrl)
    .then((response) => response.data)
    .catch((error: unknown) => {
      homeResource = null
      throw normalizeApiError(error)
    })

  return homeResource
}

async function getSession(href: string): Promise<GameSessionResource> {
  try {
    const response = await axiosClient.get<GameSessionResource>(requestUrlFromHref(href))
    return response.data
  } catch (error: unknown) {
    throw normalizeApiError(error)
  }
}

async function postSession(href: string, body?: unknown): Promise<GameSessionResource> {
  return postResource<GameSessionResource>(href, body)
}

async function postResource<T>(href: string, body?: unknown): Promise<T> {
  try {
    const response = await axiosClient.post<T>(requestUrlFromHref(href), body)
    return response.data
  } catch (error: unknown) {
    throw normalizeApiError(error)
  }
}
