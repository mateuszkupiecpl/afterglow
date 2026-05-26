package pl.com.afterglow.infrastructure;

import pl.com.afterglow.domain.BoundaryTag;
import pl.com.afterglow.domain.Card;
import pl.com.afterglow.domain.CardTarget;
import pl.com.afterglow.domain.CardType;
import pl.com.afterglow.domain.SpiceLevel;
import pl.com.afterglow.domain.port.CardCatalog;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static pl.com.afterglow.application.GameSessionApplicationService.STARTER_DECK_ID;

public final class StarterCardCatalog implements CardCatalog {

	private final List<Card> starterCards = starterCards();
	private final Map<String, Card> cardsById = starterCards.stream()
			.collect(Collectors.toUnmodifiableMap(Card::id, Function.identity()));

	@Override
	public List<Card> findByDeckIds(List<String> deckIds) {
		return starterCards.stream()
				.filter(card -> deckIds.contains(card.deckId()))
				.toList();
	}

	@Override
	public Optional<Card> findById(String cardId) {
		return Optional.ofNullable(cardsById.get(cardId));
	}

	private static List<Card> starterCards() {
		return List.of(
				new Card(
						"starter-warmup-question",
						STARTER_DECK_ID,
						"Warmup Question",
						CardType.QUESTION,
						"Ask another player a light question they can answer comfortably.",
						SpiceLevel.WARMUP,
						1,
						CardTarget.CHOSEN_PLAYER,
						false,
						Set.of()
				),
				new Card(
						"starter-group-question",
						STARTER_DECK_ID,
						"Table Question",
						CardType.GROUP,
						"Everyone answers a playful question in one sentence.",
						SpiceLevel.WARMUP,
						1,
						CardTarget.EVERYONE,
						false,
						Set.of()
				),
				new Card(
						"starter-interlude",
						STARTER_DECK_ID,
						"Atmosphere Break",
						CardType.INTERLUDE,
						"Pause for a short music or conversation break before the next turn.",
						SpiceLevel.WARMUP,
						1,
						CardTarget.GROUP,
						false,
						Set.of()
				),
				new Card(
						"starter-compliment-challenge",
						STARTER_DECK_ID,
						"Compliment Challenge",
						CardType.CHALLENGE,
						"Choose a player and give them a specific compliment.",
						SpiceLevel.TENSION,
						1,
						CardTarget.CHOSEN_PLAYER,
						false,
						Set.of()
				),
				new Card(
						"starter-counter",
						STARTER_DECK_ID,
						"Negotiate",
						CardType.REACTION,
						"Turn the current task into a softer version that still respects the card.",
						SpiceLevel.WARMUP,
						1,
						CardTarget.SELF,
						false,
						Set.of()
				),
				new Card(
						"starter-pair-story",
						STARTER_DECK_ID,
						"Two-Person Story",
						CardType.CHALLENGE,
						"Pick a willing partner and invent a short flirty story together.",
						SpiceLevel.TENSION,
						2,
						CardTarget.PAIR,
						false,
						Set.of(BoundaryTag.NO_RANDOM_PARTNER, BoundaryTag.PARTNER_ONLY)
				),
				new Card(
						"starter-prop-prompt",
						STARTER_DECK_ID,
						"Prop Prompt",
						CardType.PROP,
						"Use an agreed safe prop as a conversation prompt.",
						SpiceLevel.COURAGE,
						2,
						CardTarget.CHOSEN_PLAYER,
						true,
						Set.of(BoundaryTag.NO_PROPS)
				),
				new Card(
						"starter-group-vote",
						STARTER_DECK_ID,
						"Group Vote",
						CardType.GROUP,
						"The group votes for the most creative answer from this round.",
						SpiceLevel.TENSION,
						1,
						CardTarget.GROUP,
						false,
						Set.of()
				)
		);
	}
}
