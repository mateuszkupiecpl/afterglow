package pl.com.afterglow.domain;

public record AtmosphereLevel(int value) {

	private static final int MIN = 1;
	private static final int MAX = 5;

	public AtmosphereLevel {
		if (value < MIN || value > MAX) {
			throw new InvalidGameActionException("Atmosphere level must be between " + MIN + " and " + MAX + ".");
		}
	}

	public static AtmosphereLevel starting() {
		return new AtmosphereLevel(MIN);
	}

	public AtmosphereLevel increase() {
		return new AtmosphereLevel(Math.min(MAX, value + 1));
	}
}
