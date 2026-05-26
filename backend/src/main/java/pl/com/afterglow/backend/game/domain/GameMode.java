package pl.com.afterglow.backend.game.domain;

public enum GameMode implements CodedEnum {
	PARTY_WARMUP("party_warmup"),
	NIGHT_OF_TENSION("night_of_tension"),
	SLOW_BURN("slow_burn"),
	FANTASY_FINALE("fantasy_finale"),
	CUSTOM("custom");

	private final String code;

	GameMode(String code) {
		this.code = code;
	}

	public static GameMode fromCode(String value) {
		return DomainEnumParser.parse(GameMode.class, value);
	}

	@Override
	public String code() {
		return code;
	}
}
