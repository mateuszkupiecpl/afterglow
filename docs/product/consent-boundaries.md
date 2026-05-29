# Consent And Boundaries

Afterglow is for adults only. Consent, refusal, and player boundaries are hard
constraints, not flavor text.

## Player Confirmation

Before play, each player should confirm:

- Adult status.
- Consent to participate.
- Awareness that the game contains adult content.
- Personal boundaries.

## Boundaries

Example boundary tags:

- `no_nudity`
- `no_clothing_removal`
- `no_touch`
- `verbal_only`
- `no_props`
- `no_random_partner`
- `partner_only`
- `no_group_physical_tasks`

The final boundary model is `TBD`, but card eligibility must be explicit enough
to filter cards before they are played.

## Card Filtering

Cards must be filtered against the most restrictive boundaries of every
participant involved in the task.

If a card violates a participant boundary, the system should do one of these:

- Select another legal target.
- Use a softer legal variant.
- Ask the active player to choose another card.
- Reject the play.

The app must not force a player into content they excluded.

## Refusal

Refusal is always allowed and is a normal part of play.

Possible refusal outcomes:

- Lose a point.
- Draw a mild penalty card.
- Perform a softer replacement task.
- Give the card to another willing player.
- Skip without penalty in chill-oriented modes.

No refusal outcome may override a player's boundaries.

## Negotiation And Rescue

Negotiation and rescue mechanics should support comfort, not only strategy.

- Negotiation may change a card into a softer legal variant.
- Rescue may allow another willing player to take over a task.

## Card Metadata

Every card should include safety and filtering metadata, including:

- Type.
- Spice level.
- Target.
- Boundary tags.
- Contact requirements.
- Prop requirements.
- Whether random targeting is allowed.
- Whether group participation is allowed.

Metadata must be present even in local fixtures.

## Finale-Level Content

The strongest game level should be available only when all participants consent
to that level. Exact consent UX is `TBD`.
