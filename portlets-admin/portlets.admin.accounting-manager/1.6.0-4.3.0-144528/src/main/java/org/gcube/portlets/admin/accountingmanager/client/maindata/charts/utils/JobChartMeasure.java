package org.gcube.portlets.admin.accountingmanager.client.maindata.charts.utils;

import java.util.Arrays;
import java.util.List;

/**
 * Job Chart Measure
 * 
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */

public enum JobChartMeasure {

	Duration("Duration"), MaxInvocationTime("Max Invocation Time"), MinInvocationTime(
			"Min Invocation Time"), OperationCount("Operation Count");


	/**
	 * 
	 * @param id
	 */
	private JobChartMeasure(final String id) {
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
	public static JobChartMeasure getFromId(String id) {
		if (id == null || id.isEmpty())
			return null;

		for (JobChartMeasure measure : values()) {
			if (measure.id.compareToIgnoreCase(id) == 0) {
				return measure;
			}
		}
		return null;
	}

	public static List<JobChartMeasure> asList() {
		List<JobChartMeasure> list = Arrays.asList(values());
		return list;
	}

}
