package org.gcube.common.core.security;

import org.gcube.common.core.contexts.GCUBEServiceContext;

public interface GCUBEServiceSecurityController {

	String CONTEXT = "CONTEXT";
	String HEADERS = "HEADERS";
	String MESSAGE_CONTEXT = "MESSAGE_CONTEXT";

	
	
	/**
	 * Initialise the manager with the context of the associated service.
	 * @param ctxt the context.
	 */
	public void initialise(GCUBEServiceContext ctxt, GCUBEServiceSecurityManager securityManager) throws Exception;

	/**
	 * 
	 * @return true if the security is enabled for this security descriptor
	 */
	public boolean isSecurityEnabled ();
}
