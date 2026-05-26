package pl.com.afterglow.application;

import pl.com.afterglow.domain.ComfortProfile;
import pl.com.afterglow.domain.GameMode;
import pl.com.afterglow.domain.GamePace;
import pl.com.afterglow.domain.GameSession;
import pl.com.afterglow.domain.GameSettings;
import pl.com.afterglow.domain.Player;
import pl.com.afterglow.domain.SpiceLevel;
import pl.com.afterglow.domain.port.CardCatalog;
import pl.com.afterglow.domain.port.GameSessionRepository;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

import static pl.com.afterglow.application.GameSessionCommands.AddPlayerCommand;
import static pl.com.afterglow.application.GameSessionCommands.CreateGameSessionCommand;
import static pl.com.afterglow.application.GameSessionCommands.DiceRollCommand;
import static pl.com.afterglow.application.GameSessionCommands.GameSettingsCommand;
import static pl.com.afterglow.application.GameSessionCommands.PlayCardCommand;
import static pl.com.afterglow.application.GameSessionCommands.ResolveCurrentCardCommand;

public final class GameSessionApplicationService {

	public static final String STARTER_DECK_ID = "starter";

	private final GameSessionRepository gameSessionRepository;
	private final CardCatalog cardCatalog;
	private final Clock clock;
	private final Random random;

	public GameSessionApplicationService(
			GameSessionRepository gameSessionRepository,
			CardCatalog cardCatalog,
			Clock clock,
			Random random
	) {
		this.gameSessionRepository = gameSessionRepository;
		this.cardCatalog = cardCatalog;
		this.clock = clock;
		this.random = random;
	}

	public GameSession create(CreateGameSessionCommand command) {
		var settings = buildSettings(command.settings());
		var hostPlayerId = newId();
		var now = clock.instant();
		var host = new Player(
				hostPlayerId,
				command.hostNickname(),
				true,
				new ComfortProfile(hostPlayerId, command.confirmedAdult(), command.boundaries())
		);
		var session = new GameSession(
				newId(),
				newSessionCode(),
				command.mode() == null ? GameMode.PARTY_WARMUP : command.mode(),
				settings,
				List.of(STARTER_DECK_ID),
				now
		);
		session.addHost(host);
		gameSessionRepository.save(session);
		return session;
	}

	public GameSession findById(String sessionId) {
		return gameSessionRepository.findById(sessionId)
				.orElseThrow(() -> new GameSessionNotFoundException(sessionId));
	}

	public GameSession findByCode(String code) {
		var normalizedCode = code == null ? "" : code.trim().toUpperCase(Locale.ROOT);
		return gameSessionRepository.findByCode(normalizedCode)
				.orElseThrow(() -> new GameSessionNotFoundException(code));
	}

	public GameSession addPlayer(String sessionId, AddPlayerCommand command) {
		var session = findById(sessionId);
		synchronized (session) {
			var playerId = newId();
			session.addPlayer(
					new Player(
							playerId,
							command.nickname(),
							false,
							new ComfortProfile(playerId, command.confirmedAdult(), command.boundaries())
					),
					clock.instant()
			);
			gameSessionRepository.save(session);
			return session;
		}
	}

	public GameSession start(String sessionId) {
		var session = findById(sessionId);
		synchronized (session) {
			session.start(cardCatalog.findByDeckIds(session.deckIds()), random, this::newId, clock.instant());
			gameSessionRepository.save(session);
			return session;
		}
	}

	public GameSession playCard(String sessionId, PlayCardCommand command) {
		var session = findById(sessionId);
		synchronized (session) {
			session.playCard(
					command.playerId(),
					command.cardInstanceId(),
					command.targetPlayerId(),
					cardCatalog.findByDeckIds(session.deckIds()),
					random,
					this::newId,
					clock.instant()
			);
			gameSessionRepository.save(session);
			return session;
		}
	}

	public GameSession completeCurrentCard(String sessionId, ResolveCurrentCardCommand command) {
		var session = findById(sessionId);
		synchronized (session) {
			session.completeCurrentCard(command.playerId(), clock.instant());
			gameSessionRepository.save(session);
			return session;
		}
	}

	public GameSession refuseCurrentCard(String sessionId, ResolveCurrentCardCommand command) {
		var session = findById(sessionId);
		synchronized (session) {
			session.refuseCurrentCard(command.playerId(), clock.instant());
			gameSessionRepository.save(session);
			return session;
		}
	}

	public DiceRollResult rollDie(String sessionId, DiceRollCommand command) {
		var session = findById(sessionId);
		synchronized (session) {
			session.verifyDiceRollAllowed(command.playerId());
			return new DiceRollResult(session.id(), command.playerId(), random.nextInt(1, 7), clock.instant());
		}
	}

	public GameSession finish(String sessionId) {
		var session = findById(sessionId);
		synchronized (session) {
			session.finish(clock.instant());
			gameSessionRepository.save(session);
			return session;
		}
	}

	private GameSettings buildSettings(GameSettingsCommand command) {
		var startSpiceLevel = command == null || command.startSpiceLevel() == null
				? SpiceLevel.WARMUP
				: command.startSpiceLevel();
		var maxSpiceLevel = command == null || command.maxSpiceLevel() == null
				? SpiceLevel.COURAGE
				: command.maxSpiceLevel();
		var pace = command == null || command.pace() == null ? GamePace.STANDARD : command.pace();
		return new GameSettings(
				GameSession.MIN_PLAYERS,
				GameSession.MAX_PLAYERS,
				startSpiceLevel,
				maxSpiceLevel,
				pace,
				command != null && Boolean.TRUE.equals(command.allowProps()),
				command == null || command.allowPairTasks() == null || command.allowPairTasks(),
				command == null || command.allowGroupTasks() == null || command.allowGroupTasks()
		);
	}

	private String newId() {
		return UUID.randomUUID().toString();
	}

	private String newSessionCode() {
		var code = "";
		do {
			code = "AGL-%03d".formatted(random.nextInt(1000));
		}
		while (gameSessionRepository.existsByCode(code));
		return code;
	}
}
