package org.gcube.portlets.admin.accountingmanager.client.maindata.charts;

import org.gcube.portlets.admin.accountingmanager.shared.exception.ChartDrawException;


/**
 * Accounting Chart Director
 * 
 * @author "Giancarlo Panichi"
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class AccountingChartDirector {
	AccountingChartBuilder accountingChartBuilder;

	public void setAccountingChartBuilder(
			AccountingChartBuilder accountingChartBuilder) {
		this.accountingChartBuilder = accountingChartBuilder;
	}

	public AccountingChartPanel getAccountingChart() {
		return accountingChartBuilder.getAccountingChartSpec().getChart();

	}
	
	public void constructAccountingChart() throws ChartDrawException {
		accountingChartBuilder.createSpec();
		accountingChartBuilder.buildChart();

	}
}
