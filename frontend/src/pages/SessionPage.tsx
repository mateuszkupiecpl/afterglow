import { GameplayView } from '../features/game-flow/components/GameplayView'
import { ResultsView } from '../features/game-flow/components/ResultsView'
import { SessionHeader } from '../features/game-session/components/SessionHeader'
import { useGameSessionController } from '../features/game-session/hooks/useGameSessionController'
import { LobbyView } from '../features/players/components/LobbyView'
import { SetupView } from '../features/settings/components/SetupView'
import './SessionPage.css'

export function SessionPage() {
  const gameSession = useGameSessionController()
  const { session } = gameSession

  return (
    <main className="app-shell">
      <SessionHeader session={session} />

      {gameSession.error ? (
        <div className="error-banner" role="status">
          {gameSession.error}
        </div>
      ) : null}

      {!session ? <SetupView busy={gameSession.busy} onCreateSession={gameSession.createSession} /> : null}

      {session?.status === 'setup' ? (
        <LobbyView
          session={session}
          busy={gameSession.busy}
          onAddPlayer={gameSession.addPlayer}
          onStart={gameSession.startSession}
        />
      ) : null}

      {session?.status === 'in_progress' ? (
        <GameplayView
          busy={gameSession.busy}
          handRevealed={gameSession.handRevealed}
          lastRoll={gameSession.lastRoll}
          session={session}
          onCompleteCurrentCard={gameSession.completeCurrentCard}
          onFinish={gameSession.finishSession}
          onPlayCard={gameSession.playCard}
          onRefuseCurrentCard={gameSession.refuseCurrentCard}
          onRevealHand={gameSession.revealHand}
          onRollDie={gameSession.rollDie}
        />
      ) : null}

      {session?.status === 'finished' ? (
        <ResultsView session={session} onNewSession={gameSession.resetSession} />
      ) : null}
    </main>
  )
}
