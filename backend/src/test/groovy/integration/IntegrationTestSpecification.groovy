package integration

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.client.ClientHttpResponse
import org.springframework.web.client.ResponseErrorHandler
import org.springframework.web.client.RestTemplate
import pl.com.afterglow.Application
import spock.lang.Specification

@SpringBootTest(classes = Application, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class IntegrationTestSpecification extends Specification {

	@LocalServerPort
	int port

	private RestTemplate restTemplate = new RestTemplate()

	def setup() {
		restTemplate.errorHandler = new NonThrowingErrorHandler()
	}

	protected ResponseEntity<Map> post(String path, Object body) {
		def headers = new HttpHeaders()
		headers.contentType = MediaType.APPLICATION_JSON
		restTemplate.postForEntity(url(path), new HttpEntity<>(body, headers), Map)
	}

	private String url(String path) {
		"http://localhost:${port}${path}"
	}

	private static final class NonThrowingErrorHandler implements ResponseErrorHandler {

		@Override
		boolean hasError(ClientHttpResponse response) {
			false
		}
	}
}
