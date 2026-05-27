package pl.com.afterglow.domain;

public enum CardVisibility implements CodedEnum {
	PRIVATE("private"),
	PUBLIC("public");

	private final String code;

	CardVisibility(String code) {
		this.code = code;
	}

	public static CardVisibility fromCode(String value) {
		return DomainEnumParser.parse(CardVisibility.class, value);
	}

	@Override
	public String code() {
		return code;
	}
}
