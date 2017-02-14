/**
 * 
 */
package org.gcube.common.homelibrary.home.workspace.privilegemanager;


import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;

/**
 * @author valentina
 *
 */
public interface PrivilegeManager {

	/**
	 * @param resourcePath
	 * @param users
	 * @throws InternalErrorException
	 */
	void createCostumePrivilege(String name, String[] strings)
			throws InternalErrorException;




}
