package pl.com.afterglow.backend.game;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Clock;
import java.time.Instant;

import static pl.com.afterglow.backend.game.GameSessionDtos.ApiError;

@RestControllerAdvice
class GameSessionExceptionHandler {

	private final Clock clock;

	GameSessionExceptionHandler() {
		this(Clock.systemUTC());
	}

	GameSessionExceptionHandler(Clock clock) {
		this.clock = clock;
	}

	@ExceptionHandler(GameSessionNotFoundException.class)
	ResponseEntity<ApiError> handleNotFound(GameSessionNotFoundException exception) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(error("not_found", exception.getMessage()));
	}

	@ExceptionHandler(InvalidGameActionException.class)
	ResponseEntity<ApiError> handleInvalidGameAction(InvalidGameActionException exception) {
		return ResponseEntity.status(HttpStatus.CONFLICT)
				.body(error("invalid_game_action", exception.getMessage()));
	}

	private ApiError error(String error, String message) {
		return new ApiError(error, message, Instant.now(clock));
	}
}
