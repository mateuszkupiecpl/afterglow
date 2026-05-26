package pl.com.afterglow.backend.game.api;

import java.util.Set;

public record ComfortProfileResource(
		String playerId,
		boolean confirmedAdult,
		Set<String> boundaries
) {
}
