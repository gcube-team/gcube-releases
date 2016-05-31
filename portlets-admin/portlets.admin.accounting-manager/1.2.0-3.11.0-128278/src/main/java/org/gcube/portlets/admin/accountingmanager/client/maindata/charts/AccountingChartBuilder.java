package org.gcube.portlets.admin.accountingmanager.client.maindata.charts;

import org.gcube.portlets.admin.accountingmanager.shared.exception.AccountingManagerChartDrawException;

/**
 * Abstract class for build Accounting Chart
 * 
 * @author "Giancarlo Panichi"
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public abstract class AccountingChartBuilder {
	protected AccountingChartSpec accountingChartSpec;
	
	public AccountingChartSpec getAccountingChartSpec(){
		return accountingChartSpec;
	}
	public void createSpec(){
		accountingChartSpec=new AccountingChartSpec();
		
	}
	
	public abstract void buildChart() throws AccountingManagerChartDrawException;
	    
	
}
