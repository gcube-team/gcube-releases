/**
 * 
 */
package org.apache.jackrabbit.j2ee.workspacemanager.accounting;

import javax.jcr.Node;
import javax.jcr.RepositoryException;


/**
 * @author Valentina Marioli valentina.marioli@isti.cnr.it
 *
 */
public class JCRAccountingEntryUpdate extends JCRAccountingEntryRead  {
	
	/**
	 * @param node
	 * @throws RepositoryException
	 */
	public JCRAccountingEntryUpdate(Node node) throws RepositoryException {
		
		super(node);
		

	}

}
