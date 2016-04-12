/**
 * 
 */
package org.gcube.common.homelibrary.home.workspace;

import java.util.List;

import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;


/**
 * @author Valentina Marioli
 *
 */
public interface WorkspaceVREFolder extends WorkspaceSharedFolder {
	
	/**
	 * Get VRE Group
	 * @return Administrators
	 * @throws InternalErrorException
	 */
	List<String> getGroups() throws InternalErrorException;
	

}
