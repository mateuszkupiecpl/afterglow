package pl.com.afterglow.api;

import java.time.Instant;

public record PlayedCardResource(
		String cardInstanceId,
		String playerId,
		String targetPlayerId,
		CardResource card,
		Instant playedAt
) {
}
