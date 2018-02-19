/**
 * 
 */
package org.apache.jackrabbit.j2ee.workspacemanager.accounting;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.gcube.common.homelibary.model.items.accounting.AccountingEntryType;


public class JCRAccountingFolderEntryCut extends JCRAccountingFolderEntryRemoval{

	
	public JCRAccountingFolderEntryCut(Node node) throws RepositoryException {
		super(node);
//		item.setEntryType(AccountingEntryType.CUT);
		
	}


	

}
