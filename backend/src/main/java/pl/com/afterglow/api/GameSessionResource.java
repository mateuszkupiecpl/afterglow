package pl.com.afterglow.api;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record GameSessionResource(
		String id,
		String code,
		String mode,
		String status,
		List<PlayerResource> players,
		GameSettingsResource settings,
		List<String> deckIds,
		int currentRound,
		String currentTurnPlayerId,
		int atmosphereLevel,
		String currentSpiceLevel,
		Map<String, Integer> score,
		PlayedCardResource currentCard,
		Instant createdAt,
		Instant updatedAt,
		Map<String, String> links
) {
}
