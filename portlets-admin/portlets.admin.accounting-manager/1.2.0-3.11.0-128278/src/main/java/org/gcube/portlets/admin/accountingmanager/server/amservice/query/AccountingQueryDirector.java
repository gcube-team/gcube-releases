package org.gcube.portlets.admin.accountingmanager.server.amservice.query;

import java.util.ArrayList;

import org.gcube.portlets.admin.accountingmanager.shared.exception.AccountingManagerServiceException;


/**
 * Accounting Query Director
 * 
 * @author "Giancarlo Panichi"
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class AccountingQueryDirector {
	AccountingQueryBuilder accountingQueryBuilder;

	public void setAccountingQueryBuilder(
			AccountingQueryBuilder accountingQueryBuilder) {
		this.accountingQueryBuilder = accountingQueryBuilder;
	}

	public AccountingQuery getAccountingQuery() {
		return accountingQueryBuilder.getAccountingQuerySpec().getOp();

	}
	
	public ArrayList<AccountingQuery> getListOfAccountingQuery() {
		return accountingQueryBuilder.getAccountingQuerySpec().getOps();

	}
	
	public void constructAccountingQuery() throws AccountingManagerServiceException {
		accountingQueryBuilder.createSpec();
		accountingQueryBuilder.buildOpEx();

	}
}
