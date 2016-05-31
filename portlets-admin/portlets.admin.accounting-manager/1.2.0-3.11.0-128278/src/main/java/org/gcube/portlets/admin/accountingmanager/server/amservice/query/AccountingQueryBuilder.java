package org.gcube.portlets.admin.accountingmanager.server.amservice.query;

import java.text.SimpleDateFormat;

import org.gcube.portlets.admin.accountingmanager.shared.exception.AccountingManagerServiceException;

/**
 * Abstract class for build Accounting Query
 * 
 * @author "Giancarlo Panichi"
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public abstract class AccountingQueryBuilder {
	protected AccountingQuerySpec accountingQuerySpec;
	protected SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMMMM dd");
	
	public AccountingQuerySpec getAccountingQuerySpec(){
		return accountingQuerySpec;
	}
	public void createSpec(){
		accountingQuerySpec=new AccountingQuerySpec();
		
	}
	
	public abstract void buildOpEx() throws AccountingManagerServiceException;
	    
	
}
