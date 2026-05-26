package pl.com.afterglow.api;

import org.springframework.hateoas.RepresentationModel;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public class GameSessionResource extends RepresentationModel<GameSessionResource> {

	private final String id;
	private final String code;
	private final String mode;
	private final String status;
	private final List<PlayerResource> players;
	private final GameSettingsResource settings;
	private final List<String> deckIds;
	private final int currentRound;
	private final String currentTurnPlayerId;
	private final int atmosphereLevel;
	private final String currentSpiceLevel;
	private final Map<String, Integer> score;
	private final PlayedCardResource currentCard;
	private final Instant createdAt;
	private final Instant updatedAt;

	GameSessionResource(
			String id,
			String code,
			String mode,
			String status,
			List<PlayerResource> players,
			GameSettingsResource settings,
			List<String> deckIds,
			int currentRound,
			String currentTurnPlayerId,
			int atmosphereLevel,
			String currentSpiceLevel,
			Map<String, Integer> score,
			PlayedCardResource currentCard,
			Instant createdAt,
			Instant updatedAt
	) {
		this.id = id;
		this.code = code;
		this.mode = mode;
		this.status = status;
		this.players = players;
		this.settings = settings;
		this.deckIds = deckIds;
		this.currentRound = currentRound;
		this.currentTurnPlayerId = currentTurnPlayerId;
		this.atmosphereLevel = atmosphereLevel;
		this.currentSpiceLevel = currentSpiceLevel;
		this.score = score;
		this.currentCard = currentCard;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public String getId() {
		return id;
	}

	public String getCode() {
		return code;
	}

	public String getMode() {
		return mode;
	}

	public String getStatus() {
		return status;
	}

	public List<PlayerResource> getPlayers() {
		return players;
	}

	public GameSettingsResource getSettings() {
		return settings;
	}

	public List<String> getDeckIds() {
		return deckIds;
	}

	public int getCurrentRound() {
		return currentRound;
	}

	public String getCurrentTurnPlayerId() {
		return currentTurnPlayerId;
	}

	public int getAtmosphereLevel() {
		return atmosphereLevel;
	}

	public String getCurrentSpiceLevel() {
		return currentSpiceLevel;
	}

	public Map<String, Integer> getScore() {
		return score;
	}

	public PlayedCardResource getCurrentCard() {
		return currentCard;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public Instant getUpdatedAt() {
		return updatedAt;
	}
}
