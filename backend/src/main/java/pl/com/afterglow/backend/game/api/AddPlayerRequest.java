package pl.com.afterglow.backend.game.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record AddPlayerRequest(
		@NotBlank @Size(max = 40) String nickname,
		@NotNull Boolean confirmedAdult,
		Set<String> boundaries
) {
}
