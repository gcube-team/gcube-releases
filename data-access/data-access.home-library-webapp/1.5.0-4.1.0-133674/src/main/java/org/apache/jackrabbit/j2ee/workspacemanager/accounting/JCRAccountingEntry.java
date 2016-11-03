/**
 * 
 */
package org.apache.jackrabbit.j2ee.workspacemanager.accounting;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.gcube.common.homelibary.model.items.accounting.AccountingDelegate;
import org.gcube.common.homelibary.model.items.accounting.AccountingEntryType;
import org.gcube.common.homelibary.model.items.accounting.AccountingProperty;

public class JCRAccountingEntry{
	
	protected AccountingDelegate item;
	
	public JCRAccountingEntry(Node node) throws RepositoryException {
		
		item = new AccountingDelegate();
		
		item.setUser(node.getProperty(AccountingProperty.USER.toString()).getString());
		item.setDate(node.getProperty(AccountingProperty.DATE.toString()).getDate());
		item.setEntryType(AccountingEntryType.getEnum(node.getPrimaryNodeType().getName()));
	}
	

	public AccountingDelegate getAccoutingDelegate() {
		return item;
	}

}
