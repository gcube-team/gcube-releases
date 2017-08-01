package org.gcube.portlets.admin.accountingmanager.client.maindata.charts.utils;

import java.util.Arrays;
import java.util.List;

/**
 * Service Chart Measure
 * 
 * 
 * @author Giancarlo Panichi
 *
 *
 */

public enum SpaceChartMeasure {

	DataVolume("Data Volume");

	/**
	 * 
	 * @param id
	 */
	private SpaceChartMeasure(final String id) {
		this.id = id;
	}

	private final String id;

	@Override
	public String toString() {
		return id;
	}

	public String getLabel() {
		return id;
	}

	public String getId() {
		return id;
	}

	/**
	 * 
	 * @param id
	 *            the id
	 * @return space chart measure
	 */
	public static SpaceChartMeasure getFromId(String id) {
		if (id == null || id.isEmpty())
			return null;

		for (SpaceChartMeasure measure : values()) {
			if (measure.id.compareToIgnoreCase(id) == 0) {
				return measure;
			}
		}
		return null;
	}

	public static List<SpaceChartMeasure> asList() {
		List<SpaceChartMeasure> list = Arrays.asList(values());
		return list;
	}

}
