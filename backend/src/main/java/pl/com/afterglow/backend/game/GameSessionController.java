package pl.com.afterglow.backend.game;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

import static pl.com.afterglow.backend.game.GameSessionDtos.AddPlayerRequest;
import static pl.com.afterglow.backend.game.GameSessionDtos.CreateGameSessionRequest;
import static pl.com.afterglow.backend.game.GameSessionDtos.DiceRollRequest;
import static pl.com.afterglow.backend.game.GameSessionDtos.DiceRollResponse;
import static pl.com.afterglow.backend.game.GameSessionDtos.GameSessionResponse;
import static pl.com.afterglow.backend.game.GameSessionDtos.PlayCardRequest;
import static pl.com.afterglow.backend.game.GameSessionDtos.ResolveCurrentCardRequest;

@RestController
@RequestMapping("/api/game-sessions")
class GameSessionController {

	private final GameSessionService gameSessionService;

	GameSessionController(GameSessionService gameSessionService) {
		this.gameSessionService = gameSessionService;
	}

	@PostMapping
	ResponseEntity<GameSessionResponse> create(@Valid @RequestBody CreateGameSessionRequest request) {
		GameSessionResponse response = gameSessionService.create(request);
		return ResponseEntity.created(URI.create("/api/game-sessions/" + response.id())).body(response);
	}

	@GetMapping("/{sessionId}")
	GameSessionResponse findById(@PathVariable String sessionId) {
		return gameSessionService.findById(sessionId);
	}

	@GetMapping("/code/{code}")
	GameSessionResponse findByCode(@PathVariable String code) {
		return gameSessionService.findByCode(code);
	}

	@PostMapping("/{sessionId}/players")
	GameSessionResponse addPlayer(
			@PathVariable String sessionId,
			@Valid @RequestBody AddPlayerRequest request
	) {
		return gameSessionService.addPlayer(sessionId, request);
	}

	@PostMapping("/{sessionId}/start")
	GameSessionResponse start(@PathVariable String sessionId) {
		return gameSessionService.start(sessionId);
	}

	@PostMapping("/{sessionId}/cards/play")
	GameSessionResponse playCard(
			@PathVariable String sessionId,
			@Valid @RequestBody PlayCardRequest request
	) {
		return gameSessionService.playCard(sessionId, request);
	}

	@PostMapping("/{sessionId}/current-card/complete")
	GameSessionResponse completeCurrentCard(
			@PathVariable String sessionId,
			@Valid @RequestBody ResolveCurrentCardRequest request
	) {
		return gameSessionService.completeCurrentCard(sessionId, request);
	}

	@PostMapping("/{sessionId}/current-card/refuse")
	GameSessionResponse refuseCurrentCard(
			@PathVariable String sessionId,
			@Valid @RequestBody ResolveCurrentCardRequest request
	) {
		return gameSessionService.refuseCurrentCard(sessionId, request);
	}

	@PostMapping("/{sessionId}/dice-rolls")
	DiceRollResponse rollDie(
			@PathVariable String sessionId,
			@RequestBody(required = false) DiceRollRequest request
	) {
		return gameSessionService.rollDie(sessionId, request == null ? new DiceRollRequest(null) : request);
	}

	@PostMapping("/{sessionId}/finish")
	GameSessionResponse finish(@PathVariable String sessionId) {
		return gameSessionService.finish(sessionId);
	}
}
