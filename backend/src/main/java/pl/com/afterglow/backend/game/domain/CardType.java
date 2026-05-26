package pl.com.afterglow.backend.game.domain;

public enum CardType implements CodedEnum {
	QUESTION("question"),
	CHALLENGE("challenge"),
	GROUP("group"),
	INTERLUDE("interlude"),
	FANTASY("fantasy"),
	PROP("prop"),
	SPECIAL("special"),
	REACTION("reaction"),
	EVENT("event");

	private final String code;

	CardType(String code) {
		this.code = code;
	}

	public static CardType fromCode(String value) {
		return DomainEnumParser.parse(CardType.class, value);
	}

	@Override
	public String code() {
		return code;
	}
}
