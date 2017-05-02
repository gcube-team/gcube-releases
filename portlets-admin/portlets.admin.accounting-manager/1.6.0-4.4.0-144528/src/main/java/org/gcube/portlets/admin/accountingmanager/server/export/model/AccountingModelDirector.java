package org.gcube.portlets.admin.accountingmanager.server.export.model;

import java.util.ArrayList;

import org.gcube.portlets.admin.accountingmanager.shared.exception.ServiceException;


/**
 * Accounting Model Director
 * 
 * @author "Giancarlo Panichi"
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class AccountingModelDirector {
	AccountingModelBuilder accountingModelBuilder;

	public void setAccountingModelBuilder(
			AccountingModelBuilder accountingModelBuilder) {
		this.accountingModelBuilder = accountingModelBuilder;
	}

	public AccountingDataModel getAccountingModel() {
		return accountingModelBuilder.getAccountingModelSpec().getOp();

	}
	
	public ArrayList<AccountingDataModel> getListOfAccountingModel() {
		return accountingModelBuilder.getAccountingModelSpec().getOps();

	}
	
	public void constructAccountingModel() throws ServiceException {
		accountingModelBuilder.createSpec();
		accountingModelBuilder.buildOpEx();

	}
}
