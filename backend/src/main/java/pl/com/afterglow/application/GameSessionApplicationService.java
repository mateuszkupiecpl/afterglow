package pl.com.afterglow.application;

import pl.com.afterglow.domain.*;
import pl.com.afterglow.domain.port.CardCatalog;
import pl.com.afterglow.domain.port.GameSessionRepository;

import java.time.Clock;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static pl.com.afterglow.application.GameSessionCommands.*;

public final class GameSessionApplicationService {

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

	public GameSessionSnapshot create(CreateGameSessionCommand command) {
		return rejectInvalidActions(() -> {
			var settings = buildSettings(command.settings());
			var hostPlayerId = newId();
			var now = clock.instant();
			var host = new Player(
					hostPlayerId,
					command.hostNickname(),
					true,
					new ComfortProfile(hostPlayerId, command.confirmedAdult(), boundaries(command.boundaries()))
			);
			var session = new GameSession(
					newId(),
					newSessionCode(),
					gameModeOrDefault(command.mode(), GameMode.PARTY_WARMUP),
					settings,
					cardCatalog.defaultDeckIds(),
					now
			);
			session.addHost(host);
			gameSessionRepository.save(session);
			return toSnapshot(session);
		});
	}

	public GameSessionSnapshot findById(String sessionId) {
		return rejectInvalidActions(() -> toSnapshot(findSessionById(sessionId)));
	}

	public GameSessionSnapshot findByCode(String code) {
		return rejectInvalidActions(() -> toSnapshot(findSessionByCode(code)));
	}

	public GameSessionSnapshot addPlayer(String sessionId, AddPlayerCommand command) {
		return rejectInvalidActions(() -> {
			var session = findSessionById(sessionId);
			synchronized (session) {
				var playerId = newId();
				session.addPlayer(
						new Player(
								playerId,
								command.nickname(),
								false,
								new ComfortProfile(playerId, command.confirmedAdult(), boundaries(command.boundaries()))
						),
						clock.instant()
				);
				gameSessionRepository.save(session);
				return toSnapshot(session);
			}
		});
	}

	public GameSessionSnapshot start(String sessionId) {
		return rejectInvalidActions(() -> {
			var session = findSessionById(sessionId);
			synchronized (session) {
				session.start(cardCatalog.findByDeckIds(session.deckIds()), random, this::newId, clock.instant());
				gameSessionRepository.save(session);
				return toSnapshot(session);
			}
		});
	}

	public GameSessionSnapshot playCard(String sessionId, PlayCardCommand command) {
		return rejectInvalidActions(() -> {
			var session = findSessionById(sessionId);
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
				return toSnapshot(session);
			}
		});
	}

	public GameSessionSnapshot completeCurrentCard(String sessionId, ResolveCurrentCardCommand command) {
		return rejectInvalidActions(() -> {
			var session = findSessionById(sessionId);
			synchronized (session) {
				session.completeCurrentCard(command.playerId(), clock.instant());
				gameSessionRepository.save(session);
				return toSnapshot(session);
			}
		});
	}

	public GameSessionSnapshot refuseCurrentCard(String sessionId, ResolveCurrentCardCommand command) {
		return rejectInvalidActions(() -> {
			var session = findSessionById(sessionId);
			synchronized (session) {
				session.refuseCurrentCard(command.playerId(), clock.instant());
				gameSessionRepository.save(session);
				return toSnapshot(session);
			}
		});
	}

	public DiceRollResult rollDie(String sessionId, DiceRollCommand command) {
		return rejectInvalidActions(() -> {
			var session = findSessionById(sessionId);
			synchronized (session) {
				session.verifyDiceRollAllowed(command.playerId());
				return new DiceRollResult(session.id(), command.playerId(), random.nextInt(1, 7), clock.instant());
			}
		});
	}

	public GameSessionSnapshot finish(String sessionId) {
		return rejectInvalidActions(() -> {
			var session = findSessionById(sessionId);
			synchronized (session) {
				session.finish(clock.instant());
				gameSessionRepository.save(session);
				return toSnapshot(session);
			}
		});
	}

	private GameSettings buildSettings(GameSettingsCommand command) {
		var startSpiceLevel = command == null
				? SpiceLevel.WARMUP
				: spiceLevelOrDefault(command.startSpiceLevel(), SpiceLevel.WARMUP);
		var maxSpiceLevel = command == null
				? SpiceLevel.COURAGE
				: spiceLevelOrDefault(command.maxSpiceLevel(), SpiceLevel.COURAGE);
		var pace = command == null ? GamePace.STANDARD : paceOrDefault(command.pace(), GamePace.STANDARD);
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

	private GameSession findSessionById(String sessionId) {
		return gameSessionRepository.findById(sessionId)
				.orElseThrow(() -> new GameSessionNotFoundException(sessionId));
	}

	private GameSession findSessionByCode(String code) {
		var normalizedCode = code == null ? "" : code.trim().toUpperCase(Locale.ROOT);
		return gameSessionRepository.findByCode(normalizedCode)
				.orElseThrow(() -> new GameSessionNotFoundException(code));
	}

	private GameMode gameModeOrDefault(String value, GameMode defaultValue) {
		var gameMode = GameMode.fromCode(value);
		return gameMode == null ? defaultValue : gameMode;
	}

	private SpiceLevel spiceLevelOrDefault(String value, SpiceLevel defaultValue) {
		var spiceLevel = SpiceLevel.fromCode(value);
		return spiceLevel == null ? defaultValue : spiceLevel;
	}

	private GamePace paceOrDefault(String value, GamePace defaultValue) {
		var pace = GamePace.fromCode(value);
		return pace == null ? defaultValue : pace;
	}

	private Set<BoundaryTag> boundaries(Set<String> values) {
		if (values == null || values.isEmpty()) {
			return Set.of();
		}
		return values.stream()
				.map(BoundaryTag::fromCode)
				.filter(Objects::nonNull)
				.collect(Collectors.toUnmodifiableSet());
	}

	private GameSessionSnapshot toSnapshot(GameSession session) {
		return new GameSessionSnapshot(
				session.id(),
				session.code(),
				session.mode().code(),
				session.status().code(),
				session.players().stream().map(this::playerSnapshot).toList(),
				settingsSnapshot(session.settings()),
				session.deckIds(),
				session.currentRound(),
				session.currentTurnPlayerId(),
				session.atmosphereLevel(),
				session.currentSpiceLevel().code(),
				new LinkedHashMap<>(session.score()),
				playedCardSnapshot(session.currentCard()),
				session.createdAt(),
				session.updatedAt()
		);
	}

	private GameSessionSnapshot.PlayerSnapshot playerSnapshot(Player player) {
		return new GameSessionSnapshot.PlayerSnapshot(
				player.id(),
				player.nickname(),
				player.host(),
				player.hand().stream().map(this::cardInstanceSnapshot).toList(),
				player.points(),
				new GameSessionSnapshot.ComfortProfileSnapshot(
						player.comfortProfile().playerId(),
						player.comfortProfile().confirmedAdult(),
						codes(player.comfortProfile().boundaries())
				)
		);
	}

	private GameSessionSnapshot.GameSettingsSnapshot settingsSnapshot(GameSettings settings) {
		return new GameSessionSnapshot.GameSettingsSnapshot(
				settings.minPlayers(),
				settings.maxPlayers(),
				settings.startSpiceLevel().code(),
				settings.maxSpiceLevel().code(),
				settings.pace().code(),
				settings.allowProps(),
				settings.allowPairTasks(),
				settings.allowGroupTasks()
		);
	}

	private GameSessionSnapshot.CardInstanceSnapshot cardInstanceSnapshot(CardInstance cardInstance) {
		return new GameSessionSnapshot.CardInstanceSnapshot(
				cardInstance.instanceId(),
				cardInstance.cardId(),
				cardInstance.ownerPlayerId(),
				cardInstance.visibility().code(),
				cardSnapshot(cardInstance.cardId())
		);
	}

	private GameSessionSnapshot.PlayedCardSnapshot playedCardSnapshot(PlayedCard playedCard) {
		if (playedCard == null) {
			return null;
		}
		return new GameSessionSnapshot.PlayedCardSnapshot(
				playedCard.cardInstanceId(),
				playedCard.playerId(),
				playedCard.targetPlayerId(),
				cardSnapshot(playedCard.cardId()),
				playedCard.playedAt()
		);
	}

	private GameSessionSnapshot.CardSnapshot cardSnapshot(String cardId) {
		var card = cardCatalog.findById(cardId)
				.orElseThrow(() -> new InvalidGameActionException("Card is not available: " + cardId));
		return cardSnapshot(card);
	}

	private GameSessionSnapshot.CardSnapshot cardSnapshot(Card card) {
		return new GameSessionSnapshot.CardSnapshot(
				card.id(),
				card.deckId(),
				card.title(),
				card.type().code(),
				card.text(),
				card.spiceLevel().code(),
				card.actionPointCost(),
				card.target().code(),
				card.requiresProps(),
				codes(card.boundaries())
		);
	}

	private Set<String> codes(Set<BoundaryTag> values) {
		return values.stream()
				.map(BoundaryTag::code)
				.collect(Collectors.toUnmodifiableSet());
	}

	private <T> T rejectInvalidActions(Supplier<T> action) {
		try {
			return action.get();
		}
		catch (InvalidGameActionException exception) {
			throw new GameSessionActionRejectedException(exception.getMessage(), exception);
		}
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
