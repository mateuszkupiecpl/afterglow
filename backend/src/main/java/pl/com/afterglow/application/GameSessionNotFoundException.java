package pl.com.afterglow.application;

public final class GameSessionNotFoundException extends RuntimeException {

	public GameSessionNotFoundException(String sessionIdOrCode) {
		super("Game session not found: " + sessionIdOrCode);
	}
}
