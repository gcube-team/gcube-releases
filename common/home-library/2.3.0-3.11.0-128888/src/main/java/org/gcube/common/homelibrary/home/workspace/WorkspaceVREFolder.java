/**
 * 
 */
package org.gcube.common.homelibrary.home.workspace;

import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.usermanager.GCubeGroup;
import org.gcube.common.homelibrary.model.exceptions.RepositoryException;


/**
 * @author Valentina Marioli
 *
 */
public interface WorkspaceVREFolder extends WorkspaceSharedFolder {
	
	/**
	 * Get VRE Group
	 * @return the VRE group
	 * @throws InternalErrorException
	 */
	GCubeGroup getGroup() throws InternalErrorException;
	
	/**
	 * Get VRE scope
	 * @return the VRE scope
	 * @throws InternalErrorException
	 */
	String getScope() throws InternalErrorException;

	/**
	 * Add user to VRE folder 
	 * @param user to add to VRE folder
	 * @throws InternalErrorException
	 */
	void addUserToVRE(String user) throws InternalErrorException;
	
	/**
	 * Remove user from VRE folder
	 * @param user to remove from VRE folder
	 * @throws InternalErrorException
	 */
	void removeUserFromVRE(String user) throws InternalErrorException;

	/**
	 * Change owner to VRE folder
	 * @throws InternalErrorException
	 * @throws RepositoryException 
	 */
	void changeOwner(String user) throws InternalErrorException, RepositoryException;
	

	 
}
