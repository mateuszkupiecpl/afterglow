package pl.com.afterglow.backend.game.domain;

public enum GameStatus implements CodedEnum {
	SETUP("setup"),
	IN_PROGRESS("in_progress"),
	PAUSED("paused"),
	FINISHED("finished");

	private final String code;

	GameStatus(String code) {
		this.code = code;
	}

	public static GameStatus fromCode(String value) {
		return DomainEnumParser.parse(GameStatus.class, value);
	}

	@Override
	public String code() {
		return code;
	}
}
