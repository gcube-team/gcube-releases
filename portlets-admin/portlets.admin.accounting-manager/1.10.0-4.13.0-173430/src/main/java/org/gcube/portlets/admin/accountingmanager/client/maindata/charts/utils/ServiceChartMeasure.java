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

public enum ServiceChartMeasure {

	Duration("Duration"), MaxInvocationTime("Max Invocation Time"), MinInvocationTime(
			"Min Invocation Time"), OperationCount("Operation Count");

	/**
	 * 
	 * @param id
	 */
	private ServiceChartMeasure(final String id) {
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
	 * @param id the id
	 * @return service chart measure
	 */
	public static ServiceChartMeasure getFromId(String id) {
		if (id == null || id.isEmpty())
			return null;

		for (ServiceChartMeasure measure : values()) {
			if (measure.id.compareToIgnoreCase(id) == 0) {
				return measure;
			}
		}
		return null;
	}

	public static List<ServiceChartMeasure> asList() {
		List<ServiceChartMeasure> list = Arrays.asList(values());
		return list;
	}

}
