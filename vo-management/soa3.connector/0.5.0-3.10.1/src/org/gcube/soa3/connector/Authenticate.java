package org.gcube.soa3.connector;

import it.eng.rdlab.soa3.connector.beans.UserBean;

/**
 * 
 * Authentication service interface
 * 
 * @author Ciro Formisano (ENG)
 *
 */
public interface Authenticate 
{
	/**
	 * 
	 * Setter for the endpoint to Soa3
	 * 
	 * @param soa3Endpoint
	 */
	public void setSoa3Endpoint (String soa3Endpoint);
	
	/**
	 * Authentication service
	 * 
	 * @param parameter the authentication parameter
	 * @return
	 */
	public UserBean authenticate (String parameter, String organization);

}
