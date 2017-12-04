/**
 * 
 */
package org.gcube.common.homelibrary.home.workspace.accessmanager;

/**
 * @author valentina
 *
 */
public enum ACLType {

	/**
	 * Access denied to user.
	 */
	NONE,
	/**
	 * Allow users to only read files.
	 */
	READ_ONLY,
	/**
	 * Allow users to create, edit and delete their own files.
	 */
	WRITE_OWNER,
	/**
	 * Allow users to create, edit and delete files of everyone in the share.
	 */
	WRITE_ALL,
	/**
	 * All privileges.
	 */
	ADMINISTRATOR;


}
