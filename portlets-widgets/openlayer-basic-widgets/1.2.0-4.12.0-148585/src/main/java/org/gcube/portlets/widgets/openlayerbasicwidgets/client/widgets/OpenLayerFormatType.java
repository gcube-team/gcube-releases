package org.gcube.portlets.widgets.openlayerbasicwidgets.client.widgets;

import java.util.Arrays;
import java.util.List;

/**
 * 
 * @author Giancarlo Panichi
 * 
 *
 */
public enum OpenLayerFormatType {
	GML("GML"), GeoJSON("GeoJSON"), WKT("WKT");

	/**
	 * 
	 * @param label label
	 */
	private OpenLayerFormatType(final String label) {
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
	 * @param label label
	 * @return open layer format type
	 */
	public static OpenLayerFormatType getFromLabel(String label) {
		if (label == null || label.isEmpty())
			return null;

		for (OpenLayerFormatType type : values()) {
			if (type.label.compareToIgnoreCase(label) == 0) {
				return type;
			}
		}
		return null;
	}

	public static List<OpenLayerFormatType> asList() {
		List<OpenLayerFormatType> list = Arrays.asList(values());
		return list;
	}

}
