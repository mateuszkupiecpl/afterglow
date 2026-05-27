package pl.com.afterglow.domain;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public final class GameSession {

	public static final int MIN_PLAYERS = 2;
	public static final int MAX_PLAYERS = 8;
	public static final int STARTING_HAND_SIZE = 4;
	public static final int HAND_LIMIT = 5;

	private final String id;
	private final String code;
	private final GameMode mode;
	private GameStatus status = GameStatus.SETUP;
	private final List<Player> players = new ArrayList<>();
	private final GameSettings settings;
	private final List<String> deckIds;
	private int currentRound;
	private String currentTurnPlayerId;
	private AtmosphereLevel atmosphereLevel = AtmosphereLevel.starting();
	private SpiceLevel currentSpiceLevel;
	private final Map<String, Integer> score = new LinkedHashMap<>();
	private final Instant createdAt;
	private Instant updatedAt;
	private final ArrayDeque<String> drawPile = new ArrayDeque<>();
	private PlayedCard currentCard;
	private int currentTurnIndex;

	public GameSession(
			String id,
			String code,
			GameMode mode,
			GameSettings settings,
			List<String> deckIds,
			Instant createdAt
	) {
		this.id = id;
		this.code = code;
		this.mode = mode;
		this.settings = settings;
		this.deckIds = List.copyOf(deckIds);
		this.currentSpiceLevel = settings.startSpiceLevel();
		this.createdAt = createdAt;
		this.updatedAt = createdAt;
	}

	public void addHost(Player host) {
		requireStatus(GameStatus.SETUP, "Host can only be added while the session is in setup.");
		if (!players.isEmpty()) {
			throw new InvalidGameActionException("A session can only have one host.");
		}
		players.add(host);
		score.put(host.id(), host.points());
	}

	public void addPlayer(Player player, Instant now) {
		requireStatus(GameStatus.SETUP, "Players can only join during setup.");
		if (players.size() >= settings.maxPlayers()) {
			throw new InvalidGameActionException("The session already has the maximum number of players.");
		}
		players.add(player);
		score.put(player.id(), player.points());
		touch(now);
	}

	public void start(List<Card> availableCards, Random random, Supplier<String> idGenerator, Instant now) {
		requireStatus(GameStatus.SETUP, "Only setup sessions can be started.");
		if (players.size() < settings.minPlayers()) {
			throw new InvalidGameActionException("A session needs at least " + settings.minPlayers() + " players.");
		}
		drawPile.clear();
		drawPile.addAll(buildDrawPile(availableCards, random));
		for (var player : players) {
			player.clearHand();
			for (var index = 0; index < STARTING_HAND_SIZE; index++) {
				player.receive(drawCardFor(player.id(), availableCards, random, idGenerator));
			}
		}
		currentRound = 1;
		currentTurnIndex = 0;
		currentTurnPlayerId = players.getFirst().id();
		currentSpiceLevel = settings.startSpiceLevel();
		status = GameStatus.IN_PROGRESS;
		touch(now);
	}

	public void playCard(
			String playerId,
			String cardInstanceId,
			String targetPlayerId,
			List<Card> availableCards,
			Random random,
			Supplier<String> idGenerator,
			Instant now
	) {
		requireStatus(GameStatus.IN_PROGRESS, "Cards can only be played while a session is in progress.");
		if (currentCard != null) {
			throw new InvalidGameActionException("Resolve the current card before playing another card.");
		}
		if (!playerId.equals(currentTurnPlayerId)) {
			throw new InvalidGameActionException("Only the current turn player can play a card.");
		}

		var player = requirePlayer(playerId);
		var cardInstance = player.findCardInstance(cardInstanceId)
				.orElseThrow(() -> new InvalidGameActionException("Card is not in the player's hand."));
		var card = requireCard(cardsById(availableCards), cardInstance.cardId());
		validateTarget(card, targetPlayerId);

		player.removeCardFromHand(cardInstanceId);
		if (player.hand().size() < HAND_LIMIT) {
			player.receive(drawCardFor(player.id(), availableCards, random, idGenerator));
		}
		currentCard = new PlayedCard(
				cardInstance.instanceId(),
				player.id(),
				targetPlayerId,
				card.id(),
				now
		);
		touch(now);
	}

	public void completeCurrentCard(String playerId, Instant now) {
		requireCurrentCardOwner(playerId);
		var player = requirePlayer(playerId);
		player.addPoint();
		score.put(player.id(), player.points());
		atmosphereLevel = atmosphereLevel.increase();
		advanceTurn();
		touch(now);
	}

	public void refuseCurrentCard(String playerId, Instant now) {
		requireCurrentCardOwner(playerId);
		var player = requirePlayer(playerId);
		player.removePointWithoutGoingBelowZero();
		score.put(player.id(), player.points());
		advanceTurn();
		touch(now);
	}

	public void verifyDiceRollAllowed(String playerId) {
		if (status == GameStatus.FINISHED) {
			throw new InvalidGameActionException("Finished sessions cannot roll dice.");
		}
		if (playerId != null && !playerId.isBlank()) {
			requirePlayer(playerId);
		}
	}

	public void finish(Instant now) {
		if (status == GameStatus.FINISHED) {
			return;
		}
		status = GameStatus.FINISHED;
		currentCard = null;
		touch(now);
	}

	public String id() {
		return id;
	}

	public String code() {
		return code;
	}

	public GameMode mode() {
		return mode;
	}

	public GameStatus status() {
		return status;
	}

	public List<Player> players() {
		return Collections.unmodifiableList(players);
	}

	public GameSettings settings() {
		return settings;
	}

	public List<String> deckIds() {
		return deckIds;
	}

	public int currentRound() {
		return currentRound;
	}

	public String currentTurnPlayerId() {
		return currentTurnPlayerId;
	}

	public int atmosphereLevel() {
		return atmosphereLevel.value();
	}

	public SpiceLevel currentSpiceLevel() {
		return currentSpiceLevel;
	}

	public Map<String, Integer> score() {
		return Collections.unmodifiableMap(score);
	}

	public PlayedCard currentCard() {
		return currentCard;
	}

	public Instant createdAt() {
		return createdAt;
	}

	public Instant updatedAt() {
		return updatedAt;
	}

	private List<String> buildDrawPile(List<Card> availableCards, Random random) {
		var sessionBoundaries = players.stream()
				.flatMap(player -> player.comfortProfile().boundaries().stream())
				.collect(Collectors.toCollection(() -> EnumSet.noneOf(BoundaryTag.class)));
		var eligibleCards = availableCards.stream()
				.filter(card -> card.spiceLevel().isAtMost(settings.maxSpiceLevel()))
				.filter(card -> settings.allowProps() || !card.requiresProps())
				.filter(card -> settings.allowPairTasks() || card.target() != CardTarget.PAIR)
				.filter(card -> settings.allowGroupTasks() || !isGroupTarget(card.target()))
				.filter(card -> Collections.disjoint(card.boundaries(), sessionBoundaries))
				.map(Card::id)
				.toList();
		if (eligibleCards.isEmpty()) {
			throw new InvalidGameActionException("No cards match the session settings and player boundaries.");
		}
		var newDrawPile = new ArrayList<String>();
		while (newDrawPile.size() < players.size() * HAND_LIMIT * 4) {
			newDrawPile.addAll(eligibleCards);
		}
		Collections.shuffle(newDrawPile, random);
		return newDrawPile;
	}

	private boolean isGroupTarget(CardTarget target) {
		return target == CardTarget.GROUP || target == CardTarget.EVERYONE;
	}

	private CardInstance drawCardFor(
			String playerId,
			List<Card> availableCards,
			Random random,
			Supplier<String> idGenerator
	) {
		if (drawPile.isEmpty()) {
			drawPile.addAll(buildDrawPile(availableCards, random));
		}
		return new CardInstance(idGenerator.get(), drawPile.removeFirst(), playerId, CardVisibility.PRIVATE);
	}

	private void validateTarget(Card card, String targetPlayerId) {
		if (card.target() == CardTarget.CHOSEN_PLAYER) {
			if (targetPlayerId == null || targetPlayerId.isBlank()) {
				throw new InvalidGameActionException("This card requires a target player.");
			}
			var targetPlayer = requirePlayer(targetPlayerId);
			if (!Collections.disjoint(card.boundaries(), targetPlayer.comfortProfile().boundaries())) {
				throw new InvalidGameActionException("The selected card conflicts with the target player's boundaries.");
			}
		}
	}

	private void requireCurrentCardOwner(String playerId) {
		requireStatus(GameStatus.IN_PROGRESS, "The session is not in progress.");
		if (currentCard == null) {
			throw new InvalidGameActionException("There is no current card to resolve.");
		}
		if (!currentCard.playerId().equals(playerId)) {
			throw new InvalidGameActionException("Only the player who played the current card can resolve it.");
		}
	}

	private void advanceTurn() {
		currentCard = null;
		currentTurnIndex = (currentTurnIndex + 1) % players.size();
		if (currentTurnIndex == 0) {
			currentRound += 1;
		}
		currentTurnPlayerId = players.get(currentTurnIndex).id();
	}

	private Player requirePlayer(String playerId) {
		return players.stream()
				.filter(player -> player.id().equals(playerId))
				.findFirst()
				.orElseThrow(() -> new InvalidGameActionException("Player does not belong to this session."));
	}

	private Card requireCard(Map<String, Card> cardsById, String cardId) {
		var card = cardsById.get(cardId);
		if (card == null) {
			throw new InvalidGameActionException("Card is not available: " + cardId);
		}
		return card;
	}

	private Map<String, Card> cardsById(List<Card> availableCards) {
		return availableCards.stream()
				.collect(Collectors.toMap(Card::id, Function.identity()));
	}

	private void requireStatus(GameStatus expectedStatus, String message) {
		if (status != expectedStatus) {
			throw new InvalidGameActionException(message);
		}
	}

	private void touch(Instant now) {
		updatedAt = now;
	}
}
