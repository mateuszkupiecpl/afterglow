package pl.com.afterglow.infrastructure;

import pl.com.afterglow.domain.GameSession;
import pl.com.afterglow.domain.port.GameSessionRepository;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class InMemoryGameSessionRepository implements GameSessionRepository {

	private final Map<String, GameSession> sessionsById = new ConcurrentHashMap<>();
	private final Map<String, String> sessionIdsByCode = new ConcurrentHashMap<>();

	@Override
	public Optional<GameSession> findById(String sessionId) {
		return Optional.ofNullable(sessionsById.get(sessionId));
	}

	@Override
	public Optional<GameSession> findByCode(String code) {
		return Optional.ofNullable(sessionIdsByCode.get(normalizeCode(code)))
				.flatMap(this::findById);
	}

	@Override
	public boolean existsByCode(String code) {
		return sessionIdsByCode.containsKey(normalizeCode(code));
	}

	@Override
	public void save(GameSession session) {
		sessionsById.put(session.id(), session);
		sessionIdsByCode.put(normalizeCode(session.code()), session.id());
	}

	private String normalizeCode(String code) {
		return code == null ? "" : code.trim().toUpperCase(Locale.ROOT);
	}
}
