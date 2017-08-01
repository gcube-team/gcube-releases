package org.gcube.portlets.admin.accountingmanager.client.maindata.charts;

import com.sencha.gxt.widget.core.client.container.Container;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class AccountingChartPanel {

	private Container chart;

	public AccountingChartPanel(Container chart) {
		super();
		this.chart = chart;
	}

	public Container getChart() {
		return chart;
	}

	public void setChart(Container chart) {
		this.chart = chart;
	}

	@Override
	public String toString() {
		return "AccountingChartPanel [chart=" + chart + "]";
	}

}
