package pl.com.afterglow.backend.game.application;

import pl.com.afterglow.backend.game.domain.BoundaryTag;
import pl.com.afterglow.backend.game.domain.GameMode;
import pl.com.afterglow.backend.game.domain.GamePace;
import pl.com.afterglow.backend.game.domain.SpiceLevel;

import java.util.Set;

public final class GameSessionCommands {

	private GameSessionCommands() {
	}

	public record CreateGameSessionCommand(
			String hostNickname,
			boolean confirmedAdult,
			GameMode mode,
			GameSettingsCommand settings,
			Set<BoundaryTag> boundaries
	) {
	}

	public record AddPlayerCommand(
			String nickname,
			boolean confirmedAdult,
			Set<BoundaryTag> boundaries
	) {
	}

	public record GameSettingsCommand(
			SpiceLevel startSpiceLevel,
			SpiceLevel maxSpiceLevel,
			GamePace pace,
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
