package pl.com.afterglow.backend.game;

final class GameSessionNotFoundException extends RuntimeException {

	GameSessionNotFoundException(String sessionIdOrCode) {
		super("Game session not found: " + sessionIdOrCode);
	}
}
