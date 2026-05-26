package pl.com.afterglow.domain;

public final class InvalidGameActionException extends RuntimeException {

	public InvalidGameActionException(String message) {
		super(message);
	}
}
