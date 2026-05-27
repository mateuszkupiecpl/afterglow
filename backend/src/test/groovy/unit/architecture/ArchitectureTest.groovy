package unit.architecture

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import spock.lang.Specification

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses

class ArchitectureTest extends Specification {

	private static final JavaClasses PRODUCTION_CLASSES = new ClassFileImporter()
			.withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
			.importPackages('pl.com.afterglow')

	def 'api layer does not depend on domain or infrastructure'() {
		expect:
		noClasses()
				.that().resideInAPackage('pl.com.afterglow.api..')
				.should().dependOnClassesThat().resideInAnyPackage(
						'pl.com.afterglow.domain..',
						'pl.com.afterglow.infrastructure..'
				)
				.check(PRODUCTION_CLASSES)
	}

	def 'application layer does not depend on api or infrastructure'() {
		expect:
		noClasses()
				.that().resideInAPackage('pl.com.afterglow.application..')
				.should().dependOnClassesThat().resideInAnyPackage(
						'pl.com.afterglow.api..',
						'pl.com.afterglow.infrastructure..'
				)
				.check(PRODUCTION_CLASSES)
	}

	def 'domain layer does not depend on outer layers'() {
		expect:
		noClasses()
				.that().resideInAPackage('pl.com.afterglow.domain..')
				.should().dependOnClassesThat().resideInAnyPackage(
						'pl.com.afterglow.application..',
						'pl.com.afterglow.api..',
						'pl.com.afterglow.infrastructure..',
						'org.springframework..'
				)
				.check(PRODUCTION_CLASSES)
	}

	def 'game session aggregate is only used by allowed backend layers'() {
		expect:
		noClasses()
				.that().resideOutsideOfPackages(
						'pl.com.afterglow.domain..',
						'pl.com.afterglow.application..',
						'pl.com.afterglow.infrastructure.persistence..'
				)
				.should().dependOnClassesThat().haveFullyQualifiedName('pl.com.afterglow.domain.GameSession')
				.check(PRODUCTION_CLASSES)
	}
}
