package org.gcube.data.analysis.dataminermanagercl.shared.perspective;

import java.util.Arrays;
import java.util.List;

/**
 * 
 * @author Giancarlo Panichi
 * 
 *
 */
public enum PerspectiveType {

	User("User", "User Perspective"), Computation("Computation", "Computation Persperctive");

	/**
	 * 
	 * @param label
	 *            label
	 */
	private PerspectiveType(final String label, final String perspective) {
		this.label = label;
		this.perspective = perspective;
	}

	private final String label;
	private final String perspective;

	@Override
	public String toString() {
		return "PerspectiveType [label=" + label + ", perspective=" + perspective + "]";
	}

	public String getLabel() {
		return label;
	}

	public String getPerspective() {
		return perspective;
	}

	public String getId() {
		return name();
	}

	public static PerspectiveType getFromLabel(String label) {
		if (label == null || label.isEmpty())
			return null;

		for (PerspectiveType type : values()) {
			if (type.label.compareToIgnoreCase(label) == 0) {
				return type;
			}
		}
		return null;
	}

	public static PerspectiveType getFromPerspective(String perspective) {
		if (perspective == null || perspective.isEmpty())
			return null;

		for (PerspectiveType type : values()) {
			if (type.perspective.compareToIgnoreCase(perspective) == 0) {
				return type;
			}
		}
		return null;
	}

	public static List<PerspectiveType> asList() {
		List<PerspectiveType> list = Arrays.asList(values());
		return list;
	}
}
