package pl.com.afterglow.backend.game.domain;

public final class InvalidGameActionException extends RuntimeException {

	public InvalidGameActionException(String message) {
		super(message);
	}
}
