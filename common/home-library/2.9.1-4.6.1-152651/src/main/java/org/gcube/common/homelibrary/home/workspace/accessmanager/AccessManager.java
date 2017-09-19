/**
 * 
 */
package org.gcube.common.homelibrary.home.workspace.accessmanager;

import java.util.List;
import java.util.Map;

import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;

/**
 * @author valentina marioli
 *
 */
public interface AccessManager {
	
	/**
	 * Get the permissions which are effective for a particular node.
	 * @param absPath
	 * @throws InternalErrorException
	 */
	public Map<String, List<String>> getEACL(String absPath) throws InternalErrorException;
	
	/**
	 * Allow users to only read files.
	 * @param users
	 * @param absPath
	 * @throws InternalErrorException
	 */
	public boolean setReadOnlyACL(List<String> users, String absPath) throws InternalErrorException;
	/**
	 * 
	 * Allow users to create, edit and delete files of everyone in the share.
	 * @param users
	 * @param absPath
	 * @throws InternalErrorException
	 */
	public boolean setWriteOwnerACL(List<String> users, String absPath) throws InternalErrorException;
	/**
	 * Allow users to create, edit and delete files of everyone in the share.
	 * @param users
	 * @param absPath
	 * @throws InternalErrorException
	 */
	public boolean setWriteAllACL(List<String> users, String absPath) throws InternalErrorException;
	/**
	 * All privileges.
	 * @param users
	 * @param absPath
	 * @return 
	 * @throws InternalErrorException
	 */
	public boolean setAdminACL(List<String> users, String absPath) throws InternalErrorException;

	/**
	 * Delete old Aces
	 * @param resourcePath
	 * @param principalNamesToDelete
	 * @return
	 * @throws InternalErrorException
	 * @throws PathNotFoundException 
	 */
	public boolean deleteAces(String resourcePath, List<String> users)
			throws InternalErrorException;


}
