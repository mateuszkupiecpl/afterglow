package pl.com.afterglow.application;

public final class GameSessionActionRejectedException extends RuntimeException {

	public GameSessionActionRejectedException(String message, Throwable cause) {
		super(message, cause);
	}
}
