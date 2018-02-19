package org.gcube.portlets.user.dataminermanager.shared.parameters;

import java.util.Arrays;
import java.util.List;

/**
 * 
 * @author Giancarlo Panichi
 * 
 *
 */
public enum WKTGeometryType {
	Point("Point"), LineString("LineString"), Polygon("Polygon"), Circle("Circle"), Triangle("Triangle"), Square(
			"Square"), Pentagon("Pentagon"), Hexagon("Hexagon"), Box("Box");

	/**
	 * 
	 * @param label
	 *            label
	 */
	private WKTGeometryType(final String label) {
		this.label = label;
	}

	private final String label;

	@Override
	public String toString() {
		return label;
	}

	public String getLabel() {
		return label;
	}

	public String getId() {
		return name();
	}

	/**
	 * 
	 * @param label
	 *            label
	 * @return wkt geometry type
	 */
	public static WKTGeometryType getFromLabel(String label) {
		if (label == null || label.isEmpty())
			return null;

		for (WKTGeometryType type : values()) {
			if (type.label.compareToIgnoreCase(label) == 0) {
				return type;
			}
		}
		return null;
	}

	public static List<WKTGeometryType> asList() {
		List<WKTGeometryType> list = Arrays.asList(values());
		return list;
	}
}
