package pl.com.afterglow.domain;

public enum GamePace implements CodedEnum {
	FAST("fast"),
	STANDARD("standard"),
	CHILL("chill");

	private final String code;

	GamePace(String code) {
		this.code = code;
	}

	public static GamePace fromCode(String value) {
		return DomainEnumParser.parse(GamePace.class, value);
	}

	@Override
	public String code() {
		return code;
	}
}
