package org.gcube.data.analysis.dataminermanagercl.shared.parameters;

import java.util.Arrays;
import java.util.List;

/**
 * 
 * @author Giancarlo Panichi
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public enum WKTGeometryType {
	Point("Point"), LineString("LineString"), Polygon("Polygon"), Circle(
			"Circle"), Triangle("Triangle"), Square("Square"), Pentagon(
			"Pentagon"), Hexagon("Hexagon"), Box("Box");

	/**
	 * @param text
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
	 * @return
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
