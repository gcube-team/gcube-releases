package org.gcube.application.framework.core.security;

import java.rmi.Remote;

import org.gcube.application.framework.core.session.ASLSession;
//import org.gcube.soa3.connector.common.security.CredentialManager;
//import org.gcube.soa3.connector.common.security.Credentials;
//import org.gcube.common.core.contexts.GCUBERemotePortTypeContext;
//import org.gcube.common.core.scope.GCUBEScope;
//import org.gcube.common.core.scope.GCUBEScope.MalformedScopeExpressionException;
//import org.gcube.common.core.security.GCUBESecurityManager;
import org.ietf.jgss.GSSCredential;

/**
 * SHOULD RECODE CLASS THE FUNCTIONS, USING THE NEW FEATHERWEIGHT SECURITY MODEL
 * 
 * @author nikolas
 *
 * @param <PORTTYPE>
 */
public class ServiceContextManager<PORTTYPE extends Remote> {

	//TODO: SHOULD RECODE THE FUNCTIONS, USING THE NEW FEATHERWEIGHT SECURITY MODEL
	/*  
	public static <PORTTYPE extends Remote> PORTTYPE applySecurity(PORTTYPE stub, ASLSession session) throws Exception{
		PortalSecurityManager secManager = new PortalSecurityManager(session);
		if(secManager.isSecurityEnabled())
			secManager.useCredentials(session.getCredential());
		return GCUBERemotePortTypeContext.getProxy(stub , session.getScope(), secManager);
	}
	
	public static <PORTTYPE extends Remote> PORTTYPE applySecurity(PORTTYPE stub, String scope, GSSCredential cred) throws Exception{
		GCUBESecurityManager secManager = new PortalSecurityManager(scope);
		if(secManager.isSecurityEnabled())
			secManager.useCredentials(cred);
		return GCUBERemotePortTypeContext.getProxy(stub , scope, secManager);
	}
	*/

}
