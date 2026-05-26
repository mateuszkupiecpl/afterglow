package pl.com.afterglow.api;

import jakarta.validation.constraints.NotBlank;

public record ResolveCurrentCardRequest(
		@NotBlank String playerId
) {
}
