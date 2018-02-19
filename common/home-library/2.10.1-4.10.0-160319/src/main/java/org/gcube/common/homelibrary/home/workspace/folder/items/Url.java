/**
 * 
 */
package org.gcube.common.homelibrary.home.workspace.folder.items;

import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public interface Url {
	
	/**
	 * Return the url value.
	 * @return the url value.
	 * @throws InternalErrorException if an internal occurs.
	 */
	String getUrl() throws InternalErrorException;

}
