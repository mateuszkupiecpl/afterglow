package pl.com.afterglow.api;

import java.time.Instant;

public record ApiErrorResource(
		String error,
		String message,
		Instant timestamp
) {
}
