package pl.com.afterglow.backend.game.domain;

import java.util.Arrays;
import java.util.Locale;

final class DomainEnumParser {

	private DomainEnumParser() {
	}

	static <E extends Enum<E> & CodedEnum> E parse(Class<E> enumType, String value) {
		if (value == null || value.isBlank()) {
			return null;
		}
		String normalized = value.trim().toLowerCase(Locale.ROOT);
		return Arrays.stream(enumType.getEnumConstants())
				.filter(candidate -> candidate.code().equals(normalized))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("Unknown " + enumType.getSimpleName() + ": " + value));
	}
}
