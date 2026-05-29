# Game Design

This document captures the working game-design direction. MVP scope still takes
priority over richer later-stage mechanics.

## Product Shape

Afterglow is an adult social card game for 2-8 players. The game should feel
like a deliberate shared experience, not a random task generator.

The design pillars are:

- Choice: players hold several cards and choose what to play.
- Atmosphere: interludes, conversation, music, dance, massage, and group moments
  matter as much as direct card resolution.
- Progression: intensity rises gradually within configured limits.
- Consent: refusal is normal, and player boundaries are hard constraints.
- Interaction: players can react, counter, vote, redirect, rescue, and stay
  engaged outside their own turns.

## Current Mechanical Direction

Accepted for the working direction:

- Players have several cards in hand.
- Starting hand assumption: 4 cards.
- Hand limit assumption: 5 cards.
- Players draw after playing a card.

Strongly recommended, but still adjustable before implementation:

- 3 action points per turn.
- Reaction and counter cards.
- Atmosphere track.
- Group voting.
- Round events.

The first playable prototype may simplify these mechanics if that keeps the
local one-device loop moving.

## Basic Turn Loop

The likely full turn shape is:

1. Active player draws 1 card.
2. Active player receives action points, if action points are enabled.
3. Active player chooses a card from hand.
4. The card defines or asks for a target.
5. Other players may react if reaction cards are enabled.
6. The effect is resolved.
7. Points, atmosphere, spice level, or other state changes are applied.
8. The active player may take another action if rules allow.
9. The turn passes to the next player.

## Card Types

MVP card types:

- Question.
- Challenge.
- Interlude.
- Counter.
- Target change or redirect.
- Group card.

Later-stage card types may include:

- Fantasy or role-play.
- Prop.
- Event.
- Special action.
- Persistent status.
- Trap.
- Voting card.

Do not hardcode card content without metadata for type, spice level, target, and
boundaries.

## Targeting

Cards may target:

- Active player.
- Chosen player.
- Random player.
- Pair.
- Group.
- Everyone.
- Player to the left.
- Player to the right.

Targeting must respect consent and boundaries. If a card cannot legally target
the selected player or group, the app should select another legal target,
present a softer variant, or reject the play.

## Reactions

Reaction cards keep non-active players engaged.

Potential reaction types:

- Counter: cancel an effect.
- Reflect: return the effect to the player who played it.
- Redirect: change the target.
- Negotiate: soften the task.
- Rescue: let another player take over a task.
- Boost: strengthen an accepted task.

Reaction chains should stay short. A likely limit is one reaction per player or
at most three reactions against one card.

## Atmosphere Track

The atmosphere track represents shared session tension and progression.

Working levels:

1. Warmup.
2. Tension.
3. Courage.
4. Spicy.
5. Finale.

The track may increase after accepted tasks, stronger cards, group votes, or
round events. Exact progression rules are `TBD`.

## Dice

The app should support a simple virtual die.

Working model:

- 1-2: mild variant.
- 3-4: medium variant.
- 5-6: stronger variant.

Dice may affect task duration, intensity, number of participants, target choice,
or bonus points.

## Scoring

Simple scoring is in MVP scope, but the final victory condition is `TBD`.

Possible scoring events:

- Answering a question.
- Completing a challenge.
- Creative completion bonus.
- Dice bonus.
- Refusal consequence.
- Counter or reaction cost.

Scoring must not pressure players to violate boundaries.

## One-Device Play

MVP is local one-device play. Players pass the phone around.

The hidden-hand UX is still `TBD`. Possible approaches include a private hand
screen with a pass-device prompt, reveal/hide controls, or a simplified public
hand for the earliest prototype.

## Open Design Questions

- Final victory condition.
- Whether action points ship in MVP or after a simpler prototype.
- Exact MVP hand size.
- Whether round events belong in MVP or stage 2.
- Interlude cadence.
- Hidden-hand UX on one shared phone.
- Whether task timers are required in MVP.
