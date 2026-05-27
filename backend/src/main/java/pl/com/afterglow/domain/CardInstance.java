package pl.com.afterglow.domain;

public record CardInstance(
		String instanceId,
		String cardId,
		String ownerPlayerId,
		CardVisibility visibility
) {
}
