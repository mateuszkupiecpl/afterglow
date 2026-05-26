package pl.com.afterglow.api;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
class HomeController {

	private final HomeLinkAssembler links;

	HomeController(HomeLinkAssembler links) {
		this.links = links;
	}

	@GetMapping
	RepresentationModel<?> home() {
		return links.toModel();
	}
}
