package org.gcube.portlets.admin.accountingmanager.server.export.model;

import java.text.SimpleDateFormat;

import org.gcube.portlets.admin.accountingmanager.shared.exception.ServiceException;

/**
 * Abstract class for build Accounting Model
 * 
  * @author Giancarlo Panichi
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public abstract class AccountingModelBuilder {
	protected AccountingModelSpec accountingModelSpec;
	
	protected SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM dd");
	protected SimpleDateFormat sdfCSVDate = new SimpleDateFormat("yyyy/MM/dd HH:mm");
	protected SimpleDateFormat sdfFile = new SimpleDateFormat("yyyyMMdd");
	
	public AccountingModelSpec getAccountingModelSpec(){
		return accountingModelSpec;
	}
	public void createSpec(){
		accountingModelSpec=new AccountingModelSpec();
		
	}
	
	public abstract void buildOpEx() throws ServiceException;
	    
	
}
