package pl.com.afterglow.api;

import jakarta.validation.constraints.NotBlank;

public record PlayCardRequest(
		@NotBlank String playerId,
		@NotBlank String cardInstanceId,
		String targetPlayerId
) {
}
