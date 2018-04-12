package org.gcube.portlets.admin.accountingmanager.shared.data;


/**
 * 
 * @author Giancarlo Panichi
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class AccountingFilterDefinition {

	protected ChartType chartType;

	public AccountingFilterDefinition() {
		super();
	}

	public ChartType getChartType() {
		return chartType;
	}

	@Override
	public String toString() {
		return "AccountingFilterDefinition [chartType="
				+ chartType + "]";
	}

	

}