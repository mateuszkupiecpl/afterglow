package pl.com.afterglow.api;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
class HomeLinkAssembler {

	RepresentationModel<?> toModel() {
		RepresentationModel<?> model = new RepresentationModel<>();
		model.add(linkTo(methodOn(HomeController.class).home()).withSelfRel());
		model.add(linkTo(methodOn(GameSessionController.class).create(null)).withRel("createGameSession"));
		model.add(linkTo(methodOn(GameSessionController.class).findByCode("{code}")).withRel("findGameSessionByCode"));
		return model;
	}
}
