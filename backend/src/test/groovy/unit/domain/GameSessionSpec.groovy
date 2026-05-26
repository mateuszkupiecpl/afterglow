package unit.domain

import pl.com.afterglow.backend.game.domain.BoundaryTag
import pl.com.afterglow.backend.game.domain.Card
import pl.com.afterglow.backend.game.domain.CardTarget
import pl.com.afterglow.backend.game.domain.CardType
import pl.com.afterglow.backend.game.domain.ComfortProfile
import pl.com.afterglow.backend.game.domain.GameMode
import pl.com.afterglow.backend.game.domain.GamePace
import pl.com.afterglow.backend.game.domain.GameSession
import pl.com.afterglow.backend.game.domain.GameSettings
import pl.com.afterglow.backend.game.domain.InvalidGameActionException
import pl.com.afterglow.backend.game.domain.Player
import pl.com.afterglow.backend.game.domain.SpiceLevel
import spock.lang.Specification

import java.time.Instant
import java.util.function.Supplier

class GameSessionSpec extends Specification {

	def 'does not deal cards that violate participant boundaries'() {
		given:
		def now = Instant.parse('2026-05-26T12:00:00Z')
		def session = twoPlayerSession([
				BoundaryTag.NO_PROPS
		] as Set, now)
		def safeCard = card('safe-question', false, [] as Set)
		def propCard = card('prop-card', true, [BoundaryTag.NO_PROPS] as Set)

		when:
		session.start([safeCard, propCard], new Random(1), ids(), now)

		then:
		session.players().every { it.hand().size() == 4 }
		session.players()
				.collectMany { it.hand() }
				.every { it.cardId() == safeCard.id() }
	}

	def 'refuses a card without reducing player points below zero'() {
		given:
		def now = Instant.parse('2026-05-26T12:00:00Z')
		def session = twoPlayerSession([] as Set, now)
		session.start([card('safe-question', false, [] as Set)], new Random(1), ids(), now)
		def currentPlayer = session.players().find { it.id() == session.currentTurnPlayerId() }
		def cardInstance = currentPlayer.hand().first()
		session.playCard(currentPlayer.id(), cardInstance.instanceId(), 'guest', [card('safe-question', false, [] as Set)], new Random(1), ids(), now)

		when:
		session.refuseCurrentCard(currentPlayer.id(), now)

		then:
		session.score()[currentPlayer.id()] == 0
		session.currentCard() == null
		session.currentTurnPlayerId() == 'guest'
	}

	def 'rejects starting when no card satisfies boundaries and settings'() {
		given:
		def now = Instant.parse('2026-05-26T12:00:00Z')
		def session = twoPlayerSession([BoundaryTag.NO_PROPS] as Set, now)

		when:
		session.start([card('prop-card', true, [BoundaryTag.NO_PROPS] as Set)], new Random(1), ids(), now)

		then:
		thrown(InvalidGameActionException)
	}

	private static GameSession twoPlayerSession(Set<BoundaryTag> hostBoundaries, Instant now) {
		def session = new GameSession(
				'session',
				'AGL-001',
				GameMode.PARTY_WARMUP,
				new GameSettings(2, 8, SpiceLevel.WARMUP, SpiceLevel.COURAGE, GamePace.STANDARD, true, true, true),
				['starter'],
				now
		)
		session.addHost(new Player('host', 'Host', true, new ComfortProfile('host', true, hostBoundaries)))
		session.addPlayer(new Player('guest', 'Guest', false, new ComfortProfile('guest', true, [] as Set)), now)
		session
	}

	private static Card card(String id, boolean requiresProps, Set<BoundaryTag> boundaries) {
		new Card(
				id,
				'starter',
				'Test Card',
				CardType.QUESTION,
				'Test text.',
				SpiceLevel.WARMUP,
				1,
				CardTarget.CHOSEN_PLAYER,
				requiresProps,
				boundaries
		)
	}

	private static Supplier<String> ids() {
		return new Supplier<String>() {
			private int index = 0

			@Override
			String get() {
				"card-instance-${++index}"
			}
		}
	}
}
