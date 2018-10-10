package org.gcube.portlets.admin.accountingmanager.client.maindata.charts;

import org.gcube.portlets.admin.accountingmanager.client.maindata.charts.space.SpaceChartSpacesPanel;
import org.gcube.portlets.admin.accountingmanager.client.state.AccountingClientStateData;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.SeriesSpace;
import org.gcube.portlets.admin.accountingmanager.shared.exception.ChartDrawException;

import com.google.gwt.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;

/**
 * Accounting Chart 4 Space
 * 
   * @author Giancarlo Panichi
 *
 * 
 */
public class AccountingChart4Space extends AccountingChartBuilder {
	private EventBus eventBus;
	private AccountingClientStateData accountingStateData;
	

	public AccountingChart4Space(EventBus eventBus, AccountingClientStateData accountingStateData) {
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

		if (!(accountingStateData.getSeriesResponse() instanceof SeriesSpace)) {
			accountingChartSpec.setChart(accountingChartPanel);
			return;
		}

		SimpleContainer container = createSpaceChartPanel();

		accountingChartPanel = new AccountingChartPanel(container);

		accountingChartSpec.setChart(accountingChartPanel);

	}

	private SimpleContainer createSpaceChartPanel()
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

		case Spaces:
			return new SpaceChartSpacesPanel(eventBus,accountingStateData);	
		default:
			return null;
		}

	}
}
