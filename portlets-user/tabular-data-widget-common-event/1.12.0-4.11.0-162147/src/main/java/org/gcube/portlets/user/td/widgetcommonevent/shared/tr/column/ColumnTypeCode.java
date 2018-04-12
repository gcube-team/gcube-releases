package org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column;

/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public enum ColumnTypeCode {
	ANNOTATION("ANNOTATION"), ATTRIBUTE("ATTRIBUTE"), CODE("CODE"), CODEDESCRIPTION("CODEDESCRIPTION"), CODENAME(
			"CODENAME"), DIMENSION("DIMENSION"), MEASURE("MEASURE"), TIMEDIMENSION("TIMEDIMENSION");

	private ColumnTypeCode(final String id) {
		this.id = id;
	}

	private final String id;

	@Override
	public String toString() {
		return id;
	}

	public String getId() {
		return id;
	}

	public String getLabel() {
		switch (this) {
		case ANNOTATION:
			return "Annotation";
		case ATTRIBUTE:
			return "Attribute";
		case CODE:
			return "Code";
		case CODEDESCRIPTION:
			return "Code Description";
		case CODENAME:
			return "Code Name";
		case DIMENSION:
			return "Dimension";
		case MEASURE:
			return "Measure";
		case TIMEDIMENSION:
			return "Time Dimension";
		default:
			return null;
		}

	}

	public static ColumnTypeCode getColumnTypeCodeFromId(String id) {
		if (id == null || id.isEmpty())
			return null;

		for (ColumnTypeCode columnTypeCode : values()) {
			if (columnTypeCode.id.compareToIgnoreCase(id) == 0) {
				return columnTypeCode;
			}
		}
		return null;

	}

}
