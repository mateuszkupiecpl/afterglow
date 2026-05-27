package pl.com.afterglow.application;

import java.util.Set;

public final class GameSessionCommands {

	private GameSessionCommands() {
	}

	public record CreateGameSessionCommand(
			String hostNickname,
			boolean confirmedAdult,
			String mode,
			GameSettingsCommand settings,
			Set<String> boundaries
	) {
	}

	public record AddPlayerCommand(
			String nickname,
			boolean confirmedAdult,
			Set<String> boundaries
	) {
	}

	public record GameSettingsCommand(
			String startSpiceLevel,
			String maxSpiceLevel,
			String pace,
			Boolean allowProps,
			Boolean allowPairTasks,
			Boolean allowGroupTasks
	) {
	}

	public record PlayCardCommand(
			String playerId,
			String cardInstanceId,
			String targetPlayerId
	) {
	}

	public record ResolveCurrentCardCommand(
			String playerId
	) {
	}

	public record DiceRollCommand(
			String playerId
	) {
	}
}
