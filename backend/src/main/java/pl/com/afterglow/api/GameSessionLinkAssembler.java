package pl.com.afterglow.api;

import org.springframework.stereotype.Component;
import pl.com.afterglow.application.GameSessionSnapshot;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
class GameSessionLinkAssembler {

	private static final String SETUP = "setup";
	private static final String IN_PROGRESS = "in_progress";

	GameSessionResource addLinks(GameSessionSnapshot session, GameSessionResource resource) {
		resource.add(linkTo(methodOn(GameSessionController.class).findById(session.id())).withSelfRel());
		resource.add(linkTo(methodOn(GameSessionController.class).findByCode(session.code())).withRel("byCode"));

		if (SETUP.equals(session.status())) {
			resource.add(linkTo(GameSessionController.class).slash(session.id()).slash("players").withRel("addPlayer"));
			resource.add(linkTo(methodOn(GameSessionController.class).start(session.id())).withRel("start"));
		}
		if (IN_PROGRESS.equals(session.status())) {
			resource.add(linkTo(GameSessionController.class).slash(session.id()).slash("cards/play").withRel("playCard"));
			resource.add(linkTo(GameSessionController.class).slash(session.id()).slash("current-card/complete").withRel("completeCurrentCard"));
			resource.add(linkTo(GameSessionController.class).slash(session.id()).slash("current-card/refuse").withRel("refuseCurrentCard"));
			resource.add(linkTo(GameSessionController.class).slash(session.id()).slash("dice-rolls").withRel("rollDie"));
			resource.add(linkTo(methodOn(GameSessionController.class).finish(session.id())).withRel("finish"));
		}
		return resource;
	}
}
