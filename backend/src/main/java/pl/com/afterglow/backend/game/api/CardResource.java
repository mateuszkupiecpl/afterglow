package pl.com.afterglow.backend.game.api;

import java.util.Set;

public record CardResource(
		String id,
		String deckId,
		String title,
		String type,
		String text,
		String spiceLevel,
		int actionPointCost,
		String target,
		boolean requiresProps,
		Set<String> boundaries
) {
}
