package org.gcube.portlets.admin.accountingmanager.client.maindata.charts.utils;

import java.util.Arrays;
import java.util.List;

/**
 * Storage Chart Measure
 * 
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */

public enum StorageChartMeasure {

	DataVolume("Data Volume"), OperationCount("Operation Count");

	/**
	 * 
	 * @param id
	 */
	private StorageChartMeasure(final String id) {
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
	 * @return
	 */
	public static StorageChartMeasure getFromId(String id) {
		if (id == null || id.isEmpty())
			return null;

		for (StorageChartMeasure measure : values()) {
			if (measure.id.compareToIgnoreCase(id) == 0) {
				return measure;
			}
		}
		return null;
	}

	public static List<StorageChartMeasure> asList() {
		List<StorageChartMeasure> list = Arrays.asList(values());
		return list;
	}

}
