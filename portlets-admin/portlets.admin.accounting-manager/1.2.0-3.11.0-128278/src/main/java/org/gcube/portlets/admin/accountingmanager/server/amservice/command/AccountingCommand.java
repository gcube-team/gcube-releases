package org.gcube.portlets.admin.accountingmanager.server.amservice.command;

import org.gcube.portlets.admin.accountingmanager.shared.exception.AccountingManagerServiceException;


/**
 * 
 * @author Giancarlo Panichi
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public interface AccountingCommand<T> {
	
	T execute() throws AccountingManagerServiceException;
	
}
