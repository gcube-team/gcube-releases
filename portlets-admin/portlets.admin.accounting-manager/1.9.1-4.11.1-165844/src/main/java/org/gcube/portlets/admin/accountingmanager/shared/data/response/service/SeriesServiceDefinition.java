package org.gcube.portlets.admin.accountingmanager.shared.data.response.service;

import java.io.Serializable;

import org.gcube.portlets.admin.accountingmanager.shared.data.ChartType;

/**
 * 
  * @author Giancarlo Panichi
 *
 *
 */
public class SeriesServiceDefinition implements Serializable {

	private static final long serialVersionUID = 1499342835551822453L;
	protected ChartType chartType;

	public SeriesServiceDefinition() {
		super();
	}

	public ChartType getChartType() {
		return chartType;
	}

	@Override
	public String toString() {
		return "SeriesServiceDefinition [chartType=" + chartType + "]";
	}

}
