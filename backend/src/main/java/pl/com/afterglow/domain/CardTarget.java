package pl.com.afterglow.domain;

public enum CardTarget implements CodedEnum {
	SELF("self"),
	CHOSEN_PLAYER("chosen_player"),
	RANDOM_PLAYER("random_player"),
	PAIR("pair"),
	GROUP("group"),
	EVERYONE("everyone"),
	LEFT_PLAYER("left_player"),
	RIGHT_PLAYER("right_player");

	private final String code;

	CardTarget(String code) {
		this.code = code;
	}

	public static CardTarget fromCode(String value) {
		return DomainEnumParser.parse(CardTarget.class, value);
	}

	@Override
	public String code() {
		return code;
	}
}
