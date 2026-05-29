import type { BoundaryCode } from '../../game-session/model/gameSession'

export type AddPlayerForm = {
  nickname: string
  confirmedAdult: boolean
  boundaries: BoundaryCode[]
}
