package pl.com.afterglow.api;

import org.springframework.stereotype.Component;
import pl.com.afterglow.domain.GameSession;
import pl.com.afterglow.domain.GameStatus;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
class GameSessionLinkAssembler {

	GameSessionResource addLinks(GameSession session, GameSessionResource resource) {
		resource.add(linkTo(methodOn(GameSessionController.class).findById(session.id())).withSelfRel());
		resource.add(linkTo(methodOn(GameSessionController.class).findByCode(session.code())).withRel("byCode"));

		if (session.status() == GameStatus.SETUP) {
			resource.add(linkTo(GameSessionController.class).slash(session.id()).slash("players").withRel("addPlayer"));
			resource.add(linkTo(methodOn(GameSessionController.class).start(session.id())).withRel("start"));
		}
		if (session.status() == GameStatus.IN_PROGRESS) {
			resource.add(linkTo(GameSessionController.class).slash(session.id()).slash("cards/play").withRel("playCard"));
			resource.add(linkTo(GameSessionController.class).slash(session.id()).slash("current-card/complete").withRel("completeCurrentCard"));
			resource.add(linkTo(GameSessionController.class).slash(session.id()).slash("current-card/refuse").withRel("refuseCurrentCard"));
			resource.add(linkTo(GameSessionController.class).slash(session.id()).slash("dice-rolls").withRel("rollDie"));
			resource.add(linkTo(methodOn(GameSessionController.class).finish(session.id())).withRel("finish"));
		}
		return resource;
	}
}
