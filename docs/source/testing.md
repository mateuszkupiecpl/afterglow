# Testing

This document defines the backend testing strategy for future implementation.

## Backend Test Stack

- Language: Groovy.
- Framework: Spock.
- External dependency mocking: WireMock.
- Integration/test HTTP calls: RestTemplate.

## Directory Structure

```text
src/test/groovy
|-- unit/
`-- integration/
```

## Unit Tests

Unit tests:

- use the `Test` suffix,
- extend `Specification`,
- do not boot a Spring context,
- are fast and isolated,
- focus heavily on domain behavior, policies, value objects, and use-case
  decisions.

Example:

```groovy
package unit.domain

import spock.lang.Specification

class CardEligibilityPolicyTest extends Specification {
    def "rejects a card when it violates a player boundary"() {
        expect:
        // domain-only expectation here
    }
}
```

## Integration Tests

Integration tests:

- use the `IT` suffix,
- extend `IntegrationTestSpecification`,
- verify Spring wiring, adapters, persistence, API mapping, and integration
  behavior,
- avoid duplicating domain rule coverage already handled by unit tests.

`IntegrationTestSpecification` is responsible for:

- booting the Spring context,
- future database support,
- future security setup,
- shared fixtures,
- reusable test utilities,
- integration helpers.

Example:

```groovy
package integration.api

import integration.IntegrationTestSpecification

class GameSessionResourceIT extends IntegrationTestSpecification {
    def "creates a local game session through the API"() {
        expect:
        // integration expectation here
    }
}
```

## Principles

- Maximize domain testing.
- Use integration tests to verify wiring and adapter behavior.
- Avoid unnecessary context loading.
- Keep slow tests out of the unit suite.
