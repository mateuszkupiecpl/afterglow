package pl.com.afterglow.backend.game.domain.port;

import pl.com.afterglow.backend.game.domain.GameSession;

import java.util.Optional;

public interface GameSessionRepository {

	Optional<GameSession> findById(String sessionId);

	Optional<GameSession> findByCode(String code);

	boolean existsByCode(String code);

	void save(GameSession session);
}
