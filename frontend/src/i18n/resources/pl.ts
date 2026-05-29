import { apiMessagesPl } from '../../api/messages.pl.ts'
import { gameFlowMessagesPl } from '../../features/game-flow/messages.pl.ts'
import { gameSessionMessagesPl } from '../../features/game-session/messages.pl.ts'
import { playersMessagesPl } from '../../features/players/messages.pl.ts'
import { settingsMessagesPl } from '../../features/settings/messages.pl.ts'

export const pl = {
  ...apiMessagesPl,
  ...gameSessionMessagesPl,
  ...settingsMessagesPl,
  ...playersMessagesPl,
  ...gameFlowMessagesPl,
  errors: {
    ...apiMessagesPl.errors,
    ...gameSessionMessagesPl.errors,
  },
  game: {
    ...gameSessionMessagesPl.game,
    ...settingsMessagesPl.game,
  },
}
