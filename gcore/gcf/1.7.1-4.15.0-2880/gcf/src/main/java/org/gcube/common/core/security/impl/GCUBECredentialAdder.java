package org.gcube.common.core.security.impl;

import java.rmi.Remote;
import java.util.Map;

import javax.xml.rpc.Stub;

import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.security.GCUBEClientSecurityManager;
import org.gcube.common.core.security.SecurityCredentials;
import org.gcube.common.core.security.context.SecurityContextFactory;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.ietf.jgss.GSSCredential;

public class GCUBECredentialAdder implements GCUBEClientSecurityManager {

	private GCUBELog logger;
	private CredentialManager internalCredentialManager;
	private int securityStatus; 	// -1 = default, GHN security Status
									 	//  0 = disabled
										//	1 = enabled

	
	public GCUBECredentialAdder() 
	{
		this.logger = new GCUBELog(this);
		this.securityStatus = -1;
		this.internalCredentialManager = new CredentialManager();
	}

	@Override
	public void setIdentityParameters(Map<String, String> parameters) throws Exception 
	{
		logger.debug("No Identity parameters needed");
	}

	@Override
	public void setDefaultIdentityParameter(String identity) throws Exception 
	{
		logger.debug("No Identity parameters needed");
		
	}

	@Override
	public void forceSecurityEnabled() 
	{
		this.securityStatus = 1;
	}

	@Override
	public void forceSecurityDisabled() 
	{
		this.securityStatus = 0;
		
	}

	@Override
	public void disableSecurityStatusEnforcement() 
	{
		this.securityStatus = -1;
	}

	@Override
	public SecurityCredentials getClientBaseCredentials() 
	{
		return new GSSSecurityCredentials(SecurityContextFactory.getInstance().getSecurityContext().getDefaultCredentials());
	}

	
	@Override
	public boolean isSecurityEnabled() 
	{
		switch (this.securityStatus)
		{
			case 0: return false;
			case 1: return true;
			default: return GHNContext.getContext().isSecurityEnabled();
		
		}
		
	}

	@Deprecated
	@Override
	public synchronized void useCredentials(GSSCredential credentials) throws Exception 
	{
		this.useCredentials(Thread.currentThread(),new GSSSecurityCredentials(credentials));

	}

	@Override
	public synchronized void useCredentials(SecurityCredentials credentials) throws Exception 
	{

		this.useCredentials(Thread.currentThread(),credentials);
	}

	@Override
	public synchronized void useCredentials(Thread thread, SecurityCredentials... credentials) throws Exception {
		if (isSecurityEnabled())
		{
			if (credentials.length==0) {// if no credentials are provided, use the current ones.
				credentials=new SecurityCredentials[]{this.getCredentials()};
			}
			else if (!checkCredentialType(credentials)) throw new Exception("Invalid credentials set");
		
			this.internalCredentialManager.setCredentials(thread, credentials);
		}

	}
	
	private boolean checkCredentialType (SecurityCredentials [] credentials)
	{
		for (SecurityCredentials credential: credentials)
		{
			if (!(credential.getCredentialsAsObject() instanceof GSSCredential)) 
			{
				logger.debug("Invalid credentials");
				return false;
			}
		}
		
		return true;
	}

	@Override
	public SecurityCredentials getCredentials() 
	{
		
		return this.internalCredentialManager.getCredentials();
	}

	@Override
	public void setSecurity(Remote s, AuthMode e, DelegationMode d) throws Exception {
		
		if (isSecurityEnabled()) 
		{
			logger.debug("Adding the security credentials");
			this.internalCredentialManager.associateCurrentCredentials((Stub) s);
			logger.debug("Security credentials added");
		}
		else
			logger.debug("Security not enabled, Nothing to do");

	}

	@Override
	public void setAuthMethod(AuthMethod m) 
	{
		logger.debug("No auth method required");

	}



	
}
