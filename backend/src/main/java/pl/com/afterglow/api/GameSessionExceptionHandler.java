package pl.com.afterglow.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.com.afterglow.application.GameSessionActionRejectedException;
import pl.com.afterglow.application.GameSessionNotFoundException;

import java.time.Clock;
import java.time.Instant;

@RestControllerAdvice
class GameSessionExceptionHandler {

	private final Clock clock;

	GameSessionExceptionHandler(Clock clock) {
		this.clock = clock;
	}

	@ExceptionHandler(GameSessionNotFoundException.class)
	ResponseEntity<ApiErrorResource> handleNotFound(GameSessionNotFoundException exception) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(error("not_found", exception.getMessage()));
	}

	@ExceptionHandler(GameSessionActionRejectedException.class)
	ResponseEntity<ApiErrorResource> handleInvalidGameAction(GameSessionActionRejectedException exception) {
		return ResponseEntity.status(HttpStatus.CONFLICT)
				.body(error("invalid_game_action", exception.getMessage()));
	}

	@ExceptionHandler({
			IllegalArgumentException.class,
			MethodArgumentNotValidException.class,
			HttpMessageNotReadableException.class
	})
	ResponseEntity<ApiErrorResource> handleInvalidRequest(Exception exception) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(error("invalid_request", exception.getMessage()));
	}

	private ApiErrorResource error(String error, String message) {
		return new ApiErrorResource(error, message, Instant.now(clock));
	}
}
