package pl.com.afterglow.application;

import java.time.Instant;

public record DiceRollResult(
		String sessionId,
		String playerId,
		int value,
		Instant rolledAt
) {
}
