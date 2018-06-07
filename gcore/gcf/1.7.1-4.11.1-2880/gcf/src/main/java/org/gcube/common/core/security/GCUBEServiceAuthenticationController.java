package org.gcube.common.core.security;

import java.util.Map;

import org.gcube.common.core.faults.GCUBEException;

/**
 * 
 * Interface of the authentication controller
 * 
 * @author Ciro Formisano
 *
 */

public interface GCUBEServiceAuthenticationController extends GCUBEServiceSecurityController
{
	/**
	 * 
	 * Authenticates the call
	 * 
	 * @param parameters the parameters of the call
	 * @throws GCUBEException if the call is not authenticated or if there are problems
	 */
	public void authenticateCall (Map<String, Object> parameters) throws GCUBEException;
	
}
