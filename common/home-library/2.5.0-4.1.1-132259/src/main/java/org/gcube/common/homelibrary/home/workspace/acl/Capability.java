/**
 * 
 */
package org.gcube.common.homelibrary.home.workspace.acl;

import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public interface Capability {
	
	/**
	 * @return the user id.
	 * @throws InternalErrorException if an internal error occurs.
	 */
	String getUserId() throws InternalErrorException;
	
	/**
	 * @return teh operation.
	 */
	Operation getOperation();

}
