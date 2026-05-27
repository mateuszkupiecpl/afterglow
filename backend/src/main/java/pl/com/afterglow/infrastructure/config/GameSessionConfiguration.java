package pl.com.afterglow.infrastructure.config;

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
	GameSessionApplicationService gameSessionApplicationService(
			GameSessionRepository gameSessionRepository,
			CardCatalog cardCatalog,
			Clock clock,
			Random random
	) {
		return new GameSessionApplicationService(gameSessionRepository, cardCatalog, clock, random);
	}
}
