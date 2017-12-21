package org.gcube.portlets.admin.accountingmanager.server.amservice.query;

import org.gcube.portlets.admin.accountingmanager.shared.data.ChartType;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class AccountingQuery {
	protected ChartType chartType;

	public AccountingQuery() {

	}

	public ChartType getChartType() {
		return chartType;
	}

	@Override
	public String toString() {
		return "AccountingQuery [chartType=" + chartType + "]";
	}

}