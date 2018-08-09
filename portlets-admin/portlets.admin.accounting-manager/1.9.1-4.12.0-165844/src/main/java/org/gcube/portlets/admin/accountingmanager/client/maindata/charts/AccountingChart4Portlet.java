package org.gcube.portlets.admin.accountingmanager.client.maindata.charts;

import org.gcube.portlets.admin.accountingmanager.client.state.AccountingClientStateData;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.SeriesStorage;
import org.gcube.portlets.admin.accountingmanager.shared.exception.ChartDrawException;

import com.google.gwt.event.shared.EventBus;

/**
 * Accounting Chart 4 Portlet
 * 
   * @author Giancarlo Panichi
 *
 * 
 */
public class AccountingChart4Portlet extends AccountingChartBuilder {
	@SuppressWarnings("unused")
	private EventBus eventBus;
	private AccountingClientStateData accountingStateData;

	public AccountingChart4Portlet(EventBus eventBus, AccountingClientStateData accountingStateData) {
		this.eventBus = eventBus;
		this.accountingStateData = accountingStateData;
	}

	@Override
	public void buildChart() throws ChartDrawException {
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
