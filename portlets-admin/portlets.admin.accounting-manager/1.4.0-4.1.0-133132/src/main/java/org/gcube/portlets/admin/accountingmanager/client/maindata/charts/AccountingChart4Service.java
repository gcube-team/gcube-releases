package org.gcube.portlets.admin.accountingmanager.client.maindata.charts;

import org.gcube.portlets.admin.accountingmanager.client.maindata.charts.service.ServiceChartBasicPanel;
import org.gcube.portlets.admin.accountingmanager.client.maindata.charts.service.ServiceChartTopPanel;
import org.gcube.portlets.admin.accountingmanager.client.state.AccountingClientStateData;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.SeriesService;
import org.gcube.portlets.admin.accountingmanager.shared.exception.ChartDrawException;

import com.google.gwt.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;

/**
 * Accounting Chart 4 Service
 * 
 * @author "Giancarlo Panichi" email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class AccountingChart4Service extends AccountingChartBuilder {
	private EventBus eventBus;
	private AccountingClientStateData accountingStateData;
	

	public AccountingChart4Service(EventBus eventBus, AccountingClientStateData accountingStateData) {
		this.eventBus=eventBus;
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

		if (!(accountingStateData.getSeriesResponse() instanceof SeriesService)) {
			accountingChartSpec.setChart(accountingChartPanel);
			return;
		}

		SimpleContainer container = createServiceChartPanel();

		accountingChartPanel = new AccountingChartPanel(container);

		accountingChartSpec.setChart(accountingChartPanel);

	}

	private SimpleContainer createServiceChartPanel()
			throws ChartDrawException {

		if (accountingStateData == null
				|| accountingStateData.getSeriesRequest() == null
				|| accountingStateData.getSeriesRequest()
						.getAccountingFilterDefinition() == null
				|| accountingStateData.getSeriesRequest()
						.getAccountingFilterDefinition().getChartType() == null) {
			return null;
		}

		switch (accountingStateData.getSeriesRequest()
				.getAccountingFilterDefinition().getChartType()) {

		case Top:
			return new ServiceChartTopPanel(eventBus,accountingStateData);
		case Basic:
			return new ServiceChartBasicPanel(eventBus, accountingStateData);
		default:

			return null;
		}

	}
}
