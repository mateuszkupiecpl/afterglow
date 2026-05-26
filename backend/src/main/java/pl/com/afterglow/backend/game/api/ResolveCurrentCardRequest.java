package pl.com.afterglow.backend.game.api;

import jakarta.validation.constraints.NotBlank;

public record ResolveCurrentCardRequest(
		@NotBlank String playerId
) {
}
