package pl.com.afterglow.domain;

public enum BoundaryTag implements CodedEnum {
	NO_NUDITY("no_nudity"),
	NO_CLOTHING_REMOVAL("no_clothing_removal"),
	NO_TOUCH("no_touch"),
	VERBAL_ONLY("verbal_only"),
	NO_PROPS("no_props"),
	NO_RANDOM_PARTNER("no_random_partner"),
	PARTNER_ONLY("partner_only"),
	NO_GROUP_PHYSICAL_TASKS("no_group_physical_tasks");

	private final String code;

	BoundaryTag(String code) {
		this.code = code;
	}

	public static BoundaryTag fromCode(String value) {
		return DomainEnumParser.parse(BoundaryTag.class, value);
	}

	@Override
	public String code() {
		return code;
	}
}
