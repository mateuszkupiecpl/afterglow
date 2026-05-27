package pl.com.afterglow.domain;

import java.time.Instant;

public record PlayedCard(
		String cardInstanceId,
		String playerId,
		String targetPlayerId,
		String cardId,
		Instant playedAt
) {
}
