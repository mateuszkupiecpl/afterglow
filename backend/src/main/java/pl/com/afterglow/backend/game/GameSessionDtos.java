package pl.com.afterglow.backend.game;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class GameSessionDtos {

	private GameSessionDtos() {
	}

	public record CreateGameSessionRequest(
			@NotBlank @Size(max = 40) String hostNickname,
			@NotNull Boolean confirmedAdult,
			GameTypes.GameMode mode,
			GameSettingsRequest settings,
			Set<GameTypes.BoundaryTag> boundaries
	) {
	}

	public record AddPlayerRequest(
			@NotBlank @Size(max = 40) String nickname,
			@NotNull Boolean confirmedAdult,
			Set<GameTypes.BoundaryTag> boundaries
	) {
	}

	public record GameSettingsRequest(
			GameTypes.SpiceLevel startSpiceLevel,
			GameTypes.SpiceLevel maxSpiceLevel,
			GameTypes.GamePace pace,
			Boolean allowProps,
			Boolean allowPairTasks,
			Boolean allowGroupTasks
	) {
	}

	public record PlayCardRequest(
			@NotBlank String playerId,
			@NotBlank String cardInstanceId,
			String targetPlayerId
	) {
	}

	public record ResolveCurrentCardRequest(
			@NotBlank String playerId
	) {
	}

	public record DiceRollRequest(
			String playerId
	) {
	}

	public record GameSessionResponse(
			String id,
			String code,
			GameTypes.GameMode mode,
			GameTypes.GameStatus status,
			List<PlayerResponse> players,
			GameSettingsResponse settings,
			List<String> deckIds,
			int currentRound,
			String currentTurnPlayerId,
			int atmosphereLevel,
			GameTypes.SpiceLevel currentSpiceLevel,
			Map<String, Integer> score,
			PlayedCardResponse currentCard,
			Instant createdAt,
			Instant updatedAt
	) {
	}

	public record PlayerResponse(
			String id,
			String nickname,
			boolean host,
			List<CardInstanceResponse> hand,
			int points,
			ComfortProfileResponse comfortProfile
	) {
	}

	public record ComfortProfileResponse(
			String playerId,
			boolean confirmedAdult,
			Set<GameTypes.BoundaryTag> boundaries
	) {
	}

	public record GameSettingsResponse(
			int minPlayers,
			int maxPlayers,
			GameTypes.SpiceLevel startSpiceLevel,
			GameTypes.SpiceLevel maxSpiceLevel,
			GameTypes.GamePace pace,
			boolean allowProps,
			boolean allowPairTasks,
			boolean allowGroupTasks
	) {
	}

	public record CardInstanceResponse(
			String instanceId,
			String cardId,
			String ownerPlayerId,
			GameTypes.CardVisibility visibility,
			CardResponse card
	) {
	}

	public record CardResponse(
			String id,
			String deckId,
			String title,
			GameTypes.CardType type,
			String text,
			GameTypes.SpiceLevel spiceLevel,
			int actionPointCost,
			GameTypes.CardTarget target,
			boolean requiresProps,
			Set<GameTypes.BoundaryTag> boundaries
	) {
	}

	public record PlayedCardResponse(
			String cardInstanceId,
			String playerId,
			String targetPlayerId,
			CardResponse card,
			Instant playedAt
	) {
	}

	public record DiceRollResponse(
			String sessionId,
			String playerId,
			int value,
			Instant rolledAt
	) {
	}

	public record ApiError(
			String error,
			String message,
			Instant timestamp
	) {
	}
}
