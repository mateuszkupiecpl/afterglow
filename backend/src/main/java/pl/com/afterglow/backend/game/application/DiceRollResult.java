package pl.com.afterglow.backend.game.application;

import java.time.Instant;

public record DiceRollResult(
		String sessionId,
		String playerId,
		int value,
		Instant rolledAt
) {
}
