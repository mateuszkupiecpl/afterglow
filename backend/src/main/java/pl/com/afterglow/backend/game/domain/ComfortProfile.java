package pl.com.afterglow.backend.game.domain;

import java.util.EnumSet;
import java.util.Set;

public record ComfortProfile(
		String playerId,
		boolean confirmedAdult,
		Set<BoundaryTag> boundaries
) {

	public ComfortProfile {
		if (!confirmedAdult) {
			throw new InvalidGameActionException("Every player must confirm they are 18+ before joining.");
		}
		boundaries = boundarySet(boundaries);
	}

	private static Set<BoundaryTag> boundarySet(Set<BoundaryTag> boundaries) {
		if (boundaries == null || boundaries.isEmpty()) {
			return Set.of();
		}
		return Set.copyOf(EnumSet.copyOf(boundaries));
	}
}
