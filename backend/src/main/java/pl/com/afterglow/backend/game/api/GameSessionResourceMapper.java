package pl.com.afterglow.backend.game.api;

import org.springframework.stereotype.Component;
import pl.com.afterglow.backend.game.application.DiceRollResult;
import pl.com.afterglow.backend.game.domain.BoundaryTag;
import pl.com.afterglow.backend.game.domain.Card;
import pl.com.afterglow.backend.game.domain.CardInstance;
import pl.com.afterglow.backend.game.domain.CodedEnum;
import pl.com.afterglow.backend.game.domain.GameMode;
import pl.com.afterglow.backend.game.domain.GamePace;
import pl.com.afterglow.backend.game.domain.GameSession;
import pl.com.afterglow.backend.game.domain.InvalidGameActionException;
import pl.com.afterglow.backend.game.domain.PlayedCard;
import pl.com.afterglow.backend.game.domain.Player;
import pl.com.afterglow.backend.game.domain.SpiceLevel;
import pl.com.afterglow.backend.game.domain.port.CardCatalog;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static pl.com.afterglow.backend.game.application.GameSessionCommands.AddPlayerCommand;
import static pl.com.afterglow.backend.game.application.GameSessionCommands.CreateGameSessionCommand;
import static pl.com.afterglow.backend.game.application.GameSessionCommands.DiceRollCommand;
import static pl.com.afterglow.backend.game.application.GameSessionCommands.GameSettingsCommand;
import static pl.com.afterglow.backend.game.application.GameSessionCommands.PlayCardCommand;
import static pl.com.afterglow.backend.game.application.GameSessionCommands.ResolveCurrentCardCommand;

@Component
class GameSessionResourceMapper {

	private final CardCatalog cardCatalog;

	GameSessionResourceMapper(CardCatalog cardCatalog) {
		this.cardCatalog = cardCatalog;
	}

	CreateGameSessionCommand toCommand(CreateGameSessionRequest request) {
		return new CreateGameSessionCommand(
				request.hostNickname(),
				Boolean.TRUE.equals(request.confirmedAdult()),
				GameMode.fromCode(request.mode()),
				settingsCommand(request.settings()),
				boundaries(request.boundaries())
		);
	}

	AddPlayerCommand toCommand(AddPlayerRequest request) {
		return new AddPlayerCommand(
				request.nickname(),
				Boolean.TRUE.equals(request.confirmedAdult()),
				boundaries(request.boundaries())
		);
	}

	PlayCardCommand toCommand(PlayCardRequest request) {
		return new PlayCardCommand(request.playerId(), request.cardInstanceId(), request.targetPlayerId());
	}

	ResolveCurrentCardCommand toCommand(ResolveCurrentCardRequest request) {
		return new ResolveCurrentCardCommand(request.playerId());
	}

	DiceRollCommand toCommand(DiceRollRequest request) {
		return new DiceRollCommand(request.playerId());
	}

	GameSessionResource toResource(GameSession session) {
		return new GameSessionResource(
				session.id(),
				session.code(),
				session.mode().code(),
				session.status().code(),
				session.players().stream().map(this::playerResource).toList(),
				settingsResource(session),
				session.deckIds(),
				session.currentRound(),
				session.currentTurnPlayerId(),
				session.atmosphereLevel(),
				session.currentSpiceLevel().code(),
				new LinkedHashMap<>(session.score()),
				playedCardResource(session.currentCard()),
				session.createdAt(),
				session.updatedAt(),
				links(session)
		);
	}

	DiceRollResource toResource(DiceRollResult result) {
		return new DiceRollResource(result.sessionId(), result.playerId(), result.value(), result.rolledAt());
	}

	private GameSettingsCommand settingsCommand(GameSettingsRequest request) {
		if (request == null) {
			return null;
		}
		return new GameSettingsCommand(
				SpiceLevel.fromCode(request.startSpiceLevel()),
				SpiceLevel.fromCode(request.maxSpiceLevel()),
				GamePace.fromCode(request.pace()),
				request.allowProps(),
				request.allowPairTasks(),
				request.allowGroupTasks()
		);
	}

	private Set<BoundaryTag> boundaries(Set<String> boundaries) {
		if (boundaries == null || boundaries.isEmpty()) {
			return Set.of();
		}
		return boundaries.stream()
				.map(BoundaryTag::fromCode)
				.filter(Objects::nonNull)
				.collect(Collectors.toUnmodifiableSet());
	}

	private PlayerResource playerResource(Player player) {
		return new PlayerResource(
				player.id(),
				player.nickname(),
				player.host(),
				player.hand().stream().map(this::cardInstanceResource).toList(),
				player.points(),
				new ComfortProfileResource(
						player.comfortProfile().playerId(),
						player.comfortProfile().confirmedAdult(),
						codes(player.comfortProfile().boundaries())
				)
		);
	}

	private GameSettingsResource settingsResource(GameSession session) {
		return new GameSettingsResource(
				session.settings().minPlayers(),
				session.settings().maxPlayers(),
				session.settings().startSpiceLevel().code(),
				session.settings().maxSpiceLevel().code(),
				session.settings().pace().code(),
				session.settings().allowProps(),
				session.settings().allowPairTasks(),
				session.settings().allowGroupTasks()
		);
	}

	private CardInstanceResource cardInstanceResource(CardInstance cardInstance) {
		return new CardInstanceResource(
				cardInstance.instanceId(),
				cardInstance.cardId(),
				cardInstance.ownerPlayerId(),
				cardInstance.visibility().code(),
				cardResource(cardInstance.cardId())
		);
	}

	private PlayedCardResource playedCardResource(PlayedCard playedCard) {
		if (playedCard == null) {
			return null;
		}
		return new PlayedCardResource(
				playedCard.cardInstanceId(),
				playedCard.playerId(),
				playedCard.targetPlayerId(),
				cardResource(playedCard.cardId()),
				playedCard.playedAt()
		);
	}

	private CardResource cardResource(String cardId) {
		Card card = cardCatalog.findById(cardId)
				.orElseThrow(() -> new InvalidGameActionException("Card is not available: " + cardId));
		return new CardResource(
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

	private Set<String> codes(Set<? extends CodedEnum> values) {
		return values.stream()
				.map(CodedEnum::code)
				.collect(Collectors.toUnmodifiableSet());
	}

	private Map<String, String> links(GameSession session) {
		return Map.of(
				"self", "/api/game-sessions/" + session.id(),
				"byCode", "/api/game-sessions/code/" + session.code()
		);
	}
}
