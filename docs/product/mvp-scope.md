# MVP Scope

The MVP should test whether the core game loop is enjoyable, whether players
feel meaningful choice, and whether atmospheric interludes improve the session
instead of merely slowing it down.

## In Scope

- Create a game session.
- Add 2-8 players.
- Set player nicknames.
- Choose starting spice level.
- Choose maximum spice level.
- Choose game pace.
- Confirm basic 18+ status.
- Deal cards to players.
- Show a player's hand.
- Show current turn.
- Play cards.
- Draw cards.
- Basic virtual dice roll.
- Simple scoring.
- Refuse a task.
- Show results.
- End the game.

## MVP Card Types

- Question.
- Challenge.
- Interlude.
- Counter.
- Target change or redirect.
- Group card.

## Operating Model

- One phone.
- Local game state.
- Players pass the phone.
- No user accounts.
- No required backend, or mock API only.
- Deck stored locally as JSON or TypeScript fixtures.

## Out of Scope

- Public profiles.
- Public user-generated decks.
- Public deck catalog.
- Matchmaking.
- Chat.
- Payments.
- Native iOS app.
- Bluetooth multiplayer.
- Full card editor.
- Advanced animations.

## Later Stages

- Private card editor and IndexedDB deck storage.
- Offline deck downloads.
- Improved boundary settings.
- Java/Spring Boot backend.
- Game rooms with room code or invite link.
- WebSocket state synchronization.
- Optional accounts and paid decks after adult-content payment constraints are
  checked.
