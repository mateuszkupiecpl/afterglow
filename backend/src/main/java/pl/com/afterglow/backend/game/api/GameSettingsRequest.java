package pl.com.afterglow.backend.game.api;

public record GameSettingsRequest(
		String startSpiceLevel,
		String maxSpiceLevel,
		String pace,
		Boolean allowProps,
		Boolean allowPairTasks,
		Boolean allowGroupTasks
) {
}
