package pl.com.afterglow.backend.game.api;

import java.time.Instant;

public record DiceRollResource(
		String sessionId,
		String playerId,
		int value,
		Instant rolledAt
) {
}
