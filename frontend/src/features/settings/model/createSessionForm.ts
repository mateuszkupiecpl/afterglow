import type { BoundaryCode, GameMode, PaceCode, SpiceLevel } from '../../game-session/model/gameSession'

export type CreateSessionForm = {
  hostNickname: string
  confirmedAdult: boolean
  mode: GameMode
  startSpiceLevel: SpiceLevel
  maxSpiceLevel: SpiceLevel
  pace: PaceCode
  allowProps: boolean
  allowPairTasks: boolean
  allowGroupTasks: boolean
  boundaries: BoundaryCode[]
}
