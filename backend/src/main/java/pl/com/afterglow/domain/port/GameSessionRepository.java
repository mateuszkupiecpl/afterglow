package pl.com.afterglow.domain.port;

import pl.com.afterglow.domain.GameSession;

import java.util.Optional;

public interface GameSessionRepository {

	Optional<GameSession> findById(String sessionId);

	Optional<GameSession> findByCode(String code);

	boolean existsByCode(String code);

	void save(GameSession session);
}
