package pl.com.afterglow.backend.game.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record CreateGameSessionRequest(
		@NotBlank @Size(max = 40) String hostNickname,
		@NotNull Boolean confirmedAdult,
		String mode,
		@Valid GameSettingsRequest settings,
		Set<String> boundaries
) {
}
