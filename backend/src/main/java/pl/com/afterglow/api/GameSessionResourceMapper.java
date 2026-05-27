package pl.com.afterglow.api;

import pl.com.afterglow.application.DiceRollResult;
import pl.com.afterglow.application.GameSessionSnapshot;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;

import static pl.com.afterglow.application.GameSessionCommands.AddPlayerCommand;
import static pl.com.afterglow.application.GameSessionCommands.CreateGameSessionCommand;
import static pl.com.afterglow.application.GameSessionCommands.DiceRollCommand;
import static pl.com.afterglow.application.GameSessionCommands.GameSettingsCommand;
import static pl.com.afterglow.application.GameSessionCommands.PlayCardCommand;
import static pl.com.afterglow.application.GameSessionCommands.ResolveCurrentCardCommand;

@Component
class GameSessionResourceMapper {

	CreateGameSessionCommand toCommand(CreateGameSessionRequest request) {
		return new CreateGameSessionCommand(
				request.hostNickname(),
				Boolean.TRUE.equals(request.confirmedAdult()),
				request.mode(),
				settingsCommand(request.settings()),
				request.boundaries()
		);
	}

	AddPlayerCommand toCommand(AddPlayerRequest request) {
		return new AddPlayerCommand(
				request.nickname(),
				Boolean.TRUE.equals(request.confirmedAdult()),
				request.boundaries()
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

	GameSessionResource toResource(GameSessionSnapshot session) {
		return new GameSessionResource(
				session.id(),
				session.code(),
				session.mode(),
				session.status(),
				session.players().stream().map(this::playerResource).toList(),
				settingsResource(session),
				session.deckIds(),
				session.currentRound(),
				session.currentTurnPlayerId(),
				session.atmosphereLevel(),
				session.currentSpiceLevel(),
				new LinkedHashMap<>(session.score()),
				playedCardResource(session.currentCard()),
				session.createdAt(),
				session.updatedAt()
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
				request.startSpiceLevel(),
				request.maxSpiceLevel(),
				request.pace(),
				request.allowProps(),
				request.allowPairTasks(),
				request.allowGroupTasks()
		);
	}

	private PlayerResource playerResource(GameSessionSnapshot.PlayerSnapshot player) {
		return new PlayerResource(
				player.id(),
				player.nickname(),
				player.host(),
				player.hand().stream().map(this::cardInstanceResource).toList(),
				player.points(),
				new ComfortProfileResource(
						player.comfortProfile().playerId(),
						player.comfortProfile().confirmedAdult(),
						player.comfortProfile().boundaries()
				)
		);
	}

	private GameSettingsResource settingsResource(GameSessionSnapshot session) {
		return new GameSettingsResource(
				session.settings().minPlayers(),
				session.settings().maxPlayers(),
				session.settings().startSpiceLevel(),
				session.settings().maxSpiceLevel(),
				session.settings().pace(),
				session.settings().allowProps(),
				session.settings().allowPairTasks(),
				session.settings().allowGroupTasks()
		);
	}

	private CardInstanceResource cardInstanceResource(GameSessionSnapshot.CardInstanceSnapshot cardInstance) {
		return new CardInstanceResource(
				cardInstance.instanceId(),
				cardInstance.cardId(),
				cardInstance.ownerPlayerId(),
				cardInstance.visibility(),
				cardResource(cardInstance.card())
		);
	}

	private PlayedCardResource playedCardResource(GameSessionSnapshot.PlayedCardSnapshot playedCard) {
		if (playedCard == null) {
			return null;
		}
		return new PlayedCardResource(
				playedCard.cardInstanceId(),
				playedCard.playerId(),
				playedCard.targetPlayerId(),
				cardResource(playedCard.card()),
				playedCard.playedAt()
		);
	}

	private CardResource cardResource(GameSessionSnapshot.CardSnapshot card) {
		return new CardResource(
				card.id(),
				card.deckId(),
				card.title(),
				card.type(),
				card.text(),
				card.spiceLevel(),
				card.actionPointCost(),
				card.target(),
				card.requiresProps(),
				card.boundaries()
		);
	}

}
