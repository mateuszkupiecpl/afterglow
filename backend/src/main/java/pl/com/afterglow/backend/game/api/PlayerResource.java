package pl.com.afterglow.backend.game.api;

import java.util.List;

public record PlayerResource(
		String id,
		String nickname,
		boolean host,
		List<CardInstanceResource> hand,
		int points,
		ComfortProfileResource comfortProfile
) {
}
