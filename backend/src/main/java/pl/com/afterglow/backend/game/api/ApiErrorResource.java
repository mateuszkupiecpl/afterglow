package pl.com.afterglow.backend.game.api;

import java.time.Instant;

public record ApiErrorResource(
		String error,
		String message,
		Instant timestamp
) {
}
