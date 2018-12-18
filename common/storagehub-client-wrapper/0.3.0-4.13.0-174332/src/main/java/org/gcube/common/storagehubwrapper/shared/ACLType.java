/**
 *
 */
package org.gcube.common.storagehubwrapper.shared;


/**
 * The Enum ACLType.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Jun 15, 2018
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
