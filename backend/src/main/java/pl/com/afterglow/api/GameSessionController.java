package pl.com.afterglow.api;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.com.afterglow.application.GameSessionApplicationService;
import pl.com.afterglow.domain.GameSession;

import java.net.URI;

@RestController
@RequestMapping("/api/game-sessions")
class GameSessionController {

	private final GameSessionApplicationService gameSessions;
	private final GameSessionResourceMapper mapper;
	private final GameSessionLinkAssembler links;

	GameSessionController(
			GameSessionApplicationService gameSessions,
			GameSessionResourceMapper mapper,
			GameSessionLinkAssembler links
	) {
		this.gameSessions = gameSessions;
		this.mapper = mapper;
		this.links = links;
	}

	@PostMapping
	ResponseEntity<GameSessionResource> create(@Valid @RequestBody CreateGameSessionRequest request) {
		var resource = toResource(gameSessions.create(mapper.toCommand(request)));
		return ResponseEntity.created(URI.create(resource.getRequiredLink("self").getHref())).body(resource);
	}

	@GetMapping("/{sessionId}")
	GameSessionResource findById(@PathVariable String sessionId) {
		return toResource(gameSessions.findById(sessionId));
	}

	@GetMapping("/code/{code}")
	GameSessionResource findByCode(@PathVariable String code) {
		return toResource(gameSessions.findByCode(code));
	}

	@PostMapping("/{sessionId}/players")
	GameSessionResource addPlayer(
			@PathVariable String sessionId,
			@Valid @RequestBody AddPlayerRequest request
	) {
		return toResource(gameSessions.addPlayer(sessionId, mapper.toCommand(request)));
	}

	@PostMapping("/{sessionId}/start")
	GameSessionResource start(@PathVariable String sessionId) {
		return toResource(gameSessions.start(sessionId));
	}

	@PostMapping("/{sessionId}/cards/play")
	GameSessionResource playCard(
			@PathVariable String sessionId,
			@Valid @RequestBody PlayCardRequest request
	) {
		return toResource(gameSessions.playCard(sessionId, mapper.toCommand(request)));
	}

	@PostMapping("/{sessionId}/current-card/complete")
	GameSessionResource completeCurrentCard(
			@PathVariable String sessionId,
			@Valid @RequestBody ResolveCurrentCardRequest request
	) {
		return toResource(gameSessions.completeCurrentCard(sessionId, mapper.toCommand(request)));
	}

	@PostMapping("/{sessionId}/current-card/refuse")
	GameSessionResource refuseCurrentCard(
			@PathVariable String sessionId,
			@Valid @RequestBody ResolveCurrentCardRequest request
	) {
		return toResource(gameSessions.refuseCurrentCard(sessionId, mapper.toCommand(request)));
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
		return toResource(gameSessions.finish(sessionId));
	}

	private GameSessionResource toResource(GameSession session) {
		return links.addLinks(session, mapper.toResource(session));
	}
}
