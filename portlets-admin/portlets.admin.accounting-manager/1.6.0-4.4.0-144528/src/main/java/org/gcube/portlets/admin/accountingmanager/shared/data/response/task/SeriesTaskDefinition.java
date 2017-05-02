package org.gcube.portlets.admin.accountingmanager.shared.data.response.task;

import java.io.Serializable;

import org.gcube.portlets.admin.accountingmanager.shared.data.ChartType;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class SeriesTaskDefinition implements Serializable {

	private static final long serialVersionUID = 3736717661941074912L;
	protected ChartType chartType;

	public SeriesTaskDefinition() {
		super();
	}

	public ChartType getChartType() {
		return chartType;
	}

	@Override
	public String toString() {
		return "SeriesTaskDefinition [chartType=" + chartType + "]";
	}

}
