package org.gcube.portlets.admin.accountingmanager.client.maindata.charts;

import org.gcube.portlets.admin.accountingmanager.client.maindata.charts.job.JobChartBasicPanel;
import org.gcube.portlets.admin.accountingmanager.client.maindata.charts.job.JobChartContextPanel;
import org.gcube.portlets.admin.accountingmanager.client.maindata.charts.job.JobChartTopPanel;
import org.gcube.portlets.admin.accountingmanager.client.state.AccountingClientStateData;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.SeriesJob;
import org.gcube.portlets.admin.accountingmanager.shared.exception.ChartDrawException;

import com.google.gwt.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;

/**
 * Accounting Chart 4 Job
 * 
 * @author "Giancarlo Panichi" email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class AccountingChart4Job extends AccountingChartBuilder {

	private AccountingClientStateData accountingStateData;
	private EventBus eventBus;

	public AccountingChart4Job(EventBus eventBus,
			AccountingClientStateData accountingStateData) {
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

		if (!(accountingStateData.getSeriesResponse() instanceof SeriesJob)) {
			accountingChartSpec.setChart(accountingChartPanel);
			return;
		}

		SimpleContainer container = createJobChartPanel();

		accountingChartPanel = new AccountingChartPanel(container);

		accountingChartSpec.setChart(accountingChartPanel);

	}

	private SimpleContainer createJobChartPanel() throws ChartDrawException {

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
			return new JobChartTopPanel(eventBus, accountingStateData);
		case Basic:
			return new JobChartBasicPanel(eventBus, accountingStateData);
		case Context:
			return new JobChartContextPanel(eventBus, accountingStateData);

		default:
			return null;
		}

	}
}
