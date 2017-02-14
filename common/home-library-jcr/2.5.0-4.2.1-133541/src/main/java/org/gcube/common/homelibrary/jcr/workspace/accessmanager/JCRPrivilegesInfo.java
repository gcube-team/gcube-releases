package org.gcube.common.homelibrary.jcr.workspace.accessmanager;

import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.accessmanager.ACLType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class to assist in the usage of access control from scripts.
 */
public class JCRPrivilegesInfo {
	
	private static Logger logger = LoggerFactory.getLogger(JCRPrivilegesInfo.class);


	/**
	 * Checks whether the current user has been granted privileges
	 * to add children to the specified path.
	 * 
	 * @param owner
	 * @param currentUser
	 * @param absPath
	 * @return true if the current user has the privileges, false otherwise
	 * @throws InternalErrorException
	 */
	public static boolean canAddChildren(String owner, String currentUser, String absPath) throws InternalErrorException {

		if (owner.equals(currentUser)){
			return true;
		}

		JCRAccessManager acManager = new JCRAccessManager();
		return acManager.canAddChildren(currentUser, absPath);
	
	}



	/**
	 * Checks whether the current user has been granted privileges
	 * to delete children of the specified path.
	 * 
	 * @param user
	 * @param absPath
	 * @return true if the user can delete children
	 * @throws InternalErrorException
	 */
	public static boolean canDeleteChildren(String user, String absPath) throws InternalErrorException {

		JCRAccessManager acManager = new JCRAccessManager();
		return acManager.canDeleteChildren(user, absPath);

	}



	/**
	 * Checks whether the current user has been granted privileges
	 * to delete the specified path.
	 * 
	 * @param owner
	 * @param currentUser
	 * @param absPath
	 * @param root
	 * @return true if the current user can delete the node specified by the absPath
	 * @throws InternalErrorException
	 */
	public static boolean canDelete(String owner, String currentUser, String absPath, boolean root) throws InternalErrorException {

		if (owner.equals(currentUser)){
			return true;
		}
		JCRAccessManager acManager = new JCRAccessManager();
		return acManager.canDelete(currentUser, absPath, root);
	}


	/**
	 * 
	 * Checks whether the current user has been granted privileges
	 * to modify properties of the specified path.
	 * 
	 * @param owner
	 * @param currentUser
	 * @param absPath
	 * @param root
	 * @return true if the current user can modify properties of node specified by absPath
	 * @throws InternalErrorException
	 */
	public static boolean canModifyProperties(String owner, String currentUser, String absPath, boolean root) throws InternalErrorException {
		if (owner.equals(currentUser))
			return true;
		
		JCRAccessManager acManager = new JCRAccessManager();
		return acManager.canModifyProperties(currentUser, absPath, root);

	}


	/**
	 *  Get ACL by username
	 * @param user
	 * @param absPath
	 * @return an ACLType privilege
	 * @throws InternalErrorException
	 */
	public static ACLType getACLByUser(String user, String absPath) throws InternalErrorException {
		
		JCRAccessManager acManager = new JCRAccessManager();
		return acManager.getACLByUser(user, absPath);

	}


	public static boolean canReadNode(String owner, String currentUser,
			String absPath) throws InternalErrorException {
//		System.out.println(currentUser + " Can read the node " + absPath + "?");
		if (owner.equals(currentUser))
			return true;
		
		JCRAccessManager acManager = new JCRAccessManager();
		return acManager.canReadNode(currentUser, absPath);

	}



}
