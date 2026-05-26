package pl.com.afterglow.backend.game.domain;

public record CardInstance(
		String instanceId,
		String cardId,
		String ownerPlayerId,
		CardVisibility visibility
) {
}
