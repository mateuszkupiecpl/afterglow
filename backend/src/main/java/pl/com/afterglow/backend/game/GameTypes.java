package pl.com.afterglow.backend.game;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Locale;

public final class GameTypes {

	private GameTypes() {
	}

	public enum GameMode implements JsonBackedEnum {
		PARTY_WARMUP("party_warmup"),
		NIGHT_OF_TENSION("night_of_tension"),
		SLOW_BURN("slow_burn"),
		FANTASY_FINALE("fantasy_finale"),
		CUSTOM("custom");

		private final String jsonValue;

		GameMode(String jsonValue) {
			this.jsonValue = jsonValue;
		}

		@JsonCreator
		public static GameMode fromJson(String value) {
			return parse(GameMode.class, value);
		}

		@Override
		@JsonValue
		public String jsonValue() {
			return jsonValue;
		}
	}

	public enum GameStatus implements JsonBackedEnum {
		SETUP("setup"),
		IN_PROGRESS("in_progress"),
		PAUSED("paused"),
		FINISHED("finished");

		private final String jsonValue;

		GameStatus(String jsonValue) {
			this.jsonValue = jsonValue;
		}

		@JsonCreator
		public static GameStatus fromJson(String value) {
			return parse(GameStatus.class, value);
		}

		@Override
		@JsonValue
		public String jsonValue() {
			return jsonValue;
		}
	}

	public enum GamePace implements JsonBackedEnum {
		FAST("fast"),
		STANDARD("standard"),
		CHILL("chill");

		private final String jsonValue;

		GamePace(String jsonValue) {
			this.jsonValue = jsonValue;
		}

		@JsonCreator
		public static GamePace fromJson(String value) {
			return parse(GamePace.class, value);
		}

		@Override
		@JsonValue
		public String jsonValue() {
			return jsonValue;
		}
	}

	public enum SpiceLevel implements JsonBackedEnum {
		WARMUP("warmup"),
		TENSION("tension"),
		COURAGE("courage"),
		SPICY("spicy"),
		FINALE("finale");

		private final String jsonValue;

		SpiceLevel(String jsonValue) {
			this.jsonValue = jsonValue;
		}

		@JsonCreator
		public static SpiceLevel fromJson(String value) {
			return parse(SpiceLevel.class, value);
		}

		boolean isAtMost(SpiceLevel other) {
			return ordinal() <= other.ordinal();
		}

		@Override
		@JsonValue
		public String jsonValue() {
			return jsonValue;
		}
	}

	public enum CardType implements JsonBackedEnum {
		QUESTION("question"),
		CHALLENGE("challenge"),
		GROUP("group"),
		INTERLUDE("interlude"),
		FANTASY("fantasy"),
		PROP("prop"),
		SPECIAL("special"),
		REACTION("reaction"),
		EVENT("event");

		private final String jsonValue;

		CardType(String jsonValue) {
			this.jsonValue = jsonValue;
		}

		@JsonCreator
		public static CardType fromJson(String value) {
			return parse(CardType.class, value);
		}

		@Override
		@JsonValue
		public String jsonValue() {
			return jsonValue;
		}
	}

	public enum CardTarget implements JsonBackedEnum {
		SELF("self"),
		CHOSEN_PLAYER("chosen_player"),
		RANDOM_PLAYER("random_player"),
		PAIR("pair"),
		GROUP("group"),
		EVERYONE("everyone"),
		LEFT_PLAYER("left_player"),
		RIGHT_PLAYER("right_player");

		private final String jsonValue;

		CardTarget(String jsonValue) {
			this.jsonValue = jsonValue;
		}

		@JsonCreator
		public static CardTarget fromJson(String value) {
			return parse(CardTarget.class, value);
		}

		@Override
		@JsonValue
		public String jsonValue() {
			return jsonValue;
		}
	}

	public enum CardVisibility implements JsonBackedEnum {
		PRIVATE("private"),
		PUBLIC("public");

		private final String jsonValue;

		CardVisibility(String jsonValue) {
			this.jsonValue = jsonValue;
		}

		@JsonCreator
		public static CardVisibility fromJson(String value) {
			return parse(CardVisibility.class, value);
		}

		@Override
		@JsonValue
		public String jsonValue() {
			return jsonValue;
		}
	}

	public enum BoundaryTag implements JsonBackedEnum {
		NO_NUDITY("no_nudity"),
		NO_CLOTHING_REMOVAL("no_clothing_removal"),
		NO_TOUCH("no_touch"),
		VERBAL_ONLY("verbal_only"),
		NO_PROPS("no_props"),
		NO_RANDOM_PARTNER("no_random_partner"),
		PARTNER_ONLY("partner_only"),
		NO_GROUP_PHYSICAL_TASKS("no_group_physical_tasks");

		private final String jsonValue;

		BoundaryTag(String jsonValue) {
			this.jsonValue = jsonValue;
		}

		@JsonCreator
		public static BoundaryTag fromJson(String value) {
			return parse(BoundaryTag.class, value);
		}

		@Override
		@JsonValue
		public String jsonValue() {
			return jsonValue;
		}
	}

	private interface JsonBackedEnum {
		String jsonValue();
	}

	private static <E extends Enum<E> & JsonBackedEnum> E parse(Class<E> enumType, String value) {
		if (value == null || value.isBlank()) {
			return null;
		}
		String normalized = value.trim().toLowerCase(Locale.ROOT);
		return Arrays.stream(enumType.getEnumConstants())
				.filter(candidate -> candidate.jsonValue().equals(normalized))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("Unknown " + enumType.getSimpleName() + ": " + value));
	}
}
