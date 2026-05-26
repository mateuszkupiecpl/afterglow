package pl.com.afterglow.backend.game.api;

public record CardInstanceResource(
		String instanceId,
		String cardId,
		String ownerPlayerId,
		String visibility,
		CardResource card
) {
}
