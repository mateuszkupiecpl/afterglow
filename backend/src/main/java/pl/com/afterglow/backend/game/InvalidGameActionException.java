package pl.com.afterglow.backend.game;

final class InvalidGameActionException extends RuntimeException {

	InvalidGameActionException(String message) {
		super(message);
	}
}
