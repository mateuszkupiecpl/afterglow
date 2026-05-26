package pl.com.afterglow.backend.game.api;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.com.afterglow.backend.game.application.GameSessionApplicationService;

import java.net.URI;

@RestController
@RequestMapping("/api/game-sessions")
class GameSessionController {

	private final GameSessionApplicationService gameSessions;
	private final GameSessionResourceMapper mapper;

	GameSessionController(GameSessionApplicationService gameSessions, GameSessionResourceMapper mapper) {
		this.gameSessions = gameSessions;
		this.mapper = mapper;
	}

	@PostMapping
	ResponseEntity<GameSessionResource> create(@Valid @RequestBody CreateGameSessionRequest request) {
		GameSessionResource resource = mapper.toResource(gameSessions.create(mapper.toCommand(request)));
		return ResponseEntity.created(URI.create("/api/game-sessions/" + resource.id())).body(resource);
	}

	@GetMapping("/{sessionId}")
	GameSessionResource findById(@PathVariable String sessionId) {
		return mapper.toResource(gameSessions.findById(sessionId));
	}

	@GetMapping("/code/{code}")
	GameSessionResource findByCode(@PathVariable String code) {
		return mapper.toResource(gameSessions.findByCode(code));
	}

	@PostMapping("/{sessionId}/players")
	GameSessionResource addPlayer(
			@PathVariable String sessionId,
			@Valid @RequestBody AddPlayerRequest request
	) {
		return mapper.toResource(gameSessions.addPlayer(sessionId, mapper.toCommand(request)));
	}

	@PostMapping("/{sessionId}/start")
	GameSessionResource start(@PathVariable String sessionId) {
		return mapper.toResource(gameSessions.start(sessionId));
	}

	@PostMapping("/{sessionId}/cards/play")
	GameSessionResource playCard(
			@PathVariable String sessionId,
			@Valid @RequestBody PlayCardRequest request
	) {
		return mapper.toResource(gameSessions.playCard(sessionId, mapper.toCommand(request)));
	}

	@PostMapping("/{sessionId}/current-card/complete")
	GameSessionResource completeCurrentCard(
			@PathVariable String sessionId,
			@Valid @RequestBody ResolveCurrentCardRequest request
	) {
		return mapper.toResource(gameSessions.completeCurrentCard(sessionId, mapper.toCommand(request)));
	}

	@PostMapping("/{sessionId}/current-card/refuse")
	GameSessionResource refuseCurrentCard(
			@PathVariable String sessionId,
			@Valid @RequestBody ResolveCurrentCardRequest request
	) {
		return mapper.toResource(gameSessions.refuseCurrentCard(sessionId, mapper.toCommand(request)));
	}

	@PostMapping("/{sessionId}/dice-rolls")
	DiceRollResource rollDie(
			@PathVariable String sessionId,
			@RequestBody(required = false) DiceRollRequest request
	) {
		return mapper.toResource(gameSessions.rollDie(sessionId, mapper.toCommand(request == null ? new DiceRollRequest(null) : request)));
	}

	@PostMapping("/{sessionId}/finish")
	GameSessionResource finish(@PathVariable String sessionId) {
		return mapper.toResource(gameSessions.finish(sessionId));
	}
}
