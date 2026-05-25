package pl.com.afterglow.backend

import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest
class BackendApplicationTests extends Specification {

	def 'context loads'() {
		expect:
		true
	}
}
