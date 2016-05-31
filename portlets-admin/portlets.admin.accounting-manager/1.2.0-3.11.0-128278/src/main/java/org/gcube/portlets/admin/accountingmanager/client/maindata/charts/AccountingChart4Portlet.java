package org.gcube.portlets.admin.accountingmanager.client.maindata.charts;

import org.gcube.portlets.admin.accountingmanager.client.state.AccountingStateData;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.SeriesStorage;
import org.gcube.portlets.admin.accountingmanager.shared.exception.AccountingManagerChartDrawException;

/**
 * Accounting Chart 4 Portlet
 * 
 * @author "Giancarlo Panichi" email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class AccountingChart4Portlet extends AccountingChartBuilder {
	
	private AccountingStateData accountingStateData;

	public AccountingChart4Portlet(AccountingStateData accountingStateData) {
		this.accountingStateData = accountingStateData;
	}

	@Override
	public void buildChart() throws AccountingManagerChartDrawException {
		AccountingChartPanel accountingChartPanel = null;

		if (accountingStateData == null
				|| accountingStateData.getAccountingType() == null
				|| accountingStateData.getSeriesRequest() == null
				|| accountingStateData.getSeriesResponse() == null) {
			accountingChartSpec.setChart(accountingChartPanel);
			return;
		}

		if (!(accountingStateData.getSeriesResponse() instanceof SeriesStorage)) {
			accountingChartSpec.setChart(accountingChartPanel);
			return;
		}
			
		//StorageChart container=new StorageChart(accountingStateData);		
	
		//accountingChartPanel = new AccountingChartPanel(container);

		accountingChartSpec.setChart(accountingChartPanel);

	}
}
