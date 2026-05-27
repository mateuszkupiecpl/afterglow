package pl.com.afterglow.api;

import java.time.Instant;

public record DiceRollResource(
		String sessionId,
		String playerId,
		int value,
		Instant rolledAt
) {
}
