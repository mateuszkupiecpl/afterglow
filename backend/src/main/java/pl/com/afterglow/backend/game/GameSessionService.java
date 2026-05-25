package pl.com.afterglow.backend.game;

import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static pl.com.afterglow.backend.game.GameSessionDtos.AddPlayerRequest;
import static pl.com.afterglow.backend.game.GameSessionDtos.CardInstanceResponse;
import static pl.com.afterglow.backend.game.GameSessionDtos.CardResponse;
import static pl.com.afterglow.backend.game.GameSessionDtos.ComfortProfileResponse;
import static pl.com.afterglow.backend.game.GameSessionDtos.CreateGameSessionRequest;
import static pl.com.afterglow.backend.game.GameSessionDtos.DiceRollRequest;
import static pl.com.afterglow.backend.game.GameSessionDtos.DiceRollResponse;
import static pl.com.afterglow.backend.game.GameSessionDtos.GameSessionResponse;
import static pl.com.afterglow.backend.game.GameSessionDtos.GameSettingsRequest;
import static pl.com.afterglow.backend.game.GameSessionDtos.GameSettingsResponse;
import static pl.com.afterglow.backend.game.GameSessionDtos.PlayCardRequest;
import static pl.com.afterglow.backend.game.GameSessionDtos.PlayedCardResponse;
import static pl.com.afterglow.backend.game.GameSessionDtos.PlayerResponse;
import static pl.com.afterglow.backend.game.GameSessionDtos.ResolveCurrentCardRequest;

@Service
class GameSessionService {

	private static final int MIN_PLAYERS = 2;
	private static final int MAX_PLAYERS = 8;
	private static final int STARTING_HAND_SIZE = 4;
	private static final int HAND_LIMIT = 5;
	private static final int MIN_ATMOSPHERE_LEVEL = 1;
	private static final int MAX_ATMOSPHERE_LEVEL = 5;
	private static final String STARTER_DECK_ID = "starter";

	private final Map<String, GameSession> sessions = new ConcurrentHashMap<>();
	private final Map<String, String> sessionIdsByCode = new ConcurrentHashMap<>();
	private final Map<String, Card> cardsById = starterCards().stream()
			.collect(Collectors.toUnmodifiableMap(Card::id, card -> card));
	private final List<Card> starterCards = starterCards();
	private final Clock clock;
	private final Random random = new Random();

	GameSessionService() {
		this(Clock.systemUTC());
	}

	GameSessionService(Clock clock) {
		this.clock = clock;
	}

	GameSessionResponse create(CreateGameSessionRequest request) {
		requireAdultConfirmation(request.confirmedAdult());
		GameSettings settings = buildSettings(request.settings());
		String hostPlayerId = newId();
		Instant now = clock.instant();
		Player host = new Player(
				hostPlayerId,
				normalizeNickname(request.hostNickname()),
				true,
				new ComfortProfile(hostPlayerId, true, boundarySet(request.boundaries()))
		);
		GameSession session = new GameSession(
				newId(),
				newSessionCode(),
				request.mode() == null ? GameTypes.GameMode.PARTY_WARMUP : request.mode(),
				settings,
				List.of(STARTER_DECK_ID),
				now
		);
		session.players.add(host);
		session.score.put(host.id, host.points);
		sessions.put(session.id, session);
		sessionIdsByCode.put(session.code, session.id);
		return snapshot(session);
	}

	GameSessionResponse findById(String sessionId) {
		GameSession session = requireSession(sessionId);
		synchronized (session) {
			return snapshot(session);
		}
	}

	GameSessionResponse findByCode(String code) {
		String sessionId = sessionIdsByCode.get(code == null ? "" : code.trim().toUpperCase());
		if (sessionId == null) {
			throw new GameSessionNotFoundException(code);
		}
		return findById(sessionId);
	}

	GameSessionResponse addPlayer(String sessionId, AddPlayerRequest request) {
		requireAdultConfirmation(request.confirmedAdult());
		GameSession session = requireSession(sessionId);
		synchronized (session) {
			requireStatus(session, GameTypes.GameStatus.SETUP, "Players can only join during setup.");
			if (session.players.size() >= session.settings.maxPlayers) {
				throw new InvalidGameActionException("The session already has the maximum number of players.");
			}
			String playerId = newId();
			Player player = new Player(
					playerId,
					normalizeNickname(request.nickname()),
					false,
					new ComfortProfile(playerId, true, boundarySet(request.boundaries()))
			);
			session.players.add(player);
			session.score.put(player.id, player.points);
			touch(session);
			return snapshot(session);
		}
	}

	GameSessionResponse start(String sessionId) {
		GameSession session = requireSession(sessionId);
		synchronized (session) {
			requireStatus(session, GameTypes.GameStatus.SETUP, "Only setup sessions can be started.");
			if (session.players.size() < session.settings.minPlayers) {
				throw new InvalidGameActionException("A session needs at least " + session.settings.minPlayers + " players.");
			}
			session.drawPile.clear();
			session.drawPile.addAll(buildDrawPile(session));
			for (Player player : session.players) {
				player.hand.clear();
				for (int index = 0; index < STARTING_HAND_SIZE; index++) {
					player.hand.add(drawCardFor(session, player.id));
				}
			}
			session.currentRound = 1;
			session.currentTurnIndex = 0;
			session.currentTurnPlayerId = session.players.getFirst().id;
			session.atmosphereLevel = MIN_ATMOSPHERE_LEVEL;
			session.currentSpiceLevel = session.settings.startSpiceLevel;
			session.status = GameTypes.GameStatus.IN_PROGRESS;
			touch(session);
			return snapshot(session);
		}
	}

	GameSessionResponse playCard(String sessionId, PlayCardRequest request) {
		GameSession session = requireSession(sessionId);
		synchronized (session) {
			requireStatus(session, GameTypes.GameStatus.IN_PROGRESS, "Cards can only be played while a session is in progress.");
			if (session.currentCard != null) {
				throw new InvalidGameActionException("Resolve the current card before playing another card.");
			}
			if (!request.playerId().equals(session.currentTurnPlayerId)) {
				throw new InvalidGameActionException("Only the current turn player can play a card.");
			}
			Player player = requirePlayer(session, request.playerId());
			CardInstance cardInstance = removeCardFromHand(player, request.cardInstanceId());
			Card card = requireCard(cardInstance.cardId);
			validateTarget(session, card, request.targetPlayerId());
			if (player.hand.size() < HAND_LIMIT) {
				player.hand.add(drawCardFor(session, player.id));
			}
			session.currentCard = new PlayedCard(
					cardInstance.instanceId,
					player.id,
					request.targetPlayerId(),
					card.id,
					clock.instant()
			);
			touch(session);
			return snapshot(session);
		}
	}

	GameSessionResponse completeCurrentCard(String sessionId, ResolveCurrentCardRequest request) {
		GameSession session = requireSession(sessionId);
		synchronized (session) {
			requireCurrentCardOwner(session, request.playerId());
			Player player = requirePlayer(session, request.playerId());
			player.points += 1;
			session.score.put(player.id, player.points);
			session.atmosphereLevel = Math.min(MAX_ATMOSPHERE_LEVEL, session.atmosphereLevel + 1);
			advanceTurn(session);
			touch(session);
			return snapshot(session);
		}
	}

	GameSessionResponse refuseCurrentCard(String sessionId, ResolveCurrentCardRequest request) {
		GameSession session = requireSession(sessionId);
		synchronized (session) {
			requireCurrentCardOwner(session, request.playerId());
			Player player = requirePlayer(session, request.playerId());
			player.points = Math.max(0, player.points - 1);
			session.score.put(player.id, player.points);
			advanceTurn(session);
			touch(session);
			return snapshot(session);
		}
	}

	DiceRollResponse rollDie(String sessionId, DiceRollRequest request) {
		GameSession session = requireSession(sessionId);
		synchronized (session) {
			if (session.status == GameTypes.GameStatus.FINISHED) {
				throw new InvalidGameActionException("Finished sessions cannot roll dice.");
			}
			if (request.playerId() != null && !request.playerId().isBlank()) {
				requirePlayer(session, request.playerId());
			}
			return new DiceRollResponse(session.id, request.playerId(), random.nextInt(1, 7), clock.instant());
		}
	}

	GameSessionResponse finish(String sessionId) {
		GameSession session = requireSession(sessionId);
		synchronized (session) {
			if (session.status == GameTypes.GameStatus.FINISHED) {
				return snapshot(session);
			}
			session.status = GameTypes.GameStatus.FINISHED;
			session.currentCard = null;
			touch(session);
			return snapshot(session);
		}
	}

	private GameSettings buildSettings(GameSettingsRequest request) {
		GameTypes.SpiceLevel startSpiceLevel = request == null || request.startSpiceLevel() == null
				? GameTypes.SpiceLevel.WARMUP
				: request.startSpiceLevel();
		GameTypes.SpiceLevel maxSpiceLevel = request == null || request.maxSpiceLevel() == null
				? GameTypes.SpiceLevel.COURAGE
				: request.maxSpiceLevel();
		if (!startSpiceLevel.isAtMost(maxSpiceLevel)) {
			throw new InvalidGameActionException("Start spice level cannot be higher than max spice level.");
		}
		return new GameSettings(
				MIN_PLAYERS,
				MAX_PLAYERS,
				startSpiceLevel,
				maxSpiceLevel,
				request == null || request.pace() == null ? GameTypes.GamePace.STANDARD : request.pace(),
				request != null && Boolean.TRUE.equals(request.allowProps()),
				request == null || request.allowPairTasks() == null || request.allowPairTasks(),
				request == null || request.allowGroupTasks() == null || request.allowGroupTasks()
		);
	}

	private List<String> buildDrawPile(GameSession session) {
		Set<GameTypes.BoundaryTag> sessionBoundaries = session.players.stream()
				.flatMap(player -> player.comfortProfile.boundaries.stream())
				.collect(Collectors.toCollection(() -> EnumSet.noneOf(GameTypes.BoundaryTag.class)));
		List<String> eligibleCards = starterCards.stream()
				.filter(card -> card.spiceLevel.isAtMost(session.settings.maxSpiceLevel))
				.filter(card -> session.settings.allowProps || !card.requiresProps)
				.filter(card -> session.settings.allowPairTasks || card.target != GameTypes.CardTarget.PAIR)
				.filter(card -> session.settings.allowGroupTasks || !isGroupTarget(card.target))
				.filter(card -> Collections.disjoint(card.boundaries, sessionBoundaries))
				.map(Card::id)
				.toList();
		if (eligibleCards.isEmpty()) {
			throw new InvalidGameActionException("No cards match the session settings and player boundaries.");
		}
		List<String> drawPile = new ArrayList<>();
		while (drawPile.size() < session.players.size() * HAND_LIMIT * 4) {
			drawPile.addAll(eligibleCards);
		}
		Collections.shuffle(drawPile, random);
		return drawPile;
	}

	private boolean isGroupTarget(GameTypes.CardTarget target) {
		return target == GameTypes.CardTarget.GROUP || target == GameTypes.CardTarget.EVERYONE;
	}

	private CardInstance drawCardFor(GameSession session, String playerId) {
		if (session.drawPile.isEmpty()) {
			session.drawPile.addAll(buildDrawPile(session));
		}
		return new CardInstance(newId(), session.drawPile.removeFirst(), playerId, GameTypes.CardVisibility.PRIVATE);
	}

	private void validateTarget(GameSession session, Card card, String targetPlayerId) {
		if (card.target == GameTypes.CardTarget.CHOSEN_PLAYER) {
			if (targetPlayerId == null || targetPlayerId.isBlank()) {
				throw new InvalidGameActionException("This card requires a target player.");
			}
			Player targetPlayer = requirePlayer(session, targetPlayerId);
			if (!Collections.disjoint(card.boundaries, targetPlayer.comfortProfile.boundaries)) {
				throw new InvalidGameActionException("The selected card conflicts with the target player's boundaries.");
			}
		}
	}

	private void requireCurrentCardOwner(GameSession session, String playerId) {
		requireStatus(session, GameTypes.GameStatus.IN_PROGRESS, "The session is not in progress.");
		if (session.currentCard == null) {
			throw new InvalidGameActionException("There is no current card to resolve.");
		}
		if (!session.currentCard.playerId.equals(playerId)) {
			throw new InvalidGameActionException("Only the player who played the current card can resolve it.");
		}
	}

	private void advanceTurn(GameSession session) {
		session.currentCard = null;
		session.currentTurnIndex = (session.currentTurnIndex + 1) % session.players.size();
		if (session.currentTurnIndex == 0) {
			session.currentRound += 1;
		}
		session.currentTurnPlayerId = session.players.get(session.currentTurnIndex).id;
	}

	private CardInstance removeCardFromHand(Player player, String cardInstanceId) {
		for (int index = 0; index < player.hand.size(); index++) {
			CardInstance card = player.hand.get(index);
			if (card.instanceId.equals(cardInstanceId)) {
				return player.hand.remove(index);
			}
		}
		throw new InvalidGameActionException("Card is not in the player's hand.");
	}

	private GameSession requireSession(String sessionId) {
		GameSession session = sessions.get(sessionId);
		if (session == null) {
			throw new GameSessionNotFoundException(sessionId);
		}
		return session;
	}

	private Player requirePlayer(GameSession session, String playerId) {
		return session.players.stream()
				.filter(player -> player.id.equals(playerId))
				.findFirst()
				.orElseThrow(() -> new InvalidGameActionException("Player does not belong to this session."));
	}

	private Card requireCard(String cardId) {
		Card card = cardsById.get(cardId);
		if (card == null) {
			throw new InvalidGameActionException("Card is not available: " + cardId);
		}
		return card;
	}

	private void requireStatus(GameSession session, GameTypes.GameStatus expectedStatus, String message) {
		if (session.status != expectedStatus) {
			throw new InvalidGameActionException(message);
		}
	}

	private void requireAdultConfirmation(Boolean confirmedAdult) {
		if (!Boolean.TRUE.equals(confirmedAdult)) {
			throw new InvalidGameActionException("Every player must confirm they are 18+ before joining.");
		}
	}

	private String normalizeNickname(String nickname) {
		String normalized = nickname == null ? "" : nickname.trim();
		if (normalized.isBlank()) {
			throw new InvalidGameActionException("Nickname is required.");
		}
		return normalized;
	}

	private Set<GameTypes.BoundaryTag> boundarySet(Set<GameTypes.BoundaryTag> boundaries) {
		if (boundaries == null || boundaries.isEmpty()) {
			return EnumSet.noneOf(GameTypes.BoundaryTag.class);
		}
		return EnumSet.copyOf(boundaries);
	}

	private void touch(GameSession session) {
		session.updatedAt = clock.instant();
	}

	private String newId() {
		return UUID.randomUUID().toString();
	}

	private String newSessionCode() {
		String code;
		do {
			code = "AGL-%03d".formatted(random.nextInt(1000));
		}
		while (sessionIdsByCode.containsKey(code));
		return code;
	}

	private GameSessionResponse snapshot(GameSession session) {
		List<PlayerResponse> players = session.players.stream()
				.map(player -> new PlayerResponse(
						player.id,
						player.nickname,
						player.host,
						player.hand.stream().map(this::cardInstanceResponse).toList(),
						player.points,
						new ComfortProfileResponse(
								player.comfortProfile.playerId,
								player.comfortProfile.confirmedAdult,
								Set.copyOf(player.comfortProfile.boundaries)
						)
				))
				.toList();
		return new GameSessionResponse(
				session.id,
				session.code,
				session.mode,
				session.status,
				players,
				settingsResponse(session.settings),
				List.copyOf(session.deckIds),
				session.currentRound,
				session.currentTurnPlayerId,
				session.atmosphereLevel,
				session.currentSpiceLevel,
				Collections.unmodifiableMap(new LinkedHashMap<>(session.score)),
				playedCardResponse(session.currentCard),
				session.createdAt,
				session.updatedAt
		);
	}

	private CardInstanceResponse cardInstanceResponse(CardInstance cardInstance) {
		return new CardInstanceResponse(
				cardInstance.instanceId,
				cardInstance.cardId,
				cardInstance.ownerPlayerId,
				cardInstance.visibility,
				cardResponse(requireCard(cardInstance.cardId))
		);
	}

	private PlayedCardResponse playedCardResponse(PlayedCard playedCard) {
		if (playedCard == null) {
			return null;
		}
		return new PlayedCardResponse(
				playedCard.cardInstanceId,
				playedCard.playerId,
				playedCard.targetPlayerId,
				cardResponse(requireCard(playedCard.cardId)),
				playedCard.playedAt
		);
	}

	private GameSettingsResponse settingsResponse(GameSettings settings) {
		return new GameSettingsResponse(
				settings.minPlayers,
				settings.maxPlayers,
				settings.startSpiceLevel,
				settings.maxSpiceLevel,
				settings.pace,
				settings.allowProps,
				settings.allowPairTasks,
				settings.allowGroupTasks
		);
	}

	private CardResponse cardResponse(Card card) {
		return new CardResponse(
				card.id,
				card.deckId,
				card.title,
				card.type,
				card.text,
				card.spiceLevel,
				card.actionPointCost,
				card.target,
				card.requiresProps,
				Set.copyOf(card.boundaries)
		);
	}

	private static List<Card> starterCards() {
		return List.of(
				new Card(
						"starter-warmup-question",
						STARTER_DECK_ID,
						"Warmup Question",
						GameTypes.CardType.QUESTION,
						"Ask another player a light question they can answer comfortably.",
						GameTypes.SpiceLevel.WARMUP,
						1,
						GameTypes.CardTarget.CHOSEN_PLAYER,
						false,
						Set.of()
				),
				new Card(
						"starter-group-question",
						STARTER_DECK_ID,
						"Table Question",
						GameTypes.CardType.GROUP,
						"Everyone answers a playful question in one sentence.",
						GameTypes.SpiceLevel.WARMUP,
						1,
						GameTypes.CardTarget.EVERYONE,
						false,
						Set.of()
				),
				new Card(
						"starter-interlude",
						STARTER_DECK_ID,
						"Atmosphere Break",
						GameTypes.CardType.INTERLUDE,
						"Pause for a short music or conversation break before the next turn.",
						GameTypes.SpiceLevel.WARMUP,
						1,
						GameTypes.CardTarget.GROUP,
						false,
						Set.of()
				),
				new Card(
						"starter-compliment-challenge",
						STARTER_DECK_ID,
						"Compliment Challenge",
						GameTypes.CardType.CHALLENGE,
						"Choose a player and give them a specific compliment.",
						GameTypes.SpiceLevel.TENSION,
						1,
						GameTypes.CardTarget.CHOSEN_PLAYER,
						false,
						Set.of()
				),
				new Card(
						"starter-counter",
						STARTER_DECK_ID,
						"Negotiate",
						GameTypes.CardType.REACTION,
						"Turn the current task into a softer version that still respects the card.",
						GameTypes.SpiceLevel.WARMUP,
						1,
						GameTypes.CardTarget.SELF,
						false,
						Set.of()
				),
				new Card(
						"starter-pair-story",
						STARTER_DECK_ID,
						"Two-Person Story",
						GameTypes.CardType.CHALLENGE,
						"Pick a willing partner and invent a short flirty story together.",
						GameTypes.SpiceLevel.TENSION,
						2,
						GameTypes.CardTarget.PAIR,
						false,
						Set.of(GameTypes.BoundaryTag.NO_RANDOM_PARTNER, GameTypes.BoundaryTag.PARTNER_ONLY)
				),
				new Card(
						"starter-prop-prompt",
						STARTER_DECK_ID,
						"Prop Prompt",
						GameTypes.CardType.PROP,
						"Use an agreed safe prop as a conversation prompt.",
						GameTypes.SpiceLevel.COURAGE,
						2,
						GameTypes.CardTarget.CHOSEN_PLAYER,
						true,
						Set.of(GameTypes.BoundaryTag.NO_PROPS)
				),
				new Card(
						"starter-group-vote",
						STARTER_DECK_ID,
						"Group Vote",
						GameTypes.CardType.GROUP,
						"The group votes for the most creative answer from this round.",
						GameTypes.SpiceLevel.TENSION,
						1,
						GameTypes.CardTarget.GROUP,
						false,
						Set.of()
				)
		);
	}

	private record GameSettings(
			int minPlayers,
			int maxPlayers,
			GameTypes.SpiceLevel startSpiceLevel,
			GameTypes.SpiceLevel maxSpiceLevel,
			GameTypes.GamePace pace,
			boolean allowProps,
			boolean allowPairTasks,
			boolean allowGroupTasks
	) {
	}

	private record Card(
			String id,
			String deckId,
			String title,
			GameTypes.CardType type,
			String text,
			GameTypes.SpiceLevel spiceLevel,
			int actionPointCost,
			GameTypes.CardTarget target,
			boolean requiresProps,
			Set<GameTypes.BoundaryTag> boundaries
	) {
	}

	private static final class GameSession {
		private final String id;
		private final String code;
		private final GameTypes.GameMode mode;
		private GameTypes.GameStatus status = GameTypes.GameStatus.SETUP;
		private final List<Player> players = new ArrayList<>();
		private final GameSettings settings;
		private final List<String> deckIds;
		private int currentRound;
		private String currentTurnPlayerId;
		private int atmosphereLevel;
		private GameTypes.SpiceLevel currentSpiceLevel;
		private final Map<String, Integer> score = new LinkedHashMap<>();
		private final Instant createdAt;
		private Instant updatedAt;
		private final ArrayDeque<String> drawPile = new ArrayDeque<>();
		private PlayedCard currentCard;
		private int currentTurnIndex;

		private GameSession(
				String id,
				String code,
				GameTypes.GameMode mode,
				GameSettings settings,
				List<String> deckIds,
				Instant createdAt
		) {
			this.id = id;
			this.code = code;
			this.mode = mode;
			this.settings = settings;
			this.deckIds = deckIds;
			this.currentSpiceLevel = settings.startSpiceLevel;
			this.createdAt = createdAt;
			this.updatedAt = createdAt;
		}
	}

	private static final class Player {
		private final String id;
		private final String nickname;
		private final boolean host;
		private final List<CardInstance> hand = new ArrayList<>();
		private int points;
		private final ComfortProfile comfortProfile;

		private Player(String id, String nickname, boolean host, ComfortProfile comfortProfile) {
			this.id = id;
			this.nickname = nickname;
			this.host = host;
			this.comfortProfile = comfortProfile;
		}
	}

	private record ComfortProfile(
			String playerId,
			boolean confirmedAdult,
			Set<GameTypes.BoundaryTag> boundaries
	) {
	}

	private record CardInstance(
			String instanceId,
			String cardId,
			String ownerPlayerId,
			GameTypes.CardVisibility visibility
	) {
	}

	private record PlayedCard(
			String cardInstanceId,
			String playerId,
			String targetPlayerId,
			String cardId,
			Instant playedAt
	) {
	}
}
