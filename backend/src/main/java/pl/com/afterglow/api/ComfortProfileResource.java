package pl.com.afterglow.api;

import java.util.Set;

public record ComfortProfileResource(
		String playerId,
		boolean confirmedAdult,
		Set<String> boundaries
) {
}
