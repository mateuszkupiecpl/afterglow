package pl.com.afterglow.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class Player {

	private final String id;
	private final String nickname;
	private final boolean host;
	private final List<CardInstance> hand = new ArrayList<>();
	private int points;
	private final ComfortProfile comfortProfile;

	public Player(String id, String nickname, boolean host, ComfortProfile comfortProfile) {
		this.id = id;
		this.nickname = normalizeNickname(nickname);
		this.host = host;
		this.comfortProfile = comfortProfile;
	}

	public String id() {
		return id;
	}

	public String nickname() {
		return nickname;
	}

	public boolean host() {
		return host;
	}

	public List<CardInstance> hand() {
		return Collections.unmodifiableList(hand);
	}

	public int points() {
		return points;
	}

	public ComfortProfile comfortProfile() {
		return comfortProfile;
	}

	void clearHand() {
		hand.clear();
	}

	void receive(CardInstance cardInstance) {
		hand.add(cardInstance);
	}

	Optional<CardInstance> findCardInstance(String cardInstanceId) {
		return hand.stream()
				.filter(card -> card.instanceId().equals(cardInstanceId))
				.findFirst();
	}

	CardInstance removeCardFromHand(String cardInstanceId) {
		for (int index = 0; index < hand.size(); index++) {
			CardInstance card = hand.get(index);
			if (card.instanceId().equals(cardInstanceId)) {
				return hand.remove(index);
			}
		}
		throw new InvalidGameActionException("Card is not in the player's hand.");
	}

	void addPoint() {
		points += 1;
	}

	void removePointWithoutGoingBelowZero() {
		points = Math.max(0, points - 1);
	}

	private static String normalizeNickname(String nickname) {
		String normalized = nickname == null ? "" : nickname.trim();
		if (normalized.isBlank()) {
			throw new InvalidGameActionException("Nickname is required.");
		}
		return normalized;
	}
}
