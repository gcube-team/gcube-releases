package org.gcube.common.core.security;


import java.util.Map;

import org.gcube.common.core.faults.GCUBEException;

/**
 * 
 * @author Ciro Formisano
 *
 */

public interface GCUBEServiceAuthorizationController extends GCUBEServiceSecurityController
{
	
	/**
	 * 
	 * Checks if the call is authorized
	 * 
	 * @param parameters parameters for the authorization process
	 * @throws GCUBEException if the call is not authorized or if there are other problems
	 */
	public void authoriseCall(Map<String, Object> parameters) throws GCUBEException;
	
	
}
