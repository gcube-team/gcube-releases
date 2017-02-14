package org.gcube.portlets.widgets.openlayerbasicwidgets.client.widgets;

import java.util.Arrays;
import java.util.List;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public enum GeometryType {
	Point("Point"), LineString("LineString"), Polygon("Polygon"), Circle(
			"Circle"), Triangle("Triangle"), Square("Square"), Pentagon(
			"Pentagon"), Hexagon("Hexagon"), Box("Box"), None("None");

	/**
	 * @param text
	 */
	private GeometryType(final String label) {
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
	public static GeometryType getFromLabel(String label) {
		if (label == null || label.isEmpty())
			return null;

		for (GeometryType type : values()) {
			if (type.label.compareToIgnoreCase(label) == 0) {
				return type;
			}
		}
		return null;
	}

	public static List<GeometryType> asList() {
		List<GeometryType> list = Arrays.asList(values());
		return list;
	}

}
