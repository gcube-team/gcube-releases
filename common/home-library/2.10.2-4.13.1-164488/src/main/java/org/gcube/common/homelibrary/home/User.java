/**
 * 
 */
package org.gcube.common.homelibrary.home;

/**
 * Represent an user. 
 * An user is identified by an id.
 * One user can be present in only one scope at time.
 * @author Federico De Faveri defaveri@isti.cnr.it
 */
public interface User {
	
	/**
	 * Retrieves the user's id.
	 * @return the id.
	 */
	String getId();
	
	/**
	 * Retrieves the user's portal login.
	 * @return the portal login.
	 */
	String getPortalLogin();
	
}
