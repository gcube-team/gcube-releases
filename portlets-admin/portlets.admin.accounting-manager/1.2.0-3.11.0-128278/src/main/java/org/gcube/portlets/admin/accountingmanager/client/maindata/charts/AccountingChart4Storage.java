package org.gcube.portlets.admin.accountingmanager.client.maindata.charts;

import org.gcube.portlets.admin.accountingmanager.client.maindata.charts.storage.StorageChartBasicPanel;
import org.gcube.portlets.admin.accountingmanager.client.maindata.charts.storage.StorageChartTopPanel;
import org.gcube.portlets.admin.accountingmanager.client.state.AccountingStateData;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.SeriesStorage;
import org.gcube.portlets.admin.accountingmanager.shared.exception.AccountingManagerChartDrawException;

import com.sencha.gxt.widget.core.client.container.SimpleContainer;

/**
 * Accounting Chart 4 Storage
 * 
 * @author "Giancarlo Panichi" email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class AccountingChart4Storage extends AccountingChartBuilder {

	private AccountingStateData accountingStateData;

	public AccountingChart4Storage(AccountingStateData accountingStateData) {
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

		SimpleContainer container = createStorageChartPanel();

		accountingChartPanel = new AccountingChartPanel(container);

		accountingChartSpec.setChart(accountingChartPanel);

	}

	private SimpleContainer createStorageChartPanel()
			throws AccountingManagerChartDrawException {

		if (accountingStateData == null
				|| accountingStateData.getSeriesRequest() == null
				|| accountingStateData.getSeriesRequest()
						.getAccountingFilterDefinition() == null
				|| accountingStateData.getSeriesRequest()
						.getAccountingFilterDefinition()
						.getChartType() == null) {
			return null;
		}

		switch (accountingStateData.getSeriesRequest()
				.getAccountingFilterDefinition().getChartType()) {

		case Top:
			return new StorageChartTopPanel(accountingStateData);
					//createStorageChart(new StorageChart4Top(accountingStateData));
		case Basic:
			return new StorageChartBasicPanel(accountingStateData);
					//createStorageChart(new StorageChart4Basic(
					//accountingStateData));
		default:

			return null;
		}

	}


}
