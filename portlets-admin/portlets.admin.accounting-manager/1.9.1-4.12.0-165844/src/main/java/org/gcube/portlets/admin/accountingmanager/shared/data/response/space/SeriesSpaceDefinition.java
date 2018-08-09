package org.gcube.portlets.admin.accountingmanager.shared.data.response.space;

import java.io.Serializable;

import org.gcube.portlets.admin.accountingmanager.shared.data.ChartType;

/**
 * 
  * @author Giancarlo Panichi
 *
 *
 */
public class SeriesSpaceDefinition implements Serializable {

	private static final long serialVersionUID = -1371973741194014153L;
	protected ChartType chartType;

	public SeriesSpaceDefinition() {
		super();
	}

	public ChartType getChartType() {
		return chartType;
	}

	@Override
	public String toString() {
		return "SeriesSpaceDefinition [chartType=" + chartType + "]";
	}

}
