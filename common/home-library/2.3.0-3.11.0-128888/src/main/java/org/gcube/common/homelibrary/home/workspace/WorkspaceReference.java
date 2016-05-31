/**
 * 
 */
package org.gcube.common.homelibrary.home.workspace;

import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;


/**
 * @author Valentina Marioli valentina.marioli@isti.cnr.it
 *
 */
public interface WorkspaceReference extends WorkspaceItem {
	
	
	/**
	 * Get link
	 * @return the referenceable item
	 * @throws InternalErrorException
	 */
	public WorkspaceItem getLink() throws InternalErrorException;

}
