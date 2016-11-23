package org.gcube.portlets.admin.accountingmanager.shared.data.response.portlet;

import java.io.Serializable;

import org.gcube.portlets.admin.accountingmanager.shared.data.ChartType;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class SeriesPortletDefinition implements Serializable {

	private static final long serialVersionUID = 1499342835551822453L;
	protected ChartType chartType;

	public SeriesPortletDefinition() {
		super();
	}

	public ChartType getChartType() {
		return chartType;
	}

	@Override
	public String toString() {
		return "SeriesPortletDefinition [chartType=" + chartType + "]";
	}

}
