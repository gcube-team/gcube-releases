package org.gcube.portlets.admin.accountingmanager.shared.data.response.storage;

import java.io.Serializable;

import org.gcube.portlets.admin.accountingmanager.shared.data.ChartType;

/**
 * 
  * @author Giancarlo Panichi
 *
 *
 */
public class SeriesStorageDefinition implements Serializable {

	private static final long serialVersionUID = -838923932433584818L;
	protected ChartType chartType;

	public SeriesStorageDefinition() {
		super();
	}

	public ChartType getChartType() {
		return chartType;
	}

	@Override
	public String toString() {
		return "SeriesStorageDefinition [chartType=" + chartType + "]";
	}

}
