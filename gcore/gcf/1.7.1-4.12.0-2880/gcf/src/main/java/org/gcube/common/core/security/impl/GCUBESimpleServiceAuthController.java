package org.gcube.common.core.security.impl;

import java.util.Map;

import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.faults.GCUBEException;
import org.gcube.common.core.security.GCUBEServiceAuthenticationController;
import org.gcube.common.core.security.GCUBEServiceAuthorizationController;
import org.gcube.common.core.security.GCUBEServiceSecurityManager;

/**
 * 
 * Does Nothing
 * 
 * @author Ciro Formisano
 *
 */
public class GCUBESimpleServiceAuthController implements GCUBEServiceAuthenticationController, GCUBEServiceAuthorizationController

{

	@Override
	public void initialise(GCUBEServiceContext ctxt, GCUBEServiceSecurityManager securityConfigurationManager) throws Exception 
	{
		//Do nothing
	}

	@Override
	public void authoriseCall(Map<String, Object> parameters) throws GCUBEException 
	{
		// Authorises every call
		
	}

	@Override
	public void authenticateCall(Map<String, Object> parameters) throws GCUBEException 
			
	{
		// Authenticate every call
		
	}
	
	@Override
	public boolean isSecurityEnabled() 
	{
		return false;
	}

}
