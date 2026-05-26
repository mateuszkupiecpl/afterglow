package pl.com.afterglow.infrastructure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.com.afterglow.application.GameSessionApplicationService;
import pl.com.afterglow.domain.port.CardCatalog;
import pl.com.afterglow.domain.port.GameSessionRepository;

import java.time.Clock;
import java.util.Random;

@Configuration
class GameSessionConfiguration {

	@Bean
	Clock clock() {
		return Clock.systemUTC();
	}

	@Bean
	Random random() {
		return new Random();
	}

	@Bean
	CardCatalog cardCatalog() {
		return new StarterCardCatalog();
	}

	@Bean
	GameSessionRepository gameSessionRepository() {
		return new InMemoryGameSessionRepository();
	}

	@Bean
	GameSessionApplicationService gameSessionApplicationService(
			GameSessionRepository gameSessionRepository,
			CardCatalog cardCatalog,
			Clock clock,
			Random random
	) {
		return new GameSessionApplicationService(gameSessionRepository, cardCatalog, clock, random);
	}
}
