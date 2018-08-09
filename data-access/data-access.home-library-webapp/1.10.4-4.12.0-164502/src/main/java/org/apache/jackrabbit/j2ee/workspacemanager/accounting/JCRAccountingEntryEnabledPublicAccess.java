/**
 * 
 */
package org.apache.jackrabbit.j2ee.workspacemanager.accounting;

import javax.jcr.Node;
import javax.jcr.RepositoryException;



public class JCRAccountingEntryEnabledPublicAccess extends JCRAccountingEntryRead {
	
	protected String itemName;

	/**
	 * @param node
	 * @throws RepositoryException
	 */
	public JCRAccountingEntryEnabledPublicAccess(Node node) throws RepositoryException {
		
		super(node);

	}


}
