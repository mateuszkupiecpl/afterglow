package pl.com.afterglow.application;

import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public record GameSessionSnapshot(
		String id,
		String code,
		String mode,
		String status,
		List<PlayerSnapshot> players,
		GameSettingsSnapshot settings,
		List<String> deckIds,
		int currentRound,
		String currentTurnPlayerId,
		int atmosphereLevel,
		String currentSpiceLevel,
		Map<String, Integer> score,
		PlayedCardSnapshot currentCard,
		Instant createdAt,
		Instant updatedAt
) {

	public GameSessionSnapshot {
		players = List.copyOf(players);
		deckIds = List.copyOf(deckIds);
		score = Collections.unmodifiableMap(new LinkedHashMap<>(score));
	}

	public record PlayerSnapshot(
			String id,
			String nickname,
			boolean host,
			List<CardInstanceSnapshot> hand,
			int points,
			ComfortProfileSnapshot comfortProfile
	) {

		public PlayerSnapshot {
			hand = List.copyOf(hand);
		}
	}

	public record ComfortProfileSnapshot(
			String playerId,
			boolean confirmedAdult,
			Set<String> boundaries
	) {

		public ComfortProfileSnapshot {
			boundaries = Set.copyOf(boundaries);
		}
	}

	public record GameSettingsSnapshot(
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

	public record CardInstanceSnapshot(
			String instanceId,
			String cardId,
			String ownerPlayerId,
			String visibility,
			CardSnapshot card
	) {
	}

	public record CardSnapshot(
			String id,
			String deckId,
			String title,
			String type,
			String text,
			String spiceLevel,
			int actionPointCost,
			String target,
			boolean requiresProps,
			Set<String> boundaries
	) {

		public CardSnapshot {
			boundaries = Set.copyOf(boundaries);
		}
	}

	public record PlayedCardSnapshot(
			String cardInstanceId,
			String playerId,
			String targetPlayerId,
			CardSnapshot card,
			Instant playedAt
	) {
	}
}
