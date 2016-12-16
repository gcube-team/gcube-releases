/**
 * 
 */
package org.apache.jackrabbit.j2ee.workspacemanager.accounting;

import javax.jcr.Node;
import javax.jcr.RepositoryException;



public class JCRAccountingEntryDisabledPublicAccess extends JCRAccountingEntryRead {
	
	protected String itemName;

	public JCRAccountingEntryDisabledPublicAccess(Node node) throws RepositoryException {
		super(node);
	}

}
