package pl.com.afterglow.backend.game.api;

public record GameSettingsResource(
		int minPlayers,
		int maxPlayers,
		String startSpiceLevel,
		String maxSpiceLevel,
		String pace,
		boolean allowProps,
		boolean allowPairTasks,
		boolean allowGroupTasks
) {
}
