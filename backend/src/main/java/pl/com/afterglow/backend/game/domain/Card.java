package pl.com.afterglow.backend.game.domain;

import java.util.EnumSet;
import java.util.Set;

public record Card(
		String id,
		String deckId,
		String title,
		CardType type,
		String text,
		SpiceLevel spiceLevel,
		int actionPointCost,
		CardTarget target,
		boolean requiresProps,
		Set<BoundaryTag> boundaries
) {

	public Card {
		boundaries = boundarySet(boundaries);
	}

	private static Set<BoundaryTag> boundarySet(Set<BoundaryTag> boundaries) {
		if (boundaries == null || boundaries.isEmpty()) {
			return Set.of();
		}
		return Set.copyOf(EnumSet.copyOf(boundaries));
	}
}
