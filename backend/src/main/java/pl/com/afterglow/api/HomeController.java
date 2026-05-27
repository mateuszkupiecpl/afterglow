package pl.com.afterglow.api;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
class HomeController {

	private final HomeLinkAssembler linkAssembler;

	HomeController(HomeLinkAssembler linkAssembler) {
		this.linkAssembler = linkAssembler;
	}

	@GetMapping
	RepresentationModel<?> home() {
		var model = new RepresentationModel<>();
		return linkAssembler.toModel(model);
	}
}
