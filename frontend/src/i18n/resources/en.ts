import { apiMessages } from '../../api/messages'
import { gameFlowMessages } from '../../features/game-flow/messages'
import { gameSessionMessages } from '../../features/game-session/messages'
import { playersMessages } from '../../features/players/messages'
import { settingsMessages } from '../../features/settings/messages'

export const en = {
  ...apiMessages,
  ...gameSessionMessages,
  ...settingsMessages,
  ...playersMessages,
  ...gameFlowMessages,
  errors: {
    ...apiMessages.errors,
    ...gameSessionMessages.errors,
  },
  game: {
    ...gameSessionMessages.game,
    ...settingsMessages.game,
  },
}
