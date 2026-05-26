package pl.com.afterglow.api;

public record CardInstanceResource(
		String instanceId,
		String cardId,
		String ownerPlayerId,
		String visibility,
		CardResource card
) {
}
