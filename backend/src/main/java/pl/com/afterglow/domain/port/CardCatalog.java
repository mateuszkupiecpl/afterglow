package pl.com.afterglow.domain.port;

import pl.com.afterglow.domain.Card;

import java.util.List;
import java.util.Optional;

public interface CardCatalog {

	List<Card> findByDeckIds(List<String> deckIds);

	Optional<Card> findById(String cardId);
}
