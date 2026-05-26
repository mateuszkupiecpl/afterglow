package pl.com.afterglow.backend.game.domain;

import java.util.Objects;

public record GameSettings(
		int minPlayers,
		int maxPlayers,
		SpiceLevel startSpiceLevel,
		SpiceLevel maxSpiceLevel,
		GamePace pace,
		boolean allowProps,
		boolean allowPairTasks,
		boolean allowGroupTasks
) {

	public GameSettings {
		Objects.requireNonNull(startSpiceLevel, "startSpiceLevel");
		Objects.requireNonNull(maxSpiceLevel, "maxSpiceLevel");
		Objects.requireNonNull(pace, "pace");
		if (minPlayers < 1) {
			throw new InvalidGameActionException("Minimum player count must be positive.");
		}
		if (maxPlayers < minPlayers) {
			throw new InvalidGameActionException("Maximum player count cannot be lower than minimum player count.");
		}
		if (!startSpiceLevel.isAtMost(maxSpiceLevel)) {
			throw new InvalidGameActionException("Start spice level cannot be higher than max spice level.");
		}
	}
}
