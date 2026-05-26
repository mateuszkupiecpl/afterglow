package pl.com.afterglow.backend.game.domain;

public enum SpiceLevel implements CodedEnum {
	WARMUP("warmup"),
	TENSION("tension"),
	COURAGE("courage"),
	SPICY("spicy"),
	FINALE("finale");

	private final String code;

	SpiceLevel(String code) {
		this.code = code;
	}

	public static SpiceLevel fromCode(String value) {
		return DomainEnumParser.parse(SpiceLevel.class, value);
	}

	public boolean isAtMost(SpiceLevel other) {
		return ordinal() <= other.ordinal();
	}

	@Override
	public String code() {
		return code;
	}
}
